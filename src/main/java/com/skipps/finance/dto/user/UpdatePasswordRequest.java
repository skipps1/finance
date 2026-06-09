package com.skipps.finance.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UpdatePasswordRequest(
    @NotBlank
    String currentPassword,

    @NotBlank
    String newPassword
) {
}
