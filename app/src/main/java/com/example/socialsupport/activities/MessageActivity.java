package com.example.socialsupport.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialsupport.R;
import com.example.socialsupport.adapters.MessageAdapter;
import com.example.socialsupport.models.APIService;
import com.example.socialsupport.models.Chat;
import com.example.socialsupport.models.Person;
import com.example.socialsupport.notification.Client;
import com.example.socialsupport.notification.Data;
import com.example.socialsupport.notification.MyResponse;
import com.example.socialsupport.notification.Sender;
import com.example.socialsupport.notification.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {
    CircleImageView profile_img;
    TextView user_name;
    FirebaseUser user;
    MessageAdapter adapter;
    DatabaseReference reference;
    List<Chat>mlist;
    Toolbar toolbar;
    EditText message_content;
    ImageButton send;
    RecyclerView messages;
    ValueEventListener listener;
    APIService apiService;
    String id;
    ConstraintLayout back;
    boolean notify=false;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        profile_img=findViewById(R.id.message_profile);
        user_name=findViewById(R.id.message_username);
        message_content=(EditText)findViewById(R.id.content_message);
        send=(ImageButton) findViewById(R.id.send);
        messages=(RecyclerView)findViewById(R.id.RV_messages);
        toolbar=(Toolbar)findViewById(R.id.message_toolBar);



        back=(ConstraintLayout)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MessageActivity.this,ChatActivity.class);
                startActivity(intent);
                finish();
            }
        });



       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);

        messages.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        messages.setLayoutManager(linearLayoutManager);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        String name=getIntent().getExtras().getString("person_name");
        String photo=getIntent().getExtras().getString("person_photo");
        id=getIntent().getExtras().getString("person_id");


        Glide.with(this).load(photo).into(profile_img);
        user_name.setText(name);


        user=FirebaseAuth.getInstance().getCurrentUser();
        readmessage(user.getUid(),id);
        message_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
              String content=message_content.getText().toString().trim();
              if (content.isEmpty())
              {
                  send.setImageResource(R.drawable.ic_mic);
              }
              else
                  send.setImageResource(R.drawable.ic_send);
                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        notify=true;
                        String s=message_content.getText().toString().trim();

                        sendMessage(user.getUid(), id, s);
                        message_content.getText().clear();
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        status("Online");
        seenmessage(id);





        apiService= Client.getclient("https://fcm.googleapis.com/").create(APIService.class);

    }
    private void seenmessage(final String user_id)
    {
        reference=FirebaseDatabase.getInstance().getReference("chat");
        listener=reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapchat:dataSnapshot.getChildren())
                {
                    Chat chat=snapchat.getValue(Chat.class);
                    if(chat.getReciever().equals(user.getUid())&&chat.getSender().equals(user_id))
                    {
                        HashMap<String,Object>hashMap=new HashMap<>();
                        hashMap.put("isseen",true);
                        snapchat.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }





    private void sendMessage(String sender, final String reciever, String message)
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("reciever",reciever);
        hashMap.put("message",message);
        hashMap.put("isseen",false);
        reference.child("chat").push().setValue(hashMap);

        final String msg=message;
        reference=FirebaseDatabase.getInstance().getReference("Persons");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Person person=dataSnapshot.getValue(Person.class);
                if (notify) {
                    SendNotification(reciever, user.getDisplayName(), msg);
                }
                notify=false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendNotification(String reciever, final String name,final String msg) {
        final String photo=Glide.with(this).load(user.getPhotoUrl()).toString();
        DatabaseReference tokens=FirebaseDatabase.getInstance().getReference("Tokens");
        Query query=tokens.orderByKey().equalTo(reciever);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Token token=snapshot.getValue(Token.class);
                    Data data=new Data(user.getUid(),R.mipmap.ic_launcher,name+":"+msg,"Support Social",
                           id );
                    Sender sender=new Sender(data,token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                  if (response.code()==200)
                                    {
                                        if (response.code()!=1)
                                        {
                                            Toast.makeText(MessageActivity.this,"Sent",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void CurrentUser(String user_id)
    {
        SharedPreferences.Editor editor=getSharedPreferences("pref",MODE_PRIVATE).edit();
        editor.putString("currentuser",user_id);
        editor.apply();
    }
    private void readmessage(final String myid, final String userid)
    {
        mlist=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("chat");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mlist.clear();
                for(DataSnapshot chatsnap:dataSnapshot.getChildren())
                {
                    Chat chat=chatsnap.getValue(Chat.class);
                    if(chat.getReciever().equals(myid)&&chat.getSender().equals(userid)||
                            chat.getReciever().equals(userid)&&chat.getSender().equals(myid))
                    {
                        mlist.add(chat);
                    }
                    adapter=new MessageAdapter(MessageActivity.this,mlist);
                    messages.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void status(String status)
    {
        reference=FirebaseDatabase.getInstance().getReference("Persons").child(user.getUid());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("status",status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("Online");
        CurrentUser(id);
    }

    @Override
    protected void onStop() {
        super.onStop();
        reference.removeEventListener(listener);
        status("Offline");
        CurrentUser("none");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.message_menu,menu);
        return true;
    }
}
