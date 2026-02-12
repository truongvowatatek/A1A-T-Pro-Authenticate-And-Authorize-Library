package com.a1a.shared.auth.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Fabric Warehouse Permissions with detailed metadata.
 *
 * <p>Use {@link Code} constants for @RequirePermission annotation to ensure type safety.
 */
@Getter
@RequiredArgsConstructor
public enum FabricPermission implements PermissionCode {

    // --- PRODUCTION: DASHBOARD ---
    /** Allow user to view Fabric Inbound Dashboard */
    FAB_PRD_DBD_INBOUND_VIEW(
            "Allow user to view Fabric Inbound Dashboard",
            PermissionType.VIEW,
            Groups.BOARD_PLANNING),

    /** Allow user to view Fabric Kanban Board */
    FAB_PRD_DBD_KANBAN_VIEW(
            "Allow user to view Fabric Kanban Board", PermissionType.VIEW, Groups.BOARD_PLANNING),

    // --- PRODUCTION: PACKING ---
    /** Allow user to view Fabric Packing List Management */
    FAB_PRD_RCP_PACKING_VIEW(
            "Allow user to view Fabric Packing List Management",
            PermissionType.VIEW,
            Groups.PACKING_MNG),

    /** Allow user to import item in Fabric Packing List Management */
    FAB_PRD_RCP_PACKING_IMPORT(
            "Allow user to import item in Fabric Packing List Management",
            PermissionType.IMPORT,
            Groups.PACKING_MNG),

    /** Allow user to print item QR Code in Fabric Packing List Management */
    FAB_PRD_RCP_PACKING_PRINT(
            "Allow user to print item QR Code in Fabric Packing List Management",
            PermissionType.PRINT,
            Groups.PACKING_MNG),

    // --- PRODUCTION: INVENTORY ---
    /** Allow user to view Fabric Inventory */
    FAB_PRD_INV_FABRIC_VIEW(
            "Allow user to view Fabric Inventory", PermissionType.VIEW, Groups.FABRIC_INV),

    /** Allow user to view history transfer Fabric Inventory */
    FAB_PRD_INV_FABRIC_TRACKING(
            "Allow user to view history transfer Fabric Inventory",
            PermissionType.TRACKING,
            Groups.FABRIC_INV),

    /** Allow user to export an excel file in Fabric Inventory */
    FAB_PRD_INV_FABRIC_EXPORT(
            "Allow user to export an excel file in Fabric Inventory",
            PermissionType.EXPORT,
            Groups.FABRIC_INV),

    /** Allow user to print item QR Code in Fabric Inventory */
    FAB_PRD_INV_FABRIC_PRINT(
            "Allow user to print item QR Code in Fabric Inventory",
            PermissionType.PRINT,
            Groups.FABRIC_INV),

    /** Allow user to transfer item into another Location in Fabric Inventory */
    FAB_PRD_INV_FABRIC_TRANSFER(
            "Allow user to transfer item into another Location in Fabric Inventory",
            PermissionType.TRANSFER,
            Groups.FABRIC_INV),

    /** Allow user to delete item in Fabric Inventory */
    FAB_PRD_INV_FABRIC_DELETE(
            "Allow user to delete item in Fabric Inventory",
            PermissionType.DELETE,
            Groups.FABRIC_INV),

    // --- PRODUCTION: RELAX ---
    /** Allow user to view the process in Fabric Relaxation Management */
    FAB_PRD_INV_RELAX_VIEW(
            "Allow user to view the process in Fabric Relaxation Management",
            PermissionType.VIEW,
            Groups.RELAX_MNG),

    /** Allow user to scan in Fabric Relaxation Management */
    FAB_PRD_INV_RELAX_SCAN(
            "Allow user to scan in Fabric Relaxation Management",
            PermissionType.SCAN,
            Groups.RELAX_MNG),

    // --- PRODUCTION: DELIVERY ---
    /** Allow user to issue fabric in Issue Fabric Form */
    FAB_PRD_DLV_ISSUE_CREATE(
            "Allow user to issue fabric in Issue Fabric Form",
            PermissionType.CREATE,
            Groups.ISSUE_FORM),

    /** Allow user to view Daily Issue Fabric Report */
    FAB_PRD_DLV_REPORT_VIEW(
            "Allow user to view Daily Issue Fabric Report",
            PermissionType.VIEW,
            Groups.ISSUE_REPORT),

