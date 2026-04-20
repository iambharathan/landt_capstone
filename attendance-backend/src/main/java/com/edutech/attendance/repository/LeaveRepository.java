package com.edutech.attendance.repository;

import com.edutech.attendance.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByUserId(Long userId);
    List<LeaveRequest> findByStatus(LeaveRequest.Status status);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.user.id = :userId ORDER BY lr.startDate DESC")
    List<LeaveRequest> findUserLeaves(@Param("userId") Long userId);

    /** Overlap check: any leave for this user whose date range overlaps [start, end] */
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.user.id = :userId " +
           "AND lr.status IN ('PENDING','APPROVED') " +
           "AND lr.startDate <= :endDate AND lr.endDate >= :startDate")
    List<LeaveRequest> findOverlappingLeaves(@Param("userId") Long userId,
                                             @Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);

    /** Pending leaves scoped to employees managed by a specific manager */
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.status = 'PENDING' AND lr.user.manager.id = :managerId")
    List<LeaveRequest> findPendingLeavesByManager(@Param("managerId") Long managerId);

    long countByStatus(LeaveRequest.Status status);
}
