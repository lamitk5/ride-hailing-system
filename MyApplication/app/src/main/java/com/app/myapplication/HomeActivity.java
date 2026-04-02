package com.app.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class HomeActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);

        tvWelcome    = findViewById(R.id.tv_welcome_title);
        swipeRefresh = findViewById(R.id.swipe_refresh);

        // Set welcome text with cached name
        if (tvWelcome != null) {
            String name = com.app.myapplication.utils.SessionManager.getUserName();
            tvWelcome.setText("Xin chào" + (name.isEmpty() ? "" : ", " + name));
        }

        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(() -> {
                // Reload restaurant list
                swipeRefresh.setRefreshing(false);
            });
        }

        // Bottom navigation
        setupBottomNav();
    }

    private void setupBottomNav() {
        if (findViewById(R.id.nav_orders) != null) {
            findViewById(R.id.nav_orders).setOnClickListener(v ->
                    startActivity(new Intent(this, ActivityHistoryActivity.class)));
        }
        if (findViewById(R.id.nav_cart) != null) {
            findViewById(R.id.nav_cart).setOnClickListener(v ->
                    startActivity(new Intent(this, CheckoutActivity.class)));
        }
        if (findViewById(R.id.nav_account) != null) {
            findViewById(R.id.nav_account).setOnClickListener(v ->
                    startActivity(new Intent(this, UserAccountActivity.class)));
        }

        // Restaurant Card click
        if (findViewById(R.id.card_restaurant_1) != null) {
            findViewById(R.id.card_restaurant_1).setOnClickListener(v -> 
                    startActivity(new Intent(this, RideDetailsActivity.class)));
        }
    }
}
