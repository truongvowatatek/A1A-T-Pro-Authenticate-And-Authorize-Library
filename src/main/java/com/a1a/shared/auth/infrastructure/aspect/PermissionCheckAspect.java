package com.a1a.shared.auth.infrastructure.aspect;

import com.a1a.shared.auth.application.port.annotation.RequireAllPermissions;
import com.a1a.shared.auth.application.port.annotation.RequireAnyPermission;
import com.a1a.shared.auth.application.port.annotation.RequirePermission;
import com.a1a.shared.auth.application.port.driving.GetAuthenticatedUserUseCase;
import com.a1a.shared.auth.application.port.driving.PermissionValidatorUseCase;
import com.a1a.shared.auth.domain.exception.PermissionException;
import com.a1a.shared.auth.domain.model.UserContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * AOP Aspect to intercept and validate permission annotations Uses SPI
 * interfaces for extensibility
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class PermissionCheckAspect {

    private final GetAuthenticatedUserUseCase getAuthenticatedUserUseCase;
    private final PermissionValidatorUseCase permissionValidatorUseCase;

    /** Intercept @RequirePermission annotation */
    @Around("@annotation(com.a1a.shared.auth.application.port.annotation.RequirePermission)")
    public Object checkSinglePermission(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequirePermission annotation = method.getAnnotation(RequirePermission.class);

        Long userId = getAuthenticatedUserUseCase.getCurrentUser().getAccountId();
        String requiredPermission = annotation.value();

        log.debug("Checking permission: {} for user: {}", requiredPermission, userId);

        boolean hasPermission = permissionValidatorUseCase.hasPermission(userId, requiredPermission);

        if (!hasPermission) {
            UserContext user = getAuthenticatedUserUseCase.getCurrentUser();
            log.warn("Permission denied: User {} lacks permission {}", userId, requiredPermission);
            throw new PermissionException(
                    String.format(
                            "User %s does not have required permission: %s",
                            user.getUsername(), requiredPermission));
        }

        log.debug("Permission granted: {} for user {}", requiredPermission, userId);
        return joinPoint.proceed();
    }

    /** Intercept @RequireAnyPermission annotation */
    @Around("@annotation(com.a1a.shared.auth.application.port.annotation.RequireAnyPermission)")
    public Object checkAnyPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequireAnyPermission annotation = method.getAnnotation(RequireAnyPermission.class);

        Long userId = getAuthenticatedUserUseCase.getCurrentUser().getAccountId();
        String[] requiredPermissions = annotation.value();

        log.debug(
                "Checking ANY permission from: {} for user: {}",
                Arrays.toString(requiredPermissions),
                userId);

        boolean hasAnyPermission = permissionValidatorUseCase.hasAnyPermission(userId, requiredPermissions);

        if (!hasAnyPermission) {
            UserContext user = getAuthenticatedUserUseCase.getCurrentUser();
            log.warn(
                    "Permission denied: User {} lacks any of permissions {}",
                    userId,
                    Arrays.toString(requiredPermissions));
            throw new PermissionException(
                    String.format(
                            "User %s does not have any of required permissions: %s",
                            user.getUsername(), Arrays.toString(requiredPermissions)));
        }

        log.debug("Permission granted (ANY): User {} has at least one permission", userId);
        return joinPoint.proceed();
    }

    /** Intercept @RequireAllPermissions annotation */
    @Around("@annotation(com.a1a.shared.auth.application.port.annotation.RequireAllPermissions)")
    public Object checkAllPermissions(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequireAllPermissions annotation = method.getAnnotation(RequireAllPermissions.class);

        Long userId = getAuthenticatedUserUseCase.getCurrentUser().getAccountId();
        String[] requiredPermissions = annotation.value();

        log.debug(
                "Checking ALL permissions: {} for user: {}",
                Arrays.toString(requiredPermissions),
                userId);

        boolean hasAllPermissions = permissionValidatorUseCase.hasAllPermissions(userId, requiredPermissions);

        if (!hasAllPermissions) {
            UserContext user = getAuthenticatedUserUseCase.getCurrentUser();
            log.warn(
                    "Permission denied: User {} lacks all permissions {}",
                    userId,
                    Arrays.toString(requiredPermissions));
            throw new PermissionException(
                    String.format(
                            "User %s does not have all required permissions: %s",
                            user.getUsername(), Arrays.toString(requiredPermissions)));
        }

        log.debug("Permission granted (ALL): User {} has all required permissions", userId);
        return joinPoint.proceed();
    }
}

