package com.edutech.attendance.service;

import com.edutech.attendance.dto.LeaveRequestDTO;
import com.edutech.attendance.entity.LeaveRequest;
import com.edutech.attendance.entity.User;
import com.edutech.attendance.exception.ResourceNotFoundException;
import com.edutech.attendance.repository.LeaveRepository;
import com.edutech.attendance.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class LeaveService {

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private UserRepository userRepository;

    public LeaveRequest applyLeave(LeaveRequestDTO request, Long userId) {
        // Fix #7c: validate date range
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new IllegalArgumentException("Start date and end date are required.");
        }
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before or equal to end date.");
        }
        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Leave cannot start in the past.");
        }

        // Fix #7b: check for overlapping leaves
        List<LeaveRequest> overlaps = leaveRepository.findOverlappingLeaves(
                userId, request.getStartDate(), request.getEndDate());
        if (!overlaps.isEmpty()) {
            throw new IllegalStateException("A leave request already exists overlapping these dates.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        LeaveRequest leave = new LeaveRequest();
        leave.setUser(user);
        leave.setStartDate(request.getStartDate());
        leave.setEndDate(request.getEndDate());
        leave.setLeaveType(request.getLeaveType());
        leave.setReason(request.getReason());
        leave.setStatus(LeaveRequest.Status.PENDING);

        LeaveRequest saved = leaveRepository.save(leave);
        log.info("Leave applied by user {} from {} to {}", userId,
                request.getStartDate(), request.getEndDate());
        return saved;
    }

    // Fix #1a: real implementation (was stub returning hardcoded string)
    public List<LeaveRequest> getMyLeaves(Long userId) {
        return leaveRepository.findUserLeaves(userId);
    }

    // Fix #3: scope to manager's team only
    public List<LeaveRequest> getPendingLeaves(Long managerId) {
        return leaveRepository.findPendingLeavesByManager(managerId);
    }

    public LeaveRequest approveLeave(Long leaveId, Long approverId) {
        LeaveRequest leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found: " + leaveId));
        if (leave.getStatus() != LeaveRequest.Status.PENDING) {
            throw new IllegalStateException("Only PENDING leave requests can be approved.");
        }
        User approver = userRepository.findById(approverId)
                .orElseThrow(() -> new ResourceNotFoundException("Approver not found: " + approverId));
        leave.setStatus(LeaveRequest.Status.APPROVED);
        leave.setApprovedBy(approver);

        LeaveRequest saved = leaveRepository.save(leave);
        log.info("Leave {} approved by user {}", leaveId, approverId);
        return saved;
    }

    public LeaveRequest rejectLeave(Long leaveId, String reason) {
        LeaveRequest leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found: " + leaveId));
        if (leave.getStatus() != LeaveRequest.Status.PENDING) {
            throw new IllegalStateException("Only PENDING leave requests can be rejected.");
        }
        leave.setStatus(LeaveRequest.Status.REJECTED);
        leave.setRejectionReason(reason);

        LeaveRequest saved = leaveRepository.save(leave);
        log.info("Leave {} rejected with reason: {}", leaveId, reason);
        return saved;
    }
}
