package com.chad.instagramclone.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.instagramclone.Activity.CommentsActivity;
import com.chad.instagramclone.Constants.Constants;
import com.chad.instagramclone.Fragment.ProfileFragment;
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
import java.util.Objects;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private final Context context;
    private final List<Post> list;

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
        isLiked(post.getPostId(), holder.imageLike);
        numberOfLikes(holder.textLikes, post.getPostId());
        getComments(post.getPostId(), holder.textComment);
        isSaved(post.getPostId(), holder.imageSave);

        holder.profileImage.setOnClickListener(v -> {
            SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE).edit();
            editor.putString(Constants.SHARED_PREF_PROFILE_ID, post.getPublisherId());
            editor.apply();

            ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                    new ProfileFragment());

        });

        holder.textUsername.setOnClickListener(v -> {
            SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE).edit();
            editor.putString(Constants.SHARED_PREF_PROFILE_ID, post.getPublisherId());
            editor.apply();

            ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                    new ProfileFragment());

        });

        holder.textPublisher.setOnClickListener(v -> {
            SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE).edit();
            editor.putString(Constants.SHARED_PREF_PROFILE_ID, post.getPublisherId());
            editor.apply();

            ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                    new ProfileFragment());

        });

        holder.postImage.setOnClickListener(v -> {
            SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE).edit();
            editor.putString(Constants.POST_ID, post.getPostId());
            editor.apply();

            ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                    new ProfileFragment());

        });

        holder.imageLike.setOnClickListener(v -> {
            if (holder.imageLike.getTag().equals("Like")) {
                FirebaseDatabase.getInstance().getReference().child("Likes")
                        .child(post.getPostId()).child(firebaseUser.getUid()).setValue(true);
            } else {
                FirebaseDatabase.getInstance().getReference().child("Likes")
                        .child(post.getPostId()).child(firebaseUser.getUid()).removeValue();
            }
        });

        holder.imageComment.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentsActivity.class);
            intent.putExtra(Constants.POST_ID, post.getPostId());
            intent.putExtra(Constants.PUBLISHER_ID, post.getPublisherId());
            context.startActivity(intent);
        });

        holder.textComment.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentsActivity.class);
            intent.putExtra(Constants.POST_ID, post.getPostId());
            intent.putExtra(Constants.PUBLISHER_ID, post.getPublisherId());
            context.startActivity(intent);
        });

        holder.imageSave.setOnClickListener(v -> {
            if (holder.imageSave.getTag().equals("Save")) {
                FirebaseDatabase.getInstance().getReference().child("Save")
                        .child(firebaseUser.getUid())
                        .child(post.getPostId())
                        .setValue(true);
            } else {
                FirebaseDatabase.getInstance().getReference().child("Save")
                        .child(firebaseUser.getUid())
                        .child(post.getPostId())
                        .removeValue();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView profileImage;
        private final ImageView postImage;
        private final ImageView imageLike;
        private final ImageView imageComment;
        private final ImageView imageInbox;
        private final ImageView imageSave;

        private final TextView textUsername;
        private final TextView textCaption;
        private final TextView textComment;
        private final TextView textLikes;
        private final TextView textPublisher;

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

    private void getComments(String postId, TextView comments) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comments.setText("View All " + snapshot.getChildrenCount() + " Comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void isLiked(String postId, ImageView imageView) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (firebaseUser != null && snapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("Liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_favorite_border);
                    imageView.setTag("Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    
    private void numberOfLikes(TextView likes, String postId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText(snapshot.getChildrenCount()+" likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void publisherInfo(ImageView imageProfile, TextView userName,
                               TextView publisher, String userId ) {
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

    private void isSaved(String postId, ImageView imageView) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Saves")
                .child(Objects.requireNonNull(firebaseUser).getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postId).exists()) {
                    imageView.setImageResource(R.drawable.ic_bookmark);
                    imageView.setTag("saved");
                } else {
                    imageView.setImageResource(R.drawable.ic_bookmark_border);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    
}
