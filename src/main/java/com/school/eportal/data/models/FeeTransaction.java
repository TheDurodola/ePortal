package com.school.eportal.data.models;

import com.school.eportal.data.models.enums.TransactionStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Setter
@Getter
@Document
@Builder
public class FeeTransaction {
    @Id
    private String id;
    private BigDecimal amount;
    private Instant attemptedAt;
    private TransactionStatus status; // PENDING, CONFIRMED, FAILED
    private String paymentReference;
}