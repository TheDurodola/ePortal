package com.school.eportal.dtos.responses;

import com.school.eportal.dtos.SchoolResponseData;
import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateSchoolFeesResponse {
    private long count;
    private List<SchoolResponseData> data;

}
