package com.app.myapplication.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("token")
    private String token;

    @SerializedName("userId")
    private String userId;

    @SerializedName("role")
    private String role;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("phone")
    private String phone;

    // Constructors
    public LoginResponse() {}

    // Getters
    public String getToken() { return token; }
    public String getUserId() { return userId; }
    public String getRole() { return role; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }

    // Setters
    public void setToken(String token) { this.token = token; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setRole(String role) { this.role = role; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhone(String phone) { this.phone = phone; }
}
