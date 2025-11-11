package com.barberApp.userService.controllers;

import com.barberApp.userService.dtos.GetUsersDTO;
import com.barberApp.userService.dtos.UpdateDTO;
import com.barberApp.userService.models.User;
import com.barberApp.userService.services.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Page<GetUsersDTO>> getUsers(Pageable pageable) {
        var users = userService.findAll(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{email}")
    public ResponseEntity<GetUsersDTO> getUserByEmail(@PathVariable String email) {
        var user = userService.findUserByEmail(email);
        GetUsersDTO dto = new GetUsersDTO(user.getName(), user.getEmail(), user.getPhone(), user.getUserRole());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetUsersDTO> getUser(@PathVariable UUID id) {
        var user = userService.findUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable UUID id, @RequestBody @Valid UpdateDTO dto) {
        userService.update(dto, id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/is-barber")
    public ResponseEntity<Boolean> isBarber(@PathVariable UUID id) {
        Boolean isBarber = userService.isBarber(id);
        return ResponseEntity.ok(isBarber);
    }
}
