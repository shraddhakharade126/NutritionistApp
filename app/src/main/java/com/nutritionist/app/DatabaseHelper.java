package com.nutritionist.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "NutritionistDB";
    private static final int DB_VERSION = 2;

    // Table: Users
    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_NAME = "full_name";
    public static final String COL_USER_EMAIL = "email";
    public static final String COL_USER_PASSWORD = "password";
    public static final String COL_USER_AGE = "age";
    public static final String COL_USER_WEIGHT = "weight";
    public static final String COL_USER_HEIGHT = "height";
    public static final String COL_USER_GENDER = "gender";
    public static final String COL_USER_IS_ADMIN = "is_admin";

    // Table: Diet Plans
    public static final String TABLE_DIET = "diet_plan";
    public static final String COL_DIET_ID = "id";
    public static final String COL_DIET_USER_ID = "user_id";
    public static final String COL_DIET_MEAL_NAME = "meal_name";
    public static final String COL_DIET_MEAL_TIME = "meal_time";
    public static final String COL_DIET_CALORIES = "calories";
    public static final String COL_DIET_NOTES = "notes";
    public static final String COL_DIET_REMINDER = "reminder";

    // Table: Recipes
    public static final String TABLE_RECIPES = "recipes";
    public static final String COL_RECIPE_ID = "id";
    public static final String COL_RECIPE_USER_ID = "user_id";
    public static final String COL_RECIPE_TITLE = "title";
    public static final String COL_RECIPE_CATEGORY = "category";
    public static final String COL_RECIPE_PREP_TIME = "prep_time";
    public static final String COL_RECIPE_CALORIES = "calories";
    public static final String COL_RECIPE_INGREDIENTS = "ingredients";
    public static final String COL_RECIPE_INSTRUCTIONS = "instructions";
    public static final String COL_RECIPE_IS_MY = "is_my_recipe"; // 1=user created, 0=system/admin created

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsers = "CREATE TABLE " + TABLE_USERS + " ("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USER_NAME + " TEXT NOT NULL, "
                + COL_USER_EMAIL + " TEXT UNIQUE NOT NULL, "
                + COL_USER_PASSWORD + " TEXT NOT NULL, "
                + COL_USER_AGE + " INTEGER, "
                + COL_USER_WEIGHT + " REAL, "
                + COL_USER_HEIGHT + " REAL, "
                + COL_USER_GENDER + " TEXT, "
                + COL_USER_IS_ADMIN + " INTEGER DEFAULT 0)";

        String createDiet = "CREATE TABLE " + TABLE_DIET + " ("
                + COL_DIET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_DIET_USER_ID + " INTEGER, "
                + COL_DIET_MEAL_NAME + " TEXT, "
                + COL_DIET_MEAL_TIME + " TEXT, "
                + COL_DIET_CALORIES + " INTEGER, "
                + COL_DIET_NOTES + " TEXT, "
                + COL_DIET_REMINDER + " INTEGER DEFAULT 0, "
                + "FOREIGN KEY(" + COL_DIET_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "))";

        String createRecipes = "CREATE TABLE " + TABLE_RECIPES + " ("
                + COL_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_RECIPE_USER_ID + " INTEGER, "
                + COL_RECIPE_TITLE + " TEXT, "
                + COL_RECIPE_CATEGORY + " TEXT, "
                + COL_RECIPE_PREP_TIME + " INTEGER, "
                + COL_RECIPE_CALORIES + " INTEGER, "
                + COL_RECIPE_INGREDIENTS + " TEXT, "
                + COL_RECIPE_INSTRUCTIONS + " TEXT, "
                + COL_RECIPE_IS_MY + " INTEGER DEFAULT 1)";

        db.execSQL(createUsers);
        db.execSQL(createDiet);
        db.execSQL(createRecipes);

        // Insert a default admin
        ContentValues adminCv = new ContentValues();
        adminCv.put(COL_USER_NAME, "Admin");
        adminCv.put(COL_USER_EMAIL, "admin@nutritionist.com");
        adminCv.put(COL_USER_PASSWORD, "admin123");
        adminCv.put(COL_USER_IS_ADMIN, 1);
        db.insert(TABLE_USERS, null, adminCv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COL_USER_IS_ADMIN + " INTEGER DEFAULT 0");
            ContentValues adminCv = new ContentValues();
            adminCv.put(COL_USER_NAME, "Admin");
            adminCv.put(COL_USER_EMAIL, "admin@nutritionist.com");
            adminCv.put(COL_USER_PASSWORD, "admin123");
            adminCv.put(COL_USER_IS_ADMIN, 1);
            db.insert(TABLE_USERS, null, adminCv);
        }
    }

    // ========== USER OPERATIONS ==========

    public long registerUser(String name, String email, String password, int age, double weight, double height, String gender) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_USER_NAME, name);
        cv.put(COL_USER_EMAIL, email);
        cv.put(COL_USER_PASSWORD, password);
        cv.put(COL_USER_AGE, age);
        cv.put(COL_USER_WEIGHT, weight);
        cv.put(COL_USER_HEIGHT, height);
        cv.put(COL_USER_GENDER, gender);
        cv.put(COL_USER_IS_ADMIN, 0);
        long result = db.insert(TABLE_USERS, null, cv);
        db.close();
        return result;
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COL_USER_ID},
                COL_USER_EMAIL + "=?", new String[]{email}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public User loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
                COL_USER_EMAIL + "=? AND " + COL_USER_PASSWORD + "=?",
                new String[]{email, password}, null, null, null);
        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)));
            user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_EMAIL)));
            user.setAdmin(cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_IS_ADMIN)) == 1);
        }
        cursor.close();
        db.close();
        return user;
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
                COL_USER_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);
        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)));
            user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_EMAIL)));
            user.setAge(cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_AGE)));
            user.setWeight(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_USER_WEIGHT)));
            user.setHeight(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_USER_HEIGHT)));
            user.setGender(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_GENDER)));
            user.setAdmin(cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_IS_ADMIN)) == 1);
        }
        cursor.close();
        db.close();
        return user;
    }

    public boolean updateUser(int userId, String name, double weight, double height) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_USER_NAME, name);
        cv.put(COL_USER_WEIGHT, weight);
        cv.put(COL_USER_HEIGHT, height);
        int rows = db.update(TABLE_USERS, cv, COL_USER_ID + "=?", new String[]{String.valueOf(userId)});
        db.close();
        return rows > 0;
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COL_USER_IS_ADMIN + "=0", null, null, null, COL_USER_NAME + " ASC");
        while (cursor.moveToNext()) {
            User user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)));
            user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_EMAIL)));
            list.add(user);
        }
        cursor.close();
        db.close();
        return list;
    }

    public boolean deleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_USERS, COL_USER_ID + "=?", new String[]{String.valueOf(userId)});
        db.close();
        return rows > 0;
    }

    //DIET PLAN

    public long addMealToDiet(int userId, String mealName, String mealTime, int calories, String notes, int reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_DIET_USER_ID, userId);
        cv.put(COL_DIET_MEAL_NAME, mealName);
        cv.put(COL_DIET_MEAL_TIME, mealTime);
        cv.put(COL_DIET_CALORIES, calories);
        cv.put(COL_DIET_NOTES, notes);
        cv.put(COL_DIET_REMINDER, reminder);
        long result = db.insert(TABLE_DIET, null, cv);
        db.close();
        return result;
    }

    public List<DietMeal> getDietPlanForUser(int userId) {
        List<DietMeal> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DIET, null,
                COL_DIET_USER_ID + "=?", new String[]{String.valueOf(userId)},
                null, null, COL_DIET_MEAL_TIME + " ASC");
        while (cursor.moveToNext()) {
            DietMeal meal = new DietMeal();
            meal.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_DIET_ID)));
            meal.setMealName(cursor.getString(cursor.getColumnIndexOrThrow(COL_DIET_MEAL_NAME)));
            meal.setMealTime(cursor.getString(cursor.getColumnIndexOrThrow(COL_DIET_MEAL_TIME)));
            meal.setCalories(cursor.getInt(cursor.getColumnIndexOrThrow(COL_DIET_CALORIES)));
            meal.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(COL_DIET_NOTES)));
            meal.setReminder(cursor.getInt(cursor.getColumnIndexOrThrow(COL_DIET_REMINDER)) == 1);
            list.add(meal);
        }
        cursor.close();
        db.close();
        return list;
    }

    public boolean deleteMeal(int mealId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_DIET, COL_DIET_ID + "=?", new String[]{String.valueOf(mealId)});
        db.close();
        return rows > 0;
    }

    //RECIPE OPERATIONS

    public long addRecipe(int userId, String title, String category, int prepTime, int calories,
                          String ingredients, String instructions, int isMy) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_RECIPE_USER_ID, userId);
        cv.put(COL_RECIPE_TITLE, title);
        cv.put(COL_RECIPE_CATEGORY, category);
        cv.put(COL_RECIPE_PREP_TIME, prepTime);
        cv.put(COL_RECIPE_CALORIES, calories);
        cv.put(COL_RECIPE_INGREDIENTS, ingredients);
        cv.put(COL_RECIPE_INSTRUCTIONS, instructions);
        cv.put(COL_RECIPE_IS_MY, isMy);
        long result = db.insert(TABLE_RECIPES, null, cv);
        db.close();
        return result;
    }

    public List<Recipe> getAllRecipes(int userId) {
        // Return system recipes (is_my_recipe = 0) AND this user's recipes (is_my_recipe = 1 AND user_id = userId)
        List<Recipe> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COL_RECIPE_IS_MY + "=0 OR (" + COL_RECIPE_IS_MY + "=1 AND " + COL_RECIPE_USER_ID + "=?)";
        Cursor cursor = db.query(TABLE_RECIPES, null, selection, new String[]{String.valueOf(userId)}, null, null, COL_RECIPE_ID + " DESC");
        while (cursor.moveToNext()) {
            list.add(cursorToRecipe(cursor));
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<Recipe> getMyRecipes(int userId) {
        List<Recipe> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPES, null,
                COL_RECIPE_USER_ID + "=? AND " + COL_RECIPE_IS_MY + "=1",
                new String[]{String.valueOf(userId)}, null, null, COL_RECIPE_ID + " DESC");
        while (cursor.moveToNext()) {
            list.add(cursorToRecipe(cursor));
        }
        cursor.close();
        db.close();
        return list;
    }

    private Recipe cursorToRecipe(Cursor cursor) {
        Recipe r = new Recipe();
        r.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_RECIPE_ID)));
        r.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_TITLE)));
        r.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_CATEGORY)));
        r.setPrepTime(cursor.getInt(cursor.getColumnIndexOrThrow(COL_RECIPE_PREP_TIME)));
        r.setCalories(cursor.getInt(cursor.getColumnIndexOrThrow(COL_RECIPE_CALORIES)));
        r.setIngredients(cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_INGREDIENTS)));
        r.setInstructions(cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_INSTRUCTIONS)));
        r.setMyRecipe(cursor.getInt(cursor.getColumnIndexOrThrow(COL_RECIPE_IS_MY)) == 1);
        return r;
    }

    public boolean deleteRecipe(int recipeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_RECIPES, COL_RECIPE_ID + "=?", new String[]{String.valueOf(recipeId)});
        db.close();
        return rows > 0;
    }

    public List<Recipe> getAdminRecipes() {
        List<Recipe> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPES, null,
                COL_RECIPE_IS_MY + "=0",
                null, null, null, COL_RECIPE_ID + " DESC");
        while (cursor.moveToNext()) {
            list.add(cursorToRecipe(cursor));
        }
        cursor.close();
        db.close();
        return list;
    }

    // Updates an existing recipe by id
    public boolean updateRecipe(int recipeId, String title, String category,
                                int prepTime, int calories, String ingredients, String instructions) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_RECIPE_TITLE, title);
        cv.put(COL_RECIPE_CATEGORY, category);
        cv.put(COL_RECIPE_PREP_TIME, prepTime);
        cv.put(COL_RECIPE_CALORIES, calories);
        cv.put(COL_RECIPE_INGREDIENTS, ingredients);
        cv.put(COL_RECIPE_INSTRUCTIONS, instructions);
        int rows = db.update(TABLE_RECIPES, cv,
                COL_RECIPE_ID + "=?", new String[]{String.valueOf(recipeId)});
        db.close();
        return rows > 0;
    }

}
