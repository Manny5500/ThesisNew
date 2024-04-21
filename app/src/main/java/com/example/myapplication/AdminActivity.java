package com.example.myapplication;

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
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminActivity extends AppCompatActivity {
    ConstraintLayout dashboard, priority, usermanage,  reports, logout;
    ImageView dashboardsImage, priorityImage, UMImage, reportsImage, logOutImage;
    FirebaseAuth auth;
    FirebaseUser user;

    int color_flag = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        showWelcomeDialog();
        if (user == null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }else{
           // Toast.makeText(this, ""+ user.getEmail(), Toast.LENGTH_SHORT).show();
        }


        dashboard = findViewById(R.id.btnDashboards);
        priority = findViewById(R.id.btnPriority);
        usermanage = findViewById(R.id.btnUM);
        logout = findViewById(R.id.btnAdminLogOut);
        reports = findViewById(R.id.btnReports);

        dashboardsImage = findViewById(R.id.dashboardsImage);
        priorityImage = findViewById(R.id.priorityImage);
        UMImage = findViewById(R.id.UMImage);
        reportsImage = findViewById(R.id.reportsImage);
        logOutImage = findViewById(R.id.logOutImage);

        replaceFragment(new fragment_dashboard_new());
        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new fragment_dashboard_new());
                ButtonColorizer(dashboardsImage);
                color_flag = 1;
            }
        });
        priority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new fragmentPriority());
                ButtonColorizer(priorityImage);
                color_flag = 2;
            }
        });
        usermanage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new fragmentUM());
                ButtonColorizer(UMImage);
                color_flag = 3;
            }
        });
        reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new fragmentReports());
                ButtonColorizer(reportsImage);
                color_flag = 5;
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ButtonColorizer(logOutImage);
                color_flag = 4;
                showLogoutDialog();
            }
        });

    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager =  getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutAdmin,fragment);
        fragmentTransaction.commit();
    }

    private void ButtonColorizer(ImageView button){
        button.setColorFilter(ContextCompat.getColor(this, android.R.color.white));
        if (color_flag ==1) {
            dashboardsImage.setColorFilter(ContextCompat.getColor(this, android.R.color.white));
        }else if(color_flag==2){
            priorityImage.setColorFilter(ContextCompat.getColor(this, android.R.color.white));
        }else if(color_flag ==3){
            UMImage.setColorFilter(ContextCompat.getColor(this, android.R.color.white));
        }else if(color_flag ==4){
            logOutImage.setColorFilter(ContextCompat.getColor(this, android.R.color.white));
        }else if(color_flag ==5){
            reportsImage.setColorFilter(ContextCompat.getColor(this, android.R.color.white));
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

    private void showWelcomeDialog(){
        final Dialog dialog = new Dialog(AdminActivity.this);
        dialog.setContentView(R.layout.admin_dialog);
        Window window = dialog.getWindow();
        window.setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    private void showLogoutDialog(){
        final Dialog dialog = new Dialog(AdminActivity.this);
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
            }
        });
    }
}