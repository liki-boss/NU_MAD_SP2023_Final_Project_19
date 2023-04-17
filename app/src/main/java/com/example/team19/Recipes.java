package com.example.team19;

import java.util.ArrayList;
import java.util.List;

public class Recipes {
    private String title;
    private String author;
    private String summary;
    private long ratings;
    private List<String> comments;
    private String ingredients;
    private String steps;
    private String calories;
    private ArrayList<String> diet_category;
    private String image;
    public Recipes(){
        //Do Nothing
    }

    public Recipes(String title, String author, String summary, long ratings, List<String> comments, String ingredients, String steps, String calories, ArrayList<String> diet_category, String image) {
        this.title = title;
        this.author = author;
        this.summary = summary;
        this.ratings = ratings;
        this.comments = comments;
        this.ingredients = ingredients;
        this.steps = steps;
        this.calories = calories;
        this.diet_category = diet_category;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public long getRatings() {
        return ratings;
    }

    public void setRatings(long ratings) {
        this.ratings = ratings;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public ArrayList<String> getDiet_category() {
        return diet_category;
    }

    public void setDiet_category(ArrayList<String> diet_category) {
        this.diet_category = diet_category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
