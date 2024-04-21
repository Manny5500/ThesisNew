package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;
import java.util.Map;

public class ParentActivity extends AppCompatActivity {
    ConstraintLayout parentProfile, children, parentLogout;
    ImageView profileImage, cImage, logOutImage;
    String email, userid;
    int color_flag = 0;
    FirebaseAuth auth;
    FirebaseUser user;

    private ListenerRegistration listenerRegistration;

    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);
        showWelcomeDialog();

        email="";
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        db = FirebaseFirestore.getInstance();
        if (user == null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            email = user.getEmail();
            userid = user.getUid();
        }

        final DocumentReference docRef = db.collection("users").document(userid);
        listenerRegistration = docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("Firebase EXCEPTION", ""+e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    if(snapshot.get("readyToDelete") != null &&
                            snapshot.get("readyToDelete").equals("true"))
                        deleteAccountDialog();
                }

            }
        });

        parentProfile = findViewById(R.id.btnParentProfile);
        children = findViewById(R.id.btnChildren);
        parentLogout = findViewById(R.id.btnLogOut);
        profileImage = findViewById(R.id.profileImage);
        cImage = findViewById(R.id.cImage);
        logOutImage = findViewById(R.id.logOutImage);
        ButtonColorizer(cImage);
        color_flag=2;
        replaceFragment(new ParentChildrenList());
        parentProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new ParentProfile());
                // Set the clicked button color to indicate selection
                ButtonColorizer(profileImage);
                color_flag = 1;
            }
        });

        children.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new ParentChildrenList());
                ButtonColorizer(cImage);
                color_flag = 2;
            }
        });

        parentLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ButtonColorizer(logOutImage);
                color_flag = 3;
                showLogoutDialog();
            }
        });
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager =  getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();
    }
    private void ButtonColorizer(ImageView button){
        if (color_flag ==1) {
            profileImage.setColorFilter(ContextCompat.getColor(this, android.R.color.white));
        }else if(color_flag==2){
            cImage.setColorFilter(ContextCompat.getColor(this, android.R.color.white));
        }else if(color_flag ==3){
            logOutImage.setColorFilter(ContextCompat.getColor(this, android.R.color.white));
        }
    }
    private void showYesNoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Do you want to log out?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
                dialog.dismiss();
                if (listenerRegistration != null) {
                    listenerRegistration.remove();
                }
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void deleteAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("The admin grants your request to delete your account.\n" +
                "Are you sure you want to permanently delete your account?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DeleteUser.deleteUserAccount(user, ParentActivity.this, ParentActivity.this);
                DeleteUser.deleteFirestoreData(db, userid, ParentActivity.this, ParentActivity.this);
                dialog.dismiss();
            }
        });


        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DeleteUser.undoRequestForDeletion(db, userid, ParentActivity.this);
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void showWelcomeDialog(){
        final Dialog dialog = new Dialog(ParentActivity.this);
        dialog.setContentView(R.layout.parent_dialog);
        Window window = dialog.getWindow();
        window.setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();

    }

    private void showLogoutDialog(){
        final Dialog dialog = new Dialog(ParentActivity.this);
        dialog.setContentView(R.layout.logout_dialog);

        Button buttonNo = dialog.findViewById(R.id.buttonNo);
        Button buttonYes = dialog.findViewById(R.id.buttonYes);


        Window window = dialog.getWindow();
        window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.show();

        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
                dialog.dismiss();
                if (listenerRegistration != null) {
                    listenerRegistration.remove();
                }
            }
        });
    }


}