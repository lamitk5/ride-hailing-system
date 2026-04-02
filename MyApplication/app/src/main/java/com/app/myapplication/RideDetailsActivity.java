package com.app.myapplication;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class RideDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_details);

        ImageButton btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        android.view.View btnMessage = findViewById(R.id.btn_message);
        if (btnMessage != null) {
            btnMessage.setOnClickListener(v -> 
                android.widget.Toast.makeText(this, "Mở trình nhắn tin...", android.widget.Toast.LENGTH_SHORT).show());
        }

        android.view.View btnCall = findViewById(R.id.btn_call);
        if (btnCall != null) {
            btnCall.setOnClickListener(v -> 
                android.widget.Toast.makeText(this, "Đang gọi tài xế...", android.widget.Toast.LENGTH_SHORT).show());
        }
    }
}