package com.edutech.attendance.controller;

import com.edutech.attendance.config.SecurityContextUtil;
import com.edutech.attendance.dto.AttendanceDTO;
import com.edutech.attendance.dto.LeaveRequestDTO;
import com.edutech.attendance.entity.Attendance;
import com.edutech.attendance.entity.LeaveRequest;
import com.edutech.attendance.service.AttendanceService;
import com.edutech.attendance.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('EMPLOYEE')")
public class EmployeeController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private SecurityContextUtil securityContextUtil;

    @PostMapping("/check-in")
    public ResponseEntity<Attendance> checkIn() {
        Long userId = securityContextUtil.getCurrentUserId();
        return ResponseEntity.ok(attendanceService.checkIn(userId));
    }

    @PostMapping("/check-out")
    public ResponseEntity<Attendance> checkOut() {
        Long userId = securityContextUtil.getCurrentUserId();
        return ResponseEntity.ok(attendanceService.checkOut(userId));
    }

    @GetMapping("/attendance/today")
    public ResponseEntity<AttendanceDTO> getTodayAttendance() {
        Long userId = securityContextUtil.getCurrentUserId();
        return ResponseEntity.ok(attendanceService.getTodayAttendance(userId));
    }

    @GetMapping("/attendance")
    public ResponseEntity<List<AttendanceDTO>> getAttendanceHistory() {
        Long userId = securityContextUtil.getCurrentUserId();
        return ResponseEntity.ok(attendanceService.getAttendanceHistory(userId));
    }

    @PostMapping("/leave")
    public ResponseEntity<LeaveRequest> applyLeave(@RequestBody LeaveRequestDTO request) {
        Long userId = securityContextUtil.getCurrentUserId();
        return ResponseEntity.ok(leaveService.applyLeave(request, userId));
    }

    @GetMapping("/leaves")
    public ResponseEntity<List<LeaveRequest>> getMyLeaves() {
        Long userId = securityContextUtil.getCurrentUserId();
        return ResponseEntity.ok(leaveService.getMyLeaves(userId));
    }
}
