package com.app.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.app.myapplication.api.ApiClient;
import com.app.myapplication.api.ApiService;
import com.app.myapplication.model.LoginResponse;
import com.app.myapplication.model.RegisterRequest;
import com.app.myapplication.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRegistrationActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etPhone, etPassword, etConfirmPassword;
    private TextInputLayout tilFullName, tilPhone, tilPassword, tilConfirmPassword;
    private CheckBox cbTerms;
    private MaterialButton btnRegister;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        etFullName        = findViewById(R.id.et_full_name);
        etPhone           = findViewById(R.id.et_phone);
        etPassword        = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        tilFullName       = findViewById(R.id.til_full_name);
        tilPhone          = findViewById(R.id.til_phone);
        tilPassword       = findViewById(R.id.til_password);
        tilConfirmPassword= findViewById(R.id.til_confirm_password);
        cbTerms           = findViewById(R.id.cb_terms);
        btnRegister       = findViewById(R.id.btn_register);
        tvLogin           = findViewById(R.id.tv_login);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        btnRegister.setOnClickListener(v -> attemptRegister());
    }

    private void attemptRegister() {
        String fullName       = getText(etFullName);
        String phone          = getText(etPhone);
        String password       = getText(etPassword);
        String confirmPassword= getText(etConfirmPassword);

        boolean valid = true;
        if (TextUtils.isEmpty(fullName)) { tilFullName.setError("Nhập họ tên"); valid = false; } else tilFullName.setError(null);
        if (TextUtils.isEmpty(phone)) { tilPhone.setError("Nhập số điện thoại"); valid = false; } else tilPhone.setError(null);
        if (TextUtils.isEmpty(password) || password.length() < 6) { tilPassword.setError("Mật khẩu ít nhất 6 ký tự"); valid = false; } else tilPassword.setError(null);
        if (!password.equals(confirmPassword)) { tilConfirmPassword.setError("Mật khẩu không khớp"); valid = false; } else tilConfirmPassword.setError(null);
        if (!cbTerms.isChecked()) { Toast.makeText(this, "Vui lòng đồng ý với điều khoản", Toast.LENGTH_SHORT).show(); valid = false; }
        if (!valid) return;

        setLoading(true);

        ApiClient.getApiService().registerUser(new RegisterRequest(fullName, phone, password))
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        setLoading(false);
                        if (response.isSuccessful() && response.body() != null) {
                            LoginResponse body = response.body();
                            SessionManager.saveSession(body.getToken(), body.getUserId(),
                                    body.getRole(), body.getFullName(), body.getPhone());
                            ApiClient.setAuthToken(body.getToken());
                            Intent intent = new Intent(UserRegistrationActivity.this, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(UserRegistrationActivity.this,
                                    "Đăng ký thất bại: " + response.code(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        setLoading(false);
                        Toast.makeText(UserRegistrationActivity.this,
                                "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private String getText(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    private void setLoading(boolean loading) {
        btnRegister.setEnabled(!loading);
        btnRegister.setText(loading ? "Đang đăng ký..." : "Đăng ký");
    }
}
