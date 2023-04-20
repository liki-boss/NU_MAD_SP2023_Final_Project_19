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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private TextView name;
    private TextView email;
    private TextView diet;
    private ImageView profileImage;
    private Button editProfile;
    private Button logout;
    private fromProfileToHomePage sendData;

    public ProfileFragment() {
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
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        getActivity().setTitle("Profile");

        name = view.findViewById(R.id.userNameProfile);
        email = view.findViewById(R.id.emailProfile);
        diet = view.findViewById(R.id.dietProfile);
        profileImage = view.findViewById(R.id.profile_image);
        editProfile = view.findViewById(R.id.edit_profile_button);
        logout = view.findViewById(R.id.log_out_button);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("username", MODE_PRIVATE);
        String token = sharedPreferences.getString("token","default_value");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference usersRef = db.collection("users").document(token);

        editProfile.setOnClickListener(view13 -> sendData.editProfileData());

        usersRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    name.setText(user.getName());
                    email.setText(user.getEmail());
                    diet.setText(user.getCategory());
                    RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.broken_image);
                    Glide.with(getContext())
                            .load(user.getProfile())
                            .apply(requestOptions)
                            .into(profileImage);
                    logout.setOnClickListener(view1 -> sendData.logout());
                })
                .addOnFailureListener(e -> getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Error loading profile!", Toast.LENGTH_SHORT).show()));

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof fromProfileToHomePage){
            sendData = (fromProfileToHomePage) context;
        }
    }
    public interface fromProfileToHomePage{
        void logout();
        void editProfileData();
    }
}