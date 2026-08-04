package com.school.eportal.dtos.responses;

import com.school.eportal.dtos.profile.ProfileDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class GetProfileResponse {
    private ProfileDTO profile;
}
