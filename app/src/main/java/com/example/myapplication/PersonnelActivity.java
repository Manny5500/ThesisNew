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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class PersonnelActivity extends AppCompatActivity {

    ConstraintLayout personnelProfile, addData, manageData, logOut;
    ImageView profileImage, addDataImage, manageDataImage, logOutImage;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;

    private ListenerRegistration listenerRegistration;
    String email;
    String userid;
    int color_flag = 0;
    Dialog dialog2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personnel);
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
        showWelcomeDialog();



        final DocumentReference docRef = db.collection("users").document(userid);
        listenerRegistration = docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("Firebase myException", ""+e);
                    //Toast.makeText(PersonnelActivity.this, "" + e, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    if(snapshot.get("readyToDelete") != null &&
                            snapshot.get("readyToDelete").equals("true"))
                        deleteAccountDialog();
                } else {
                    Toast.makeText(PersonnelActivity.this, "No data"+snapshot.getData(), Toast.LENGTH_SHORT).show();
                }

            }
        });


        personnelProfile=findViewById(R.id.btnProfile);
        addData = findViewById(R.id.btnAddData);
        manageData = findViewById(R.id.btnManageData);
        logOut = findViewById(R.id.btnLogout);
        profileImage = findViewById(R.id.profileImage);
        addDataImage = findViewById(R.id.addDataImage);
        manageDataImage = findViewById(R.id.manageDataImage);
        logOutImage = findViewById(R.id.logOutImage);

        setUserData(addDataImage, 2, new ManageData());
        personnelProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new fragmentPersonnelProfile());
                ButtonColorizer(profileImage);
                color_flag = 1;
            }
        });

        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUserData(addDataImage, 2, new ManageData());
            }
        });
        manageData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                replaceFragment(new FragmentProfiling());
                ButtonColorizer(manageDataImage);
                color_flag = 3;*/
                setUserData(manageDataImage, 3, new FragmentProfiling());
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ButtonColorizer(logOutImage);
                color_flag = 4;
                showLogoutDialog();
            }
        });
    }

    private void setUserData(ImageView imageView, int flag_color, Fragment fragment){
        dialog2 = new Dialog(PersonnelActivity.this);
        dialog2.setContentView(R.layout.dialog_loader);
        dialog2.setCanceledOnTouchOutside(false);
        dialog2.setCancelable(false);
        dialog2.show();
        DocumentReference docRefs = db.collection("users").document(userid);
        docRefs.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        App.user = null;
                        User user = document.toObject(User.class);
                        user.setId(document.getId());
                        App.user = user;
                        runFragment(imageView, flag_color, fragment);


                    } else {
                        Log.d("Firetore No Docu", "No such document");
                    }
                } else {
                    Log.e("Firestore Exception", "get failed with ", task.getException());
                }
            }
        });
    }

    private void runFragment(ImageView imageView, int flag_color, Fragment fragment){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                replaceFragment(fragment);
                ButtonColorizer(imageView);
                color_flag = flag_color;
                dialog2.dismiss();
            }
        }, 500);
    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager =  getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutPersonnel,fragment);
        fragmentTransaction.commit();
    }


    private void ButtonColorizer(ImageView button){
        button.setColorFilter(ContextCompat.getColor(this, android.R.color.white));
        if (color_flag ==1) {
            profileImage.setColorFilter(ContextCompat.getColor(this, android.R.color.white));
        }else if(color_flag==2){
            addDataImage.setColorFilter(ContextCompat.getColor(this, android.R.color.white));
        }else if(color_flag ==3){
            manageDataImage.setColorFilter(ContextCompat.getColor(this, android.R.color.white));
        }else if(color_flag ==4){
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

                undoRequestForDeletion();
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
                DeleteUser.deleteUserAccount(user, PersonnelActivity.this, PersonnelActivity.this);
                DeleteUser.deleteFirestoreData(db, userid, PersonnelActivity.this, PersonnelActivity.this);
                dialog.dismiss();
            }
        });


        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                undoRequestForDeletion();
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void undoRequestForDeletion(){
        Map<String, Object> user = new HashMap<>();
        user.put("dateRequestDelete", "");
        user.put("deletionRequest", "false");
        user.put("readyToDelete", "false");
        db.collection("users").document(userid).update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(PersonnelActivity.this, "Request removed successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PersonnelActivity.this, "Failed to save changes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteFirestoreData(){
        db.collection("users").document(userid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(PersonnelActivity.this, "User deleted sucessfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PersonnelActivity.this, "Failed to delete user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showWelcomeDialog(){
        final Dialog dialog = new Dialog(PersonnelActivity.this);
        dialog.setContentView(R.layout.bns_dialog);
        Window window = dialog.getWindow();
        window.setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();

    }

    private void showLogoutDialog(){
        final Dialog dialog = new Dialog(PersonnelActivity.this);
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