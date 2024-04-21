package com.example.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ImageProcessUtils {

    public static void uploadImage(Uri imageUri, StorageReference storageRef, Dialog dialog2,
                             String gmail, FirebaseFirestore db, Context context, String userid) {
        // Create a unique filename for the image (optional)
        String fileName = "image_" + System.currentTimeMillis() + ".jpg";
        StorageReference imageRef = storageRef.child("images/" + fileName);
        dialog2.show();

        // Upload the image to Firebase Storage
        imageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                if (gmail != null) {
                                    Query query = db.collection("users").whereEqualTo("email", gmail);
                                    ImageQueryUpload(imageUrl, db, userid, dialog2, context);
                                } else {
                                    Toast.makeText(context, "User ID is null", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
    public static void ImageQueryUpload(String imageUrl, FirebaseFirestore db,
                                        String userid, Dialog dialog2, Context context){
        db.collection("users").document(userid).update("imageUrl", imageUrl)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog2.dismiss();
                        Toast.makeText(context, "Successfully changed the profile picture", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failures
                        Toast.makeText(context, "Failed to update image URL in user documents", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
