package com.a1a.shared.auth.application.port.driving;

import java.util.Set;

public interface GatewayPermissionClientUseCase {
    Set<String> fetchUserPermissions(String accessToken);
}



