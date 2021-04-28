package com.example.socialsupport.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.socialsupport.R;
import com.google.firebase.auth.FirebaseAuth;

public class DashboardActivity extends AppCompatActivity {
LinearLayout Home,Notify,Profile,Attendance,logout;
FirebaseAuth mauth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mauth=FirebaseAuth.getInstance();
        Home=findViewById(R.id.linear_home);
        Attendance=findViewById(R.id.linear_attendance);
        Notify=findViewById(R.id.linear_notification);
        Profile=findViewById(R.id.linear_profile);
        logout=findViewById(R.id.linear_logout);
        YoYo.with(Techniques.FadeIn).duration(3000).repeat(0).playOn(Home);
        YoYo.with(Techniques.FadeIn).duration(3000).repeat(0).playOn(Profile);
        YoYo.with(Techniques.FadeIn).duration(3000).repeat(0).playOn(Notify);
        YoYo.with(Techniques.FadeIn).duration(3000).repeat(0).playOn(logout);
        YoYo.with(Techniques.FadeIn).duration(3000).repeat(0).playOn(Attendance);
        Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DashboardActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DashboardActivity.this,ProfileActivity.class);
                startActivity(intent);
               finish();
            }
        });
        Notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DashboardActivity.this,ChatActivity.class);
                startActivity(intent);
                finish();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            mauth.signOut();

                Intent intent=new Intent(DashboardActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }
}
