package com.a1a.shared.auth.application.port.driving;

import com.a1a.shared.auth.domain.model.UserContext;

/**
 * Driving Port for other domains to access authenticated user information.
 *
 * <p>This is the main contract exported by IAM domain to other domains (Warehouse, Facility, etc.).
 *
 * <p>Example usage:
 *
 * <pre>
 * &#64;Service
 * &#64;RequiredArgsConstructor
 * public class WarehouseService {
 *     private final GetAuthenticatedUserUseCase getAuthenticatedUser;
 *
 *     public void doSomething() {
 *         UserContext user = getAuthenticatedUser.getCurrentUser();
 *         if (user != null) {
 *             // Use user info
 *         }
 *     }
 * }
 * </pre>
 */
public interface GetAuthenticatedUserUseCase {
    /**
     * Get the currently authenticated user from thread-local context.
     *
     * <p>This retrieves the user that was authenticated via JWT token in the current HTTP request.
     *
     * @return UserContext or null if no user is authenticated
     */
    UserContext getCurrentUser();

    /**
     * Check if a user is currently authenticated.
     *
     * <p>This is a convenience method that checks if there is a valid user in the thread-local
     * context.
     *
     * @return true if a user is authenticated, false otherwise
     */
    boolean isAuthenticated();
}
