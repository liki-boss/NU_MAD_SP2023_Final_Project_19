package com.example.team19;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUp#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUp extends Fragment {

    private static final String ARG_USER = "user";
    private EditText name;
    private EditText email;
    private EditText password;
    private EditText confirm_password;
    private Button register;
    private ImageView profile;
    private Spinner category;
    private String category_value;
    private User user;
    fromSignUpToMainActivity sendControl;
    FirebaseAuth mAuth;

    public SignUp() {
        user = new User();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            user = (User) getArguments().getSerializable(ARG_USER);
        }
    }
    public static SignUp newInstance(User user) {
        SignUp fragment = new SignUp();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("Sign Up");
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        name = view.findViewById(R.id.signup_name);
        email = view.findViewById(R.id.signup_email);
        password = view.findViewById(R.id.signup_password);
        confirm_password = view.findViewById(R.id.signup_confirm_password);
        register = view.findViewById(R.id.signup_button);
        profile = view.findViewById(R.id.profile_image);
        category = view.findViewById(R.id.signup_category_spinner);
        mAuth = FirebaseAuth.getInstance();

        ArrayAdapter<CharSequence> localAdapter2 = ArrayAdapter.createFromResource(getContext(),
                R.array.category_array, android.R.layout.simple_spinner_item);
        localAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(localAdapter2);

        if(user != null){
            if(!user.getEmail().equals("")){
                email.setText(user.getEmail());
            }
            if(!user.getName().equals("")){
                name.setText(user.getName());
            }
            if(!user.getCategory().equals("")){
                category.setSelection(Arrays.asList(R.array.category_array).indexOf(user.getCategory()));
            }
            if(!user.getProfile().equals("")){
                RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.broken_image);
                Glide.with(getContext())
                        .load(user.getProfile())
                        .apply(requestOptions)
                        .into(profile);
            }
        }
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category_value = category.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                category_value = Arrays.asList(R.array.category_array).get(0).toString();
            }
        });
        profile.setOnClickListener(view12 -> {
            User user = new User(name.getText().toString(),email.getText().toString(),category_value, "", "", new ArrayList<>(), new ArrayList<>());
            sendControl.profilePhoto(user);
        });
        register.setOnClickListener(view1 -> {
            if(name.getText().toString().equals("") || email.getText().toString().equals("") || password.getText().toString().equals("") || confirm_password.getText().toString().equals("")){
                Toast.makeText(getContext(), "Fields cannot be empty!", Toast.LENGTH_LONG).show();
            }
            else if(!emailValidation(email.getText().toString())){
                Toast.makeText(getContext(), "Enter a valid email-Id!", Toast.LENGTH_LONG).show();
            }
            else if(user.getProfile().equals("")){
                Toast.makeText(getContext(), "Upload a profile photo!", Toast.LENGTH_LONG).show();
            }
            else if(!password.getText().toString().equals(confirm_password.getText().toString())){
                Toast.makeText(getContext(), "Password mismatch error!", Toast.LENGTH_LONG).show();
            }
            else {
                User tempUser = new User(name.getText().toString(),email.getText().toString(),category_value, user.getProfile(), password.getText().toString(), new ArrayList<>(), new ArrayList<>());
                sendControl.register(tempUser);
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof fromSignUpToMainActivity){
            sendControl = (fromSignUpToMainActivity) context;
        }
        else {
            throw new RuntimeException("must implement fromSignUpTo08 methods in root class!");
        }
    }

    private boolean emailValidation(String emailIdTemp){
        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(emailIdTemp).matches();
    }

    public interface fromSignUpToMainActivity{
        void register(User user);
        void profilePhoto(User user);
    }
}