package com.edutech.attendance.repository;

import com.edutech.attendance.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByUserId(Long userId);
    List<LeaveRequest> findByStatus(LeaveRequest.Status status);
    
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.status = :status")
    List<LeaveRequest> findByStatusForApproval(@Param("status") LeaveRequest.Status status);
    
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.user.id = :userId ORDER BY lr.startDate DESC")
    List<LeaveRequest> findUserLeaves(@Param("userId") Long userId);
}
