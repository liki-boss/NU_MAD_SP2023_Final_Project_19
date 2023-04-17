package com.example.team19;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class Login extends Fragment {

    private EditText name;
    private EditText password;
    private Button login;
    private TextView signup;
    fromLoginToMainActivity sendControl;

    public Login() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof fromLoginToMainActivity){
            sendControl = (fromLoginToMainActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("Login");
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        name = view.findViewById(R.id.etEmail);
        password = view.findViewById(R.id.etPassword);
        login = view.findViewById(R.id.btnLogin);
        signup = view.findViewById(R.id.tvSignUp);

        login.setOnClickListener(view12 -> {
            if(emailValidation(name.getText().toString())){
                sendControl.login(name.getText().toString(),password.getText().toString());
                name.setText("");
                password.setText("");
            }
            else{
                Toast.makeText(getContext(), "Invalid credentials!", Toast.LENGTH_LONG).show();
            }
        });

        signup.setOnClickListener(view1 -> sendControl.clickedRegister());

        return view;
    }
    private boolean emailValidation(String emailIdTemp){
        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(emailIdTemp).matches();
    }

    public interface fromLoginToMainActivity{
        void clickedRegister();
        void login(String name, String password);
    }
}