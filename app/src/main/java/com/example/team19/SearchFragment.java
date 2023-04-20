package com.example.team19;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements RecipesAdapter.fromRecipesAdapterToFragment {
    private RecyclerView search_recycler;
    private RecipesAdapter recipes_adapter;
    private List<Recipes> recipesList = new ArrayList<>();
    private EditText searchBar;
    private fromSearchFragmentToHomePage sendData;

    public SearchFragment() {
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
        getActivity().setTitle("Search Recipes");
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recipes_adapter = new RecipesAdapter(recipesList, getActivity());
        recipes_adapter.setmListener(this);
        search_recycler = view.findViewById(R.id.recipe_cards_recycler_view);
        search_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        search_recycler.setAdapter(recipes_adapter);
        searchBar = view.findViewById(R.id.search_bar);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("username", MODE_PRIVATE);
        String token = sharedPreferences.getString("token","default_value");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference recipesRef = db.collection("recipes");

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Do nothing
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String matching = editable.toString();

                recipesRef.get()
                        .addOnSuccessListener(documentSnapshot -> {
                            recipesList = new ArrayList<>();
                            List<Recipes> tempRecipesList = documentSnapshot.toObjects(Recipes.class);
                            for(Recipes value: tempRecipesList){
                                if(value.getTitle().toLowerCase().startsWith(matching.toLowerCase()))
                                    recipesList.add(value);
                            }
                            recipes_adapter.updateValues(recipesList);
                        })
                        .addOnFailureListener(e -> getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Unable to load data!\n Try again!", Toast.LENGTH_SHORT).show()));
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof fromSearchFragmentToHomePage){
            sendData = (fromSearchFragmentToHomePage) context;
        }
    }

    @Override
    public void onClick(String id) {
        sendData.selectedRecipe(id);
    }
    public interface fromSearchFragmentToHomePage{
        void selectedRecipe(String id);
    }
}