package com.example.socialsupport.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialsupport.R;
import com.example.socialsupport.models.Comment;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>  {

     private List<Comment> comments;
     private Context mcontext1;

    public CommentsAdapter(List<Comment> comments, Context mcontext) {
        this.comments = comments;
        this.mcontext1 = mcontext;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(mcontext1).inflate(R.layout.comments,parent,false);

        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.content.setText(comments.get(position).getContent());
        holder.person_name.setText(comments.get(position).getUname());
        Glide.with(mcontext1).load(comments.get(position).getUimg()).into(holder.img);
        holder.time.setText(timeStamp((Long) comments.get(position).getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView person_name,time;
        TextView content;
        CircleImageView img;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
             person_name=(TextView) itemView.findViewById(R.id.notify_tittle);
             content=(TextView) itemView.findViewById(R.id.notify_desc);
             img=(CircleImageView) itemView.findViewById(R.id.notify_user_photo);
             time=(TextView)itemView.findViewById(R.id.notify_time);

        }
    }



    private String timeStamp(Long time)
    {
        Calendar calendar= Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date= DateFormat.format("hh-mm",calendar).toString();
        return date;
    }
}
