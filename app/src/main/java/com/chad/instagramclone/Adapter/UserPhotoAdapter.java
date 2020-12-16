package com.chad.instagramclone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.instagramclone.Model.Post;
import com.chad.instagramclone.R;

import java.util.List;

public class UserPhotoAdapter extends RecyclerView.Adapter<UserPhotoAdapter.ViewHolder> {

    private Context context;
    private List<Post> list;

    public UserPhotoAdapter(Context context, List<Post> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.photo_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = list.get(position);
        Glide.with(context).load(post.getPostImage()).into(holder.imagePosts);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imagePosts;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imagePosts = itemView.findViewById(R.id.imagePosts);
        }
    }
}
