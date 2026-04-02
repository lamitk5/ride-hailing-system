package com.app.myapplication.api;

import com.app.myapplication.model.DriverRegisterRequest;
import com.app.myapplication.model.LoginRequest;
import com.app.myapplication.model.LoginResponse;
import com.app.myapplication.model.RegisterRequest;
import com.app.myapplication.model.UserProfile;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * Retrofit interface for all Spring Boot backend API endpoints.
 *
 * Base URL: http://10.0.2.2:8080/api/
 *
 * Adjust endpoint paths to match your Spring Boot @RequestMapping annotations.
 */
public interface ApiService {

    // ── Auth ──────────────────────────────────────────────────────
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("users/register")
    Call<LoginResponse> registerUser(@Body RegisterRequest request);

    @POST("drivers/register")
    Call<LoginResponse> registerDriver(@Body DriverRegisterRequest request);

    // ── User ──────────────────────────────────────────────────────
    @GET("user/profile")
    Call<UserProfile> getUserProfile();

    @PUT("user/profile")
    Call<UserProfile> updateUserProfile(@Body UserProfile profile);
}
