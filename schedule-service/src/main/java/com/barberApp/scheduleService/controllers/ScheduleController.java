package com.barberApp.scheduleService.controllers;

import com.barberApp.scheduleService.dtos.CreateScheduleDTO;
import com.barberApp.scheduleService.dtos.ScheduleDTO;
import com.barberApp.scheduleService.services.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    private final ScheduleService service;

    public ScheduleController(ScheduleService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ScheduleDTO> create(@RequestBody @Valid CreateScheduleDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<ScheduleDTO> confirm(@PathVariable UUID id) {
        return ResponseEntity.ok(service.confirm(id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ScheduleDTO> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(service.cancel(id));
    }

    @GetMapping
    public ResponseEntity<List<ScheduleDTO>> list(@RequestParam(required = false) UUID userId,
                                                  @RequestParam(required = false) UUID barberId) {
        return ResponseEntity.ok(service.list(userId, barberId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleDTO> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.get(id));
    }
}

