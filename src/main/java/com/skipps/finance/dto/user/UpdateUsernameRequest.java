package com.skipps.finance.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UpdateUsernameRequest(
    @NotBlank
    String username
) {}
