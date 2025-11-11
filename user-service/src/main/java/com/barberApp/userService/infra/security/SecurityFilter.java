package com.barberApp.userService.infra.security;

import com.barberApp.userService.models.User;
import com.barberApp.userService.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserRepository repository;

    public SecurityFilter(JwtUtils jwtUtils, UserRepository repository) {
        this.jwtUtils = jwtUtils;
        this.repository = repository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var tokenJWT = jwtUtils.retrieveToken(request);
        if (tokenJWT != null) {
            System.out.println(tokenJWT + "passando por aqui");
            try {
                var subject = jwtUtils.validate(tokenJWT);
                System.out.println(subject + "passando por aqui");
                Optional<User> user = repository.findByEmail(subject);
                if (user.isPresent()) {
                    var authentication = new UsernamePasswordAuthenticationToken(user, null, user.get().getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                logger.warn("Invalid or expired");
            }
        }
        filterChain.doFilter(request, response);
    }
}
