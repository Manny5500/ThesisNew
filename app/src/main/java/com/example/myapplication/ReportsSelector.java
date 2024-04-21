package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.card.MaterialCardView;

public class ReportsSelector extends AppCompatActivity {

    MaterialCardView masterView, sumView, prevailView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports_selector);

        masterView = findViewById(R.id.masterView);
        sumView = findViewById(R.id.sumView);
        prevailView = findViewById(R.id.prevailView);

        masterViewEvent();
        sumViewEvent();
        prevailViewEvent();
    }
    private void masterViewEvent(){
        masterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportsSelector.this, Malnourished_List.class);
                startActivity(intent);
            }
        });
    }

    private void sumViewEvent(){
        sumView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportsSelector.this, SummaryReport.class);
                startActivity(intent);
            }
        });
    }

    private void prevailViewEvent(){
        prevailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportsSelector.this, Prevailance_Reports.class);
                startActivity(intent);

            }
        });
    }
}