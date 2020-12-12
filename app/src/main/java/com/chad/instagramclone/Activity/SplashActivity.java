package com.chad.instagramclone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.chad.instagramclone.MainActivity;
import com.chad.instagramclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initialize();
    }

    private void initialize() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (firebaseUser != null) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(SplashActivity.this, StartActivity.class);
                startActivity(intent);
            }
        }, 2000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initialize();
    }
}