package com.nutritionist.app;

public class DietMeal {
    private int id;
    private String mealName;
    private String mealTime;
    private int calories;
    private String notes;
    private boolean reminder;

    public DietMeal() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMealName() { return mealName; }
    public void setMealName(String mealName) { this.mealName = mealName; }

    public String getMealTime() { return mealTime; }
    public void setMealTime(String mealTime) { this.mealTime = mealTime; }

    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isReminder() { return reminder; }
    public void setReminder(boolean reminder) { this.reminder = reminder; }
}
