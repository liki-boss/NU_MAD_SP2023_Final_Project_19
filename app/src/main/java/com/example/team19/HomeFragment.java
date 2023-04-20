package com.example.team19;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements RecipesAdapter.fromRecipesAdapterToFragment {
    private RecyclerView categories;
    private RecyclerView recipe_cards;
    private List<Recipes> recipesList = new ArrayList<>();
    private RecipesAdapter recipes_adapter;
    private Button all;
    private Button vegetarian;
    private Button vegan;
    private Button gluten_free;
    private Button dairy_free;
    private Button paleo;
    private Button keto;
    private Button low_carb;
    private Button low_fat;
    public fromHomeFragmentToHomePage sendData;

    public HomeFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().setTitle("Recipes");

        all = view.findViewById(R.id.btn_all);
        vegetarian = view.findViewById(R.id.btn_vegetarian);
        vegan = view.findViewById(R.id.btn_vegan);
        gluten_free = view.findViewById(R.id.btn_gluten_free);
        dairy_free = view.findViewById(R.id.btn_dairy_free);
        paleo = view.findViewById(R.id.btn_paleo);
        keto = view.findViewById(R.id.btn_keto);
        low_carb = view.findViewById(R.id.btn_low_carb);
        low_fat = view.findViewById(R.id.btn_low_fat);

        recipes_adapter = new RecipesAdapter(recipesList, getActivity());
        recipes_adapter.setmListener(this);
        recipe_cards = view.findViewById(R.id.recipe_recycler_view);
        recipe_cards.setLayoutManager(new LinearLayoutManager(getActivity()));
        recipe_cards.setAdapter(recipes_adapter);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("username", MODE_PRIVATE);
        String token = sharedPreferences.getString("token","default_value");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference recipesRef = db.collection("recipes");

        recipesRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Recipes> values = new ArrayList<>();
                    for(QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots){
                        values = queryDocumentSnapshots.toObjects(Recipes.class);
                    }
                    recipes_adapter.updateValues(values);

                })
                .addOnFailureListener(e -> getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Unable to retrieve data!", Toast.LENGTH_SHORT).show()));

        all.setOnClickListener(view12 -> recipesRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Recipes> values = new ArrayList<>();
                    for(QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots){
                        values = queryDocumentSnapshots.toObjects(Recipes.class);
                    }
                    recipes_adapter.updateValues(values);
                })
                .addOnFailureListener(e -> getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Unable to retrieve data!", Toast.LENGTH_SHORT).show())));

        vegetarian.setOnClickListener(view1 -> recipesRef.whereArrayContains("diet_category","Vegetarian")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Recipes> values = new ArrayList<>();
                    for(QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots){
                        values.add(queryDocumentSnapshot.toObject(Recipes.class));
                    }
                    recipes_adapter.updateValues(values);
                })
                .addOnFailureListener(e -> getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Unable to retrieve data!", Toast.LENGTH_SHORT).show())));

        vegan.setOnClickListener(view13 -> recipesRef.whereArrayContains("diet_category","Vegan")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Recipes> values = new ArrayList<>();
                    for(QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots){
                        values.add(queryDocumentSnapshot.toObject(Recipes.class));
                    }
                    recipes_adapter.updateValues(values);
                })
                .addOnFailureListener(e -> getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Unable to retrieve data!", Toast.LENGTH_SHORT).show())));

        gluten_free.setOnClickListener(view13 -> recipesRef.whereArrayContains("diet_category","Gluten-Free")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Recipes> values = new ArrayList<>();
                    for(QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots){
                        values.add(queryDocumentSnapshot.toObject(Recipes.class));
                    }
                    recipes_adapter.updateValues(values);
                })
                .addOnFailureListener(e -> getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Unable to retrieve data!", Toast.LENGTH_SHORT).show())));

        dairy_free.setOnClickListener(view13 -> recipesRef.whereArrayContains("diet_category","Dairy-Free")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Recipes> values = new ArrayList<>();
                    for(QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots){
                        values.add(queryDocumentSnapshot.toObject(Recipes.class));
                    }
                    recipes_adapter.updateValues(values);
                })
                .addOnFailureListener(e -> getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Unable to retrieve data!", Toast.LENGTH_SHORT).show())));

        keto.setOnClickListener(view13 -> recipesRef.whereArrayContains("diet_category","Keto")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Recipes> values = new ArrayList<>();
                    for(QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots){
                        values.add(queryDocumentSnapshot.toObject(Recipes.class));
                    }
                    recipes_adapter.updateValues(values);
                })
                .addOnFailureListener(e -> getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Unable to retrieve data!", Toast.LENGTH_SHORT).show())));

        paleo.setOnClickListener(view13 -> recipesRef.whereArrayContains("diet_category","Paleo")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Recipes> values = new ArrayList<>();
                    for(QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots){
                        values.add(queryDocumentSnapshot.toObject(Recipes.class));
                    }
                    recipes_adapter.updateValues(values);
                })
                .addOnFailureListener(e -> getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Unable to retrieve data!", Toast.LENGTH_SHORT).show())));

        low_carb.setOnClickListener(view13 -> recipesRef.whereArrayContains("diet_category","Low-Carb")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Recipes> values = new ArrayList<>();
                    for(QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots){
                        values.add(queryDocumentSnapshot.toObject(Recipes.class));
                    }
                    recipes_adapter.updateValues(values);
                })
                .addOnFailureListener(e -> getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Unable to retrieve data!", Toast.LENGTH_SHORT).show())));

        low_fat.setOnClickListener(view13 -> recipesRef.whereArrayContains("diet_category","Low-Fat")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Recipes> values = new ArrayList<>();
                    for(QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots){
                        values.add(queryDocumentSnapshot.toObject(Recipes.class));
                    }
                    recipes_adapter.updateValues(values);
                })
                .addOnFailureListener(e -> getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Unable to retrieve data!", Toast.LENGTH_SHORT).show())));

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof fromHomeFragmentToHomePage){
            sendData = (fromHomeFragmentToHomePage) context;
        }
    }

    @Override
    public void onClick(String id) {
        sendData.selectedRecipe(id);
    }
    public interface fromHomeFragmentToHomePage{
        void selectedRecipe(String id);
    }
}