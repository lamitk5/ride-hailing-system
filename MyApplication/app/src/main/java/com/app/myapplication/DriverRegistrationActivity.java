package com.app.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.app.myapplication.api.ApiClient;
import com.app.myapplication.model.DriverRegisterRequest;
import com.app.myapplication.model.LoginResponse;
import com.app.myapplication.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverRegistrationActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etPhone, etPassword, etIdentityCard, etLicensePlate;
    private TextInputLayout tilFullName, tilPhone, tilPassword, tilIdentityCard, tilLicensePlate;
    private RadioGroup rgVehicleType;
    private MaterialButton btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_registration);

        etFullName     = findViewById(R.id.et_full_name);
        etPhone        = findViewById(R.id.et_phone);
        etPassword     = findViewById(R.id.et_password);
        etIdentityCard = findViewById(R.id.et_identity_card);
        etLicensePlate = findViewById(R.id.et_license_plate);
        tilFullName    = findViewById(R.id.til_full_name);
        tilPhone       = findViewById(R.id.til_phone);
        tilPassword    = findViewById(R.id.til_password);
        tilIdentityCard= findViewById(R.id.til_identity_card);
        tilLicensePlate= findViewById(R.id.til_license_plate);
        rgVehicleType  = findViewById(R.id.rg_vehicle_type);
        btnSubmit      = findViewById(R.id.btn_submit);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        btnSubmit.setOnClickListener(v -> attemptRegister());
    }

    private void attemptRegister() {
        String fullName     = getText(etFullName);
        String phone        = getText(etPhone);
        String identityCard = getText(etIdentityCard);
        String password     = getText(etPassword);
        String licensePlate = getText(etLicensePlate);

        boolean valid = true;
        if (TextUtils.isEmpty(fullName)) { tilFullName.setError("Nhập họ tên"); valid = false; } else tilFullName.setError(null);
        if (TextUtils.isEmpty(phone)) { tilPhone.setError("Nhập số điện thoại"); valid = false; } else tilPhone.setError(null);
        if (TextUtils.isEmpty(identityCard) || identityCard.length() < 9) { tilIdentityCard.setError("Nhập đúng số CCCD/CMND"); valid = false; } else tilIdentityCard.setError(null);
        if (TextUtils.isEmpty(password) || password.length() < 6) { tilPassword.setError("Mật khẩu ít nhất 6 ký tự"); valid = false; } else tilPassword.setError(null);
        if (TextUtils.isEmpty(licensePlate)) { tilLicensePlate.setError("Nhập biển số xe"); valid = false; } else tilLicensePlate.setError(null);
        if (rgVehicleType.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Vui lòng chọn loại xe", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if (!valid) return;

        // Map radio group selection to vehicleType string
        String vehicleType = "CAR";
        int checkedId = rgVehicleType.getCheckedRadioButtonId();
        if (checkedId == R.id.rb_bike) vehicleType = "BIKE";
        else if (checkedId == R.id.rb_plus) vehicleType = "PLUS";

        setLoading(true);
        final String finalVehicleType = vehicleType;

        ApiClient.getApiService()
                .registerDriver(new DriverRegisterRequest(fullName, phone, password,
                        finalVehicleType, licensePlate, identityCard, "Toyota Vios"))
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        setLoading(false);
                        if (response.isSuccessful() && response.body() != null) {
                            LoginResponse body = response.body();
                            SessionManager.saveSession(body.getToken(), body.getUserId(),
                                    body.getRole(), body.getFullName(), body.getPhone());
                            ApiClient.setAuthToken(body.getToken());
                            Intent intent = new Intent(DriverRegistrationActivity.this, DriverDashboardActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(DriverRegistrationActivity.this,
                                    "Đăng ký thất bại: " + response.code(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        setLoading(false);
                        Toast.makeText(DriverRegistrationActivity.this,
                                "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private String getText(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    private void setLoading(boolean loading) {
        btnSubmit.setEnabled(!loading);
        btnSubmit.setText(loading ? "Đang gửi..." : "Gửi đăng ký");
    }
}
