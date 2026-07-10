package com.school.eportal.dtos.responses;

import com.school.eportal.data.models.Account;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Builder
@Getter
@Setter
public class RegisterBulkUsersResponse {
    private Map<String, Map<String,List<String>>> data;
}