    /** Allow user to export an excel file item Daily Issue Fabric Report */
    FAB_PRD_DLV_REPORT_EXPORT(
            "Allow user to export an excel file item Daily Issue Fabric Report",
            PermissionType.EXPORT,
            Groups.ISSUE_REPORT),

    // --- PRODUCTION: SCAN ---
    /** Allow user to scan to put/move/view fabric item */
    FAB_PRD_SCN_PUT_SCAN(
            "Allow user to scan to put fabric item into Location",
            PermissionType.SCAN,
            Groups.SCAN_QR),
    FAB_PRD_SCN_MOVE_SCAN(
            "Allow user to scan to move fabric item into another Location",
            PermissionType.SCAN,
            Groups.SCAN_QR),
    FAB_PRD_SCN_INFOR_SCAN(
            "Allow user to scan to view information fabric item",
            PermissionType.SCAN,
            Groups.SCAN_QR),
    FAB_PRD_SCN_ISSUE_SCAN(
            "Allow user to scan to issue fabric item", PermissionType.SCAN, Groups.SCAN_QR),

    // --- QUALITY: RELAX STANDARD ---
    /** Allow user to view/manage Relax Time Standard Management */
    FAB_QLT_RELAXSTANDARD_VIEW(
            "Allow user to view Relax Time Standard Management",
            PermissionType.VIEW,
            Groups.RELAX_STD),
    FAB_QLT_RELAXSTANDARD_UPDATE(
            "Allow user to update Relax Time Standard Management",
            PermissionType.UPDATE,
            Groups.RELAX_STD),
    FAB_QLT_RELAXSTANDARD_CREATE(
            "Allow user to create Relax Time Standard Management",
            PermissionType.CREATE,
            Groups.RELAX_STD),
    FAB_QLT_RELAXSTANDARD_DELETE(
            "Allow user to delete Relax Time Standard Management",
            PermissionType.DELETE,
            Groups.RELAX_STD),

    // --- QUALITY: PLAN ---
    /** Allow user to view/manage Action Plan Management */
    FAB_QLT_PLAN_VIEW(
            "Allow user to view Action Plan Management", PermissionType.VIEW, Groups.ACTION_PLAN),
    FAB_QLT_PLAN_UPDATE(
            "Allow user to update Action Plan Management",
            PermissionType.UPDATE,
            Groups.ACTION_PLAN),
    FAB_QLT_PLAN_CREATE(
            "Allow user to create Action Plan Management",
            PermissionType.CREATE,
            Groups.ACTION_PLAN),
    FAB_QLT_PLAN_DELETE(
            "Allow user to delete Action Plan Management",
            PermissionType.DELETE,
            Groups.ACTION_PLAN),

    // --- OTHER ---
    /** Allow user to view Fabric Temp Warehouse Inventory */
    FAB_TMP_VIEW(
            "Allow user to view Fabric Temp Warehouse Inventory",
            PermissionType.VIEW,
            "Fabric Warehouse System (Fabric Temp Warehouse Inventory)");

    private static final Map<String, FabricPermission> CODE_CACHE;

    static {
        Map<String, FabricPermission> map = new HashMap<>();
        for (FabricPermission perm : values()) {
            map.put(perm.name(), perm);
        }
        CODE_CACHE = Collections.unmodifiableMap(map);
    }

    private final String description;
    private final PermissionType type;
    private final String permissionGroupName;

    /** Find permission by code - O(1) lookup */
    public static FabricPermission fromCode(String code) {
        FabricPermission perm = CODE_CACHE.get(code);
        if (perm == null) throw new IllegalArgumentException("Unknown permission code: " + code);
        return perm;
    }

    public static boolean isValidCode(String code) {
        return CODE_CACHE.containsKey(code);
    }

    @Override
    public String getCode() {
        return this.name();
    }

    /** * Internal constants for Group Names to avoid String repetition */
    private static class Groups {
        private static final String BOARD_PLANNING = "Fabric Warehouse System (Board & Planning)";
        private static final String PACKING_MNG =
                "Fabric Warehouse System (Packing List Management)";
        private static final String FABRIC_INV = "Fabric Warehouse System (Fabric Inventory)";
        private static final String RELAX_MNG =
                "Fabric Warehouse System (Fabric Relaxation Management)";
        private static final String ISSUE_FORM = "Fabric Warehouse System (Issue Fabric Form)";
        private static final String ISSUE_REPORT =
                "Fabric Warehouse System (Daily Issue Fabric report)";
        private static final String SCAN_QR = "Fabric Warehouse System (Scan QR Code)";
        private static final String RELAX_STD =
                "Fabric Warehouse System (Relax Time Standard Management)";
        private static final String ACTION_PLAN =
                "Fabric Warehouse System (Action Plan Management)";
    }

