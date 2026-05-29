package com.nutritionist.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            SessionManager session = new SessionManager(SplashActivity.this);
            if (session.isLoggedIn()) {
                if (session.isAdmin()) {
                    startActivity(new Intent(this, AdminDashboardActivity.class));
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                }
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish();
        }, SPLASH_DELAY);
    }
}
