package com.chad.instagramclone.Adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.instagramclone.Model.Post;
import com.chad.instagramclone.Model.User;
import com.chad.instagramclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context context;
    private List<Post> list;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context context, List<Post> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Post post = list.get(position);
        Glide.with(context).load(post.getPostImage()).into(holder.postImage);

        if (post.getCaption().equals("")) {
            holder.textCaption.setVisibility(View.GONE);
        } else {
            holder.textCaption.setVisibility(View.VISIBLE);
            holder.textCaption.setText(post.getCaption());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.imageInbox.setTooltipText("Inbox");
        }
        holder.imageInbox.setOnClickListener(v -> Toast.makeText(context, "Inbox", Toast.LENGTH_SHORT).show());

        publisherInfo(holder.profileImage, holder.textUsername, holder.textPublisher, post.getPublisherId());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView profileImage;
        private ImageView postImage;
        private ImageView imageLike;
        private ImageView imageComment;
        private ImageView imageInbox;
        private ImageView imageSave;

        private TextView textUsername;
        private TextView textCaption;
        private TextView textComment;
        private TextView textLikes;
        private TextView textPublisher;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profileImage);
            postImage = itemView.findViewById(R.id.postImage);
            imageLike = itemView.findViewById(R.id.imageLike);
            imageComment = itemView.findViewById(R.id.imageComment);
            imageInbox = itemView.findViewById(R.id.imageInbox);
            imageSave = itemView.findViewById(R.id.imageSave);
            textUsername = itemView.findViewById(R.id.textUsername);
            textCaption = itemView.findViewById(R.id.textCaption);
            textComment = itemView.findViewById(R.id.textComments);
            textLikes = itemView.findViewById(R.id.textLikes);
            textPublisher = itemView.findViewById(R.id.textPublisher);
        }
    }

    private void publisherInfo(ImageView imageProfile, TextView userName, TextView publisher, String userId ) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    Glide.with(context).load(user.getImageUrl()).into(imageProfile);
                    userName.setText(user.getUserName());
                    publisher.setText(user.getUserName());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
