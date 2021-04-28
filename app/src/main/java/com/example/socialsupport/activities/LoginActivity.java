package com.example.socialsupport.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.socialsupport.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends AppCompatActivity {

    TextView UserIMage;
    EditText User_Mail,User_Password;
    Button login;
    ProgressBar LoginProgressBar;
    FirebaseAuth auth;
    private Intent Homeactivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initview();
        Login();
        MovetoRegister();
    }

    private void MovetoRegister() {
        UserIMage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Register=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(Register);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=auth.getCurrentUser();
        if (user !=null) {
            updateUI();
        }
    }

    private void Login() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginProgressBar.setVisibility(View.VISIBLE);
                login.setVisibility(View.INVISIBLE);
                final String mail=User_Mail.getText().toString();
                final String pass=User_Password.getText().toString();
                if(mail.isEmpty()||pass.isEmpty())
                {
                    ShowMesssage("Please Verify All Fields!");
                    login.setVisibility(View.VISIBLE);
                    LoginProgressBar.setVisibility(View.INVISIBLE);
                }
                else
                {
                    SignIn(mail,pass);
                }
            }
        });
    }

    private void SignIn(String mail, String pass) {
        auth.signInWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful())
                {
                    LoginProgressBar.setVisibility(View.INVISIBLE);
                    login.setVisibility(View.VISIBLE);
                    updateUI();
                }
                else
                {
                    ShowMesssage(task.getException().getMessage());

                    login.setVisibility(View.VISIBLE);
                    LoginProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void updateUI() {
        startActivity(Homeactivity);
        finish();
    }


    private void initview()
    {
        UserIMage=(TextView)findViewById(R.id.Create);
        User_Password=(EditText)findViewById(R.id.log_password);
        User_Mail=(EditText)findViewById(R.id.login_mail);
        LoginProgressBar=(ProgressBar)findViewById(R.id.progressBar2);
        login=(Button)findViewById(R.id.log_btn);
        LoginProgressBar.setVisibility(View.INVISIBLE);
        auth= FirebaseAuth.getInstance();
        Homeactivity=new Intent(this, DashboardActivity.class);
    }
    private void ShowMesssage(String s) {
        Toast.makeText(LoginActivity.this,s, Toast.LENGTH_LONG).show();
    }
}
