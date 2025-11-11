package com.barberApp.scheduleService.services;

import com.barberApp.scheduleService.dtos.CreateScheduleDTO;
import com.barberApp.scheduleService.dtos.ScheduleDTO;
import com.barberApp.scheduleService.enums.ScheduleStatus;
import com.barberApp.scheduleService.infra.clients.UserServiceClient;
import com.barberApp.scheduleService.models.Schedule;
import com.barberApp.scheduleService.repositories.ScheduleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ScheduleService {

    private final ScheduleRepository repository;
    private final UserServiceClient userClient;

    @Value("${user.service.validateBarberRole:false}")
    private boolean validateBarberRole;

    public ScheduleService(ScheduleRepository repository, UserServiceClient userClient) {
        this.repository = repository;
        this.userClient = userClient;
    }

    @Transactional
    public ScheduleDTO create(CreateScheduleDTO dto) {
        if (dto.startTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("startTime must be in the future");
        }

        if (repository.existsByBarberIdAndStartTime(dto.barberId(), dto.startTime())) {
            throw new IllegalStateException("Schedule already exists for this barber at this time");
        }

        if (userClient.getUser(dto.userId()) == null) {
            throw new IllegalArgumentException("User not found");
        }
        if (userClient.getUser(dto.barberId()) == null) {
            throw new IllegalArgumentException("Barber user not found");
        }
        if (validateBarberRole) {
            Boolean barber = userClient.isBarber(dto.barberId());
            if (barber == null || !barber) {
                throw new IllegalArgumentException("Provided barberId is not a barber");
            }
        }

        Schedule schedule = new Schedule();
        schedule.setUserId(dto.userId());
        schedule.setBarberId(dto.barberId());
        schedule.setStartTime(dto.startTime());
        schedule.setStatus(ScheduleStatus.PENDING);

        Schedule saved = repository.save(schedule);
        return toDTO(saved);
    }

    @Transactional
    public ScheduleDTO confirm(UUID id) {
        Schedule s = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Schedule not found"));
        s.setStatus(ScheduleStatus.CONFIRMED);
        return toDTO(s);
    }

    @Transactional
    public ScheduleDTO cancel(UUID id) {
        Schedule s = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Schedule not found"));
        s.setStatus(ScheduleStatus.CANCELED);
        return toDTO(s);
    }

    public ScheduleDTO get(UUID id) {
        return repository.findById(id).map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));
    }

    public List<ScheduleDTO> list(UUID userId, UUID barberId) {
        if (userId != null) {
            return repository.findByUserId(userId).stream().map(this::toDTO).toList();
        }
        if (barberId != null) {
            return repository.findByBarberId(barberId).stream().map(this::toDTO).toList();
        }
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    private ScheduleDTO toDTO(Schedule s) {
        return new ScheduleDTO(s.getId(), s.getUserId(), s.getBarberId(), s.getStartTime(), s.getStatus());
    }
}

