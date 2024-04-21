package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class DeleteUser {
    public static void deleteFirestoreData(FirebaseFirestore db, String userid, Context context, AppCompatActivity activity){
        db.collection("users").document(userid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context, "User deleted sucessfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(activity.getApplicationContext(), Login.class);
                activity.startActivity(intent);
                activity.finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed to delete user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void deleteUserAccount(FirebaseUser user, Context context,AppCompatActivity activity){
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "User account is deleted", Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().signOut();
                        }
                    }
                });
    }

    public static void undoRequestForDeletion(FirebaseFirestore db, String userid, Context context){
        Map<String, Object> user = new HashMap<>();
        user.put("dateRequestDelete", "");
        user.put("deletionRequest", "false");
        user.put("readyToDelete", "false");
        db.collection("users").document(userid).update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context, "Request removed successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed to save changes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void requestForDeletion(FirebaseFirestore db, String userid, Context context){
        Map<String, Object> user = new HashMap<>();
        user.put("dateRequestDelete", "");
        user.put("deletionRequest", "true");
        db.collection("users").document(userid).update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context, "Requested Successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed to save changes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void grantRequestForDeletionAdmin(FirebaseFirestore db, Context context, AppCompatActivity activity){
        Map<String, Object> user = new HashMap<>();
        user.put("readyToDelete", "true");
        db.collection("users").document(App.user.getId()).update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context, "User can now delete the account", Toast.LENGTH_SHORT).show();
                activity.finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed to save changes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void undoRequestForDeletionAdmin(FirebaseFirestore db, String userid, Context context, AppCompatActivity activity){
        Map<String, Object> user = new HashMap<>();
        user.put("dateRequestDelete", "");
        user.put("deletionRequest", "false");
        user.put("readyToDelete", "false");
        db.collection("users").document(userid).update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context, "Request removed successfully", Toast.LENGTH_SHORT).show();
                activity.finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed to save changes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void deleteChildrenData(FirebaseFirestore db, String gmail, Context context){
        db.collection("children").whereEqualTo("gmail", gmail).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                db.collection("children").document(documentSnapshot.getId()).delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                            }
                                        });
                            }
                        } else {

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error querying database", Toast.LENGTH_SHORT).show();
                    }
                });
    }



}
