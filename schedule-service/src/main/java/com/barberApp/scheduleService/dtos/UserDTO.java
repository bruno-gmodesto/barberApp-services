package com.barberApp.scheduleService.dtos;

public record UserDTO(
        String name,
        String email,
        String phone,
        String userRole
) { }

