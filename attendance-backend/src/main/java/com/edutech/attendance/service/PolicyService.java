package com.edutech.attendance.service;

import com.edutech.attendance.dto.PolicyDTO;
import com.edutech.attendance.entity.Policy;
import com.edutech.attendance.repository.PolicyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
public class PolicyService {

    @Autowired
    private PolicyRepository policyRepository;

    public Policy createPolicy(PolicyDTO policyDTO) {
        Policy policy = new Policy();
        policy.setLeaveType(policyDTO.getLeaveType());
        policy.setMaxDays(policyDTO.getMaxDays());
        policy.setRules(policyDTO.getRules());
        policy.setIsActive(true);
        
        Policy saved = policyRepository.save(policy);
        log.info("Policy created for leave type: {}", policyDTO.getLeaveType());
        return saved;
    }

    public Policy updatePolicy(Long policyId, PolicyDTO policyDTO) {
        Policy policy = policyRepository.findById(policyId).orElseThrow();
        policy.setMaxDays(policyDTO.getMaxDays());
        policy.setRules(policyDTO.getRules());
        policy.setIsActive(policyDTO.getIsActive());
        
        Policy updated = policyRepository.save(policy);
        log.info("Policy updated: {}", policyId);
        return updated;
    }

    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }
}