    /**
     * Constant codes for use in annotations
     * like @RequirePermission(FabricPermission.Code.FAB_PRD_INV_FABRIC_VIEW)
     */
    public static final class Code {
        public static final String FAB_PRD_DBD_INBOUND_VIEW = "FAB_PRD_DBD_INBOUND_VIEW";
        public static final String FAB_PRD_DBD_KANBAN_VIEW = "FAB_PRD_DBD_KANBAN_VIEW";
        public static final String FAB_PRD_RCP_PACKING_VIEW = "FAB_PRD_RCP_PACKING_VIEW";
        public static final String FAB_PRD_RCP_PACKING_IMPORT = "FAB_PRD_RCP_PACKING_IMPORT";
        public static final String FAB_PRD_RCP_PACKING_PRINT = "FAB_PRD_RCP_PACKING_PRINT";
        public static final String FAB_PRD_INV_FABRIC_VIEW = "FAB_PRD_INV_FABRIC_VIEW";
        public static final String FAB_PRD_INV_FABRIC_TRACKING = "FAB_PRD_INV_FABRIC_TRACKING";
        public static final String FAB_PRD_INV_FABRIC_EXPORT = "FAB_PRD_INV_FABRIC_EXPORT";
        public static final String FAB_PRD_INV_FABRIC_PRINT = "FAB_PRD_INV_FABRIC_PRINT";
        public static final String FAB_PRD_INV_FABRIC_TRANSFER = "FAB_PRD_INV_FABRIC_TRANSFER";
        public static final String FAB_PRD_INV_FABRIC_DELETE = "FAB_PRD_INV_FABRIC_DELETE";
        public static final String FAB_PRD_INV_RELAX_VIEW = "FAB_PRD_INV_RELAX_VIEW";
        public static final String FAB_PRD_INV_RELAX_SCAN = "FAB_PRD_INV_RELAX_SCAN";
        public static final String FAB_PRD_DLV_ISSUE_CREATE = "FAB_PRD_DLV_ISSUE_CREATE";
        public static final String FAB_PRD_DLV_REPORT_VIEW = "FAB_PRD_DLV_REPORT_VIEW";
        public static final String FAB_PRD_DLV_REPORT_EXPORT = "FAB_PRD_DLV_REPORT_EXPORT";
        public static final String FAB_PRD_SCN_PUT_SCAN = "FAB_PRD_SCN_PUT_SCAN";
        public static final String FAB_PRD_SCN_MOVE_SCAN = "FAB_PRD_SCN_MOVE_SCAN";
        public static final String FAB_PRD_SCN_INFOR_SCAN = "FAB_PRD_SCN_INFOR_SCAN";
        public static final String FAB_PRD_SCN_ISSUE_SCAN = "FAB_PRD_SCN_ISSUE_SCAN";
        public static final String FAB_QLT_RELAXSTANDARD_VIEW = "FAB_QLT_RELAXSTANDARD_VIEW";
        public static final String FAB_QLT_RELAXSTANDARD_UPDATE = "FAB_QLT_RELAXSTANDARD_UPDATE";
        public static final String FAB_QLT_RELAXSTANDARD_CREATE = "FAB_QLT_RELAXSTANDARD_CREATE";
        public static final String FAB_QLT_RELAXSTANDARD_DELETE = "FAB_QLT_RELAXSTANDARD_DELETE";
        public static final String FAB_QLT_PLAN_VIEW = "FAB_QLT_PLAN_VIEW";
        public static final String FAB_QLT_PLAN_UPDATE = "FAB_QLT_PLAN_UPDATE";
        public static final String FAB_QLT_PLAN_CREATE = "FAB_QLT_PLAN_CREATE";
        public static final String FAB_QLT_PLAN_DELETE = "FAB_QLT_PLAN_DELETE";
        public static final String FAB_TMP_VIEW = "FAB_TMP_VIEW";

        private Code() {}
    }
}



