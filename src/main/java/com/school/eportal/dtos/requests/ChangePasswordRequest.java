package com.school.eportal.dtos.requests;

public record ChangePasswordRequest(String oldPassword, String newPassword) {
}
