package com.school.eportal.data.models;

import com.school.eportal.data.models.enums.FeeLedgerStatus;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "fee_ledgers")
@CompoundIndex(name = "unique_student_session", def = "{'studentId': 1, 'academicSessionId': 1}", unique = true)
public class FeeLedger {


    @Id
    private String id;

    private String studentId;
    private String schoolFeesId;
    private String academicSessionId;
    private long totalExpectedAmount;
    private FeeLedgerStatus status = FeeLedgerStatus.UNPAID;

    @Builder.Default
    private List<String> transactions = new ArrayList<>();

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

}