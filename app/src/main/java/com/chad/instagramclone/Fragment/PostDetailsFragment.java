package com.chad.instagramclone.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.instagramclone.Adapter.PostAdapter;
import com.chad.instagramclone.Constants.Constants;
import com.chad.instagramclone.Model.Post;
import com.chad.instagramclone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostDetailsFragment extends Fragment {

    private String postId;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> list;
    private ImageView imageClose;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_details, container, false);
        initialize(view);
        return view;
    }

    private void initialize(View view) {
        recyclerView = view.findViewById(R.id.postDetailsRecyclerView);
        imageClose = view.findViewById(R.id.imageClose);

        SharedPreferences preferences = getContext().getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        postId = preferences.getString(Constants.POST_ID, "none");

        list = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(postAdapter);
        recyclerView.setLayoutManager(layoutManager);

        imageClose.setOnClickListener(v -> {

        });

        readPosts();
    }

    private void readPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                Post post = snapshot.getValue(Post.class);
                list.add(post);

                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}