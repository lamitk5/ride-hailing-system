package com.app.myapplication;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.app.myapplication.api.ApiClient;
import com.app.myapplication.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize session manager
        SessionManager.init(getApplicationContext());

        // Restore auth token into Retrofit interceptor if logged in
        if (SessionManager.isLoggedIn()) {
            ApiClient.setAuthToken(SessionManager.getToken());

            // Route based on role
            if (SessionManager.isDriver()) {
                startActivity(new Intent(this, DriverDashboardActivity.class));
            } else {
                startActivity(new Intent(this, HomeActivity.class));
            }
        } else {
            startActivity(new Intent(this, WelcomeActivity.class));
        }
        finish();
    }
}