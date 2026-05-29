package com.nutritionist.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.text.DecimalFormat;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvProfileName, tvProfileEmail, tvProfileAge, tvProfileWeight, tvProfileHeight, tvProfileBmi;
    private TextInputEditText etEditName, etEditWeight, etEditHeight;
    private DatabaseHelper db;
    private SessionManager session;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        db = new DatabaseHelper(this);
        session = new SessionManager(this);
        currentUser = db.getUserById(session.getUserId());
        initViews();
        loadProfile();
    }

    private void initViews() {
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvProfileAge = findViewById(R.id.tvProfileAge);
        tvProfileWeight = findViewById(R.id.tvProfileWeight);
        tvProfileHeight = findViewById(R.id.tvProfileHeight);
        tvProfileBmi = findViewById(R.id.tvProfileBmi);
        etEditName = findViewById(R.id.etEditName);
        etEditWeight = findViewById(R.id.etEditWeight);
        etEditHeight = findViewById(R.id.etEditHeight);
        findViewById(R.id.ivBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnUpdateProfile).setOnClickListener(v -> updateProfile());
        findViewById(R.id.btnLogout).setOnClickListener(v -> logout());
    }

    private void loadProfile() {
        if (currentUser == null) return;
        tvProfileName.setText(currentUser.getFullName());
        tvProfileEmail.setText(currentUser.getEmail());
        tvProfileAge.setText(String.valueOf(currentUser.getAge()));
        tvProfileWeight.setText(String.valueOf(currentUser.getWeight()));
        tvProfileHeight.setText(String.valueOf(currentUser.getHeight()));
        DecimalFormat df = new DecimalFormat("#.#");
        double bmi = currentUser.calculateBMI();
        tvProfileBmi.setText(bmi > 0 ? df.format(bmi) : "--");
        etEditName.setText(currentUser.getFullName());
        etEditWeight.setText(String.valueOf(currentUser.getWeight()));
        etEditHeight.setText(String.valueOf(currentUser.getHeight()));
    }

    private void updateProfile() {
        String name = etEditName.getText().toString().trim();
        String weightStr = etEditWeight.getText().toString().trim();
        String heightStr = etEditHeight.getText().toString().trim();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(weightStr) || TextUtils.isEmpty(heightStr)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        double weight = Double.parseDouble(weightStr);
        double height = Double.parseDouble(heightStr);
        boolean updated = db.updateUser(session.getUserId(), name, weight, height);
        if (updated) {
            currentUser = db.getUserById(session.getUserId());
            loadProfile();
            Toast.makeText(this, "Profile updated! ✅", Toast.LENGTH_SHORT).show();
        }
    }

    private void logout() {
        session.logout();
        startActivity(new Intent(this, LoginActivity.class));
        finishAffinity();
    }
}
