package com.nutritionist.app;

public class Recipe {
    private int id;
    private String title;
    private String category;
    private int prepTime;
    private int calories;
    private String ingredients;
    private String instructions;
    private boolean isMyRecipe;

    public Recipe() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getPrepTime() { return prepTime; }
    public void setPrepTime(int prepTime) { this.prepTime = prepTime; }

    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }

    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public boolean isMyRecipe() { return isMyRecipe; }
    public void setMyRecipe(boolean myRecipe) { isMyRecipe = myRecipe; }
}
