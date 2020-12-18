package com.chad.instagramclone.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.instagramclone.Activity.EditProfileActivity;
import com.chad.instagramclone.Adapter.UserPhotoAdapter;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private SwipeRefreshLayout swipeRefreshLayout;


    private String profileId;
    private FirebaseUser firebaseUser;

    private RecyclerView userPhotosRecyclerView;
    private UserPhotoAdapter userPhotoAdapter;
    private List<Post> userList;

    private List<String> mySaves;
    private RecyclerView savedPhotosRecyclerView;
    private UserPhotoAdapter savedPhotoAdapter;
    private List<Post> savedList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initialize(view);
        swipeRefreshLayout.setRefreshing(true);
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void initialize(View view) {
        ImageView imageOptions = view.findViewById(R.id.imageOptions);
        imageProfile = view.findViewById(R.id.imageProfileUser);
        textUsername = view.findViewById(R.id.textUsernameProfile);
        textPosts = view.findViewById(R.id.textPosts);
        textFollowers = view.findViewById(R.id.textFollowers);
        textFollowing = view.findViewById(R.id.textFollowing);
        textFullname = view.findViewById(R.id.textFullNameProfile);
        textBio = view.findViewById(R.id.textBio);
        editProfileButton = view.findViewById(R.id.editProfileButton);
        ImageButton userPhotos = view.findViewById(R.id.userPhotos);
        ImageButton savedPhotos = view.findViewById(R.id.savedPhotos);
        userPhotosRecyclerView = view.findViewById(R.id.userPhotosRecyclerView);
        savedPhotosRecyclerView = view.findViewById(R.id.savedPhotosRecyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutProfile);
        swipeRefreshLayout.setOnRefreshListener(this::getUserInfo);

        userList = new ArrayList<>();
        userPhotoAdapter = new UserPhotoAdapter(getActivity(), userList);
        GridLayoutManager userLayoutManager = new GridLayoutManager(getContext(), 3);
        userPhotosRecyclerView.setHasFixedSize(true);
        userPhotosRecyclerView.setAdapter(userPhotoAdapter);
        userPhotosRecyclerView.setLayoutManager(userLayoutManager);

        savedList = new ArrayList<>();
        savedPhotoAdapter = new UserPhotoAdapter(getActivity(), savedList);
        GridLayoutManager savedLayoutManager = new GridLayoutManager(getContext(), 3);
        savedPhotosRecyclerView.setHasFixedSize(true);
        savedPhotosRecyclerView.setAdapter(savedPhotoAdapter);
        savedPhotosRecyclerView.setLayoutManager(savedLayoutManager);

        savedPhotosRecyclerView.setVisibility(View.GONE);
        userPhotosRecyclerView.setVisibility(View.VISIBLE);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences preferences = Objects.requireNonNull(getContext()).getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        profileId = preferences.getString(Constants.SHARED_PREF_PROFILE_ID, "none");

        editProfileButton.setOnClickListener(v -> {
            String button = editProfileButton.getText().toString();
            switch (button) {
                case "Edit Profile":
                    Intent intent = new Intent(getContext(), EditProfileActivity.class);
                    startActivity(intent);
                    break;
                case "Follow":
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileId).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                            .child("followers").child(firebaseUser.getUid()).setValue(true);
                    break;
                case "Following":
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileId).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                            .child("followers").child(firebaseUser.getUid()).removeValue();
                    break;
            }
        });

        imageOptions.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "", Toast.LENGTH_SHORT).show();
            Toast.makeText(requireContext(), "", Toast.LENGTH_SHORT).show();
        });

        getUserInfo();
        getFollowers();
        getNumberOfPosts();
        getUserPhotos();
        getMySaves();

        if (profileId.equals(firebaseUser.getUid())) {
            editProfileButton.setText("Edit Profile");
        } else {
            checkFollowing();
            savedPhotos.setVisibility(View.GONE);
        }

        userPhotos.setOnClickListener(v -> {
            savedPhotosRecyclerView.setVisibility(View.GONE);
            userPhotosRecyclerView.setVisibility(View.VISIBLE);
        });

        savedPhotos.setOnClickListener(v -> {
            savedPhotosRecyclerView.setVisibility(View.VISIBLE);
            userPhotosRecyclerView.setVisibility(View.GONE);
        });

    }

    private void getUserInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(profileId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                swipeRefreshLayout.setRefreshing(false);
                if (getContext() == null) {
                    return;
                }
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    Glide.with(requireContext()).load(user.getImageUrl()).into(imageProfile);
                    textBio.setText(user.getBio());
                    textUsername.setText(user.getUserName());
                    textFullname.setText(user.getFullName());
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

    private void getUserPhotos() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post != null && post.getPublisherId().equals(profileId)) {
                        userList.add(post);
                    }
                }
                Collections.reverse(userList);
                userPhotoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMySaves() {
        mySaves = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    mySaves.add(dataSnapshot.getKey());
                }
                readSaves();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readSaves() {
        mySaves = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                savedList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    for (String id: mySaves) {
                        if (post != null && post.getPostId().equals(id)) {
                            savedList.add(post);
                        }
                    }
                }
                savedPhotoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}