package com.example.team19;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class ComposeFragment extends Fragment {
    private EditText title;
    private EditText summary;
    private EditText ingredients;
    private EditText steps;
    private EditText calories;
    private CheckBox vegetarian;
    private CheckBox vegan;
    private CheckBox gluten;
    private CheckBox dairy;
    private CheckBox keto;
    private CheckBox paleo;
    private CheckBox low_carb;
    private CheckBox low_fat;
    private String recipeImage;
    private Button upload_image;
    private Button submit;
    private FirebaseFirestore db;

    public ComposeFragment() {
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
        getActivity().setTitle("Compose a recipe");
        View view = inflater.inflate(R.layout.fragment_compose, container, false);

        title = view.findViewById(R.id.recipe_title);
        summary = view.findViewById(R.id.recipe_description);
        ingredients = view.findViewById(R.id.recipe_ingredients);
        steps = view.findViewById(R.id.recipe_steps);
        calories = view.findViewById(R.id.recipe_calories);
        upload_image = view.findViewById(R.id.btn_upload_image);
        submit = view.findViewById(R.id.btn_submit_recipe);
        vegetarian = view.findViewById(R.id.vegetarian_checkbox);
        vegan = view.findViewById(R.id.vegan_checkbox);
        gluten = view.findViewById(R.id.gluten_free_checkbox);
        dairy = view.findViewById(R.id.dairy_free_checkbox);
        keto = view.findViewById(R.id.keto_checkbox);
        paleo = view.findViewById(R.id.paleo_checkbox);
        low_carb = view.findViewById(R.id.cb_low_carb);
        low_fat = view.findViewById(R.id.cb_low_fat);

        upload_image.setOnClickListener(view1 -> openGallery());

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("username", MODE_PRIVATE);
        String token = sharedPreferences.getString("token","default_value");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> category_value = new ArrayList<>();
                if(title.getText().equals("") || summary.getText().equals("") || calories.getText().equals("") || ingredients.getText().equals("") || steps.getText().equals("") || recipeImage == null){
                    if(recipeImage == null){
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Upload an Image!", Toast.LENGTH_LONG).show());
                    }
                    else{
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "One or more fields are missing!", Toast.LENGTH_LONG).show());
                    }
                }
                else{
                    if(vegetarian.isChecked()){
                        category_value.add("Vegetarian");
                    }
                    if(vegan.isChecked()){
                        category_value.add("Vegan");
                    }
                    if(gluten.isChecked()){
                        category_value.add("Gluten-Free");
                    }
                    if(dairy.isChecked()){
                        category_value.add("Dairy-Free");
                    }
                    if(keto.isChecked()){
                        category_value.add("Keto");
                    }
                    if(paleo.isChecked()){
                        category_value.add("Paleo");
                    }
                    if(low_carb.isChecked()){
                        category_value.add("Low-Carb");
                    }
                    if(low_fat.isChecked()){
                        category_value.add("Low-Fat");
                    }
                    db = FirebaseFirestore.getInstance();
                    Map<String, Object> data = new HashMap<>();

                    data.put("title",title.getText().toString());
                    data.put("summary",summary.getText().toString());
                    data.put("ingredients",ingredients.getText().toString());
                    data.put("steps",steps.getText().toString());
                    data.put("calories",calories.getText().toString());
                    data.put("diet_category",category_value);
                    data.put("image",recipeImage);
                    data.put("author",token);
                    data.put("ratings",0);
                    data.put("comments",new ArrayList<String>());

                    db.collection("recipes").document(title.getText().toString()).set(data)
                            .addOnSuccessListener(documentReference -> {
                                String id = title.getText().toString();
                                db.collection("users").document(token)
                                        .update("recipes_composed", FieldValue.arrayUnion(id))
                                        .addOnSuccessListener(unused -> getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Successfully added recipe!", Toast.LENGTH_SHORT).show()))
                                        .addOnFailureListener(e -> getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Unable to submit recipe!\n Try again!", Toast.LENGTH_SHORT).show()));
                            })
                            .addOnFailureListener(e -> getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Unable to submit recipe!\n Try again!", Toast.LENGTH_SHORT).show()));
                }
            }
        });

        return view;
    }
    ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode()==RESULT_OK){
                    Intent data = result.getData();
                    Uri selectedImageUri = data.getData();
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReference().child("images/"+selectedImageUri.getLastPathSegment());
                    UploadTask uploadImage = storageReference.putFile(selectedImageUri);
                    uploadImage.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(uri -> recipeImage = uri.toString())
                            .addOnFailureListener(exception -> Toast.makeText(getContext(), "Upload Failed! Try again!", Toast.LENGTH_SHORT).show());;
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Upload Failed! Try again!", Toast.LENGTH_SHORT).show());
                }
            }
    );
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        galleryLauncher.launch(intent);
    }
}