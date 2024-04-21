package com.example.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileUtils {
    public static void getProfile(FirebaseFirestore db, String userid, Context context,
                            TextView textage, TextView textname, TextView textaddress,
                            TextView textemail, TextView textcontact, CircleImageView imagePersonnel){
        db.collection("users").document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    String name = null, age = null, address = null, email = null, contact = null, imageLink = null;
                    if(doc.exists()) {
                        name = doc.getString("firstName") + " " + doc.getString("middleName") + " " + doc.getString("lastName");
                        age = doc.getString("birthdate");
                        FormUtils formUtils = new FormUtils();
                        Date parsedDate = formUtils.parseDate(age);
                        if (parsedDate != null) {
                            age = String.valueOf(formUtils.calculateMonthsDifference(parsedDate)/12);
                        } else {
                            Toast.makeText(context, "Failed to parse the date", Toast.LENGTH_SHORT).show();
                        }
                        address = doc.getString("barangay") + ", Magdalena, Laguna";
                        email = doc.getString("email");
                        contact = doc.getString("contact");
                        imageLink = doc.getString("imageUrl");
                    }
                    displayProfile(name, age, address, email, contact, imageLink, context, textage, textname, textaddress,
                            textemail, textcontact, imagePersonnel);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed to get all users", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static void displayProfile(String name, String age, String address, String email,
                                String contact, String imageLink, Context context,
                                TextView textage, TextView textname, TextView textaddress,
                                TextView textemail, TextView textcontact, CircleImageView imagePersonnel){
        textage.setText(age);
        textname.setText(name);
        textaddress.setText(address);
        textemail.setText(email);
        textcontact.setText(contact);
        if (context != null) {
            Glide.with(context)
                    .load(imageLink) // Load the image from the URL
                    .placeholder(R.drawable.nutriassist_logo) // Placeholder image while loading (drawable resource ID)
                    .error(R.drawable.nutriassist_logo) // Error image if loading fails (drawable resource ID)
                    .override(300, 300) // Set the target dimensions
                    .centerCrop()
                    .into(imagePersonnel);

        }
    }


}
