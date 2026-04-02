package com.app.myapplication.model;

import com.google.gson.annotations.SerializedName;

public class UserProfile {
    @SerializedName("id")
    private Long id;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("phone")
    private String phone;

    @SerializedName("email")
    private String email;

    @SerializedName("walletBalance")
    private Double walletBalance;

    @SerializedName("referralCode")
    private String referralCode;

    @SerializedName("avatarUrl")
    private String avatarUrl;

    @SerializedName("role")
    private String role;

    public UserProfile() {}

    // Getters
    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public Double getWalletBalance() { return walletBalance != null ? walletBalance : 0.0; }
    public String getReferralCode() { return referralCode; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getRole() { return role; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setWalletBalance(Double walletBalance) { this.walletBalance = walletBalance; }
    public void setReferralCode(String referralCode) { this.referralCode = referralCode; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public void setRole(String role) { this.role = role; }
}
