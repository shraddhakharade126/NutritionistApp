package com.nutritionist.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etEmail, etAge, etWeight, etHeight, etPassword, etConfirmPassword;
    private TextInputLayout tilFullName, tilEmail, tilAge, tilWeight, tilHeight, tilPassword, tilConfirmPassword;
    private AutoCompleteTextView actvGender;
    private MaterialButton btnRegister;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new DatabaseHelper(this);
        initViews();
        setupGenderDropdown();
        setupClickListeners();
    }

    private void initViews() {
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etAge = findViewById(R.id.etAge);
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        actvGender = findViewById(R.id.actvGender);
        btnRegister = findViewById(R.id.btnRegister);
        tilFullName = findViewById(R.id.tilFullName);
        tilEmail = findViewById(R.id.tilEmail);
        tilAge = findViewById(R.id.tilAge);
        tilWeight = findViewById(R.id.tilWeight);
        tilHeight = findViewById(R.id.tilHeight);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
    }

    private void setupGenderDropdown() {
        String[] genders = {"Male", "Female", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, genders);
        actvGender.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> registerUser());
        findViewById(R.id.tvGoToLogin).setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String name = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();
        String gender = actvGender.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPwd = etConfirmPassword.getText().toString().trim();

        // Validate
        if (TextUtils.isEmpty(name)) { tilFullName.setError("Enter your name"); return; } else { tilFullName.setError(null); }
        if (TextUtils.isEmpty(email)) { tilEmail.setError("Enter email"); return; } else { tilEmail.setError(null); }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) { tilEmail.setError("Invalid email"); return; }
        if (TextUtils.isEmpty(ageStr)) { tilAge.setError("Enter age"); return; } else { tilAge.setError(null); }
        if (TextUtils.isEmpty(weightStr)) { tilWeight.setError("Enter weight"); return; } else { tilWeight.setError(null); }
        if (TextUtils.isEmpty(heightStr)) { tilHeight.setError("Enter height"); return; } else { tilHeight.setError(null); }
        if (TextUtils.isEmpty(password)) { tilPassword.setError("Enter password"); return; } else { tilPassword.setError(null); }
        if (password.length() < 6) { tilPassword.setError("Min 6 characters"); return; }
        if (!password.equals(confirmPwd)) { tilConfirmPassword.setError("Passwords don't match"); return; } else { tilConfirmPassword.setError(null); }

        if (db.isEmailExists(email)) {
            tilEmail.setError("Email already registered");
            return;
        }

        int age = Integer.parseInt(ageStr);
        double weight = Double.parseDouble(weightStr);
        double height = Double.parseDouble(heightStr);

        long userId = db.registerUser(name, email, password, age, weight, height, gender);
        if (userId > 0) {
            SessionManager session = new SessionManager(this);
            session.saveSession((int) userId, false); // Normal users are not admins
            Toast.makeText(this, "Welcome, " + name + "! 🌿", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Registration failed. Try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
