package com.example.team19;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {
    private List<Recipes> recipes = new ArrayList<>();
    private Activity activity;
    public fromRecipesAdapterToFragment mListener;

    public void setmListener(fromRecipesAdapterToFragment mListener) {
        this.mListener = mListener;
    }

    public RecipesAdapter(List<Recipes>recipe, Activity activity){
        this.recipes = recipe;
        this.activity = activity;
    }

    public void updateValues(List<Recipes> recipe){
        this.recipes = recipe;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cards_for_category,parent,false);
        ViewHolder viewHolder = new ViewHolder(view, recipes);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(recipes.get(position).getTitle());
        holder.summary.setText(recipes.get(position).getSummary());
        holder.author.setText(recipes.get(position).getAuthor());
        holder.ratingBar.setRating(recipes.get(position).getRatings());
        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.broken_image);
        Glide.with(activity.getBaseContext())
                .load(recipes.get(position).getImage())
                .apply(requestOptions)
                .into(holder.recipeImage);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView summary;
        private TextView author;
        private RatingBar ratingBar;
        private ImageView recipeImage;

        public ViewHolder(@NonNull View itemView, List<Recipes> recipe) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipe_image);
            title = itemView.findViewById(R.id.recipe_title);
            summary = itemView.findViewById(R.id.recipe_summary);
            author = itemView.findViewById(R.id.recipe_author);
            ratingBar = itemView.findViewById(R.id.recipe_rating);
            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onClick(recipe.get(position).getTitle());
                }
            });
        }
    }

    public interface fromRecipesAdapterToFragment{
        void onClick(String id);
    }

}
