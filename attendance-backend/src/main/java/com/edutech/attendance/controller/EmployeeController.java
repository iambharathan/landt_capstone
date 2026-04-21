package com.edutech.attendance.controller;

import com.edutech.attendance.config.SecurityContextUtil;
import com.edutech.attendance.dto.AttendanceDTO;
import com.edutech.attendance.dto.LeaveRequestDTO;
import com.edutech.attendance.dto.UserDTO;
import com.edutech.attendance.entity.Attendance;
import com.edutech.attendance.entity.LeaveRequest;
import com.edutech.attendance.service.AttendanceService;
import com.edutech.attendance.service.LeaveService;
import com.edutech.attendance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employee")
@CrossOrigin(origins = "*")
@PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER')")
public class EmployeeController {

    @Autowired private AttendanceService attendanceService;
    @Autowired private LeaveService leaveService;
    @Autowired private UserService userService;
    @Autowired private SecurityContextUtil securityContextUtil;

    @PostMapping("/check-in")
    public ResponseEntity<Attendance> checkIn() {
        return ResponseEntity.ok(attendanceService.checkIn(securityContextUtil.getCurrentUserId()));
    }

    @PostMapping("/check-out")
    public ResponseEntity<Attendance> checkOut() {
        return ResponseEntity.ok(attendanceService.checkOut(securityContextUtil.getCurrentUserId()));
    }

    @GetMapping("/attendance/today")
    public ResponseEntity<AttendanceDTO> getTodayAttendance() {
        AttendanceDTO dto = attendanceService.getTodayAttendance(securityContextUtil.getCurrentUserId());
        return dto == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(dto);
    }

    @GetMapping("/attendance")
    public ResponseEntity<List<AttendanceDTO>> getAttendanceHistory() {
        return ResponseEntity.ok(attendanceService.getAttendanceHistory(securityContextUtil.getCurrentUserId()));
    }

    @PostMapping("/leave")
    public ResponseEntity<LeaveRequest> applyLeave(@RequestBody LeaveRequestDTO request) {
        return ResponseEntity.ok(leaveService.applyLeave(request, securityContextUtil.getCurrentUserId()));
    }

    @GetMapping("/leaves")
    public ResponseEntity<List<LeaveRequest>> getMyLeaves() {
        return ResponseEntity.ok(leaveService.getMyLeaves(securityContextUtil.getCurrentUserId()));
    }

    /** Feature 2: Leave balance remaining */
    @GetMapping("/leave-balance")
    public ResponseEntity<Map<String, Long>> getLeaveBalance() {
        Long userId = securityContextUtil.getCurrentUserId();
        return ResponseEntity.ok(leaveService.getLeaveBalance(userId));
    }

    /** Feature 1: Get own profile */
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile() {
        return ResponseEntity.ok(userService.getUserById(securityContextUtil.getCurrentUserId()));
    }

    /** Feature 1: Update own profile (name only) */
    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateProfile(@RequestBody Map<String, String> body) {
        Long userId = securityContextUtil.getCurrentUserId();
        return ResponseEntity.ok(userService.updateProfile(userId, body.get("fullName")));
    }
}
