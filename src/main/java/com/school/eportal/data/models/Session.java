package com.school.eportal.data.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Setter
@Getter
@Builder
@Document(collection = "sessions")
public class Session {

    @Id
    private String id;

    private int startYear;

    private int endYear;

    @Indexed(unique = true, partialFilter = "{ isCurrent: true }")
    private boolean isCurrent;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}