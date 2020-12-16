package com.chad.instagramclone.Activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chad.instagramclone.Constants.Constants;
import com.chad.instagramclone.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    private ImageView imagePost;

    private TextView textPost;

    private TextInputEditText edittextCaption;

    private Uri imageUri;

    private String url;

    private StorageReference reference;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        int s = intent.getIntExtra("themeId", R.style.AppTheme);

        setTheme(s);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        initialize();
    }

    private void initialize() {
        reference = FirebaseStorage.getInstance().getReference("Posts");

        ImageView imageClose = findViewById(R.id.imageClose);
        imagePost = findViewById(R.id.imagePost);
        textPost = findViewById(R.id.textPost);
        edittextCaption = findViewById(R.id.edittextCaption);
        progressBar = findViewById(R.id.postProgressBar);

        imageClose.setOnClickListener(v -> {
            Intent intent = new Intent(PostActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        textPost.setOnClickListener(v -> uploadImage());

        CropImage.activity()
                .setAspectRatio(1,1)
                .start(PostActivity.this);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        progressBar.setVisibility(View.VISIBLE);
        textPost.setVisibility(View.GONE);

        if (imageUri != null) {
            StorageReference storageReference = reference.child(System.currentTimeMillis()
            + "." + getFileExtension(imageUri));
            StorageTask uploadTask = storageReference.putFile(imageUri);
            uploadTask.continueWithTask((Continuation) task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return storageReference.getDownloadUrl();
            }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    url = downloadUri.toString();

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

                    String postId = reference.push().getKey();

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put(Constants.POST_ID, postId);
                    hashMap.put(Constants.POST_IMAGE, url);
                    hashMap.put(Constants.CAPTION, edittextCaption.getText().toString());
                    hashMap.put(Constants.PUBLISHER_ID, FirebaseAuth.getInstance().getCurrentUser().getUid());

                    if (postId != null) {
                        reference.child(postId).setValue(hashMap);
                    }

                    progressBar.setVisibility(View.GONE);
                    textPost.setVisibility(View.VISIBLE);

                    startActivity(new Intent(PostActivity.this, MainActivity.class));
                    finish();
                } else {
                    progressBar.setVisibility(View.GONE);
                    textPost.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                progressBar.setVisibility(View.GONE);
                textPost.setVisibility(View.VISIBLE);
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }else {
            Toast.makeText(this, "Please select an image!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null) {
                imageUri = result.getUri();
            }
            imagePost.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(PostActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}