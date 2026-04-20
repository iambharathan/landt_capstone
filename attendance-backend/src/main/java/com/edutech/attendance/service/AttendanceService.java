package com.edutech.attendance.service;

import com.edutech.attendance.dto.AttendanceDTO;
import com.edutech.attendance.entity.Attendance;
import com.edutech.attendance.entity.User;
import com.edutech.attendance.exception.ResourceNotFoundException;
import com.edutech.attendance.repository.AttendanceRepository;
import com.edutech.attendance.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private UserRepository userRepository;

    public Attendance checkIn(Long userId) {
        // Fix #7a: prevent duplicate check-in on the same day
        attendanceRepository.findByUserIdAndDate(userId, LocalDate.now()).ifPresent(a -> {
            throw new IllegalStateException("User " + userId + " has already checked in today.");
        });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setDate(LocalDate.now());
        attendance.setCheckInTime(LocalTime.now());
        attendance.setStatus(Attendance.Status.PRESENT);

        Attendance saved = attendanceRepository.save(attendance);
        log.info("User {} checked in at {}", userId, LocalTime.now());
        return saved;
    }

    public Attendance checkOut(Long userId) {
        Attendance attendance = attendanceRepository.findByUserIdAndDate(userId, LocalDate.now())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No check-in record found for user " + userId + " today."));
        if (attendance.getCheckOutTime() != null) {
            throw new IllegalStateException("User " + userId + " has already checked out today.");
        }
        attendance.setCheckOutTime(LocalTime.now());

        Attendance saved = attendanceRepository.save(attendance);
        log.info("User {} checked out at {}", userId, LocalTime.now());
        return saved;
    }

    public AttendanceDTO getTodayAttendance(Long userId) {
        Attendance attendance = attendanceRepository.findByUserIdAndDate(userId, LocalDate.now())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No attendance record for user " + userId + " today."));
        return toDTO(attendance);
    }

    // Fix #1b: attendance history for an employee
    public List<AttendanceDTO> getAttendanceHistory(Long userId) {
        return attendanceRepository.findByUserId(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private AttendanceDTO toDTO(Attendance a) {
        return new AttendanceDTO(
                a.getId(),
                a.getUser().getId(),
                a.getDate(),
                a.getCheckInTime(),
                a.getCheckOutTime(),
                a.getStatus().toString()
        );
    }
}
