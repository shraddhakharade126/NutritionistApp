package com.nutritionist.app;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private List<Recipe> recipeList;
    private OnRecipeDeleteListener deleteListener;

    public interface OnRecipeDeleteListener {
        void onDelete(Recipe recipe, int position);
    }

    public RecipeAdapter(List<Recipe> recipeList, OnRecipeDeleteListener listener) {
        this.recipeList = recipeList;
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.tvRecipeTitle.setText(recipe.getTitle());
        holder.tvRecipeCategory.setText(recipe.getCategory());
        holder.tvRecipeTime.setText("⏱ " + recipe.getPrepTime() + " min");
        holder.tvRecipeCalories.setText("🔥 " + recipe.getCalories() + " kcal");

        holder.btnViewRecipe.setOnClickListener(v -> {
            // Show recipe details in a dialog
            new AlertDialog.Builder(v.getContext())
                .setTitle("🍽️ " + recipe.getTitle())
                .setMessage("📂 Category: " + recipe.getCategory()
                        + "\n⏱ Prep Time: " + recipe.getPrepTime() + " min"
                        + "\n🔥 Calories: " + recipe.getCalories() + " kcal"
                        + "\n\n🛒 Ingredients:\n" + recipe.getIngredients()
                        + "\n\n📝 Instructions:\n" + recipe.getInstructions())
                .setPositiveButton("Close", null)
                .show();
            holder.tvRecipeIngredients.setVisibility(
                    holder.tvRecipeIngredients.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            holder.tvRecipeIngredients.setText(recipe.getIngredients());
        });

        holder.ivDeleteRecipe.setOnClickListener(v -> {
            if (deleteListener != null) deleteListener.onDelete(recipe, holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public void removeItem(int position) {
        recipeList.remove(position);
        notifyItemRemoved(position);
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView tvRecipeTitle, tvRecipeCategory, tvRecipeTime, tvRecipeCalories, tvRecipeIngredients;
        ImageView ivDeleteRecipe;
        MaterialButton btnViewRecipe;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRecipeTitle = itemView.findViewById(R.id.tvRecipeTitle);
            tvRecipeCategory = itemView.findViewById(R.id.tvRecipeCategory);
            tvRecipeTime = itemView.findViewById(R.id.tvRecipeTime);
            tvRecipeCalories = itemView.findViewById(R.id.tvRecipeCalories);
            tvRecipeIngredients = itemView.findViewById(R.id.tvRecipeIngredients);
            ivDeleteRecipe = itemView.findViewById(R.id.ivDeleteRecipe);
            btnViewRecipe = itemView.findViewById(R.id.btnViewRecipe);
        }
    }
}
