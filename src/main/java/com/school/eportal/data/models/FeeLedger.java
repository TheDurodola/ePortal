package com.school.eportal.data.models;

import com.school.eportal.data.models.FeeTransaction;
import com.school.eportal.data.models.enums.TransactionStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
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
    private String academicSessionId;
    private BigDecimal totalExpectedAmount;
    private FeeLedgerStatus status = FeeLedgerStatus.UNPAID;

    @Builder.Default
    private List<FeeTransaction> transactions = new ArrayList<>();

    public BigDecimal getAmountPaid() {
        return transactions.stream()
                .filter(t -> t.getStatus() == TransactionStatus.CONFIRMED)
                .map(FeeTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }



    public boolean hasOutstandingBalance() {
        return getAmountPaid().compareTo(totalExpectedAmount) < 0;
    }

    public void recordAttempt(FeeTransaction transaction) {
        if (transaction.getStatus()== TransactionStatus.CONFIRMED) {
            if (getAmountPaid().add(transaction.getAmount()).equals(totalExpectedAmount)) {
                setStatus(FeeLedgerStatus.FULLY_PAID);
            }else  {
                setStatus(FeeLedgerStatus.PARTIALLY_PAID);
            }
        }
        this.transactions.add(transaction);
    }
}