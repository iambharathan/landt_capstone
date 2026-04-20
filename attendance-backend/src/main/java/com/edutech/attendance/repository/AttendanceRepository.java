package com.edutech.attendance.repository;

import com.edutech.attendance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByUserId(Long userId);
    List<Attendance> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT a FROM Attendance a WHERE a.user.id = :userId AND a.date = :date")
    Optional<Attendance> findByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    long countByDate(LocalDate date);
    long countByStatus(Attendance.Status status);
    long countByDateAndStatus(LocalDate date, Attendance.Status status);
}
