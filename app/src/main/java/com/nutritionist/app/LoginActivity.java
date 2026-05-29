package com.nutritionist.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etLoginEmail, etLoginPassword;
    private TextInputLayout tilLoginEmail, tilLoginPassword;
    private MaterialButton btnLogin;
    private DatabaseHelper db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DatabaseHelper(this);
        session = new SessionManager(this);

        if (session.isLoggedIn()) {
            navigate();
            return;
        }

        initViews();
        setupClickListeners();
    }

    private void navigate() {
        if (session.isAdmin()) {
            startActivity(new Intent(this, AdminDashboardActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }

    private void initViews() {
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        tilLoginEmail = findViewById(R.id.tilLoginEmail);
        tilLoginPassword = findViewById(R.id.tilLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> loginUser());
        findViewById(R.id.tvGoToRegister).setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void loginUser() {
        String email = etLoginEmail.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) { tilLoginEmail.setError("Enter email"); return; } else { tilLoginEmail.setError(null); }
        if (TextUtils.isEmpty(password)) { tilLoginPassword.setError("Enter password"); return; } else { tilLoginPassword.setError(null); }

        User user = db.loginUser(email, password);
        if (user != null) {
            session.saveSession(user.getId(), user.isAdmin());
            Toast.makeText(this, "Welcome " + (user.isAdmin() ? "Admin" : "") + "! 🌿", Toast.LENGTH_SHORT).show();
            navigate();
        } else {
            tilLoginPassword.setError("Invalid email or password");
        }
    }
}
