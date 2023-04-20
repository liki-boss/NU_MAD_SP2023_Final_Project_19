package com.example.team19;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeFragment extends Fragment {

    private static final String ARG_PARAM1 = "abc";
    private TextView recipeTitle;
    private TextView calorieCount;
    private TextView vegetarianIcon;
    private TextView veganIcon;
    private TextView glutenFreeIcon;
    private TextView dairyFreeIcon;
    private TextView ketoIcon;
    private TextView paleoIcon;
    private TextView lowCarbIcon;
    private TextView lowFatIcon;
    private TextView ingredientList;
    private TextView instructionsText;
    private MapView mapView;
    private TextView ratingTitle;
    private LinearLayout linearLayout;
    private Recipes recipe;
    private RatingBar ratingBar;
    private ListView myListView;
    private EditText newComment;
    private Button newCommentConfirm;
    private Button saved;

    public static RecipeFragment newInstance(Recipes recipe) {
        RecipeFragment fragment = new RecipeFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, recipe);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipe = (Recipes) getArguments().getSerializable(ARG_PARAM1);
        }
    }
    public RecipeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find views by id
        linearLayout = view.findViewById(R.id.linear_layout_categories);
        recipeTitle = view.findViewById(R.id.recipe_title);
        calorieCount = view.findViewById(R.id.calorie_count);
        vegetarianIcon = view.findViewById(R.id.vegetarian_icon);
        veganIcon = view.findViewById(R.id.vegan_icon);
        glutenFreeIcon = view.findViewById(R.id.gluten_free_icon);
        dairyFreeIcon = view.findViewById(R.id.dairy_free_icon);
        ketoIcon = view.findViewById(R.id.keto_icon);
        paleoIcon = view.findViewById(R.id.paleo_icon);
        lowCarbIcon = view.findViewById(R.id.low_carb_icon);
        lowFatIcon = view.findViewById(R.id.low_fat_icon);
        ingredientList = view.findViewById(R.id.ingredient_list);
        instructionsText = view.findViewById(R.id.instructions_text);
        mapView = view.findViewById(R.id.map_view);
        ratingTitle = view.findViewById(R.id.rating_title);
        ratingBar = view.findViewById(R.id.rating_bar);
        myListView = view.findViewById(R.id.comments_list);
        newComment = view.findViewById(R.id.comment_input);
        newCommentConfirm = view.findViewById(R.id.submit_comment_button);
        saved = view.findViewById(R.id.saved_button_for_recipes);

        // Set values for views
        recipeTitle.setText(recipe.getTitle());
        calorieCount.setText(recipe.getCalories());
        List<String> categories = new ArrayList<>();
        categories.add("Vegetarian");
        categories.add("Vegan");
        categories.add("Gluten-Free");
        categories.add("Dairy-Free");
        categories.add("Keto");
        categories.add("Paleo");
        categories.add("Low-Carb");
        categories.add("Low-Fat");

        for(String value: categories){
            if(!recipe.getDiet_category().contains(value)){
                switch (value){
                    case "Vegetarian":
                        linearLayout.removeView(vegetarianIcon);
                        break;
                    case "Vegan":
                        linearLayout.removeView(veganIcon);
                        break;
                    case "Gluten-Free":
                        linearLayout.removeView(glutenFreeIcon);
                        break;
                    case "Dairy-Free":
                        linearLayout.removeView(dairyFreeIcon);
                        break;
                    case "Keto":
                        linearLayout.removeView(ketoIcon);
                        break;
                    case "Paleo":
                        linearLayout.removeView(paleoIcon);
                        break;
                    case "Low-Carb":
                        linearLayout.removeView(lowCarbIcon);
                        break;
                    case "Low-Fat":
                        linearLayout.removeView(lowFatIcon);
                        break;
                }
            }
        }

        ingredientList.setText(recipe.getIngredients());
        instructionsText.setText(recipe.getSteps());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ratings");

        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            // Get the current user ID from Firebase Authentication
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference recipesRef = db.collection("recipes").document(recipe.getTitle());

            Map<String, Object> data = new HashMap<>();
            data.put("ratings",((recipe.getRatings()*recipe.getRatings_count())+rating)/(recipe.getRatings_count()+1));
            data.put("ratings_count",recipe.getRatings_count()+1);

            recipesRef.update(data)
                            .addOnFailureListener(e -> getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Unable to rate!\n Try again!", Toast.LENGTH_SHORT).show()));
        });

        ratingBar.setNumStars(5);

        ratingBar.setStepSize(1);

        ratingBar.setRating(recipe.getRatings());

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(googleMap -> {
            LatLng storeLocation = new LatLng(37.7749, -122.4194);
            MarkerOptions markerOptions = new MarkerOptions().position(storeLocation).title("Store");
            googleMap.addMarker(markerOptions);

            CameraPosition cameraPosition = new CameraPosition.Builder().target(storeLocation).zoom(12).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        });

        MyListAdapter adapterForComments = new MyListAdapter(getContext(), recipe.getComments().toArray(new String[0]));
        myListView.setAdapter(adapterForComments);

        newCommentConfirm.setOnClickListener(view12 -> {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("username", MODE_PRIVATE);
            String userName = sharedPreferences.getString("displayName","default_value");
            String text = "[" + userName + "]: " + newComment.getText().toString();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference recipesRef = db.collection("recipes").document(recipe.getTitle());

            recipesRef.update("comments", FieldValue.arrayUnion(text))
                    .addOnSuccessListener(unused -> {
                        newComment.setText("");
                        FirebaseFirestore db1 = FirebaseFirestore.getInstance();
                        DocumentReference recipesRefTemp = db1.collection("recipes").document(recipe.getTitle());
                        recipesRefTemp.get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    Recipes recipes = documentSnapshot.toObject(Recipes.class);
                                    recipe = recipes;
                                    MyListAdapter adapterForComments1 = new MyListAdapter(getContext(), recipe.getComments().toArray(new String[0]));
                                    myListView.setAdapter(adapterForComments1);
                                });
                    })
                    .addOnFailureListener(e -> getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Unable to post comment!", Toast.LENGTH_SHORT).show()));
        });

        saved.setOnClickListener(view1 -> {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("username", MODE_PRIVATE);
            String token = sharedPreferences.getString("token","default_value");

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference usersRef = db.collection("users").document(token);

            usersRef.update("saved", FieldValue.arrayUnion(recipe.getTitle()))
                    .addOnSuccessListener(unused -> getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Successfully added to saved", Toast.LENGTH_SHORT).show()))
                    .addOnFailureListener(e -> getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Unable to save recipe!\n Try again!", Toast.LENGTH_SHORT).show()));

        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}