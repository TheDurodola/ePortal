package com.school.eportal.utils;

import com.school.eportal.dtos.requests.ParentRegistrationRequest;
import org.jspecify.annotations.NonNull;

public class Mutator {


    public static void mutate(@NonNull ParentRegistrationRequest request){
        request.setUsername(request.getUsername().toLowerCase());
        request.setChildSchoolId(request.getChildSchoolId().toLowerCase());
        request.setFirstName(request.getFirstName().toLowerCase());
        request.setLastName(request.getLastName().toLowerCase());
    }
}
