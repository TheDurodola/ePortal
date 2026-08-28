package com.school.eportal.data.models;

import com.school.eportal.data.models.enums.TransactionStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Setter
@Getter
@Document(collection = "feeTransactions")
@Builder
public class FeeTransaction {

    @Id
    private String id;
    private long amount;
    private Instant attemptedAt;
    private TransactionStatus status;
    private String paymentReference;
    private String madeBy;
    private String feeLedger;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}