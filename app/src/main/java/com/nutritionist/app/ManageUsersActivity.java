package com.nutritionist.app;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.List;

public class ManageUsersActivity extends AppCompatActivity {

    private RecyclerView rvUsers;
    private UserAdapter adapter;
    private DatabaseHelper db;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        db = new DatabaseHelper(this);
        rvUsers = findViewById(R.id.rvUsers);
        findViewById(R.id.ivBack).setOnClickListener(v -> finish());

        loadUsers();
    }

    private void loadUsers() {
        userList = db.getAllUsers();
        adapter = new UserAdapter(userList, userId -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Delete User")
                    .setMessage("Are you sure you want to delete this user? This action cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        if (db.deleteUser(userId)) {
                            Toast.makeText(this, "User deleted", Toast.LENGTH_SHORT).show();
                            loadUsers();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(adapter);
    }
}
