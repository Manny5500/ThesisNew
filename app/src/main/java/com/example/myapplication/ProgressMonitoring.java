package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class ProgressMonitoring extends AppCompatActivity {

    FirebaseFirestore db;
    private ChildHAdapter childHAdapter;
    RecyclerView recyclerView;
    String full_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_monitoring);
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recycler);
        full_name = App.child.getChildFirstName() + " " +
                App.child.getChildMiddleName() + " " +
                App.child.getChildLastName();
        Populate_now();
    }

    private void Populate_now(){
        DocumentReference childDocRef = db.collection("children_historical").document(full_name);
        CollectionReference dateExaminedDocRef = childDocRef.collection("dates");
        dateExaminedDocRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            ArrayList<ChildH> arrayList = new ArrayList<>();
                            for (QueryDocumentSnapshot doc: task.getResult()){
                                ChildH childh = doc.toObject(ChildH.class);
                                childh.setId(doc.getId());
                                arrayList.add(childh);
                            }
                            Collections.reverse(arrayList);
                            childHAdapter = new ChildHAdapter(ProgressMonitoring.this, arrayList);
                            recyclerView.setAdapter(childHAdapter);

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Firebase myexception", ""+e);
                    }
                });
    }
}