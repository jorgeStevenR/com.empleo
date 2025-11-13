package com.portalempleos.model.enums;

/**
 * Enumeración de roles del sistema.
 * Todos llevan el prefijo ROLE_ porque así lo requiere Spring Security.
 */
public enum Role {
    ROLE_USER,
    ROLE_COMPANY,
    ROLE_ADMIN
}
