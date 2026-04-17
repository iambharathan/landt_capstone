package com.edutech.attendance.service;

import com.edutech.attendance.dto.AttendanceDTO;
import com.edutech.attendance.entity.Attendance;
import com.edutech.attendance.entity.User;
import com.edutech.attendance.repository.AttendanceRepository;
import com.edutech.attendance.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private UserRepository userRepository;

    public Attendance checkIn(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
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
                .orElseThrow();
        attendance.setCheckOutTime(LocalTime.now());
        
        Attendance saved = attendanceRepository.save(attendance);
        log.info("User {} checked out at {}", userId, LocalTime.now());
        return saved;
    }

    public AttendanceDTO getTodayAttendance(Long userId) {
        Attendance attendance = attendanceRepository.findByUserIdAndDate(userId, LocalDate.now())
                .orElseThrow();
        return new AttendanceDTO(attendance.getId(), attendance.getUser().getId(), 
                attendance.getDate(), attendance.getCheckInTime(), 
                attendance.getCheckOutTime(), attendance.getStatus().toString());
    }
}
