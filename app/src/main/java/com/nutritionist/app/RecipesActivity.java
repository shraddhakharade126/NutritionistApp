package com.nutritionist.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;
import java.util.List;

public class RecipesActivity extends AppCompatActivity {

    private RecyclerView rvRecipes;
    private View layoutEmptyRecipes;
    private TabLayout tabLayout;
    private DatabaseHelper db;
    private SessionManager session;
    private RecipeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        db = new DatabaseHelper(this);
        session = new SessionManager(this);

        initViews();
        setupTabs();
        loadRecipes(true); // load all recipes by default
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecipes(tabLayout.getSelectedTabPosition() == 0);
    }

    private void initViews() {
        rvRecipes = findViewById(R.id.rvRecipes);
        layoutEmptyRecipes = findViewById(R.id.layoutEmptyRecipes);
        tabLayout = findViewById(R.id.tabLayoutRecipes);

        rvRecipes.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.ivBack).setOnClickListener(v -> finish());
        findViewById(R.id.ivAddRecipe).setOnClickListener(v ->
                startActivity(new Intent(this, AddRecipeActivity.class)));
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("All Recipes"));
        tabLayout.addTab(tabLayout.newTab().setText("My Recipes"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadRecipes(tab.getPosition() == 0);
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadRecipes(boolean all) {
        int userId = session.getUserId();
        List<Recipe> recipeList = all ? db.getAllRecipes(userId) : db.getMyRecipes(userId);

        adapter = new RecipeAdapter(recipeList, (recipe, position) -> {
            if (!recipe.isMyRecipe()) {
                Toast.makeText(this, "Cannot delete saved recipes", Toast.LENGTH_SHORT).show();
                return;
            }
            new AlertDialog.Builder(this)
                .setTitle("Delete Recipe")
                .setMessage("Delete \"" + recipe.getTitle() + "\"?")
                .setPositiveButton("Delete", (d, w) -> {
                    db.deleteRecipe(recipe.getId());
                    adapter.removeItem(position);
                    checkEmpty(recipeList);
                    Toast.makeText(this, "Recipe deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
        });

        rvRecipes.setAdapter(adapter);
        checkEmpty(recipeList);
    }

    private void checkEmpty(List<Recipe> list) {
        if (list.isEmpty()) {
            rvRecipes.setVisibility(View.GONE);
            layoutEmptyRecipes.setVisibility(View.VISIBLE);
        } else {
            rvRecipes.setVisibility(View.VISIBLE);
            layoutEmptyRecipes.setVisibility(View.GONE);
        }
    }
}
