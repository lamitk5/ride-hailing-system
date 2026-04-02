package com.app.myapplication.model;

import com.google.gson.annotations.SerializedName;

public class DriverRegisterRequest {
    @SerializedName("fullName")
    private String fullName;

    @SerializedName("phoneNumber")
    private String phone;

    @SerializedName("password")
    private String password;

    @SerializedName("vehicleType")
    private String vehicleType; // "CAR", "BIKE", "PLUS"

    @SerializedName("licensePlate")
    private String licensePlate;

    @SerializedName("identityCard")
    private String identityCard;

    @SerializedName("brandModel")
    private String brandModel;

    public DriverRegisterRequest(String fullName, String phone, String password,
                                  String vehicleType, String licensePlate, String identityCard, String brandModel) {
        this.fullName = fullName;
        this.phone = phone;
        this.password = password;
        this.vehicleType = vehicleType;
        this.licensePlate = licensePlate;
        this.identityCard = identityCard;
        this.brandModel = brandModel;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public String getIdentityCard() { return identityCard; }
    public void setIdentityCard(String identityCard) { this.identityCard = identityCard; }
    public String getBrandModel() { return brandModel; }
    public void setBrandModel(String brandModel) { this.brandModel = brandModel; }
}
