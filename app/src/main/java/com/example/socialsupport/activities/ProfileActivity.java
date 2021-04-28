package com.example.socialsupport.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.socialsupport.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    CircleImageView user_profile;
    ImageView cover;
    TextView user_name;
    FirebaseAuth mauth;
    FirebaseUser current_User;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        progressBar=findViewById(R.id.profile_progress);
        progressBar.setVisibility(View.VISIBLE);
        user_profile=findViewById(R.id.circle_profile);
        cover=findViewById(R.id.user);
        user_name=findViewById(R.id.textView);
        mauth = FirebaseAuth.getInstance();
        current_User = mauth.getCurrentUser();
        Glide.with(this).load(current_User.getPhotoUrl()).into(cover);
        Glide.with(this).load(current_User.getPhotoUrl()).into(user_profile);
        user_name.setText(current_User.getDisplayName());
        progressBar.setVisibility(View.INVISIBLE);
        Window window=getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(ProfileActivity.this,DashboardActivity.class);
        startActivity(intent);
        finish();
    }
}
