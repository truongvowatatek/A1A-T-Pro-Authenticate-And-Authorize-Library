package com.a1a.shared.auth.application.service;

import com.a1a.shared.auth.application.dto.GatewayPermissionResponse;
import com.a1a.shared.auth.application.port.driving.GatewayPermissionClientUseCase;
import com.a1a.shared.auth.domain.exception.AuthenticationException;
import com.a1a.shared.auth.domain.exception.PermissionException;
import com.a1a.shared.auth.infrastructure.config.AuthProperties;

import io.netty.channel.ChannelOption;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import reactor.netty.http.client.HttpClient;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/** Client for calling Gateway API to fetch user permissions */
@Slf4j
public class GatewayPermissionClientService implements GatewayPermissionClientUseCase {

    private final AuthProperties properties;
    private final WebClient webClient;

    public GatewayPermissionClientService(
            WebClient.Builder webClientBuilder, AuthProperties properties) {
        this.properties = properties;

        // Configure timeouts using Reactor Netty HttpClient
        HttpClient httpClient =
                HttpClient.create()
                        .option(
                                ChannelOption.CONNECT_TIMEOUT_MILLIS,
                                (int) properties.getPermission().getConnectTimeout().toMillis())
                        .responseTimeout(properties.getPermission().getReadTimeout());

        this.webClient =
                webClientBuilder
                        .clientConnector(new ReactorClientHttpConnector(httpClient))
                        .build();
    }

    /**
     * Fetch user permissions from Gateway API
     *
     * @param accessToken JWT access token from authentication
     * @return Set of permission codes
     */
    @Override
    public Set<String> fetchUserPermissions(String accessToken) {
        try {
            String url = buildPermissionUrl();

            log.debug("Calling Gateway API: {}", url);

            GatewayPermissionResponse response =
                    webClient
                            .get()
                            .uri(url)
                            .headers(
                                    headers -> {
                                        headers.setContentType(MediaType.APPLICATION_JSON);
                                        headers.setBearerAuth(accessToken);
                                    })
                            .retrieve()
                            .bodyToMono(GatewayPermissionResponse.class)
                            .block(); // Block to maintain synchronous behavior

            return parsePermissionResponse(response);

        } catch (WebClientResponseException.Unauthorized e) {
            // 401: Token is invalid or expired
            log.error("Authentication failed when fetching permissions: {}", e.getMessage());
            throw new AuthenticationException("Token is invalid or expired", e);

        } catch (WebClientResponseException.Forbidden e) {
            // 403: Access forbidden by Gateway
            log.error("Gateway API denied access: {}", e.getMessage());
            throw new PermissionException("Access denied by Gateway", e);

        } catch (WebClientResponseException e) {
            // Other HTTP errors
            log.error("Gateway API returned error: {} - {}", e.getStatusCode(), e.getMessage(), e);
            throw new PermissionException("Failed to load permissions from Gateway", e);

        } catch (Exception e) {
            // Network, timeout, or other errors
            log.error("Failed to fetch permissions from Gateway API: {}", e.getMessage(), e);
            throw new PermissionException("Failed to load permissions from Gateway", e);
        }
    }

    /** Build full URL for permission endpoint */
    private String buildPermissionUrl() {
        AuthProperties.PermissionConfig gateway = properties.getPermission();

        return UriComponentsBuilder.fromUriString(gateway.getUrl()).toUriString();
    }

    /** Parse Gateway API response and extract permission codes */
    private Set<String> parsePermissionResponse(GatewayPermissionResponse response) {
        if (response == null || response.getData() == null) {
            log.warn("Empty response from Gateway API");
            return Collections.emptySet();
        }

        if (response.getCode() != 200) {
            log.error(
                    "Gateway API returned error code: {} - {}",
                    response.getCode(),
                    response.getMessage());
            throw new PermissionException("Gateway API error: " + response.getMessage());
        }

        Set<String> permissionCodes =
                response.getData().stream()
                        .map(GatewayPermissionResponse.PermissionData::getPermissionCode)
                        .collect(Collectors.toSet());

        log.info("Fetched {} permissions from Gateway API", permissionCodes.size());
        log.debug("Permission codes: {}", permissionCodes);

        return permissionCodes;
    }
}



