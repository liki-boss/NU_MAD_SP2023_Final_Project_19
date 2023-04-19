package com.example.team19;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class SavedFragment extends Fragment implements RecipesAdapter.fromRecipesAdapterToFragment{
    private RecyclerView saved_recycler;
    private RecipesAdapter recipes_adapter;
    private List<Recipes> recipesList = new ArrayList<>();
    private fromSavedFragmentToHome sendData;

    public SavedFragment() {
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
        getActivity().setTitle("Saved Recipes");
        View view = inflater.inflate(R.layout.fragment_saved, container, false);

        recipes_adapter = new RecipesAdapter(recipesList, getActivity());
        recipes_adapter.setmListener(this);
        saved_recycler = view.findViewById(R.id.saved_recipe_cards_recycler_view);
        saved_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        saved_recycler.setAdapter(recipes_adapter);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("username", MODE_PRIVATE);
        String token = sharedPreferences.getString("token","default_value");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference recipesRef = db.collection("recipes");
        DocumentReference userRef = db.collection("users").document(token);

        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    recipesList = new ArrayList<>();
                    User user = documentSnapshot.toObject(User.class);
                    for(String value: user.getSaved()){
                        recipesRef.document(value).get()
                                .addOnSuccessListener(documentSnapshot1 -> {
                                    Recipes recipes = documentSnapshot1.toObject(Recipes.class);
                                    recipesList.add(recipes);
                                    recipes_adapter.updateValues(recipesList);
                                })
                                .addOnFailureListener(e -> getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Unable to load data!\n Try again!", Toast.LENGTH_SHORT).show()));
                    }
                })
                .addOnFailureListener(e -> getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Unable to load data!\n Try again!", Toast.LENGTH_SHORT).show()));

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof fromSavedFragmentToHome){
            sendData = (fromSavedFragmentToHome) context;
        }
    }

    @Override
    public void onClick(String id) {
        sendData.selectedRecipe(id);
    }

    public interface fromSavedFragmentToHome{
        void selectedRecipe(String id);
    }
}