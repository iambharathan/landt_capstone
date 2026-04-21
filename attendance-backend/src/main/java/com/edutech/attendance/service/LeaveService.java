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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class LeaveService {

    @Autowired private LeaveRepository leaveRepository;
    @Autowired private UserRepository userRepository;

    // Annual leave entitlements per year
    private static final long ANNUAL_DAYS = 15;
    private static final long SICK_DAYS = 10;
    private static final long CASUAL_DAYS = 7;

    public LeaveRequest applyLeave(LeaveRequestDTO request, Long userId) {
        if (request.getStartDate() == null || request.getEndDate() == null)
            throw new IllegalArgumentException("Start date and end date are required.");
        if (request.getStartDate().isAfter(request.getEndDate()))
            throw new IllegalArgumentException("Start date must be before or equal to end date.");
        if (request.getStartDate().isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Leave cannot start in the past.");

        List<LeaveRequest> overlaps = leaveRepository.findOverlappingLeaves(userId, request.getStartDate(), request.getEndDate());
        if (!overlaps.isEmpty())
            throw new IllegalStateException("A leave request already exists overlapping these dates.");

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
        log.info("Leave applied by user {} from {} to {}", userId, request.getStartDate(), request.getEndDate());
        return saved;
    }

    public List<LeaveRequest> getMyLeaves(Long userId) {
        return leaveRepository.findUserLeaves(userId);
    }

    public List<LeaveRequest> getPendingLeaves(Long managerId) {
        return leaveRepository.findPendingLeavesByManager(managerId);
    }

    public LeaveRequest approveLeave(Long leaveId, Long approverId) {
        LeaveRequest leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found: " + leaveId));
        if (leave.getStatus() != LeaveRequest.Status.PENDING)
            throw new IllegalStateException("Only PENDING leave requests can be approved.");
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
        if (leave.getStatus() != LeaveRequest.Status.PENDING)
            throw new IllegalStateException("Only PENDING leave requests can be rejected.");
        leave.setStatus(LeaveRequest.Status.REJECTED);
        leave.setRejectionReason(reason);
        LeaveRequest saved = leaveRepository.save(leave);
        log.info("Leave {} rejected with reason: {}", leaveId, reason);
        return saved;
    }

    /** Admin override — bypasses PENDING restriction, can flip any leave's state */
    public LeaveRequest adminOverrideLeave(Long leaveId, String status, String reason) {
        LeaveRequest leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found: " + leaveId));
        leave.setStatus(LeaveRequest.Status.valueOf(status.toUpperCase()));
        if (reason != null && !reason.isBlank()) leave.setRejectionReason(reason);
        LeaveRequest saved = leaveRepository.save(leave);
        log.info("Admin override: Leave {} set to {} (reason: {})", leaveId, status, reason);
        return saved;
    }

    /** All leaves for a specific user – used by Admin deep-dive */
    public List<LeaveRequest> getAllLeavesForUser(Long userId) {
        return leaveRepository.findUserLeaves(userId);
    }

    /** Feature 2: Compute remaining leave balance for a user */
    public Map<String, Long> getLeaveBalance(Long userId) {
        List<LeaveRequest> leaves = leaveRepository.findUserLeaves(userId);
        long annualUsed = countApprovedDays(leaves, "ANNUAL");
        long sickUsed = countApprovedDays(leaves, "SICK");
        long casualUsed = countApprovedDays(leaves, "CASUAL");

        Map<String, Long> balance = new HashMap<>();
        balance.put("annualTotal", ANNUAL_DAYS);
        balance.put("annualUsed", annualUsed);
        balance.put("annualRemaining", Math.max(0, ANNUAL_DAYS - annualUsed));
        balance.put("sickTotal", SICK_DAYS);
        balance.put("sickUsed", sickUsed);
        balance.put("sickRemaining", Math.max(0, SICK_DAYS - sickUsed));
        balance.put("casualTotal", CASUAL_DAYS);
        balance.put("casualUsed", casualUsed);
        balance.put("casualRemaining", Math.max(0, CASUAL_DAYS - casualUsed));
        return balance;
    }

    private long countApprovedDays(List<LeaveRequest> leaves, String type) {
        return leaves.stream()
                .filter(l -> l.getStatus() == LeaveRequest.Status.APPROVED
                        && type.equals(l.getLeaveType()))
                .mapToLong(l -> l.getStartDate().datesUntil(l.getEndDate().plusDays(1)).count())
                .sum();
    }
}
