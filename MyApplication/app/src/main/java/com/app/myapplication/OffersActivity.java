package com.app.myapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class OffersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

        // Bottom navigation
        setupBottomNav();
    }

    private void setupBottomNav() {
        if (findViewById(R.id.nav_home) != null) {
            findViewById(R.id.nav_home).setOnClickListener(v -> {
                startActivity(new android.content.Intent(this, HomeActivity.class));
            });
        }
        if (findViewById(R.id.nav_account) != null) {
            findViewById(R.id.nav_account).setOnClickListener(v -> {
                startActivity(new android.content.Intent(this, UserAccountActivity.class));
            });
        }
    }
}
