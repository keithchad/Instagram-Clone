package com.chad.instagramclone.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.chad.instagramclone.Constants.Constants;
import com.chad.instagramclone.MainActivity;
import com.chad.instagramclone.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText edittextUsername;
    private TextInputEditText edittextEmail;
    private TextInputEditText edittextPassword;
    private TextInputEditText edittextConfirmPassword;
    private TextInputEditText edittextFullname;

    private MaterialButton buttonRegister;

    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initialize();
    }

    private void initialize() {
        edittextUsername = findViewById(R.id.edittextUsernameRegister);
        edittextEmail = findViewById(R.id.edittextEmailRegister);
        edittextPassword = findViewById(R.id.edittextPasswordRegister);
        edittextConfirmPassword = findViewById(R.id.edittextConfirmPasswordRegister);
        edittextFullname = findViewById(R.id.edittextFullnameRegister);
        buttonRegister = findViewById(R.id.buttonRegisterUser);
        progressBar = findViewById(R.id.registerProgressBar);

        firebaseAuth = FirebaseAuth.getInstance();

        buttonRegister.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            buttonRegister.setVisibility(View.GONE);
            registerUser();
        });
    }

    private void registerUser() {

        if (Objects.requireNonNull(edittextUsername.getText()).toString().trim().isEmpty()) {
            Toast.makeText(this, "Username can't be empty", Toast.LENGTH_SHORT).show();
            return;
        } else if (Objects.requireNonNull(edittextFullname.getText()).toString().trim().isEmpty()) {
            Toast.makeText(this, "Your Fullname is needed", Toast.LENGTH_SHORT).show();
            return;
        } else if (Objects.requireNonNull(edittextEmail.getText()).toString().trim().isEmpty()) {
            Toast.makeText(this, "Email can't be empty", Toast.LENGTH_SHORT).show();
            return;
        } else if (Objects.requireNonNull(edittextPassword.getText()).toString().trim().isEmpty()) {
            Toast.makeText(this, "Please input a password", Toast.LENGTH_SHORT).show();
            return;
        } else if (Objects.requireNonNull(edittextPassword.getText()).length() < 6) {
            Toast.makeText(this, "Password is too short", Toast.LENGTH_SHORT).show();
            return;
        } else if(!Objects.requireNonNull(edittextConfirmPassword.getText()).toString().equals(edittextPassword.getText().toString())) {
            Toast.makeText(this, "Both Passwords should be the same", Toast.LENGTH_SHORT).show();
            return;
        }

        String userName = edittextUsername.getText().toString();
        String email = edittextEmail.getText().toString();
        String password = edittextPassword.getText().toString();
        String fullName = edittextFullname.getText().toString();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                String userId = firebaseUser.getUid();

                reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(Constants.USER_ID, userId);
                hashMap.put(Constants.USER_NAME, userName.toLowerCase());
                hashMap.put(Constants.FULL_NAME, fullName);
                hashMap.put(Constants.BIO, "");
                hashMap.put(Constants.IMAGE_URL, "");

                reference.setValue(hashMap).addOnCompleteListener(task1 -> {
                    progressBar.setVisibility(View.GONE);
                    buttonRegister.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                });
            }
        })
                .addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            buttonRegister.setVisibility(View.VISIBLE);
            Toast.makeText(this, "You can't register now. Try again later!", Toast.LENGTH_SHORT).show();
        });
    }

}