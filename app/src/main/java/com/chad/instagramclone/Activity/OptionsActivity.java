package com.chad.instagramclone.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.chad.instagramclone.Constants.Constants;
import com.chad.instagramclone.R;
import com.chad.instagramclone.Util.AppSettings;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class OptionsActivity extends AppCompatActivity {

    private AppSettings appSettings;
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
        setContentView(R.layout.activity_options);
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
        MaterialButton buttonLogout = findViewById(R.id.buttonLogout);
        LinearLayout themeLayout = findViewById(R.id.themeLayout);

        buttonLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(OptionsActivity.this, SplashActivity.class);
            startActivity(intent);
        });
        themeLayout.setOnClickListener(v -> openDialog());
    }

    private void openDialog() {
        Dialog dialog = new Dialog(this, R.style.FullScreenDialogTheme);
        dialog.setContentView(R.layout.theme_dialog);
        dialog.show();
        initViews(dialog);

    }

    private void initViews(Dialog dialog) {
        RadioGroup radioGroupTheme = dialog.findViewById(R.id.radioGroupTheme);
        RadioButton radioButtonLightTheme = dialog.findViewById(R.id.radioButtonLightTheme);
        RadioButton radioButtonDarkTheme = dialog.findViewById(R.id.radioButtonDarkTheme);

        appSettings = new AppSettings(this);

        if (appSettings.getTheme() == Constants.THEME_DARK) {
            radioButtonDarkTheme.setChecked(true);
        } else {
            radioButtonLightTheme.setChecked(true);
        }

        radioGroupTheme.setOnCheckedChangeListener((group, checkedId) -> {

            if (checkedId == R.id.radioButtonDarkTheme) {
                appSettings.setTheme(Constants.THEME_DARK);
            } else {
                appSettings.setTheme(Constants.THEME_LIGHT);
            }

            Intent intent = new Intent(OptionsActivity.this, SplashActivity.class);
            startActivity(intent);

        });
    }
}