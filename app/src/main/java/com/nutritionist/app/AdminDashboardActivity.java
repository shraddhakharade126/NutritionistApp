package com.nutritionist.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class AdminDashboardActivity extends AppCompatActivity {

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        session = new SessionManager(this);

        if (!session.isLoggedIn() || !session.isAdmin()) {
            startActivity(new Intent(this, LoginActivity.class));
            finishAffinity();
            return;
        }

        DatabaseHelper db = new DatabaseHelper(this);
        User admin = db.getUserById(session.getUserId());
        TextView tvAdminName = findViewById(R.id.tvAdminName);
        if (tvAdminName != null && admin != null) {
            tvAdminName.setText("Welcome, " + admin.getFullName());
        }

        // Add Recipe
        findViewById(R.id.cardAddRecipe).setOnClickListener(v -> {
            Intent intent = new Intent(this, AddRecipeActivity.class);
            intent.putExtra("isAdminTask", true);
            startActivity(intent);
        });

        // Manage Users
        findViewById(R.id.cardManageUsers).setOnClickListener(v ->
                startActivity(new Intent(this, ManageUsersActivity.class)));

        // ── NEW: View/Edit/Delete Recipes ─────────────────────────
        findViewById(R.id.cardViewRecipes).setOnClickListener(v ->
                startActivity(new Intent(this, AdminRecipesActivity.class)));

        // Logout
        findViewById(R.id.btnLogout).setOnClickListener(v ->
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Logout", (dialog, which) -> {
                            session.logout();
                            startActivity(new Intent(this, LoginActivity.class));
                            finishAffinity();
                        })
                        .setNegativeButton("Cancel", null)
                        .show());
    }

    @Override
    public void onBackPressed() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Logout")
                .setMessage("Do you want to logout?")
                .setPositiveButton("Logout", (d, w) -> {
                    session.logout();
                    startActivity(new Intent(this, LoginActivity.class));
                    finishAffinity();
                })
                .setNegativeButton("Stay", null)
                .show();
    }
}
