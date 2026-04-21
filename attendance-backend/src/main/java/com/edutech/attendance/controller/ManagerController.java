package com.edutech.attendance.controller;

import com.edutech.attendance.config.SecurityContextUtil;
import com.edutech.attendance.dto.AttendanceDTO;
import com.edutech.attendance.dto.UserDTO;
import com.edutech.attendance.entity.LeaveRequest;
import com.edutech.attendance.service.AttendanceService;
import com.edutech.attendance.service.LeaveService;
import com.edutech.attendance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manager")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('MANAGER')")
public class ManagerController {

    @Autowired private LeaveService leaveService;
    @Autowired private UserService userService;
    @Autowired private AttendanceService attendanceService;
    @Autowired private SecurityContextUtil securityContextUtil;

    @GetMapping("/leaves")
    public ResponseEntity<List<LeaveRequest>> getPendingLeaves() {
        return ResponseEntity.ok(leaveService.getPendingLeaves(securityContextUtil.getCurrentUserId()));
    }

    @PutMapping("/leave/{id}/approve")
    public ResponseEntity<LeaveRequest> approveLeave(@PathVariable Long id) {
        return ResponseEntity.ok(leaveService.approveLeave(id, securityContextUtil.getCurrentUserId()));
    }

    @PutMapping("/leave/{id}/reject")
    public ResponseEntity<LeaveRequest> rejectLeave(@PathVariable Long id, @RequestParam String reason) {
        return ResponseEntity.ok(leaveService.rejectLeave(id, reason));
    }

    /** Feature 4: Manager's assigned team members */
    @GetMapping("/team")
    public ResponseEntity<List<UserDTO>> getTeam() {
        Long managerId = securityContextUtil.getCurrentUserId();
        return ResponseEntity.ok(userService.getTeamByManager(managerId));
    }

    /** Manager can see a team member's attendance */
    @GetMapping("/team/{userId}/attendance")
    public ResponseEntity<List<AttendanceDTO>> getMemberAttendance(@PathVariable Long userId) {
        return ResponseEntity.ok(attendanceService.getAttendanceHistory(userId));
    }
}
