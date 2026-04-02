package com.app.myapplication;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class CheckoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_cart);

        ImageButton btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        android.view.View btnCheckout = findViewById(R.id.btn_checkout);
        if (btnCheckout != null) {
            btnCheckout.setOnClickListener(v -> {
                android.widget.Toast.makeText(this, "Đặt hàng thành công!", android.widget.Toast.LENGTH_SHORT).show();
                android.content.Intent intent = new android.content.Intent(this, ActivityHistoryActivity.class);
                startActivity(intent);
                finish();
            });
        }
    }
}