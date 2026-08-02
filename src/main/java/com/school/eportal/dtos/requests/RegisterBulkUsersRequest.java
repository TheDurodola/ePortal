package com.school.eportal.dtos.requests;

import com.school.eportal.dtos.BulkAccountDTO;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RegisterBulkUsersRequest {
    private List< @Valid BulkAccountDTO> data;
}
