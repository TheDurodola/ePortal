package com.school.eportal.dtos.responses;

import com.school.eportal.dtos.SchoolFeesDetailPayload;
import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetSchoolFeesDetailsResponse {
    private List<SchoolFeesDetailPayload> data;
}
