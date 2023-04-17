package com.example.team19;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.core.view.Change;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class HomePage extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, ProfileFragment.fromProfileToHomePage, FragmentCameraController.DisplayTakenPhoto, FragmentDisplayImageForConfirmation.RetakePhoto {
    final static String USER_KEY = "sending data";

    private User user;
    private FirebaseAuth mAuth;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        setTitle("Recipes");
        FirebaseUser user = getIntent().getParcelableExtra(USER_KEY);
        mAuth = FirebaseAuth.getInstance();

        bottomNavigationView = findViewById(R.id.bottomNav);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");
        Query query = usersRef.whereEqualTo("email", user.getEmail());

        SharedPreferences sharedPreferences = getSharedPreferences("username", MODE_PRIVATE);
        String token = sharedPreferences.getString("token","default_value");

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentHome,new HomeFragment(),"fragments")
                .addToBackStack(null)
                .commit();
    }

    ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode()==RESULT_OK){
                    Intent data = result.getData();
                    Uri selectedImageUri = data.getData();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentHome,FragmentDisplayImageForConfirmation.newInstance(selectedImageUri),"displayFragment")
                            .commit();
                }
            }
    );

    @Override
    public void onTakePhoto(Uri imageUri) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentHome,FragmentDisplayImageForConfirmation.newInstance(imageUri),"displayFragment")
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
                .replace(R.id.fragmentHome, FragmentCameraController.newInstance(), "cameraFragment")
                .commit();
    }

    @Override
    public void onUploadButtonPressed(Uri imageUri, ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("images/"+imageUri.getLastPathSegment());
        UploadTask uploadImage = storageReference.putFile(imageUri);
        uploadImage.addOnSuccessListener(taskSnapshot -> {
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                String downloadUrl = uri.toString();
                                user.setProfilePhoto(downloadUrl);
                                getSupportFragmentManager().popBackStack();
                                getSupportFragmentManager().popBackStack();
                                getSupportFragmentManager().popBackStack();
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fragmentHome,new ProfileFragment(),"login")
                                        .addToBackStack("main")
                                        .commit();
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fragmentHome, ChangeProfileFragment.newInstance(user),"signup")
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

    @Override
    public void logout() {
        Toast.makeText(this, "Logging Out!", Toast.LENGTH_LONG).show();
        mAuth.signOut();
        finish();
    }

    @Override
    public void editProfileData() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentHome, ChangeProfileFragment.newInstance(user), "fragments")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentHome, new HomeFragment(), "fragments")
                        .addToBackStack(null)
                        .commit();
                return true;
            case R.id.saved:
                getSupportFragmentManager().popBackStack();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentHome, new SavedFragment(), "fragments")
                        .addToBackStack(null)
                        .commit();
                return true;
            case R.id.search:
                getSupportFragmentManager().popBackStack();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentHome, new SearchFragment(), "fragments")
                        .addToBackStack(null)
                        .commit();
                return true;
            case R.id.compose:
                getSupportFragmentManager().popBackStack();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentHome, new ComposeFragment(), "fragments")
                        .addToBackStack(null)
                        .commit();
                return true;
            case R.id.profile:
                getSupportFragmentManager().popBackStack();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentHome, new ProfileFragment(), "fragments")
                        .addToBackStack(null)
                        .commit();
                return true;
        }
        return false;
    }
}