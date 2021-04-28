package com.example.socialsupport.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialsupport.R;
import com.example.socialsupport.activities.MessageActivity;
import com.example.socialsupport.models.Chat;
import com.example.socialsupport.models.Person;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.MyViewHolder> {

   Context mcontext;
   List<Person> mlist;
   String lastmessage;
   private boolean ischat;
    public PersonAdapter(Context mcontext, List<Person> mlist,boolean ischat) {
        this.mcontext = mcontext;
        this.mlist = mlist;
        this.ischat=ischat;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row= LayoutInflater.from(mcontext).inflate(R.layout.chat_row,parent,false);
        return new PersonAdapter.MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.chat_user_name.setText(mlist.get(position).getUser_name());
            Glide.with(mcontext).load(mlist.get(position).getUser_profile()).into(holder.chat_row_img);
            if (ischat)
            {
                lastmessage(mlist.get(position).getId(),holder.last_msg);
            }
            else
            {
                holder.last_msg.setVisibility(View.GONE);
            }
            if (ischat)
            {
                if(mlist.get(position).getStatus().equals("Online"))
                {
                    holder.imge_on.setVisibility(View.VISIBLE);
                    holder.img_off.setVisibility(View.INVISIBLE);
                }
                else
                {
                    holder.img_off.setVisibility(View.VISIBLE);
                    holder.imge_on.setVisibility(View.INVISIBLE);
                }
            }
            else
            {
                holder.img_off.setVisibility(View.GONE);
                holder.imge_on.setVisibility(View.GONE);
            }
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
     public TextView chat_user_name;
        public TextView last_msg;
        CircleImageView chat_row_img,imge_on,img_off;
        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            chat_row_img=(CircleImageView) itemView.findViewById(R.id.chat_UserProfile);
            chat_user_name=(TextView) itemView.findViewById(R.id.chat_UserName);
            img_off=(CircleImageView)itemView.findViewById(R.id.img_off);
            imge_on=(CircleImageView)itemView.findViewById(R.id.img_on);
            last_msg=(TextView)itemView.findViewById(R.id.last_msg);
            itemView.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onClick(View view) {
                    Intent postdetailActivity=new Intent(mcontext, MessageActivity.class);
                    int position=getAdapterPosition();

                    postdetailActivity.putExtra("person_name",mlist.get(position).getUser_name());
                    postdetailActivity.putExtra("person_photo",mlist.get(position).getUser_profile());
                    postdetailActivity.putExtra("person_id",mlist.get(position).getId());
                    ActivityOptions options= null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        options = ActivityOptions.makeSceneTransitionAnimation((Activity) mcontext,chat_row_img,"SharedName");
                    }
                    mcontext.startActivity(postdetailActivity,options.toBundle());
                }
            });
        }
    }
    private void lastmessage(final String user_id, final TextView view_msg)
    {
        lastmessage="default";
        final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("chat");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Chat chat=snapshot.getValue(Chat.class);
                    if(chat.getReciever().equals(user.getUid())&&chat.getSender().equals(user_id)||
                    chat.getReciever().equals(user_id)&&chat.getSender().equals(user.getUid()))
                    {
                        lastmessage=chat.getMessage();
                    }
                }
                switch (lastmessage)
                {
                    case "default":
                        view_msg.setText(" ");
                        break;
                        default:
                            view_msg.setText(lastmessage);
                            break;

                }
                lastmessage="default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
