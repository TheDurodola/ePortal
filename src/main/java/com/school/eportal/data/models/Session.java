package com.school.eportal.data.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

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
}