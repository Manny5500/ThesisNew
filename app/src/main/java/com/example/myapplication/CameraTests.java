package com.example.myapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class CameraTests extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 2;
    private Uri imageUri;
    private StorageReference storageReference;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private FirebaseFirestore db;



    private String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_tests);
        storageReference = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("email")) {
            email = intent.getStringExtra("email");
        }
        // Check for camera permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Request permission if not granted
            Toast.makeText(this, "Camera permission not granted. Camera functionality disabled.",
                    Toast.LENGTH_SHORT).show();
            finish();

        } else {
            setupCameraLauncher();
            dispatchTakePictureIntent();

        }
    }
    private void setupCameraLauncher() {
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                // Get the captured image as a bitmap
                                Bitmap imageBitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");

                                // Save the bitmap to a file and get the file's URI
                                imageUri = saveBitmapToFile(imageBitmap);

                                // Upload the image to Firebase Storage
                                uploadImageToFirebaseStorage();
                            }
                        } else {
                            Toast.makeText(CameraTests.this, "Image capture canceled", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(takePictureIntent);
        }
    }

    private Uri saveBitmapToFile(Bitmap bitmap) {
        // Create a file to save the bitmap
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MyApp");
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }

        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + System.currentTimeMillis() + ".jpg");

        try (FileOutputStream out = new FileOutputStream(mediaFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Return the file's URI
        return Uri.fromFile(mediaFile);
    }

    private void uploadImageToFirebaseStorage() {
        if (imageUri != null) {
            StorageReference photoRef = storageReference.child("images/" + imageUri.getLastPathSegment());
            photoRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        photoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();

                            if (email != null) {
                                Query query = db.collection("users").whereEqualTo("email", email);
                                ImageQueryUpload(query, downloadUrl);
                            } else {
                                // Handle the case where the user ID is null
                                Toast.makeText(CameraTests.this, "User ID is null", Toast.LENGTH_SHORT).show();
                            }
                            finish();
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(CameraTests.this, "Upload failed: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    });
        }
    }
    private void ImageQueryUpload(Query query, String imageUrl){
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Update the "imageUrl" field of each matching document with the download URL
                        db.collection("users").document(document.getId()).update("imageUrl", imageUrl)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Handle failures
                                        Toast.makeText(CameraTests.this, "Failed to update image URL in user documents", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } else {
                    Toast.makeText(CameraTests.this, "Error fetching documents", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}