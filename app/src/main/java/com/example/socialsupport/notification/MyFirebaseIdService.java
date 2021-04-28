package com.example.socialsupport.notification;

import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String RefreshToken=FirebaseInstanceId.getInstance().getToken();
        if(user!=null)
        {
            uodatetoken(RefreshToken);
        }
    }

    private void uodatetoken(String refreshToken) {
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Tokens");
        Token token=new Token(refreshToken);
        reference.child(user.getUid()).setValue(token);
    }
}
