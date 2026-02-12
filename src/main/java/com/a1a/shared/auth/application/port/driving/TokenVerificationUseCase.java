package com.a1a.shared.auth.application.port.driving;

import com.a1a.shared.auth.domain.model.UserContext;

public interface TokenVerificationUseCase {
    UserContext verifyAndExtract(String token);
}



