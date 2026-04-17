package com.edutech.attendance.service;

import com.edutech.attendance.dto.LeaveRequestDTO;
import com.edutech.attendance.entity.LeaveRequest;
import com.edutech.attendance.entity.User;
import com.edutech.attendance.repository.LeaveRepository;
import com.edutech.attendance.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
public class LeaveService {

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private UserRepository userRepository;

    public LeaveRequest applyLeave(LeaveRequestDTO request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
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

    public List<LeaveRequest> getPendingLeaves() {
        return leaveRepository.findByStatus(LeaveRequest.Status.PENDING);
    }

    public LeaveRequest approveLeave(Long leaveId, Long approverId) {
        LeaveRequest leave = leaveRepository.findById(leaveId).orElseThrow();
        User approver = userRepository.findById(approverId).orElseThrow();
        leave.setStatus(LeaveRequest.Status.APPROVED);
        leave.setApprovedBy(approver);
        
        LeaveRequest saved = leaveRepository.save(leave);
        log.info("Leave {} approved by user {}", leaveId, approverId);
        return saved;
    }

    public LeaveRequest rejectLeave(Long leaveId, String reason) {
        LeaveRequest leave = leaveRepository.findById(leaveId).orElseThrow();
        leave.setStatus(LeaveRequest.Status.REJECTED);
        leave.setRejectionReason(reason);
        
        LeaveRequest saved = leaveRepository.save(leave);
        log.info("Leave {} rejected with reason: {}", leaveId, reason);
        return saved;
    }
}
