package com.a1a.shared.auth.infrastructure.config.autoconfigure;

import com.a1a.shared.auth.application.port.driving.GetAuthenticatedUserUseCase;
import com.a1a.shared.auth.application.port.driving.PermissionValidatorUseCase;
import com.a1a.shared.auth.infrastructure.aspect.PermissionCheckAspect;
import com.a1a.shared.auth.infrastructure.aspect.RoleCheckAspect;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Auto-configuration for AOP aspects.
 *
 * <p>
 * Enables AspectJ auto-proxy and creates aspects for permission/role
 * annotations.
 */
@Configuration
@ConditionalOnClass(name = "org.aspectj.lang.annotation.Aspect")
@EnableAspectJAutoProxy
public class AspectConfiguration {

    /** Permission check aspect for @RequirePermission annotations */
    @Bean
    @ConditionalOnMissingBean
    public PermissionCheckAspect permissionCheckAspect(
            GetAuthenticatedUserUseCase getAuthenticatedUser,
            PermissionValidatorUseCase permissionValidator) {
        return new PermissionCheckAspect(getAuthenticatedUser, permissionValidator);
    }

    /** Role check aspect for @RequireRole annotations */
    @Bean
    @ConditionalOnMissingBean
    public RoleCheckAspect roleCheckAspect(GetAuthenticatedUserUseCase getAuthenticatedUser) {
        return new RoleCheckAspect(getAuthenticatedUser);
    }
}

