package com.chad.instagramclone.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.chad.instagramclone.Activity.MainActivity;
import com.chad.instagramclone.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText edittextEmail;
    private TextInputEditText edittextPassword;

    private MaterialButton buttonLogin;

    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorWhite));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorWhite));
    }

    private void initialize() {
        edittextEmail = findViewById(R.id.edittextEmailLogin);
        edittextPassword = findViewById(R.id.edittextPasswordLogin);
        buttonLogin = findViewById(R.id.buttonLoginUser);
        progressBar = findViewById(R.id.loginProgressBar);

        firebaseAuth = FirebaseAuth.getInstance();

        buttonLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {

        if (Objects.requireNonNull(edittextEmail.getText()).toString().trim().isEmpty()) {
            Toast.makeText(this, "Email can't be empty", Toast.LENGTH_SHORT).show();
            return;
        } else if (Objects.requireNonNull(edittextPassword.getText()).toString().trim().isEmpty()) {
            Toast.makeText(this, "Please input a password", Toast.LENGTH_SHORT).show();
            return;
        } else if (Objects.requireNonNull(edittextPassword.getText()).length() < 6) {
            Toast.makeText(this, "Password is too short", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        buttonLogin.setVisibility(View.GONE);

        String email = edittextEmail.getText().toString();
        String password = edittextPassword.getText().toString();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                        .child("Users").child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressBar.setVisibility(View.GONE);
                        buttonLogin.setVisibility(View.VISIBLE);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);
                        buttonLogin.setVisibility(View.VISIBLE);
                    }
                });
            }
        })
                .addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            buttonLogin.setVisibility(View.VISIBLE);
            Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
        });
    }
}