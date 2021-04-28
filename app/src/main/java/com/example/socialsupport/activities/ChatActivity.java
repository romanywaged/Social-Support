package com.example.socialsupport.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialsupport.R;
import com.example.socialsupport.adapters.PersonAdapter;
import com.example.socialsupport.models.Person;
import com.example.socialsupport.notification.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    FirebaseAuth mauth;
    FirebaseUser current_User;
    CircleImageView chat_profile;
    TextView chat_user_name;
    RecyclerView users;
    DatabaseReference reference;
    ArrayList<Person> mpersons;
    PersonAdapter adapter;
    Toolbar toolbar;
    ProgressBar progressBar;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        users = (RecyclerView) findViewById(R.id.user_recycle);
        toolbar=(Toolbar) findViewById(R.id.chat_toolBar);
        progressBar=(ProgressBar)findViewById(R.id.chat_progress);
        progressBar.setVisibility(View.VISIBLE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        users.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        users.setHasFixedSize(true);
        mauth = FirebaseAuth.getInstance();
        current_User = mauth.getCurrentUser();
        chat_profile=findViewById(R.id.chat_profile);
        chat_user_name=findViewById(R.id.chat_username);
        mpersons = new ArrayList<>();
        Glide.with(this).load(current_User.getPhotoUrl()).into(chat_profile);
        chat_user_name.setText(current_User.getDisplayName());
        status("Online");
    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference myref=FirebaseDatabase.getInstance().getReference("Persons");
        myref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mpersons.clear();
                for (DataSnapshot personshot : dataSnapshot.getChildren()) {
                    Person person = personshot.getValue(Person.class);
                    assert person!=null;
                    assert current_User!=null;
                    if (!person.getId().equals(current_User.getUid())) {
                        mpersons.add(person);
                    }
                }
                progressBar.setVisibility(View.INVISIBLE);
                adapter = new PersonAdapter(ChatActivity.this, mpersons,true);
                users.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        updateToken(FirebaseInstanceId.getInstance().getToken());
    }
    private void updateToken(String Token1)
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Tokens");
        Token token=new Token(Token1);
        reference.child(current_User.getUid()).setValue(token);
    }

    private void status(String status)
    {
        reference=FirebaseDatabase.getInstance().getReference("Persons").child(current_User.getUid());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("status",status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("Online");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(ChatActivity.this,DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }
}
