package com.a1a.shared.auth.domain.model;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/** System Roles with detailed metadata */
@Getter
public enum SystemRole implements RoleCode {

    // System Administration
    SUPER_ADMIN("Super Admin", "System administrator with full access to all features"),

    // Production Management
    PROD_MGR("Production Manager", "Manages all production operations and warehouse managers"),

    // Fabric Warehouse
    FAB_MGR("Fabric Manager", "Manages fabric warehouse operations"),
    FAB_LEADER("Fabric Leader", "Leads fabric warehouse team"),
    FAB_STAFF("Fabric Staff", "Fabric warehouse staff member"),
    FAB_OFFICER("Fabric Officer", "Fabric warehouse officer"),

    // Fabric Quality Control
    FAB_QC_MGR("Quality Manager", "Manages quality control operations in Fabric warehouse"),
    FAB_QC_LEADER("QC Leader", "Leads quality control team in Fabric warehouse"),
    FAB_QC_STAFF("QC Staff", "Quality control staff member in Fabric warehouse"),

    // Accessory Warehouse
    ACC_MGR("Accessory Manager", "Manages accessory warehouse operations"),
    ACC_LEADER("Accessory Leader", "Leads accessory warehouse team"),
    ACC_STAFF("Accessory Staff", "Accessory warehouse staff member"),
    ACC_OFFICER("Accessory Officer", "Accessory warehouse officer"),

    // Accessory Quality Control
    ACC_QC_MGR("Quality Manager", "Manages quality control operations in Accessory warehouse"),
    ACC_QC_LEADER("QC Leader", "Leads quality control team in Accessory warehouse"),
    ACC_QC_STAFF("QC Staff", "Quality control staff member in Accessory warehouse"),

    // Packaging Warehouse
    PKG_MGR("Packaging Manager", "Manages packaging warehouse operations"),
    PKG_LEADER("Packaging Leader", "Leads packaging warehouse team"),
    PKG_STAFF("Packaging Staff", "Packaging warehouse staff member"),
    PKG_OFFICER("Packaging Officer", "Packaging warehouse officer"),

    // Packaging Quality Control
    PKG_QC_MGR("Quality Manager", "Manages quality control operations in Packaging warehouse"),
    PKG_QC_LEADER("QC Leader", "Leads quality control team in Packaging warehouse"),
    PKG_QC_STAFF("QC Staff", "Quality control staff member in Packaging warehouse");

    // Static cache for fast lookup - O(1) instead of O(n)
    private static final Map<String, SystemRole> CODE_CACHE = new HashMap<>();

    static {
        for (SystemRole role : values()) {
            CODE_CACHE.put(role.name(), role);
        }
    }

    private final String roleName;
    private final String description;

    SystemRole(String roleName, String description) {
        this.roleName = roleName;
        this.description = description;
    }

    /**
     * Find role by code - O(1) lookup
     *
     * @param code Role code
     * @return SystemRole
     * @throws IllegalArgumentException if code not found
     */
    public static SystemRole fromCode(String code) {
        SystemRole role = CODE_CACHE.get(code);
        if (role == null) {
            throw new IllegalArgumentException("Unknown system role code: " + code);
        }
        return role;
    }

    /**
     * Check if code exists
     *
     * @param code Role code
     * @return true if exists
     */
    public static boolean isValidCode(String code) {
        return CODE_CACHE.containsKey(code);
    }

    @Override
    public String getCode() {
        return this.name();
    }
}



