package com.example.socialsupport.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.socialsupport.R;
import com.example.socialsupport.models.Person;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    CircleImageView reguserprofile;
    static int PReqcode = 1;
    static int REQUESTCODE = 1;
    Uri PickedImageUri;
    EditText UserName, UserEmail, Password, Password2;
    ProgressBar progressBar;
    Button Register;
    private FirebaseAuth mAuth;
    private boolean Picked;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseAuth mauth;
    FirebaseUser current_User;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        initview();
        TakePhoto();
        AddUser();
    }


    private void AddUser() {
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                String name = UserName.getText().toString();
                String email = UserEmail.getText().toString();
                String password = Password.getText().toString();
                String password2 = Password2.getText().toString();
                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || !password2.equals(password)) {
                    ShowMesssage("Please Verify All Fields!");
                    Register.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
                if (Picked == false) {
                    ShowMesssage("Please Put Image!");
                    Register.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    CreateUserAccount(name, email, password);
                    UserName.setEnabled(false);
                    UserEmail.setEnabled(false);
                    Password.setEnabled(false);
                    Password2.setEnabled(false);
                }
            }
        });
    }


    private void CreateUserAccount(final String name, final String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            ShowMesssage("Account Created!");
                            UpdateCurrentUser(name, PickedImageUri, mAuth.getCurrentUser());

                            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("chat_profils");
                            final StorageReference imageFilePath = storageReference.child(PickedImageUri.getLastPathSegment());
                            imageFilePath.putFile(PickedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageDownloadLink = uri.toString();
                                            Person person = new Person(name, mAuth.getCurrentUser().getUid(), imageDownloadLink, email, "Offline");
                                            addPerson(person, mAuth.getCurrentUser().getUid());

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            ShowMesssage(e.getMessage());
                                        }
                                    });
                                }
                            });
                        } else {
                            ShowMesssage("Account Creation Faild! " + task.getException().getMessage());
                            Register.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    private void UpdateCurrentUser(final String name, Uri pickedImageUri, final FirebaseUser currentUser) {
        StorageReference mstorage = FirebaseStorage.getInstance().getReference().child("User Photos");
        final StorageReference imageFilePath = mstorage.child(pickedImageUri.getLastPathSegment());
        imageFilePath.putFile(pickedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        UserProfileChangeRequest ProfileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();
                        currentUser.updateProfile(ProfileUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            ShowMesssage("Register Completed");
                                            UpdateUI();
                                        }
                                    }
                                });
                    }
                });
            }
        });
    }

    private void UpdateUI() {
        Intent homeintent = new Intent(RegisterActivity.this, DashboardActivity.class);
        startActivity(homeintent);
        finish();
    }


    private void ShowMesssage(String s) {
        Toast.makeText(RegisterActivity.this, s, Toast.LENGTH_LONG).show();
    }

    private void initview() {
        reguserprofile = (CircleImageView) findViewById(R.id.regUserPhoto);
        UserName = (EditText) findViewById(R.id.regName);
        UserEmail = (EditText) findViewById(R.id.regEmail);
        Password = (EditText) findViewById(R.id.regPassword);
        Password2 = (EditText) findViewById(R.id.regPassword2);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Register = (Button) findViewById(R.id.regBtn);
        progressBar.setVisibility(View.INVISIBLE);
        mAuth = FirebaseAuth.getInstance();
        Picked = false;
    }

    private void OpenGellary() {
        Intent gellaryintent = new Intent(Intent.ACTION_GET_CONTENT);
        gellaryintent.setType("image/*");
        startActivityForResult(gellaryintent, REQUESTCODE);
    }

    private void CheckAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(RegisterActivity.this, "Please Accept Requered Permission!", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PReqcode);
            }

        } else {
            OpenGellary();
        }
    }

    private void TakePhoto() {
        reguserprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 22) {
                    CheckAndRequestForPermission();
                } else {
                    OpenGellary();
                }
            }
        });
    }

    private void addPerson(Person person, String uid) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Persons").child(uid);
        myRef.setValue(person).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                ShowMesssage("Person Added Successfully!");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUESTCODE && data != null) {
            PickedImageUri = data.getData();
            reguserprofile.setImageURI(PickedImageUri);
            Picked = true;

        }

    }
}
