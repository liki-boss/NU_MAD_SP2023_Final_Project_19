package com.example.team19;

import static com.example.team19.HomePage.USER_KEY;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements Login.fromLoginToMainActivity, SignUp.fromSignUpToMainActivity, FragmentCameraController.DisplayTakenPhoto, FragmentDisplayImageForConfirmation.RetakePhoto {
    private static final int PERMISSIONS_CODE = 0x100;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    User user = new User();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(getBaseContext());

        mAuth = FirebaseAuth.getInstance();

        Boolean cameraAllowed = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        Boolean readAllowed = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        Boolean writeAllowed = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        Boolean locationAllowed = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;


        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(!(cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting())) {
            Toast.makeText(getBaseContext(),"Device not connected to the Internet!",Toast.LENGTH_LONG).show();
            finish();
        }
        else{
            if(cameraAllowed && readAllowed && writeAllowed){
                Toast.makeText(this, "All permissions granted!", Toast.LENGTH_LONG).show();
            }else{
                //Request Permissions for location
                requestPermissions(new String[]{
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                }, PERMISSIONS_CODE);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.logInFragment,new Login(),"login")
                    .addToBackStack("main")
                    .commit();
        } else {
            FirebaseUser user = mAuth.getCurrentUser();
            SharedPreferences sharedPreferences = getSharedPreferences("username", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("token", user.getEmail());
            editor.putString("displayName",user.getDisplayName());
            editor.apply();
            Intent toInClass08Activity2 = new Intent(MainActivity.this, HomePage.class);
            toInClass08Activity2.putExtra(USER_KEY,user);
            startActivity(toInClass08Activity2);
        }
    }

    @Override
    public void clickedRegister() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.logInFragment, new SignUp(),"signup")
                .addToBackStack("main")
                .commit();
    }

    @Override
    public void login(String name, String password) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(name, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        SharedPreferences sharedPreferences = getSharedPreferences("username", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("token", user.getEmail());
                        editor.putString("displayName",user.getDisplayName());
                        editor.apply();
                        Intent toInClass08Activity2 = new Intent(MainActivity.this, HomePage.class);
                        toInClass08Activity2.putExtra(USER_KEY,user);
                        startActivity(toInClass08Activity2);
                    } else {
                        Toast.makeText(this, "Authentication failed!\n Try again!", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void register(User tempUser) {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(tempUser.getEmail(),tempUser.getPassword())
                .addOnCompleteListener( task -> {
                    if(task.isSuccessful()){
                        FirebaseUser user = mAuth.getCurrentUser();
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(tempUser.getEmail())
                                .build();
                        user.updateProfile(profileUpdate)
                                .addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful()){
                                        FirebaseUser user1 = mAuth.getCurrentUser();

                                        Map<String, Object> data = new HashMap<>();
                                        data.put("name", tempUser.getName());
                                        data.put("email", tempUser.getEmail());
                                        data.put("password", tempUser.getPassword());
                                        data.put("profile", tempUser.getProfile());
                                        data.put("category", tempUser.getCategory());
                                        data.put("saved",new ArrayList<FieldValue>());
                                        data.put("recipes_composed", new ArrayList<FieldValue>());

                                        db.collection("users").document(tempUser.getEmail())
                                                .set(data)
                                                .addOnSuccessListener(documentReference -> {
                                                    Toast.makeText(this, "Successfully registered user!", Toast.LENGTH_LONG).show();
                                                })
                                                .addOnFailureListener(e -> Toast.makeText(this, "Error registering user!", Toast.LENGTH_SHORT).show());
                                    }
                                    else {
                                        Toast.makeText(this, "Error registering user!", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                    else {
                        Toast.makeText(this,"Error registering user!", Toast.LENGTH_LONG).show();
                    }
                });
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void profilePhoto(User tempUser) {
        user = tempUser;
        getSupportFragmentManager().beginTransaction()
                .add(R.id.logInFragment, FragmentCameraController.newInstance(), "cameraFragment")
                .addToBackStack("main")
                .commit();
    }
    ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode()==RESULT_OK){
                    Intent data = result.getData();
                    Uri selectedImageUri = data.getData();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.logInFragment,FragmentDisplayImageForConfirmation.newInstance(selectedImageUri),"displayFragment")
                            .commit();
                }
            }
    );

    @Override
    public void onTakePhoto(Uri imageUri) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.logInFragment,FragmentDisplayImageForConfirmation.newInstance(imageUri),"displayFragment")
                .commit();
    }

    @Override
    public void onOpenGalleryPressed() {
        openGallery();
    }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        galleryLauncher.launch(intent);
    }

    @Override
    public void onRetakePressed() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.logInFragment, FragmentCameraController.newInstance(), "cameraFragment")
                .commit();
    }

    @Override
    public void onUploadButtonPressed(Uri imageUri, ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
        db = FirebaseFirestore.getInstance();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("images/"+imageUri.getLastPathSegment());
        UploadTask uploadImage = storageReference.putFile(imageUri);
        uploadImage.addOnSuccessListener(taskSnapshot -> {
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                String downloadUrl = uri.toString();
                                user.setProfile(downloadUrl);
                                getSupportFragmentManager().popBackStack();
                                getSupportFragmentManager().popBackStack();
                                getSupportFragmentManager().popBackStack();
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.logInFragment,new Login(),"login")
                                        .addToBackStack("main")
                                        .commit();
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.logInFragment, SignUp.newInstance(user),"signup")
                                        .addToBackStack("main")
                                        .commit();
                            })
                            .addOnFailureListener(exception -> Toast.makeText(this, "Upload Failed! Try again!", Toast.LENGTH_SHORT).show());;
                    progressBar.setVisibility(View.GONE);
                })
                .addOnProgressListener(snapshot -> {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    progressBar.setProgress((int) progress);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Upload Failed! Try again!", Toast.LENGTH_SHORT).show());
    }
}