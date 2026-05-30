package com.lims.core.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "lims_outbox")
public class OutboxEntity {

    @Id
    public UUID eventId;

    @Column(nullable = false)
    public String eventType; // 用於識別 Event Class

    @Column(columnDefinition = "TEXT", nullable = false)
    public String payload; // 序列化後的 JSON

    @Column(nullable = false)
    public Instant createdAt;

    @Column(nullable = false)
    public String status; // PENDING, PUBLISHED, FAILED
}