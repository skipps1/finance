package com.skipps.finance.dto.auth;

import com.skipps.finance.dto.user.UserDTO;

public record AuthResponse(
    String jwt,

    UserDTO response
) {
}
