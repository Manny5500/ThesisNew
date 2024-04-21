package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FPList extends AppCompatActivity {

    FirebaseFirestore db;
    RecyclerView recyclerView;

    private ChildAdapter userAdapter;

    ArrayList<Child> arrayList = new ArrayList<>();


    ArrayList<Timestamp> tsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fplist);
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recycler);

        SearchView searchView = findViewById(R.id.searchView);


        Intent intent = getIntent();
        Bundle tsargs = intent.getBundleExtra("BTSFP");
        tsList = (ArrayList<Timestamp>) tsargs.getSerializable("TSLFP");
        Populate();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                userAdapter.getFilter().filter(s);
                return true;
            }
        });
    }

    public void Populate(){
        db.collection("children")
                .whereEqualTo("barangay", App.user.getBarangay()).whereGreaterThanOrEqualTo("dateAdded", tsList.get(0))
                .whereLessThanOrEqualTo("dateAdded", tsList.get(1))
                .orderBy("dateAdded", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){


                            for (QueryDocumentSnapshot doc: task.getResult()){
                                Child child = doc.toObject(Child.class);
                                child.setId(doc.getId());
                                arrayList.add(child);
                            }

                            arrayList = RemoveDuplicates.removeDuplicates(arrayList);
                            ArrayList<Child> fAL = new ArrayList<>();
                            for(Child child: arrayList){
                                String feedStatus = child.getForfeeding();
                                if(feedStatus!=null){
                                    boolean isFeeding = child.getForfeeding().equals("Yes");
                                    if(isFeeding) {
                                        fAL.add(child);
                                    }
                                }
                            }
                            userAdapter = new ChildAdapter(FPList.this, fAL);
                            recyclerView.setAdapter(userAdapter);

                            userAdapter.setOnItemClickListener(new ChildAdapter.OnItemClickListener() {
                                @Override
                                public void onClick(Child child) {
                                }
                            });

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Firestore Error", "Error: "+e);
                        Toast.makeText(FPList.this, "Failed to get all users", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}