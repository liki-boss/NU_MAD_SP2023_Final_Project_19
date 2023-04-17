package com.example.team19;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChangeProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangeProfileFragment extends Fragment {

    private static final String ARG_USER = "user";
    private EditText name;
    private EditText email;
    private Spinner dietary_category;
    private String category;
    private ImageView profileImage;
    private Button confirm;
    private User user;
    private fromProfileEdit sendControl;

    public ChangeProfileFragment() {
        // Required empty public constructor
    }

    public static ChangeProfileFragment newInstance(User user) {
        ChangeProfileFragment fragment = new ChangeProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            user = (User) getArguments().getSerializable(ARG_USER);
            category = user.getCategory();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("Edit Profile");
        View view = inflater.inflate(R.layout.fragment_change_profile, container, false);

        name = view.findViewById(R.id.editNameProfile);
        email = view.findViewById(R.id.editEmailProfile);
        profileImage = view.findViewById(R.id.profile_image);
        dietary_category = view.findViewById(R.id.dietProfile);
        confirm = view.findViewById(R.id.edit_profile_button);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("username", MODE_PRIVATE);
        String token = sharedPreferences.getString("token","default_value");

        if(user != null){
            if(!user.getName().equals("")){
                name.setText(user.getName());
            }
            if(!user.getEmail().equals("")){
                email.setText(user.getEmail());
            }
            if(!user.getCategory().equals("")){
                dietary_category.setSelection(Arrays.asList(R.array.category_array).indexOf(user.getCategory()));
            }
            if(!user.getProfilePhoto().equals("")){
                RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.broken_image);
                Glide.with(getContext())
                        .load(user.getProfilePhoto())
                        .apply(requestOptions)
                        .into(profileImage);
            }
        }
        else{
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docsRef = db.collection("users").document(token);

            docsRef.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User user2 = documentSnapshot.toObject(User.class);
                            user = user2;
                            name.setText(user2.getName());
                            email.setText(user2.getEmail());
                            dietary_category.setSelection(Arrays.asList(R.array.category_array).indexOf(user.getCategory()));
                            RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.broken_image);
                            Glide.with(getContext())
                                    .load(user2.getProfilePhoto())
                                    .apply(requestOptions)
                                    .into(profileImage);
                        }
                    })
                    .addOnFailureListener(e -> getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Unable to fetch data!", Toast.LENGTH_SHORT).show()));

        }

        profileImage.setOnClickListener(view1 -> {
            User user2 = new User(name.getText().toString(),email.getText().toString(),category, user.getProfilePhoto(), user.getPassword(), user.getSaved(), user.getRecipes_composed());
            sendControl.profilePhotoEdit(user2);
        });

        dietary_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = dietary_category.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                category = user.getCategory();
            }
        });

        confirm.setOnClickListener(view12 -> {
            if(name.getText().toString().equals("") || email.getText().toString().equals("")){
                FirebaseFirestore db2 = FirebaseFirestore.getInstance();
                DocumentReference docsRef2 = db2.collection("users").document(token);

                Map<String, Object> updates = new HashMap<>();
                updates.put("name", name.getText().toString());
                updates.put("email", email.getText().toString());
                updates.put("category", category);
                updates.put("profile",user.getProfilePhoto());

                docsRef2.update(updates)
                        .addOnSuccessListener(unused -> getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Successfully updated profile!", Toast.LENGTH_SHORT).show();
                            sendControl.finishEdit();
                        }))
                        .addOnFailureListener(e -> getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Couldn't update profile!\n Try again!", Toast.LENGTH_SHORT).show()));
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof fromProfileEdit){
            sendControl = (fromProfileEdit) context;
        }
    }
    public interface fromProfileEdit{
        void profilePhotoEdit(User user);
        void finishEdit();
    }
}