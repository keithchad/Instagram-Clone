package com.chad.instagramclone.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.instagramclone.Constants.Constants;
import com.chad.instagramclone.Model.Post;
import com.chad.instagramclone.Model.User;
import com.chad.instagramclone.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private ImageView imageProfile;
    private TextView textPosts;
    private TextView textFollowers;
    private TextView textFollowing;
    private TextView textFullname;
    private TextView textUsername;
    private TextView textBio;
    private MaterialButton editProfileButton;

    private String profileId;
    private FirebaseUser firebaseUser;

    private ImageButton userPhotos;
    private ImageButton savedPhotos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initialize(view);
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void initialize(View view) {
        ImageView imageOptions = view.findViewById(R.id.imageOptions);
        imageProfile = view.findViewById(R.id.imageProfile);
        textUsername = view.findViewById(R.id.textUsername);
        textPosts = view.findViewById(R.id.textPosts);
        textFollowers = view.findViewById(R.id.textFollowers);
        textFollowing = view.findViewById(R.id.textFollowing);
        textFullname = view.findViewById(R.id.textFullname);
        textBio = view.findViewById(R.id.textBio);
        editProfileButton = view.findViewById(R.id.editProfileButton);
        userPhotos = view.findViewById(R.id.userPhotos);
        savedPhotos = view.findViewById(R.id.savedPhotos);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences preferences = Objects.requireNonNull(getContext()).getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        profileId = preferences.getString(Constants.SHARED_PREF_PROFILE_ID, "none");

        editProfileButton.setOnClickListener(v -> {
            String button = editProfileButton.getText().toString();
            if (button.equals("Edit Profile")) {
                //Go to editProfile
            } else if(button.equals("Follow")) {
                FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                        .child("following").child(profileId).setValue(true);
                FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                        .child("followers").child(firebaseUser.getUid()).setValue(true);
            } else if (button.equals("Following")) {
                FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                        .child("following").child(profileId).removeValue();
                FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                        .child("followers").child(firebaseUser.getUid()).removeValue();
            }
        });

        getUserInfo();
        getFollowers();
        getNumberOfPosts();

        if (profileId.equals(firebaseUser.getUid())) {
            editProfileButton.setText("Edit Profile");
        } else {
            checkFollowing();
            savedPhotos.setVisibility(View.GONE);
        }

    }

    private void getUserInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(profileId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null) {
                    return;
                }
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    Glide.with(requireContext()).load(user.getImageUrl()).into(imageProfile);
                    textBio.setText(user.getBio());
                    textUsername.setText(user.getUserName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkFollowing() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow")
                .child(firebaseUser.getUid())
                .child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(profileId).exists()) {
                    editProfileButton.setText(R.string.follow);
                } else {
                    editProfileButton.setText(R.string.following);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowers() {
        DatabaseReference followersReference = FirebaseDatabase.getInstance().getReference()
                .child("Follow")
                .child(profileId)
                .child("followers");
        followersReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                textFollowers.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference followingReference = FirebaseDatabase.getInstance().getReference()
                .child("Follow")
                .child(profileId)
                .child("following");
        followingReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                textFollowing.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getNumberOfPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post != null && post.getPublisherId().equals(profileId)) {
                        i++;
                    }
                }
                textPosts.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}