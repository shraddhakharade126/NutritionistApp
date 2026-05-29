package com.nutritionist.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import android.widget.AutoCompleteTextView;
import java.util.List;

public class AdminRecipesActivity extends AppCompatActivity
        implements AdminRecipeAdapter.AdminRecipeListener {

    private RecyclerView rvAdminRecipes;
    private View layoutEmpty;
    private TextView tvRecipeCount;
    private DatabaseHelper db;
    private AdminRecipeAdapter adapter;
    private List<Recipe> recipeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_recipes);

        db = new DatabaseHelper(this);

        rvAdminRecipes = findViewById(R.id.rvAdminRecipes);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        tvRecipeCount = findViewById(R.id.tvRecipeCount);

        findViewById(R.id.ivBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnAddNew).setOnClickListener(v -> {
            Intent intent = new Intent(this, AddRecipeActivity.class);
            intent.putExtra("isAdminTask", true);
            startActivity(intent);
        });

        rvAdminRecipes.setLayoutManager(new LinearLayoutManager(this));
        loadRecipes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecipes();
    }

    private void loadRecipes() {
        recipeList = db.getAdminRecipes();
        tvRecipeCount.setText(recipeList.size() + " recipes");

        if (recipeList.isEmpty()) {
            rvAdminRecipes.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvAdminRecipes.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
            adapter = new AdminRecipeAdapter(recipeList, this);
            rvAdminRecipes.setAdapter(adapter);
        }
    }

    // ── Called by adapter when Edit tapped ────────────────────────
    @Override
    public void onEdit(Recipe recipe, int position) {
        showEditDialog(recipe, position);
    }

    // ── Called by adapter when Delete tapped ──────────────────────
    @Override
    public void onDelete(Recipe recipe, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Recipe")
                .setMessage("Delete \"" + recipe.getTitle() + "\"? This cannot be undone.")
                .setPositiveButton("Delete", (d, w) -> {
                    boolean deleted = db.deleteRecipe(recipe.getId());
                    if (deleted) {
                        adapter.removeItem(position);
                        tvRecipeCount.setText(recipeList.size() + " recipes");
                        if (recipeList.isEmpty()) {
                            rvAdminRecipes.setVisibility(View.GONE);
                            layoutEmpty.setVisibility(View.VISIBLE);
                        }
                        Toast.makeText(this, "Recipe deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ── Edit dialog with pre-filled fields ────────────────────────
    private void showEditDialog(Recipe recipe, int position) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_recipe, null);

        TextInputEditText etTitle = dialogView.findViewById(R.id.etEditTitle);
        AutoCompleteTextView actvCategory = dialogView.findViewById(R.id.actvEditCategory);
        TextInputEditText etPrepTime = dialogView.findViewById(R.id.etEditPrepTime);
        TextInputEditText etCalories = dialogView.findViewById(R.id.etEditCalories);
        TextInputEditText etIngredients = dialogView.findViewById(R.id.etEditIngredients);
        TextInputEditText etInstructions = dialogView.findViewById(R.id.etEditInstructions);

        // Pre-fill current values
        etTitle.setText(recipe.getTitle());
        actvCategory.setText(recipe.getCategory());
        etPrepTime.setText(String.valueOf(recipe.getPrepTime()));
        etCalories.setText(String.valueOf(recipe.getCalories()));
        etIngredients.setText(recipe.getIngredients());
        etInstructions.setText(recipe.getInstructions());

        // Category dropdown
        String[] categories = {"Breakfast", "Lunch", "Dinner", "Snack",
                "Smoothie", "Salad", "Soup", "Dessert",
                "Vegan", "High Protein", "Low Calorie", "Other"};
        actvCategory.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, categories));

        new AlertDialog.Builder(this)
                .setTitle("✏️ Edit Recipe")
                .setView(dialogView)
                .setPositiveButton("Save", (d, w) -> {
                    String title = etTitle.getText().toString().trim();
                    String category = actvCategory.getText().toString().trim();
                    String prepStr = etPrepTime.getText().toString().trim();
                    String calStr = etCalories.getText().toString().trim();
                    String ingredients = etIngredients.getText().toString().trim();
                    String instructions = etInstructions.getText().toString().trim();

                    if (title.isEmpty()) {
                        Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int prepTime = prepStr.isEmpty() ? 0 : Integer.parseInt(prepStr);
                    int calories = calStr.isEmpty() ? 0 : Integer.parseInt(calStr);

                    boolean updated = db.updateRecipe(recipe.getId(), title, category,
                            prepTime, calories, ingredients, instructions);

                    if (updated) {
                        // Update local object
                        recipe.setTitle(title);
                        recipe.setCategory(category);
                        recipe.setPrepTime(prepTime);
                        recipe.setCalories(calories);
                        recipe.setIngredients(ingredients);
                        recipe.setInstructions(instructions);
                        adapter.notifyItemChanged(position);
                        Toast.makeText(this, "Recipe updated ✅", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
