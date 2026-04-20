package com.edutech.attendance.service;

import com.edutech.attendance.entity.Attendance;
import com.edutech.attendance.entity.LeaveRequest;
import com.edutech.attendance.repository.AttendanceRepository;
import com.edutech.attendance.repository.LeaveRepository;
import com.edutech.attendance.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
public class ReportService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private UserRepository userRepository;

    // Fix #2: real attendance aggregation
    public Map<String, Object> getAttendanceSummary() {
        LocalDate today = LocalDate.now();
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("date", today.toString());
        report.put("checkedInToday", attendanceRepository.countByDate(today));
        report.put("presentTotal", attendanceRepository.countByStatus(Attendance.Status.PRESENT));
        report.put("absentTotal", attendanceRepository.countByStatus(Attendance.Status.ABSENT));
        report.put("lateTotal", attendanceRepository.countByStatus(Attendance.Status.LATE));
        log.info("Attendance summary generated");
        return report;
    }

    // Fix #2: real leave aggregation
    public Map<String, Object> getLeaveSummary() {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("pending", leaveRepository.countByStatus(LeaveRequest.Status.PENDING));
        report.put("approved", leaveRepository.countByStatus(LeaveRequest.Status.APPROVED));
        report.put("rejected", leaveRepository.countByStatus(LeaveRequest.Status.REJECTED));
        report.put("total", leaveRepository.count());
        log.info("Leave summary generated");
        return report;
    }

    // Fix #2: comprehensive report combining both
    public Map<String, Object> getComprehensiveReport() {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("totalUsers", userRepository.count());
        report.put("attendance", getAttendanceSummary());
        report.put("leaves", getLeaveSummary());
        log.info("Comprehensive report generated");
        return report;
    }
}
