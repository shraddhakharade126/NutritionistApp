package com.nutritionist.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AdminRecipeAdapter extends RecyclerView.Adapter<AdminRecipeAdapter.ViewHolder> {

    public interface AdminRecipeListener {
        void onEdit(Recipe recipe, int position);
        void onDelete(Recipe recipe, int position);
    }

    private final List<Recipe> list;
    private final AdminRecipeListener listener;

    public AdminRecipeAdapter(List<Recipe> list, AdminRecipeListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_recipe, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Recipe r = list.get(position);
        h.tvTitle.setText(r.getTitle());
        h.tvCategory.setText(r.getCategory());
        h.tvTime.setText("⏱ " + r.getPrepTime() + " min");
        h.tvCalories.setText("🔥 " + r.getCalories() + " kcal");
        h.tvIngredients.setText(r.getIngredients());

        // Toggle ingredients on card click
        h.itemView.setOnClickListener(v -> {
            boolean visible = h.tvIngredients.getVisibility() == View.VISIBLE;
            h.tvIngredients.setVisibility(visible ? View.GONE : View.VISIBLE);
            h.tvInstructions.setVisibility(visible ? View.GONE : View.VISIBLE);
            h.tvInstructions.setText(r.getInstructions());
        });

        h.ivEdit.setOnClickListener(v -> listener.onEdit(r, h.getAdapterPosition()));
        h.ivDelete.setOnClickListener(v -> listener.onDelete(r, h.getAdapterPosition()));
    }

    @Override
    public int getItemCount() { return list.size(); }

    public void removeItem(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCategory, tvTime, tvCalories, tvIngredients, tvInstructions;
        ImageView ivEdit, ivDelete;

        ViewHolder(View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvAdminRecipeTitle);
            tvCategory = v.findViewById(R.id.tvAdminRecipeCategory);
            tvTime = v.findViewById(R.id.tvAdminRecipeTime);
            tvCalories = v.findViewById(R.id.tvAdminRecipeCalories);
            tvIngredients = v.findViewById(R.id.tvAdminRecipeIngredients);
            tvInstructions = v.findViewById(R.id.tvAdminRecipeInstructions);
            ivEdit = v.findViewById(R.id.ivEditRecipe);
            ivDelete = v.findViewById(R.id.ivDeleteAdminRecipe);
        }
    }
}
