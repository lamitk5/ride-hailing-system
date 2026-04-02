package com.app.myapplication;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class DriverDashboardActivity extends AppCompatActivity {

    private TextView tvRideCount, tvEarnings, tvHours;
    private MaterialButton btnAccept, btnDecline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_dashboard);

        tvRideCount = findViewById(R.id.tv_ride_count);
        tvEarnings  = findViewById(R.id.tv_earnings);
        tvHours     = findViewById(R.id.tv_hours);
        btnAccept   = findViewById(R.id.btn_accept_ride);
        btnDecline  = findViewById(R.id.btn_decline_ride);

        // Online/offline toggle
        if (findViewById(R.id.btn_online) != null) {
            findViewById(R.id.btn_online).setOnClickListener(v ->
                    Toast.makeText(this, "Trạng thái: Online", Toast.LENGTH_SHORT).show());
        }
        if (findViewById(R.id.btn_offline) != null) {
            findViewById(R.id.btn_offline).setOnClickListener(v ->
                    Toast.makeText(this, "Trạng thái: Offline", Toast.LENGTH_SHORT).show());
        }

        if (btnAccept != null) {
            btnAccept.setOnClickListener(v ->
                    Toast.makeText(this, "Đã chấp nhận chuyến đi!", Toast.LENGTH_SHORT).show());
        }
        if (btnDecline != null) {
            btnDecline.setOnClickListener(v ->
                    Toast.makeText(this, "Đã từ chối chuyến đi.", Toast.LENGTH_SHORT).show());
        }

        // Set demo stats
        if (tvRideCount != null) tvRideCount.setText("12");
        if (tvEarnings  != null) tvEarnings.setText("$184");
        if (tvHours     != null) tvHours.setText("6.5");
    }
}
