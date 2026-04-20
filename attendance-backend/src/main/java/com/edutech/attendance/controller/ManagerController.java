package com.edutech.attendance.controller;

import com.edutech.attendance.config.SecurityContextUtil;
import com.edutech.attendance.entity.LeaveRequest;
import com.edutech.attendance.service.LeaveService;
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

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private SecurityContextUtil securityContextUtil;

    /** Returns ONLY leaves belonging to the authenticated manager's team */
    @GetMapping("/leaves")
    public ResponseEntity<List<LeaveRequest>> getPendingLeaves() {
        Long managerId = securityContextUtil.getCurrentUserId();
        return ResponseEntity.ok(leaveService.getPendingLeaves(managerId));
    }

    @PutMapping("/leave/{id}/approve")
    public ResponseEntity<LeaveRequest> approveLeave(@PathVariable Long id) {
        Long approverId = securityContextUtil.getCurrentUserId();
        return ResponseEntity.ok(leaveService.approveLeave(id, approverId));
    }

    @PutMapping("/leave/{id}/reject")
    public ResponseEntity<LeaveRequest> rejectLeave(@PathVariable Long id,
                                                     @RequestParam String reason) {
        return ResponseEntity.ok(leaveService.rejectLeave(id, reason));
    }
}
