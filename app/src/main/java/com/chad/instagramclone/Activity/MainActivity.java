package com.chad.instagramclone.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.chad.instagramclone.Constants.Constants;
import com.chad.instagramclone.Fragment.HomeFragment;
import com.chad.instagramclone.Fragment.NotificationFragment;
import com.chad.instagramclone.Fragment.ProfileFragment;
import com.chad.instagramclone.Fragment.SearchFragment;
import com.chad.instagramclone.R;
import com.chad.instagramclone.Util.AppSettings;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Fragment selectedFragment = null;
    private int selectedTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppSettings appSettings = new AppSettings(this);
        if (appSettings.getTheme() == Constants.THEME_DARK) {
            selectedTheme = R.style.DarkTheme;
        } else {
            selectedTheme = R.style.AppTheme;
        }
        setTheme(selectedTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initialize();

        if(selectedTheme == R.style.DarkTheme) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorBlack));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorBlack));
        } else {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorWhite));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorWhite));
        }
    }

    private void initialize() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        Bundle intent = getIntent().getExtras();
        if (intent != null) {
            String publisher = intent.getString(Constants.PUBLISHER_ID);

            SharedPreferences.Editor editor = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE).edit();
            editor.putString(Constants.SHARED_PREF_PROFILE_ID, publisher);
            editor.apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                    new ProfileFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                    new HomeFragment()).commit();
        }
    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener =
            item -> {
                switch (item.getItemId()) {

                    case R.id.home:
                        selectedFragment = new HomeFragment();
                        break;
                    case R.id.search:
                        selectedFragment = new SearchFragment();
                        break;
                    case R.id.addPhoto:
                        Intent intent = new Intent(MainActivity.this, PostActivity.class);
                        intent.putExtra("themeId", selectedTheme);
                        startActivity(intent);
                        break;
                    case R.id.notification:
                        selectedFragment = new NotificationFragment();
                        break;
                    case R.id.profile:
                            SharedPreferences.Editor editor = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE).edit();
                            editor.putString(Constants.SHARED_PREF_PROFILE_ID, Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                            editor.apply();
                        selectedFragment = new ProfileFragment();
                        break;
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                            selectedFragment).commit();
                }
                return true;
    };

}
