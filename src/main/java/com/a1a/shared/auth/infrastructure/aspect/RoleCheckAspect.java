package com.a1a.shared.auth.infrastructure.aspect;

import com.a1a.shared.auth.application.port.annotation.RequireAllRoles;
import com.a1a.shared.auth.application.port.annotation.RequireAnyRole;
import com.a1a.shared.auth.application.port.annotation.RequireRole;
import com.a1a.shared.auth.application.port.driving.GetAuthenticatedUserUseCase;
import com.a1a.shared.auth.domain.model.UserContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.access.AccessDeniedException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * AOP Aspect to intercept and validate role annotations
 *
 * <p>This aspect checks user roles from UserContext.roles against required roles specified in
 * annotations.
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class RoleCheckAspect {

    private final GetAuthenticatedUserUseCase getAuthenticatedUserUseCase;

    /** Intercept @RequireRole annotation */
    @Around("@annotation(com.a1a.shared.auth.application.port.annotation.RequireRole)")
    public Object checkSingleRole(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequireRole annotation = method.getAnnotation(RequireRole.class);

        UserContext user = getAuthenticatedUserUseCase.getCurrentUser();
        String requiredRole = annotation.value();

        log.debug(
                "Checking role: {} for user: {} (roles: {})",
                requiredRole,
                user.getAccountId(),
                user.getRoles());

        List<String> userRoles = user.getRoles();
        boolean hasRole = userRoles != null && userRoles.contains(requiredRole);

        if (!hasRole) {
            log.warn(
                    "Role check failed: User {} lacks role {}. User roles: {}",
                    user.getAccountId(),
                    requiredRole,
                    userRoles);
            throw new AccessDeniedException(annotation.errorMessage());
        }

        log.debug("Role granted: {} for user {}", requiredRole, user.getAccountId());
        return joinPoint.proceed();
    }

    /** Intercept @RequireAnyRole annotation */
    @Around("@annotation(com.a1a.shared.auth.application.port.annotation.RequireAnyRole)")
    public Object checkAnyRole(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequireAnyRole annotation = method.getAnnotation(RequireAnyRole.class);

        UserContext user = getAuthenticatedUserUseCase.getCurrentUser();
        String[] requiredRoles = annotation.value();

        log.debug(
                "Checking ANY role from: {} for user: {} (roles: {})",
                Arrays.toString(requiredRoles),
                user.getAccountId(),
                user.getRoles());

        List<String> userRoles = user.getRoles();
        boolean hasAnyRole =
                userRoles != null && Arrays.stream(requiredRoles).anyMatch(userRoles::contains);

        if (!hasAnyRole) {
            log.warn(
                    "Role check failed: User {} lacks any of roles {}. User roles: {}",
                    user.getAccountId(),
                    Arrays.toString(requiredRoles),
                    userRoles);
            throw new AccessDeniedException(annotation.errorMessage());
        }

        log.debug(
                "Role granted (ANY): User {} has at least one required role", user.getAccountId());
        return joinPoint.proceed();
    }

    /** Intercept @RequireAllRoles annotation */
    @Around("@annotation(com.a1a.shared.auth.application.port.annotation.RequireAllRoles)")
    public Object checkAllRoles(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequireAllRoles annotation = method.getAnnotation(RequireAllRoles.class);

        UserContext user = getAuthenticatedUserUseCase.getCurrentUser();
        String[] requiredRoles = annotation.value();

        log.debug(
                "Checking ALL roles: {} for user: {} (roles: {})",
                Arrays.toString(requiredRoles),
                user.getAccountId(),
                user.getRoles());

        List<String> userRoles = user.getRoles();
        boolean hasAllRoles =
                userRoles != null && Arrays.stream(requiredRoles).allMatch(userRoles::contains);

        if (!hasAllRoles) {
            log.warn(
                    "Role check failed: User {} lacks all roles {}. User roles: {}",
                    user.getAccountId(),
                    Arrays.toString(requiredRoles),
                    userRoles);
            throw new AccessDeniedException(annotation.errorMessage());
        }

        log.debug("Role granted (ALL): User {} has all required roles", user.getAccountId());
        return joinPoint.proceed();
    }
}

