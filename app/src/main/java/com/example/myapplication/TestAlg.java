package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class TestAlg extends AppCompatActivity {

    TextView textStats;
    EditText textHeight, textWeight, textMonth, textGender;

    Button btnSubmit;

    double height, weight;
    int month;
    String gender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_alg);

        textStats = findViewById(R.id.textStatsDisplay);
        textHeight = findViewById(R.id.textHeight);
        textWeight = findViewById(R.id.textWeight);
        textMonth = findViewById(R.id.textMonth);
        textGender = findViewById(R.id.textGender);
        btnSubmit = findViewById(R.id.btnSubmit);



        submitEvent();

    }

    private void submitEvent(){
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setValues();
                testAlgo();
            }
        });
    }

    private  void setValues(){
        height = Double.parseDouble(textHeight.getText().toString());
        weight = Double.parseDouble(textWeight.getText().toString());
        month = Integer.parseInt(textMonth.getText().toString());
        gender = textGender.getText().toString();
    }

    private void testAlgo(){
        CGC assessment = new CGC();


        // Weight-for-Age
        assessment.addRule("Severe Underweight", Arrays.asList("SD before -4", "SD after -3", "SD now -3"));
        assessment.addRule("Underweight", Arrays.asList("SD before -3", "SD after -3", "SD now -3"));
        assessment.addRule("Underweight", Arrays.asList("SD before -3", "SD after -2", "SD now -2"));
        assessment.addRule("Overweight", Arrays.asList("SD before 1", "SD after 2", "SD now 1"));
        assessment.addRule("Overweight", Arrays.asList("SD before 1", "SD after 1", "SD now 1"));


        // Height-for-Age
        assessment.addRule("Stunted growth", Arrays.asList("Short height", "Age < 59 months"));
        assessment.addRule("Normal height", Arrays.asList("Age < 59 months", "Height appropriate for age"));
        assessment.addRule("Tall stature", Arrays.asList("Tall height", "Age < 59 months"));

        // Weight-for-Length
        assessment.addRule("Underweight for length", Arrays.asList("Low weight", "Length appropriate for age"));
        assessment.addRule("Normal weight for length", Arrays.asList("Weight appropriate for length", "Length appropriate for age"));
        assessment.addRule("Overweight for length", Arrays.asList("High weight", "Length appropriate for age"));

        // Add facts for a sample child
        StatusFinder sf = new StatusFinder(height, weight, month, "male");
        for(String wa: sf.WFA_Boys_M()){
            assessment.addFact(wa);
        }

        // Execute forward chaining
        assessment.executeForwardChaining();

        String hello = "";
        for (String fact : assessment.facts) {
            hello = hello + "\n"+ fact;
        }
        textStats.setText(hello);
    }
}