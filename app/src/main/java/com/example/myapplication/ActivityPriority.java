package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ActivityPriority extends AppCompatActivity {

    FirebaseFirestore db;
    RecyclerView recyclerView;

    private ChildAdapter userAdapter;
    int whiteColor;

    String firstName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_priority);

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerap);
        whiteColor = ContextCompat.getColor(this, R.color.viola);
        userAdapter = new ChildAdapter(this, new ArrayList<>());

        Populate();

        SearchView searchView = findViewById(R.id.searchViewap);
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);

        searchEditText.setTextColor(whiteColor);
        searchEditText.setHintTextColor(whiteColor);
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
                .orderBy("dateAdded", Query.Direction.DESCENDING).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            ArrayList<Child> arrayList = new ArrayList<>();

                            for (QueryDocumentSnapshot doc: task.getResult()){
                                Child child = doc.toObject(Child.class);
                                child.setId(doc.getId());


                                db.collection("tempEmail").whereEqualTo("gmail", child.getGmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                for(QueryDocumentSnapshot doc1:task.getResult()){
                                                    TempEmail tempEmail = doc1.toObject(TempEmail.class);
                                                    child.setPhoneNumber(tempEmail.getContactNo());
                                                }
                                            }
                                        });
                                arrayList.add(child);
                            }

                            arrayList = RemoveDuplicates.removeDuplicates(arrayList);
                            ArrayList<Child> fAL = new ArrayList<>();

                            for(Child child: arrayList){
                                boolean isSU = child.getStatusdb().contains("Severe Underweight");
                                boolean isSW = child.getStatusdb().contains("Severe Wasted");
                                boolean isSS = child.getStatusdb().contains("Severe Stunted");
                                boolean isO = child.getStatusdb().contains("Obese");
                                if(isSU||isSW||isSS||isO) {
                                    fAL.add(child);
                                }
                            }

                            firstName = fAL.get(0).getChildFirstName();

                            userAdapter = new ChildAdapter(ActivityPriority.this, fAL);
                            recyclerView.setAdapter(userAdapter);
                            userAdapter.setOnItemClickListener(new ChildAdapter.OnItemClickListener() {
                                @Override
                                public void onClick(Child child) {
                                    App.child = child;
                                    startActivity(new Intent(ActivityPriority.this, PriorityClicked.class));
                                }
                            });



                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ActivityPriority.this, "Failed to get all users", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}