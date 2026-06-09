package com.skipps.finance.dto.user;

import jakarta.validation.constraints.NotBlank;

public record DeleteUserRequest(
    @NotBlank
    String password
) {
}
