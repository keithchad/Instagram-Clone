package com.chad.instagramclone.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.instagramclone.Constants.Constants;
import com.chad.instagramclone.Fragment.ProfileFragment;
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

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private final Context context;
    private final List<User> list;

    private FirebaseUser firebaseUser;

    public UserAdapter(Context context, List<User> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        User user = list.get(position);

        holder.buttonFollow.setVisibility(View.VISIBLE);
        holder.textUserName.setText(user.getUserName());
        holder.textFullName.setText(user.getFullName());
        Glide.with(context).load(user.getImageUrl()).into(holder.imageSearchProfile);
        isFollowing(user.getId(), holder.buttonFollow);

        if (user.getId().equals(firebaseUser.getUid())) {
            holder.buttonFollow.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener((View.OnClickListener) v -> {
            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = (SharedPreferences.Editor) context.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE).edit();
            editor.putString(Constants.SHARED_PREF_PROFILE_ID, user.getId());
            editor.apply();
            ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new ProfileFragment()).commit();
        });

        holder.buttonFollow.setOnClickListener(v -> {
            if (holder.buttonFollow.getText().toString().equals("follow")) {
                FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                        .child("following").child(user.getId()).setValue(true);
                FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                        .child("followers").child(firebaseUser.getUid()).setValue(true);
            } else {
                FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                        .child("following").child(user.getId()).removeValue();
                FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                        .child("followers").child(firebaseUser.getUid()).removeValue();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textUserName;
        private final TextView textFullName;
        private final ImageView imageSearchProfile;
        private final MaterialButton buttonFollow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageSearchProfile = itemView.findViewById(R.id.imageSearchProfile);
            textUserName = itemView.findViewById(R.id.textUsername);
            textFullName = itemView.findViewById(R.id.textFullname);
            buttonFollow = itemView.findViewById(R.id.buttonFollow);
        }
    }

    private void isFollowing(String userId, Button button) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                .child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(userId).exists()) {
                    button.setText(R.string.following);
                } else {
                    button.setText(R.string.follow);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
