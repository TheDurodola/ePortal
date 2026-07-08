package com.school.eportal.data.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.school.eportal.data.models.enums.AccountStatus;
import com.school.eportal.data.models.enums.Role;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
@Document(collection = "accounts")
public class Account {

    public Account() {
        id = UUID.randomUUID().toString();
    }

    @Id
    private String id;

    private String firstName;

    private String lastName;

    @Indexed(unique = true)
    private String username;

    @JsonIgnore
    private String password;

    private Role role;

    private AccountStatus status;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
