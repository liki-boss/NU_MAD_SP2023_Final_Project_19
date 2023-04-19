package com.example.team19;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private String name;
    private String email;
    private String category;
    private String profile;
    private String password;
    private ArrayList<String> saved;
    private ArrayList<String> recipes_composed;

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", category='" + category + '\'' +
                ", profile='" + profile + '\'' +
                ", password='" + password + '\'' +
                ", saved=" + saved +
                ", recipes_composed=" + recipes_composed +
                '}';
    }

    public User(){
        this.name = "";
        this.email = "";
        this.category = "";
        this.profile = "";
        this.password = "";
        this.saved = new ArrayList<>();
        this.recipes_composed = new ArrayList<>();
    }

    public User(String name, String email, String category, String profile, String password, ArrayList<String> saved, ArrayList<String> recipes_composed) {
        this.name = name;
        this.email = email;
        this.category = category;
        this.profile = profile;
        this.password = password;
        this.saved = saved;
        this.recipes_composed = recipes_composed;
    }

    public ArrayList<String> getSaved() {
        return saved;
    }

    public void setSaved(ArrayList<String> saved) {
        this.saved = saved;
    }

    public ArrayList<String> getRecipes_composed() {
        return recipes_composed;
    }

    public void setRecipes_composed(ArrayList<String> recipes_composed) {
        this.recipes_composed = recipes_composed;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profilePhoto) {
        this.profile = profilePhoto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
