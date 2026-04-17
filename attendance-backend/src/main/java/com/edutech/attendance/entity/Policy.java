package com.edutech.attendance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "leave_policies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Policy {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String leaveType;
    
    @Column(nullable = false)
    private Integer maxDays;
    
    @Column(columnDefinition = "TEXT")
    private String rules;
    
    @Column(nullable = false)
    private Boolean isActive = true;
}
