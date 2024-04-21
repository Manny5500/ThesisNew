package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class SearchParent extends AppCompatActivity {
    FirebaseFirestore db;
    RecyclerView recyclerView;

    private ParentAdapter parentAdapter;

    int whiteColor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_parent);

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recycler);

        Populate_now();
        SearchView searchView = findViewById(R.id.searchView);
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);

        whiteColor = ContextCompat.getColor(SearchParent.this, R.color.viola);
        searchEditText.setTextColor(whiteColor);
        searchEditText.setHintTextColor(whiteColor);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                parentAdapter.getFilter().filter(s);
                return true;
            }
        });
    }

    private void Populate_now(){
        db.collection("tempEmail").whereEqualTo("barangay", App.user.getBarangay()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            ArrayList<TempEmail> arrayList = new ArrayList<>();
                            for (QueryDocumentSnapshot doc: task.getResult()){
                                TempEmail tMail= doc.toObject(TempEmail.class);
                                tMail.setGmail(doc.getId());
                                arrayList.add(tMail);
                            }

                            parentAdapter = new ParentAdapter(SearchParent.this, arrayList);
                            recyclerView.setAdapter(parentAdapter);
                            parentAdapter.setOnItemClickListener(new ParentAdapter.OnItemClickListener() {
                                @Override
                                public void onClick(TempEmail tMail) {
                                    App.tempEmail = tMail;
                                    startActivity(new Intent(SearchParent.this, AddDataWithParent.class));
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Firebase myexception", ""+e);
                        Toast.makeText(SearchParent.this, ""+e, Toast.LENGTH_SHORT).show();
                    }
                });
    }

}