package com.a1a.shared.auth.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import java.util.List;

/** Gateway API response for permission account */
@Data
public class GatewayPermissionResponse {

    private Integer code;
    private String message;
    private List<PermissionData> data;

    @Data
    public static class PermissionData {

        @JsonProperty("groupId")
        private Integer groupId;

        @JsonProperty("groupCode")
        private String groupCode;

        @JsonProperty("groupName")
        private String groupName;

        @JsonProperty("permissionId")
        private Integer permissionId;

        @JsonProperty("permissionCode")
        private String permissionCode;

        @JsonProperty("permissionGroupName")
        private String permissionGroupName;

        @JsonProperty("menuId")
        private Integer menuId;

        @JsonProperty("menuCode")
        private String menuCode;
    }
}


