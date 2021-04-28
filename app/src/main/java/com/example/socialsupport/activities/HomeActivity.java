package com.example.socialsupport.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialsupport.R;
import com.example.socialsupport.adapters.PostAdapter;
import com.example.socialsupport.models.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseUser currentuser;
    Dialog popaddpost;
    FloatingActionButton action;
    EditText tittle,description;
    CircleImageView User_Profile_image;
    ImageView post_image,Post;
    ProgressBar postProgressBar;
    private final static int REQUESTCODE=2;
    private final static int PReqcode=2;
    private Uri PickedImageUri=null;
    RecyclerView posts;
    List<Post> mData;
    ProgressBar load;
    PostAdapter adapter;
    DatabaseReference myRef;
    FirebaseDatabase database;
    ConstraintLayout home;
    Snackbar snackbar;
    boolean snack=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        home=findViewById(R.id.home2);
        initview();
        inipopup();
        popAction();
        setupPopupImageCilck();
    }

    private void setupPopupImageCilck() {

        post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckAndRequestForPermission();
            }
        });
    }
    private void OpenGellary() {
        Intent gellaryintent=new Intent(Intent.ACTION_GET_CONTENT);
        gellaryintent.setType("image/*");
        startActivityForResult(gellaryintent,REQUESTCODE);
    }
    private void CheckAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(HomeActivity.this,"Please Accept Requered Permission!", Toast.LENGTH_LONG).show();
            }
            else
            {
                ActivityCompat.requestPermissions(HomeActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PReqcode);
            }

        }
        else
        {
            OpenGellary();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==REQUESTCODE && data!=null){
            PickedImageUri=data.getData();
            post_image.setImageURI(PickedImageUri);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(HomeActivity.this,DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mData = new ArrayList<>();
                for (DataSnapshot postshot:dataSnapshot.getChildren()) {
                    Post post = postshot.getValue(Post.class);
                    mData.add(post);
                }
                load.setVisibility(View.INVISIBLE);
                adapter = new PostAdapter(HomeActivity.this, mData);
                posts.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ShowMesssage(databaseError.getMessage());
            }
        });

    }
    private void inipopup() {
        popaddpost=new Dialog(this);
        popaddpost.setContentView(R.layout.popup_add_post);
        popaddpost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popaddpost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popaddpost.getWindow().getAttributes().gravity= Gravity.TOP;


        tittle= (EditText) popaddpost.findViewById(R.id.popup_tittle);
        description=(EditText) popaddpost.findViewById(R.id.popup_description);
        postProgressBar=(ProgressBar)popaddpost.findViewById(R.id.popup_progressbar);
        User_Profile_image=(CircleImageView)popaddpost.findViewById(R.id.popup_user_photo);
        post_image=(ImageView)popaddpost.findViewById(R.id.popup_img);
        Post=(ImageView)popaddpost.findViewById(R.id.popup_add);

        Glide.with(this).load(currentuser.getPhotoUrl()).into(User_Profile_image);
        Post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postProgressBar.setVisibility(View.VISIBLE);
                Post.setVisibility(View.INVISIBLE);
                if (!tittle.getText().toString().isEmpty()&& !description.getText().toString().isEmpty()&&
                        PickedImageUri !=null)
                {
                    StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("bolg_images");
                    final StorageReference imageFilePath=storageReference.child(PickedImageUri.getLastPathSegment());
                    imageFilePath.putFile(PickedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageDownloadLink=uri.toString();
                                    Post post=new Post(tittle.getText().toString()
                                            ,description.getText().toString(),imageDownloadLink,currentuser.getUid()
                                            ,currentuser.getPhotoUrl().toString());
                                    addPost(post);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    ShowMesssage(e.getMessage());
                                    Post.setVisibility(View.VISIBLE);
                                    postProgressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    });

                }
                else
                {
                    ShowMesssage("Please Verify All Fields!");
                    postProgressBar.setVisibility(View.INVISIBLE);
                    Post.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    private void addPost(Post post) {
       database= FirebaseDatabase.getInstance();
         myRef=database.getReference("Posts").push();
        String Key=myRef.getKey();
        post.setPostKey(Key);
        myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                ShowMesssage("Post Added Successfully!");
                postProgressBar.setVisibility(View.INVISIBLE);
                Post.setVisibility(View.VISIBLE);
                popaddpost.dismiss();
            }
        });

    }
    private void ShowMesssage(String s) {
        Toast.makeText(HomeActivity.this,s, Toast.LENGTH_LONG).show();
    }
    private void initview()
    {
        load=(ProgressBar)findViewById(R.id.progress);
        load.setVisibility(View.VISIBLE);
        action=(FloatingActionButton)findViewById(R.id.float_action);
        auth= FirebaseAuth.getInstance();
        currentuser=auth.getCurrentUser();
        posts = (RecyclerView) findViewById(R.id.posts);
        posts.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        posts.setHasFixedSize(true);
        database=FirebaseDatabase.getInstance();
        myRef=database.getReference("Posts");
    }
    private void popAction()
    {
        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popaddpost.show();
            }
        });
    }

}
