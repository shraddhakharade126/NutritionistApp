package com.nutritionist.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DietMealAdapter extends RecyclerView.Adapter<DietMealAdapter.MealViewHolder> {

    private List<DietMeal> mealList;
    private OnMealDeleteListener deleteListener;

    public interface OnMealDeleteListener {
        void onDelete(DietMeal meal, int position);
    }

    public DietMealAdapter(List<DietMeal> mealList, OnMealDeleteListener listener) {
        this.mealList = mealList;
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_diet_meal, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        DietMeal meal = mealList.get(position);
        holder.tvMealName.setText(meal.getMealName());
        holder.tvMealTime.setText("⏰ " + meal.getMealTime());
        holder.tvMealCalories.setText("🔥 " + meal.getCalories() + " kcal");

        if (meal.getNotes() != null && !meal.getNotes().isEmpty()) {
            holder.tvMealNotes.setVisibility(View.VISIBLE);
            holder.tvMealNotes.setText(meal.getNotes());
        } else {
            holder.tvMealNotes.setVisibility(View.GONE);
        }

        holder.tvReminderBadge.setVisibility(meal.isReminder() ? View.VISIBLE : View.GONE);

        // Color strip per meal type (breakfast=green, lunch=orange, dinner=blue)
        String time = meal.getMealTime().toLowerCase();
        int color;
        if (time.contains("am") || time.startsWith("0") || time.startsWith("1")) {
            color = 0xFF2E7D32; // Green - morning
        } else if (time.contains("12") || time.contains("1:") || time.contains("2:")) {
            color = 0xFFE65100; // Orange - lunch
        } else {
            color = 0xFF1565C0; // Blue - dinner/evening
        }
        holder.colorStrip.setBackgroundColor(color);

        holder.ivDeleteMeal.setOnClickListener(v -> {
            if (deleteListener != null) deleteListener.onDelete(meal, holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    public void removeItem(int position) {
        mealList.remove(position);
        notifyItemRemoved(position);
    }

    static class MealViewHolder extends RecyclerView.ViewHolder {
        TextView tvMealName, tvMealTime, tvMealCalories, tvMealNotes, tvReminderBadge;
        ImageView ivDeleteMeal;
        View colorStrip;

        MealViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMealName = itemView.findViewById(R.id.tvMealName);
            tvMealTime = itemView.findViewById(R.id.tvMealTime);
            tvMealCalories = itemView.findViewById(R.id.tvMealCalories);
            tvMealNotes = itemView.findViewById(R.id.tvMealNotes);
            tvReminderBadge = itemView.findViewById(R.id.tvReminderBadge);
            ivDeleteMeal = itemView.findViewById(R.id.ivDeleteMeal);
            colorStrip = itemView.findViewById(R.id.colorStrip);
        }
    }
}
