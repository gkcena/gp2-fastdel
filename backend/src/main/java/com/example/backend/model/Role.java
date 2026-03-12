package com.example.backend.model;

/**
 * RBAC roles for FastDel.
 * <ul>
 *   <li>ADMIN  — Web panel: full access, user management</li>
 *   <li>STAFF  — Web panel: package & barcode management</li>
 *   <li>COURIER — Mobile app: scan, route, deliver</li>
 * </ul>
 */
public enum Role {
    ADMIN,
    STAFF,
    COURIER
}
