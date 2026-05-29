package com.nutritionist.app;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodDetailActivity extends AppCompatActivity {

    private RecyclerView rvFoods;
    private TextInputEditText etFoodQuery;
    private TextView tvFoodCount;

    private FoodCardAdapter adapter;
    private List<FoodData> allFoods = new ArrayList<>();
    private List<FoodData> filtered = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        rvFoods    = findViewById(R.id.rvFoods);
        etFoodQuery = findViewById(R.id.etFoodQuery);
        tvFoodCount = findViewById(R.id.tvFoodCount);

        findViewById(R.id.ivBack).setOnClickListener(v -> finish());

        buildFoodList();

        // 2-column grid
        rvFoods.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new FoodCardAdapter(filtered, this::showFoodDetail);
        rvFoods.setAdapter(adapter);

        // Live search filter
        etFoodQuery.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            public void afterTextChanged(Editable s) {}
            public void onTextChanged(CharSequence s, int st, int b, int c) {
                filterFoods(s.toString().trim());
            }
        });

        // Handle incoming query from MainActivity chips
        String incomingQuery = getIntent().getStringExtra("query");
        if (incomingQuery != null && !incomingQuery.isEmpty()) {
            etFoodQuery.setText(incomingQuery);
            filterFoods(incomingQuery);
        } else {
            updateCount();
        }
    }

    private void filterFoods(String query) {
        filtered.clear();

        if (query.isEmpty()) {
            filtered.addAll(allFoods);
        } else {
            String lower = query.toLowerCase();

            for (FoodData f : allFoods) {
                if (f.name.toLowerCase().contains(lower)
                        || f.category.toLowerCase().contains(lower)) {
                    filtered.add(f);
                }
            }
        }

        adapter.notifyDataSetChanged();
        updateCount();
        if (filtered.isEmpty()) {
            tvFoodCount.setText("No food found in database yet");

            Toast.makeText(this, "No food found for your search", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateCount() {
        tvFoodCount.setText(filtered.size() + " foods");
    }

    // ── Show full detail in dialog when card clicked
    private void showFoodDetail(FoodData food) {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_food_detail, null);

        ((TextView) v.findViewById(R.id.tvDetailEmoji)).setText(food.emoji);
        ((TextView) v.findViewById(R.id.tvDetailName)).setText(food.name);
        ((TextView) v.findViewById(R.id.tvDetailCategory)).setText(food.category);
        ((TextView) v.findViewById(R.id.tvDetailCalories)).setText(food.calories + " kcal");
        ((TextView) v.findViewById(R.id.tvDetailCarbs)).setText(food.carbs + "g");
        ((TextView) v.findViewById(R.id.tvDetailProtein)).setText(food.protein + "g");
        ((TextView) v.findViewById(R.id.tvDetailFat)).setText(food.fat + "g");
        ((TextView) v.findViewById(R.id.tvDetailFiber)).setText(food.fiber + "g");
        ((TextView) v.findViewById(R.id.tvDetailSugar)).setText(food.sugar + "g");
        ((TextView) v.findViewById(R.id.tvDetailSodium)).setText(food.sodium + "mg");
        ((TextView) v.findViewById(R.id.tvDetailBenefits)).setText(food.benefits);

        new AlertDialog.Builder(this)
                .setView(v)
                .setPositiveButton("Close", null)
                .show();
    }

    // ── Inner RecyclerView Adapter
    static class FoodCardAdapter extends RecyclerView.Adapter<FoodCardAdapter.VH> {

        interface OnFoodClick { void onClick(FoodData food); }

        private final List<FoodData> list;
        private final OnFoodClick listener;

        FoodCardAdapter(List<FoodData> list, OnFoodClick listener) {
            this.list = list;
            this.listener = listener;
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_food_card, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH h, int pos) {
            FoodData f = list.get(pos);
            h.tvEmoji.setText(f.emoji);
            h.tvName.setText(f.name);
            h.tvCategory.setText(f.category);
            h.tvCalories.setText(f.calories + " kcal");
            h.tvCarbs.setText("C " + f.carbs + "g");
            h.tvProtein.setText("P " + f.protein + "g");
            h.tvFat.setText("F " + f.fat + "g");
            h.itemView.setOnClickListener(v -> listener.onClick(f));
        }

        @Override
        public int getItemCount() { return list.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvEmoji, tvName, tvCategory, tvCalories, tvCarbs, tvProtein, tvFat;
            VH(View v) {
                super(v);
                tvEmoji    = v.findViewById(R.id.tvFoodEmoji);
                tvName     = v.findViewById(R.id.tvFoodCardName);
                tvCategory = v.findViewById(R.id.tvFoodCardCategory);
                tvCalories = v.findViewById(R.id.tvFoodCardCalories);
                tvCarbs    = v.findViewById(R.id.tvFoodCardCarbs);
                tvProtein  = v.findViewById(R.id.tvFoodCardProtein);
                tvFat      = v.findViewById(R.id.tvFoodCardFat);
            }
        }
    }

    // ── Food data model ───────────────────────────────────────────
    static class FoodData {
        String name, category, benefits, emoji;
        int calories, sodium;
        double carbs, protein, fat, fiber, sugar;

        FoodData(String emoji, String name, String category, int calories,
                 double carbs, double protein, double fat,
                 double fiber, double sugar, int sodium, String benefits) {
            this.emoji = emoji; this.name = name; this.category = category;
            this.calories = calories; this.carbs = carbs; this.protein = protein;
            this.fat = fat; this.fiber = fiber; this.sugar = sugar;
            this.sodium = sodium; this.benefits = benefits;
        }
    }

    // ── All 50 foods with emojis ──────────────────────────────────
    private void buildFoodList() {
        allFoods.add(new FoodData("🍎","Apple","Fruit",52,14,0.3,0.2,2.4,10,1,"Rich in antioxidants and dietary fiber. Supports heart health, helps regulate blood sugar, and promotes gut health. Contains Vitamin C and potassium."));
        allFoods.add(new FoodData("🍌","Banana","Fruit",89,23,1.1,0.3,2.6,12,1,"Excellent source of potassium supporting heart health and blood pressure. Provides quick energy and contains Vitamin B6 and magnesium."));
        allFoods.add(new FoodData("🥭","Mango","Fruit",60,15,0.8,0.4,1.6,14,1,"High in Vitamin C and A, boosts immunity. Contains digestive enzymes and supports eye health with beta-carotene."));
        allFoods.add(new FoodData("🍊","Orange","Fruit",47,12,0.9,0.1,2.4,9,0,"Packed with Vitamin C boosting immunity. Supports skin health, reduces inflammation, and provides flavonoids for heart protection."));
        allFoods.add(new FoodData("🍇","Grapes","Fruit",67,17,0.6,0.4,0.9,16,2,"Rich in resveratrol, a powerful antioxidant. Supports heart health and reduces inflammation."));
        allFoods.add(new FoodData("🍉","Watermelon","Fruit",30,8,0.6,0.2,0.4,6,1,"92% water — excellent for hydration. Contains lycopene for heart and skin health. Low calorie snack."));
        allFoods.add(new FoodData("🍓","Strawberry","Fruit",32,8,0.7,0.3,2.0,5,1,"Extremely rich in Vitamin C. Provides powerful antioxidants and supports brain health and heart health."));
        allFoods.add(new FoodData("🍑","Peach","Fruit",39,10,0.9,0.3,1.5,8,0,"Good source of Vitamin C and A. Supports skin health, digestion, and contains antioxidants."));
        allFoods.add(new FoodData("🍍","Pineapple","Fruit",50,13,0.5,0.1,1.4,10,1,"Contains bromelain enzyme that aids digestion. Rich in Vitamin C and manganese. Anti-inflammatory properties."));
        allFoods.add(new FoodData("🥥","Coconut","Fruit",354,15,3.3,33,9,6,20,"Rich in healthy medium-chain fatty acids. Provides quick energy and supports brain function and metabolism."));
        allFoods.add(new FoodData("🥦","Broccoli","Vegetable",34,7,2.8,0.4,2.6,1.7,33,"Superfood rich in Vitamin K, C and folate. Contains sulforaphane with cancer-fighting properties."));
        allFoods.add(new FoodData("🥬","Spinach","Vegetable",23,3.6,2.9,0.4,2.2,0.4,79,"Loaded with iron, calcium and Vitamin K. Supports bone density and boosts energy levels."));
        allFoods.add(new FoodData("🥕","Carrot","Vegetable",41,10,0.9,0.2,2.8,4.7,69,"Extremely rich in beta-carotene (Vitamin A). Promotes excellent eye health and boosts immunity."));
        allFoods.add(new FoodData("🍅","Tomato","Vegetable",18,3.9,0.9,0.2,1.2,2.6,5,"Rich in lycopene — a powerful antioxidant. Supports heart health and contains Vitamins C and K."));
        allFoods.add(new FoodData("🥔","Potato","Vegetable",77,17,2.0,0.1,2.2,0.8,6,"Good source of Vitamin C, B6 and potassium. Provides sustained energy from complex carbs."));
        allFoods.add(new FoodData("🧅","Onion","Vegetable",40,9,1.1,0.1,1.7,4.2,4,"Rich in quercetin, an anti-inflammatory antioxidant. Supports heart health and boosts immunity."));
        allFoods.add(new FoodData("🥒","Cucumber","Vegetable",16,3.6,0.7,0.1,0.5,1.7,2,"Extremely hydrating — 96% water. Contains Vitamin K and antioxidants. Supports skin health."));
        allFoods.add(new FoodData("🌽","Corn","Vegetable",86,19,3.2,1.2,2.7,3.2,15,"Rich in fiber and antioxidants like lutein. Provides B vitamins and energy. Supports eye health."));
        allFoods.add(new FoodData("🫛","Peas","Vegetable",81,14,5,0.4,5.1,5.7,5,"Excellent plant protein source. Rich in Vitamin K, C and fiber. Supports heart health and digestion."));
        allFoods.add(new FoodData("🍆","Eggplant","Vegetable",25,6,1,0.2,3,3.5,2,"Rich in nasunin, a brain-protective antioxidant. Good source of fiber and supports heart health."));
        allFoods.add(new FoodData("🍚","White Rice","Grain",130,28,2.7,0.3,0.4,0,1,"Primary source of quick energy. Easy to digest and gluten-free. Provides manganese and selenium."));
        allFoods.add(new FoodData("🌾","Oats","Grain",389,66,17,7,10.6,0,2,"Rich in beta-glucan fiber that lowers cholesterol. Provides slow-release energy and supports gut health."));
        allFoods.add(new FoodData("🫓","Whole Wheat","Grain",340,72,13,2.5,10.7,0.4,2,"Rich in complex carbohydrates and fiber. Provides sustained energy and supports digestive health."));
        allFoods.add(new FoodData("🌽","Corn Flour","Grain",361,76,7,3.9,7.3,0.6,5,"Gluten-free grain flour. Good source of carbohydrates and fiber. Provides B vitamins and minerals."));
        allFoods.add(new FoodData("🍗","Chicken Breast","Protein",165,0,31,3.6,0,0,74,"Excellent lean protein source for muscle building. Rich in niacin supporting metabolism and phosphorus for bones."));
        allFoods.add(new FoodData("🥚","Egg","Protein",155,1.1,13,11,0,1.1,124,"Complete protein with all essential amino acids. Rich in choline for brain health and Vitamin D."));
        allFoods.add(new FoodData("🐟","Salmon","Fish",208,0,20,13,0,0,59,"Rich in Omega-3 fatty acids EPA and DHA. Supports heart and brain health, reduces inflammation."));
        allFoods.add(new FoodData("🍖","Mutton","Protein",258,0,25,17,0,0,72,"Rich source of protein and iron. Contains zinc, B12 and selenium important for immune function."));
        allFoods.add(new FoodData("🦐","Shrimp","Fish",99,0.9,24,0.3,0,0,111,"Very high protein, low fat seafood. Excellent source of iodine, selenium and Vitamin B12."));
        allFoods.add(new FoodData("🫘","Lentils","Legume",116,20,9,0.4,7.9,1.8,2,"High in plant protein and fiber. Supports heart health, regulates blood sugar and rich in folate and iron."));
        allFoods.add(new FoodData("🫘","Chickpeas","Legume",164,27,9,2.6,7.6,4.8,7,"Excellent source of plant protein and fiber. Supports blood sugar control, heart health and digestion."));
        allFoods.add(new FoodData("🫘","Black Beans","Legume",132,24,8.9,0.5,8.7,0.3,1,"Rich in protein, fiber and antioxidants. Supports heart health, stabilizes blood sugar and gut health."));
        allFoods.add(new FoodData("🥛","Milk","Dairy",61,4.8,3.2,3.3,0,5.1,43,"Excellent source of calcium and Vitamin D for strong bones. Provides complete protein and B vitamins."));
        allFoods.add(new FoodData("🧀","Cheese","Dairy",402,1.3,25,33,0,0.5,621,"High in calcium and protein. Contains Vitamin B12, zinc and phosphorus. Supports bone and muscle health."));
        allFoods.add(new FoodData("🥛","Greek Yogurt","Dairy",59,3.6,10,0.4,0,3.2,36,"Probiotic powerhouse supporting gut health. High in protein, calcium and B12. Boosts immunity."));
        allFoods.add(new FoodData("🥑","Avocado","Fruit",160,9,2,15,7,0.7,7,"Rich in heart-healthy monounsaturated fats. Packed with potassium, folate and Vitamins K, C, E."));
        allFoods.add(new FoodData("🥜","Almonds","Nuts",579,22,21,50,12.5,4.4,1,"Heart-healthy fats, high in Vitamin E and magnesium. Supports brain health and reduces bad cholesterol."));
        allFoods.add(new FoodData("🥜","Walnuts","Nuts",654,14,15,65,6.7,2.6,2,"Highest plant source of Omega-3 fatty acids. Supports brain health, reduces inflammation and heart disease risk."));
        allFoods.add(new FoodData("🥜","Peanuts","Nuts",567,16,26,49,8.5,4,18,"High in protein and healthy fats. Rich in niacin, folate and resveratrol. Supports heart health."));
        allFoods.add(new FoodData("🍯","Honey","Other",304,82,0.3,0,0.2,82,4,"Natural sweetener with antibacterial properties. Contains antioxidants, trace minerals and enzymes. Soothes sore throats."));
        allFoods.add(new FoodData("🫒","Olive Oil","Other",884,0,0,100,0,0,2,"Rich in heart-healthy oleic acid. Contains powerful antioxidants oleuropein and hydroxytyrosol. Reduces inflammation."));
        allFoods.add(new FoodData("🧄","Garlic","Vegetable",149,33,6.4,0.5,2.1,1,17,"Contains allicin with powerful antibacterial properties. Boosts immunity, reduces blood pressure and cholesterol."));
        allFoods.add(new FoodData("🫚","Coconut Oil","Other",862,0,0,100,0,0,0,"Rich in medium-chain triglycerides (MCTs). Boosts metabolism and provides quick energy for the brain."));
        allFoods.add(new FoodData("🍵","Green Tea","Drink",1,0,0.2,0,0,0,1,"Rich in EGCG catechins — powerful antioxidants. Boosts metabolism, reduces inflammation and supports brain function."));
        allFoods.add(new FoodData("☕","Coffee","Drink",2,0,0.3,0,0,0,2,"Rich in antioxidants. Boosts alertness and metabolism. Associated with reduced risk of Parkinson's and diabetes."));
        allFoods.add(new FoodData("🍠","Sweet Potato","Vegetable",86,20,1.6,0.1,3,4.2,55,"Extremely rich in beta-carotene. Contains Vitamin C, B6 and potassium. Regulates blood sugar."));
        allFoods.add(new FoodData("🫐","Blueberries","Fruit",57,14,0.7,0.3,2.4,10,1,"Among the highest antioxidant foods. Improves brain function, reduces DNA damage and supports heart health."));
        allFoods.add(new FoodData("🥗","Dal (Lentil Soup)","Indian",116,20,9,0.4,7.9,1,400,"Staple Indian protein source. High in iron and folate. Supports digestion and provides sustained energy."));
        allFoods.add(new FoodData("🫓","Roti","Indian",104,21,3,1,2.7,0,190,"Whole wheat flatbread. Good source of complex carbohydrates, fiber and B vitamins. Low glycemic index."));
        allFoods.add(new FoodData("🍛","Biryani","Indian",290,40,12,10,2,1,680,"Aromatic rice dish with protein. Provides carbohydrates, protein and spices with anti-inflammatory properties."));

        filtered.addAll(allFoods);
    }
}
