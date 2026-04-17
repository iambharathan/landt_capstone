package com.edutech.attendance.controller;

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

@RestController
@RequestMapping("/api/employee")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('EMPLOYEE')")
public class EmployeeController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private LeaveService leaveService;

    @PostMapping("/check-in")
    public ResponseEntity<Attendance> checkIn(@RequestParam Long userId) {
        return ResponseEntity.ok(attendanceService.checkIn(userId));
    }

    @PostMapping("/check-out")
    public ResponseEntity<Attendance> checkOut(@RequestParam Long userId) {
        return ResponseEntity.ok(attendanceService.checkOut(userId));
    }

    @GetMapping("/attendance/today")
    public ResponseEntity<AttendanceDTO> getTodayAttendance(@RequestParam Long userId) {
        return ResponseEntity.ok(attendanceService.getTodayAttendance(userId));
    }

    @PostMapping("/leave")
    public ResponseEntity<LeaveRequest> applyLeave(@RequestBody LeaveRequestDTO request, @RequestParam Long userId) {
        return ResponseEntity.ok(leaveService.applyLeave(request, userId));
    }

    @GetMapping("/leaves")
    public ResponseEntity<String> getMyLeaves(@RequestParam Long userId) {
        return ResponseEntity.ok("Employee leaves retrieved");
    }
}
