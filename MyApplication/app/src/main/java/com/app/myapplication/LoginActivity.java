package com.app.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.app.myapplication.api.ApiClient;
import com.app.myapplication.api.ApiService;
import com.app.myapplication.model.LoginRequest;
import com.app.myapplication.model.LoginResponse;
import com.app.myapplication.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etPhone, etPassword;
    private TextInputLayout tilPhone, tilPassword;
    private MaterialButton btnLogin;
    private View btnBack, tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Bind views
        etPhone    = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        tilPhone   = findViewById(R.id.til_phone);
        tilPassword= findViewById(R.id.til_password);
        btnLogin   = findViewById(R.id.btn_login);
        btnBack    = findViewById(R.id.btn_back);
        tvRegister = findViewById(R.id.tv_register);

        btnBack.setOnClickListener(v -> finish());

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, UserRegistrationActivity.class));
            finish();
        });

        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String phone    = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        boolean valid = true;
        if (TextUtils.isEmpty(phone)) {
            tilPhone.setError("Vui lòng nhập số điện thoại");
            valid = false;
        } else {
            tilPhone.setError(null);
        }
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Vui lòng nhập mật khẩu");
            valid = false;
        } else {
            tilPassword.setError(null);
        }
        if (!valid) return;

        setLoading(true);

        ApiService apiService = ApiClient.getApiService();
        apiService.login(new LoginRequest(phone, password)).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse body = response.body();

                    // Save session
                    SessionManager.saveSession(
                            body.getToken(),
                            body.getUserId(),
                            body.getRole(),
                            body.getFullName(),
                            body.getPhone()
                    );
                    ApiClient.setAuthToken(body.getToken());

                    // Navigate based on role
                    Intent intent;
                    if (SessionManager.isDriver()) {
                        intent = new Intent(LoginActivity.this, DriverDashboardActivity.class);
                    } else {
                        intent = new Intent(LoginActivity.this, HomeActivity.class);
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    int code = response.code();
                    if (code == 401) {
                        Toast.makeText(LoginActivity.this,
                                "Sai số điện thoại hoặc mật khẩu", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this,
                                "Lỗi đăng nhập: " + code, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                setLoading(false);
                Toast.makeText(LoginActivity.this,
                        "Không thể kết nối đến máy chủ: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        btnLogin.setEnabled(!loading);
        btnLogin.setText(loading ? "Đang đăng nhập..." : "Đăng nhập");
    }
}
