package com.chad.instagramclone.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.instagramclone.Constants.Constants;
import com.chad.instagramclone.Fragment.PostDetailsFragment;
import com.chad.instagramclone.Fragment.ProfileFragment;
import com.chad.instagramclone.Model.Notification;
import com.chad.instagramclone.Model.Post;
import com.chad.instagramclone.Model.User;
import com.chad.instagramclone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final Context context;
    private final List<Notification> list;

    public NotificationAdapter(Context context, List<Notification> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = list.get(position);
        holder.textComment.setText(notification.getTextComment());

        getUserInfo(holder.textUsername, holder.profileImage, notification.getUserId());

        if (notification.isPost()) {
            holder.imagePost.setVisibility(View.VISIBLE);
            getPostImage(holder.imagePost, notification.getPostId());
        } else{
            holder.imagePost.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (notification.isPost()) {
                SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE).edit();
                editor.putString(Constants.POST_ID, notification.getPostId());
                editor.apply();

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                        new PostDetailsFragment());
            } else {
                SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE).edit();
                editor.putString(Constants.USER_ID, notification.getUserId());
                editor.apply();

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                        new ProfileFragment());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView profileImage;
        private ImageView imagePost;

        private TextView textUsername;
        private TextView textComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.imageProfileNotification);
            imagePost = itemView.findViewById(R.id.imagePostNotification);
            textUsername = itemView.findViewById(R.id.textUserNameNotification);
            textComment = itemView.findViewById(R.id.textCommentNotification);
        }
    }

    private void getUserInfo(TextView userName, ImageView imageView, String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    userName.setText(user.getUserName());
                    Glide.with(context).load(user.getImageUrl()).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPostImage(ImageView imageView, String postId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                if (post != null) {
                    Glide.with(context).load(post.getPostImage()).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
