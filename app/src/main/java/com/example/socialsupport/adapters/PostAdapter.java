package com.example.socialsupport.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialsupport.R;
import com.example.socialsupport.activities.PostDetailActivity;
import com.example.socialsupport.models.Post;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

   Context mcontext;
   List<Post> mlist;

    public PostAdapter(Context mcontext, List<Post> mlist) {
        this.mcontext = mcontext;
        this.mlist = mlist;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row= LayoutInflater.from(mcontext).inflate(R.layout.row_post_item,parent,false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.post_tittle.setText(mlist.get(position).getTittle());
        Glide.with(mcontext).load(mlist.get(position).getPicture()).into(holder.post_img);
        Glide.with(mcontext).load(mlist.get(position).getUserPhoto()).into(holder.post_profile_img);
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView post_tittle;
        ImageView post_img;
        CircleImageView post_profile_img;
        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);

            post_tittle=(TextView)itemView.findViewById(R.id.row_post_tittle);
            post_img=(ImageView)itemView.findViewById(R.id.row_post_img);
            post_profile_img=(CircleImageView)itemView.findViewById(R.id.row_post_profile_img);
            itemView.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onClick(View view) {
                    Intent postdetailActivity=new Intent(mcontext, PostDetailActivity.class);
                    int position=getAdapterPosition();

                    postdetailActivity.putExtra("tittle",mlist.get(position).getTittle());
                    postdetailActivity.putExtra("description",mlist.get(position).getDescription());
                    postdetailActivity.putExtra("post_photo",mlist.get(position).getPicture());
                    postdetailActivity.putExtra("post_key",mlist.get(position).getPostKey());
                    postdetailActivity.putExtra("user_photo",mlist.get(position).getUserPhoto());

                    Long timestamp= (Long) mlist.get(position).getTimeStamp();
                    postdetailActivity.putExtra("post_date",timestamp);
                    ActivityOptions options= null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        options = ActivityOptions.makeSceneTransitionAnimation((Activity) mcontext,post_img,"SharedName");
                    }
                    mcontext.startActivity(postdetailActivity,options.toBundle());
                }
            });


        }
    }
}
