package com.lims.core.application;

import com.lims.core.domain.exception.DomainConstraintViolationException;
import com.lims.core.domain.order.repository.OrderQueryRepository;
import com.lims.core.domain.order.repository.OrderQuery;
import com.lims.core.domain.result.TestResult;
import com.lims.core.domain.result.TestResultRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class TestResultService {

    @Inject
    @OrderQuery OrderQueryRepository orderRepository; // 介面，非實作類別

    @Inject
    TestResultRepository testResultRepository;

    @Transactional
    public void createResult(TestResult result) {
        // 1. 跨聚合根驗證：這是 Application Service 的職責
        if (orderRepository.findById(result.sampleId()).isEmpty()) {
            throw new DomainConstraintViolationException("Invalid Order/Sample", "GOV-RESULT-404");
        }
        
        // 2. 持久化
        testResultRepository.save(result);
        
        // 3. 發布事件 (Transactional Outbox)
    }
}