package com.portalempleos.dto;

public class AuthResponse {
    private boolean ok;
    private String token;
    private String role;
    private Object principal; // user o company sin password
    private String message;

    public AuthResponse() {}

    public AuthResponse(boolean ok, String token, String role, Object principal, String message) {
        this.ok = ok;
        this.token = token;
        this.role = role;
        this.principal = principal;
        this.message = message;
    }

    public boolean isOk() { return ok; }
    public void setOk(boolean ok) { this.ok = ok; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Object getPrincipal() { return principal; }
    public void setPrincipal(Object principal) { this.principal = principal; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
