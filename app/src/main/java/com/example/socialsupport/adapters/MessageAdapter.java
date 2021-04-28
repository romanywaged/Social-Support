package com.example.socialsupport.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialsupport.R;
import com.example.socialsupport.models.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder>{
    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;
    Context mcontext;
    List<Chat>mdata;
    FirebaseUser user;
    public MessageAdapter(Context context, List<Chat> mlist)
    {
        this.mcontext=context;
        this.mdata=mlist;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==MSG_TYPE_RIGHT)
        {
            View row= LayoutInflater.from(mcontext).inflate(R.layout.chat_row_right,parent,false);
            return new MessageAdapter.MyViewHolder(row);
        }
        else
        {
            View row= LayoutInflater.from(mcontext).inflate(R.layout.chat_item_left,parent,false);
            return new MessageAdapter.MyViewHolder(row);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Chat chat=mdata.get(position);
        holder.message.setText(chat.getMessage());
        if (position==mdata.size()-1)
        {
            if (chat.isIsseen())
            {
                holder.isseen.setText("seen");
            }else
            {
                holder.isseen.setText("Delivered");
            }
        }
        else
        {
            holder.isseen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView message,isseen;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            message=(TextView)itemView.findViewById(R.id.show_message);
            isseen=(TextView)itemView.findViewById(R.id.txt_seen);
        }
    }

    @Override
    public int getItemViewType(int position) {
        user= FirebaseAuth.getInstance().getCurrentUser();
        if (mdata.get(position).getSender().equals(user.getUid()))
        {
            return MSG_TYPE_RIGHT;
        }
        else
           return MSG_TYPE_LEFT;
    }
}
