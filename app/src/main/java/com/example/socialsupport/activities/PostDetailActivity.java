package com.example.socialsupport.activities;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialsupport.R;
import com.example.socialsupport.adapters.CommentsAdapter;
import com.example.socialsupport.models.Comment;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostDetailActivity extends AppCompatActivity {
    ImageView detail_post_img,like,share;
    CircleImageView current_user_profile;
    EditText comment;
    Button add_Comment;
    TextView txt_desc_post,txt_date_post,txt_tittle_post,likat,commentat;
    FirebaseAuth mauth;
    FirebaseUser current_User;
    FirebaseDatabase firebaseDatabase;
    String post_key;
    RecyclerView comment_RV;
    CommentsAdapter adapter;
    List<Comment> mcomment;
    ShareDialog shareDialog;
    CallbackManager callbackManager;
    static String COMMENT_KEY = "Comment" ;



    Target target=new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            SharePhoto sharePhoto=new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    . build();

            if(shareDialog.canShow(SharePhotoContent.class))
            {
                SharePhotoContent content=new SharePhotoContent.Builder()
                        .addPhoto(sharePhoto)
                        .build();
                shareDialog.show(content);
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postdetail);

        initview();
        setupView();
        addcomment();
        iniRvComment();
    }

    void addcomment() {
        Calendar calendar= Calendar.getInstance();

        add_Comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_Comment.setVisibility(View.INVISIBLE);
                DatabaseReference commentReference = firebaseDatabase.getReference(COMMENT_KEY).child(post_key).push();
                String Scomment=comment.getText().toString();
                Comment comment1=new Comment(Scomment,current_User.getUid(),current_User.getPhotoUrl().toString(),
                        current_User.getDisplayName());

                commentReference.setValue(comment1).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showMessage("comment added");
                        comment.setText("");
                        add_Comment.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("fail to add comment : "+e.getMessage());
                    }
                });
            }
        });
    }

    private void iniRvComment() {

        comment_RV.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference commentRef = firebaseDatabase.getReference(COMMENT_KEY).child(post_key);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mcomment = new ArrayList<>();
                for (DataSnapshot snap:dataSnapshot.getChildren()) {

                    Comment comment = snap.getValue(Comment.class);
                    mcomment.add(comment) ;

                }

                adapter = new CommentsAdapter(mcomment,PostDetailActivity.this);
                comment_RV.setAdapter(adapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    private void showMessage(String s) {
        Toast.makeText(PostDetailActivity.this,s, Toast.LENGTH_LONG).show();
    }

    private void initview()
    {
        setContentView(R.layout.activity_postdetail);
        final String post_photo=getIntent().getExtras().getString("post_photo");
        detail_post_img=(ImageView)findViewById(R.id.post_detail_img);
        current_user_profile=(CircleImageView)findViewById(R.id.post_detail_currentuser_profile);
        comment=(EditText)findViewById(R.id.post_detail_comment);
        add_Comment=(Button)findViewById(R.id.post_detail_add_comment);
        txt_tittle_post=(TextView)findViewById(R.id.post_detail_tittle);
        txt_date_post=(TextView)findViewById(R.id.post_detail_date_name);
        txt_desc_post=(TextView)findViewById(R.id.post_detail_desc);
        share=(ImageView)findViewById(R.id.share);
        comment_RV=(RecyclerView)findViewById(R.id.comment_id);
        likat=(TextView)findViewById(R.id.txt_num_likes);
        commentat=(TextView)findViewById(R.id.txt_num_comments);
        like=(ImageView)findViewById(R.id.like);
        mauth= FirebaseAuth.getInstance();
        current_User=mauth.getCurrentUser();
        firebaseDatabase= FirebaseDatabase.getInstance();
        Window window=getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (like.getTag().equals("Like"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post_key)
                            .child(current_User.getUid()).setValue(true);
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post_key)
                            .child(current_User.getUid()).removeValue();
                }
            }
        });

        //facebook
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager=CallbackManager.Factory.create();
        shareDialog=new ShareDialog(this);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        showMessage("Share Successfully!");
                    }

                    @Override
                    public void onCancel() {
                        showMessage("Share Canceld!");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        showMessage(error.getMessage());
                    }
                });
                Picasso.with(getBaseContext()).load(post_photo).into(target);
            }
        });


    }

    private void setupView()
    {
        String post=getIntent().getExtras().getString("post_photo");
        String tittle=getIntent().getExtras().getString("tittle");
        String description=getIntent().getExtras().getString("description");
        String user_photo_intent=getIntent().getExtras().getString("user_photo");
        post_key=getIntent().getExtras().getString("post_key");
        String post_date=timeStamp(getIntent().getExtras().getLong("post_date"));
        txt_tittle_post.setText(tittle);
        txt_desc_post.setText(description);
        txt_date_post.setText(post_date);
        Glide.with(this).load(post).into(detail_post_img);
        Glide.with(this).load(current_User.getPhotoUrl()).into(current_user_profile);
        isLike(post_key,like);
        numOfLikes(likat,post_key);
        numOfComments(commentat,post_key);
    }

    private String timeStamp(Long time)
    {
        Calendar calendar= Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date= DateFormat.format("dd-MM-yyyy",calendar).toString();
        return date;
    }

    private void isLike(String post_id, final ImageView img)
    {
        final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Likes").child(post_id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(user.getUid()).exists())
                {
                    img.setImageResource(R.drawable.ic_favorite_black_pink);
                    img.setTag("Liked");
                }
                else
                {
                    img.setImageResource(R.drawable.ic_favorite);
                    img.setTag("Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void numOfLikes(final TextView txt, String post_id)
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Likes").child(post_id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                txt.setText(dataSnapshot.getChildrenCount()+"Likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void numOfComments(final TextView txt, String post_id)
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Comment").child(post_id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                txt.setText(dataSnapshot.getChildrenCount()+"Comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
