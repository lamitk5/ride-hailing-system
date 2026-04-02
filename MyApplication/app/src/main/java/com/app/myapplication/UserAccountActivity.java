package com.app.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.app.myapplication.api.ApiClient;
import com.app.myapplication.api.ApiService;
import com.app.myapplication.model.UserProfile;
import com.app.myapplication.utils.SessionManager;
import android.view.View;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserAccountActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserPhone, tvWalletBalance;
    private MaterialButton btnTopUp, btnHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        tvUserName      = findViewById(R.id.tv_user_name);
        tvUserPhone     = findViewById(R.id.tv_user_phone);
        tvWalletBalance = findViewById(R.id.tv_wallet_balance);
        btnTopUp        = findViewById(R.id.btn_top_up);
        btnHistory      = findViewById(R.id.btn_history);

        // History button
        if (btnHistory != null) {
            btnHistory.setOnClickListener(v -> 
                    startActivity(new Intent(this, ActivityHistoryActivity.class)));
        }

        // Bottom navigation
        setupBottomNav();

        // Logout button
        findViewById(R.id.btn_logout).setOnClickListener(v -> logout());

        // Account Menus
        android.view.View menuPayment = findViewById(R.id.menu_payment);
        if(menuPayment != null) menuPayment.setOnClickListener(v -> showToast("Tính năng đang phát triển..."));
        
        android.view.View menuSettings = findViewById(R.id.menu_settings);
        if(menuSettings != null) menuSettings.setOnClickListener(v -> showToast("Tính năng đang phát triển..."));
        
        android.view.View menuHelp = findViewById(R.id.menu_help);
        if(menuHelp != null) menuHelp.setOnClickListener(v -> showToast("Tính năng đang phát triển..."));

        // Load profile from API
        loadProfile();
    }

    private void showToast(String msg) {
        android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_SHORT).show();
    }

    private void loadProfile() {
        // Show cached data immediately
        tvUserName.setText(SessionManager.getUserName());
        tvUserPhone.setText(SessionManager.getUserPhone());

        ApiClient.getApiService().getUserProfile().enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfile profile = response.body();
                    tvUserName.setText(profile.getFullName());
                    tvUserPhone.setText(profile.getPhone());
                    // Format wallet balance as Vietnamese currency
                    tvWalletBalance.setText(String.format("%,.0fđ", profile.getWalletBalance()));
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                // Keep showing cached data; silent failure is acceptable here
            }
        });
    }

    private void setupBottomNav() {
        // Home
        View homeBtn = findViewById(R.id.nav_home);
        if (homeBtn != null) homeBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        // Offers
        View offersBtn = findViewById(R.id.nav_offers);
        if (offersBtn != null) offersBtn.setOnClickListener(v ->
                startActivity(new Intent(this, OffersActivity.class)));
    }

    private void logout() {
        SessionManager.clearSession();
        ApiClient.clearAuthToken();
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
