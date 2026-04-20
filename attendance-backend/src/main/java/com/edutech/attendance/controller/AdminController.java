package com.edutech.attendance.controller;

import com.edutech.attendance.dto.PolicyDTO;
import com.edutech.attendance.dto.UserDTO;
import com.edutech.attendance.entity.Policy;
import com.edutech.attendance.entity.User;
import com.edutech.attendance.service.PolicyService;
import com.edutech.attendance.service.ReportService;
import com.edutech.attendance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private PolicyService policyService;

    @Autowired
    private ReportService reportService;

    // --- User Management ---

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.createUser(userDTO));
    }

    // Fix #5: list all users
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Fix #5: get user by ID
    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/users/role/{role}")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable String role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(id, userDTO));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    // --- Policy Management ---

    @PostMapping("/policies")
    public ResponseEntity<Policy> createPolicy(@RequestBody PolicyDTO policyDTO) {
        return ResponseEntity.ok(policyService.createPolicy(policyDTO));
    }

    @PutMapping("/policies/{id}")
    public ResponseEntity<Policy> updatePolicy(@PathVariable Long id,
                                                @RequestBody PolicyDTO policyDTO) {
        return ResponseEntity.ok(policyService.updatePolicy(id, policyDTO));
    }

    @GetMapping("/policies")
    public ResponseEntity<List<Policy>> getAllPolicies() {
        return ResponseEntity.ok(policyService.getAllPolicies());
    }

    // --- Reports (Fix #2: real aggregation) ---

    @GetMapping("/reports/comprehensive")
    public ResponseEntity<Map<String, Object>> getComprehensiveReport() {
        return ResponseEntity.ok(reportService.getComprehensiveReport());
    }

    @GetMapping("/reports/attendance")
    public ResponseEntity<Map<String, Object>> getAttendanceReport() {
        return ResponseEntity.ok(reportService.getAttendanceSummary());
    }

    @GetMapping("/reports/leave")
    public ResponseEntity<Map<String, Object>> getLeaveReport() {
        return ResponseEntity.ok(reportService.getLeaveSummary());
    }

    // --- Health ---

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Server is running");
    }
}
