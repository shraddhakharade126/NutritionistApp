package com.nutritionist.app;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddRecipeActivity extends AppCompatActivity {

    private TextInputEditText etRecipeTitle, etPrepTime, etCaloriesRecipe, etIngredients, etInstructions;
    private AutoCompleteTextView actvCategory;
    private MaterialButton btnSaveRecipe;
    private DatabaseHelper db;
    private SessionManager session;
    private boolean isAdminTask = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        db = new DatabaseHelper(this);
        session = new SessionManager(this);
        isAdminTask = getIntent().getBooleanExtra("isAdminTask", false);

        initViews();
        setupCategoryDropdown();
        setupClickListeners();

        if (isAdminTask) {
            ((TextView) findViewById(R.id.tvTitle)).setText("Add System Recipe");
        }
    }

    private void initViews() {
        etRecipeTitle = findViewById(R.id.etRecipeTitle);
        etPrepTime = findViewById(R.id.etPrepTime);
        etCaloriesRecipe = findViewById(R.id.etCaloriesRecipe);
        etIngredients = findViewById(R.id.etIngredients);
        etInstructions = findViewById(R.id.etInstructions);
        actvCategory = findViewById(R.id.actvCategory);
        btnSaveRecipe = findViewById(R.id.btnSaveRecipe);
        findViewById(R.id.ivBack).setOnClickListener(v -> finish());
    }

    private void setupCategoryDropdown() {
        String[] categories = {
            "Breakfast", "Lunch", "Dinner", "Snack",
            "Smoothie", "Salad", "Soup", "Dessert",
            "Vegan", "High Protein", "Low Calorie", "Other"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, categories);
        actvCategory.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnSaveRecipe.setOnClickListener(v -> saveRecipe());
    }

    private void saveRecipe() {
        String title = etRecipeTitle.getText().toString().trim();
        String category = actvCategory.getText().toString().trim();
        String prepTimeStr = etPrepTime.getText().toString().trim();
        String calStr = etCaloriesRecipe.getText().toString().trim();
        String ingredients = etIngredients.getText().toString().trim();
        String instructions = etInstructions.getText().toString().trim();

        if (TextUtils.isEmpty(title)) { etRecipeTitle.setError("Enter recipe title"); return; }
        if (TextUtils.isEmpty(category)) { actvCategory.setError("Select category"); return; }
        if (TextUtils.isEmpty(ingredients)) { etIngredients.setError("Enter ingredients"); return; }
        if (TextUtils.isEmpty(instructions)) { etInstructions.setError("Enter instructions"); return; }

        int prepTime = TextUtils.isEmpty(prepTimeStr) ? 0 : Integer.parseInt(prepTimeStr);
        int calories = TextUtils.isEmpty(calStr) ? 0 : Integer.parseInt(calStr);

        // If admin, isMy = 0 (system recipe), else isMy = 1 (user recipe)
        int isMy = isAdminTask ? 0 : 1;
        long id = db.addRecipe(session.getUserId(), title, category, prepTime, calories,
                ingredients, instructions, isMy);

        if (id > 0) {
            Toast.makeText(this, (isAdminTask ? "System recipe" : "Recipe") + " saved! 👨‍🍳", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save recipe", Toast.LENGTH_SHORT).show();
        }
    }
}
