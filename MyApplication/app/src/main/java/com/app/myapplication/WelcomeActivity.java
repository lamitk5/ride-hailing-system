package com.app.myapplication;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Start 3D hover animations on the logo container
        View logoContainer = findViewById(R.id.logo_container);
        if (logoContainer != null) {
            // Smooth vertical floating
            ObjectAnimator floatAnim = ObjectAnimator.ofFloat(logoContainer, "translationY", 0f, -40f, 0f);
            floatAnim.setDuration(3000);
            floatAnim.setInterpolator(new AccelerateDecelerateInterpolator());
            floatAnim.setRepeatCount(ValueAnimator.INFINITE);
            floatAnim.start();

            // 3D Tilt on Y-axis
            ObjectAnimator rotateAnimY = ObjectAnimator.ofFloat(logoContainer, "rotationY", -10f, 10f, -10f);
            rotateAnimY.setDuration(4500); // Out of sync for organic vibe
            rotateAnimY.setInterpolator(new AccelerateDecelerateInterpolator());
            rotateAnimY.setRepeatCount(ValueAnimator.INFINITE);
            rotateAnimY.start();
            
            // 3D Tilt on X-axis
            ObjectAnimator rotateAnimX = ObjectAnimator.ofFloat(logoContainer, "rotationX", -5f, 5f, -5f);
            rotateAnimX.setDuration(3500);
            rotateAnimX.setInterpolator(new AccelerateDecelerateInterpolator());
            rotateAnimX.setRepeatCount(ValueAnimator.INFINITE);
            rotateAnimX.start();
        }

        // "Bắt đầu ngay" → Registration based on flavor
        findViewById(R.id.btn_get_started).setOnClickListener(v -> {
            if ("driver".equals(BuildConfig.FLAVOR)) {
                startActivity(new Intent(this, DriverRegistrationActivity.class));
            } else {
                startActivity(new Intent(this, UserRegistrationActivity.class));
            }
        });

        // "Đăng nhập" → Login screen
        findViewById(R.id.btn_login).setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));
    }
}
