package com.nutritionist.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.text.DecimalFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private TextView tvGreeting, tvUserName, tvBmiValue, tvBmiStatus, tvCalories, tvHealthTip;
    private TextInputEditText etSearchFood;
    private BottomNavigationView bottomNavigation;
    private SessionManager session;
    private DatabaseHelper db;
    private User currentUser;

    private final String[] healthTips = {
        "Eat 5 servings of fruits and vegetables daily for optimal nutrition.",
        "Drink at least 8 glasses of water every day to stay hydrated.",
        "Include lean proteins like chicken, fish, or legumes in every meal.",
        "Avoid skipping breakfast – it kick-starts your metabolism!",
        "Limit processed sugar intake to keep your energy levels stable.",
        "Whole grains like oats and brown rice provide lasting energy.",
        "Eat slowly and mindfully to avoid overeating.",
        "A handful of nuts daily provides healthy fats and protein.",
        "Colorful vegetables contain different vitamins – eat the rainbow!",
        "Fiber-rich foods keep your gut healthy and you full longer."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(this);
        db = new DatabaseHelper(this);

        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
// NEW — block admin from user screen
        if (session.isAdmin()) {
            startActivity(new Intent(this, AdminDashboardActivity.class));
            finish();
            return;
        }
        currentUser = db.getUserById(session.getUserId());
        initViews();
        loadUserData();
        setupBottomNav();
        setupSearchAndChips();
        setDailyTip();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh user data on return
        if (currentUser != null) {
            currentUser = db.getUserById(session.getUserId());
            loadUserData();
        }
    }

    private void initViews() {
        tvGreeting = findViewById(R.id.tvGreeting);
        tvUserName = findViewById(R.id.tvUserName);
        tvBmiValue = findViewById(R.id.tvBmiValue);
        tvBmiStatus = findViewById(R.id.tvBmiStatus);
        tvCalories = findViewById(R.id.tvCalories);
        tvHealthTip = findViewById(R.id.tvHealthTip);
        etSearchFood = findViewById(R.id.etSearchFood);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Dashboard cards
        findViewById(R.id.cardFoodDetails).setOnClickListener(v ->
                startActivity(new Intent(this, FoodDetailActivity.class)));
        findViewById(R.id.cardDietPlan).setOnClickListener(v ->
                startActivity(new Intent(this, DietPlanActivity.class)));
        findViewById(R.id.cardRecipes).setOnClickListener(v ->
                startActivity(new Intent(this, RecipesActivity.class)));
        findViewById(R.id.cardProfile).setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
        findViewById(R.id.ivProfileTop).setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }

    private void loadUserData() {
        if (currentUser == null) return;
        tvUserName.setText(currentUser.getFullName());

        // Greeting by time
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if (hour < 12) tvGreeting.setText("Good Morning! 🌅");
        else if (hour < 17) tvGreeting.setText("Good Afternoon! ☀️");
        else tvGreeting.setText("Good Evening! 🌙");

        // BMI
        double bmi = currentUser.calculateBMI();
        if (bmi > 0) {
            DecimalFormat df = new DecimalFormat("#.#");
            tvBmiValue.setText(df.format(bmi));
            tvBmiStatus.setText(currentUser.getBMIStatus());
        }

        // Calories
        int calories = currentUser.getDailyCalories();
        if (calories > 0) {
            tvCalories.setText(calories + " kcal");
        }
    }

    private void setupBottomNav() {
        bottomNavigation.setSelectedItemId(R.id.nav_home);
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_food) {
                startActivity(new Intent(this, FoodDetailActivity.class));
                return true;
            } else if (itemId == R.id.nav_diet) {
                startActivity(new Intent(this, DietPlanActivity.class));
                return true;
            } else if (itemId == R.id.nav_recipes) {
                startActivity(new Intent(this, RecipesActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    private void setupSearchAndChips() {
        // Search action
        etSearchFood.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String query = etSearchFood.getText().toString().trim();
                if (!query.isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, FoodDetailActivity.class);
                    intent.putExtra("query", query);
                    startActivity(intent);
                }
                return true;
            }
            return false;
        });

        // TextInputLayout end icon (search button)
        TextInputLayout tilSearch = findViewById(R.id.tilSearch);
        if (tilSearch != null) {
            tilSearch.setEndIconOnClickListener(v -> {
                String query = etSearchFood.getText().toString().trim();
                if (!query.isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, FoodDetailActivity.class);
                    intent.putExtra("query", query);
                    startActivity(intent);
                }
            });
        }

        // Chips quick search
        ((Chip) findViewById(R.id.chipFruits)).setOnClickListener(v -> openSearch("apple"));
        ((Chip) findViewById(R.id.chipVeggies)).setOnClickListener(v -> openSearch("broccoli"));
        ((Chip) findViewById(R.id.chipGrains)).setOnClickListener(v -> openSearch("rice"));
        ((Chip) findViewById(R.id.chipProtein)).setOnClickListener(v -> openSearch("chicken"));
    }

    private void openSearch(String query) {
        Intent intent = new Intent(MainActivity.this, FoodDetailActivity.class);
        intent.putExtra("query", query);
        startActivity(intent);
    }

    private void setDailyTip() {
        int day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        String tip = healthTips[day % healthTips.length];
        tvHealthTip.setText(tip);
    }
}
