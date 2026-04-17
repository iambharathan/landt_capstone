package com.edutech.attendance.service;

import com.edutech.attendance.repository.AttendanceRepository;
import com.edutech.attendance.repository.LeaveRepository;
import com.edutech.attendance.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReportService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private UserRepository userRepository;

    public String getComprehensiveReport() {
        log.info("Generating comprehensive report");
        return "Comprehensive Report Generated";
    }

    public String getAttendanceSummary() {
        log.info("Generating attendance summary");
        return "Attendance Summary Generated";
    }

    public String getLeaveSummary() {
        log.info("Generating leave summary");
        return "Leave Summary Generated";
    }
}
