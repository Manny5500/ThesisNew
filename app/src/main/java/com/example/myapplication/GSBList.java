package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class GSBList extends AppCompatActivity {
    private RecyclerView umrecyclerView;
    private ParentAdapter adapter;
    private SearchView searchView;
    private List<ComponentModel> originalComponents;


    FirebaseFirestore db;
    int whiteColor;

    String[] userList;

    ArrayList<Timestamp> tsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gsblist);

        db = FirebaseFirestore.getInstance();

        umrecyclerView = findViewById(R.id.recycler);

        Intent intent = getIntent();
        Bundle tsargs = intent.getBundleExtra("BTSGSB");
        tsList = (ArrayList<Timestamp>) tsargs.getSerializable("TSLGSB");


        userList = new String[]{"More than 1 child", "Low Income", "Have a Malnourished Child", "All"};


        SearchView searchView = findViewById(R.id.searchView);
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
                adapter.getFilter().filter(s);
                return true;
            }
        });

        Populate();

    }
    private ArrayList<Child> getVulnerableList(ArrayList<Child> childList){
        ArrayList<Child> vulnerableList = new ArrayList<>();
        ArrayList<Child> fGmail = RemoveDuplicates.rDGmail(childList);
        for(Child child: fGmail){
            boolean isMal = !child.getStatusdb().contains("Normal");
            boolean isLowest = child.getMonthlyIncome().equals("Less than 9,100");
            boolean isLower = child.getMonthlyIncome().equals("9,100 to 18,200");
            boolean isLow = child.getMonthlyIncome().equals("18,200 to 36,400");
            boolean isLowIncome = isLowest || isLower || isLow;

            int count_child = 0;
            for(Child child1: childList){
                if(child1.getGmail().equals(child.getGmail())){
                    count_child++;
                }
            }

            boolean isGOne = count_child>1;

            if(isMal||isLowIncome||isGOne){
                vulnerableList.add(child);
            }
        }
        return vulnerableList;
    }
    private ArrayList<Child>  getLowIncomeList(ArrayList<Child> childList){
        ArrayList<Child> vulnerableList = new ArrayList<>();
        ArrayList<Child> fGmail = RemoveDuplicates.rDGmail(childList);
        for(Child child: fGmail){
            boolean isLowest = child.getMonthlyIncome().equals("Less than 9,100");
            boolean isLower = child.getMonthlyIncome().equals("9,100 to 18,200");
            boolean isLow = child.getMonthlyIncome().equals("18,200 to 36,400");
            boolean isLowIncome = isLowest || isLower || isLow;


            if(isLowIncome){
                vulnerableList.add(child);
            }
        }
        return vulnerableList;
    }
    private ArrayList<Child>  getMoreOneChildList(ArrayList<Child> childList){
        ArrayList<Child> vulnerableList = new ArrayList<>();
        ArrayList<Child> fGmail = RemoveDuplicates.rDGmail(childList);
        for(Child child: fGmail){
            int count_child = 0;
            for(Child child1: childList){
                if(child1.getGmail().equals(child.getGmail())){
                    count_child++;
                }
            }

            boolean isGOne = count_child>1;
            if(isGOne){
                vulnerableList.add(child);
            }
        }
        return vulnerableList;
    }
    private ArrayList<Child>  getMalList(ArrayList<Child> childList){
        ArrayList<Child> vulnerableList = new ArrayList<>();
        ArrayList<Child> fGmail = RemoveDuplicates.rDGmail(childList);

        for(Child child: fGmail){
            if(child.getForgulayan()!=null){
                boolean isGB = child.getForgulayan().equals("Yes");
                if(isGB){
                    vulnerableList.add(child);
                }
            }
        }
        return vulnerableList;
    }
    private void setTempEmailData(ArrayList<Child> filteredList){
        db.collection("tempEmail").whereEqualTo("barangay", App.user.getBarangay()).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            ArrayList<TempEmail> arrayList = new ArrayList<>();
                            ArrayList<TempEmail> faList = new ArrayList<>();
                            for (QueryDocumentSnapshot doc: task.getResult()){
                                TempEmail tempEmail = doc.toObject(TempEmail.class);
                                arrayList.add(tempEmail);
                            }

                            for(TempEmail tempEmail: arrayList){

                                for(Child child: filteredList){
                                    boolean teExist = tempEmail.getGmail()!=null;
                                    boolean ceExist = child.getGmail()!=null;
                                    if(teExist && ceExist){
                                        boolean emEqual = tempEmail.getGmail().equals(child.getGmail());
                                        if(emEqual){
                                            faList.add(tempEmail);
                                        }
                                    }
                                }

                            }

                            adapter = new ParentAdapter(GSBList.this, faList);
                            umrecyclerView.setAdapter(adapter);
                            adapter.setOnItemClickListener(new ParentAdapter.OnItemClickListener() {
                                @Override
                                public void onClick(TempEmail tempEmail) {
                                }
                            });

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(GSBList.this, "Failed to get all users", Toast.LENGTH_SHORT).show();
                    }
                });




    }

    public void Populate(){
        db.collection("children").whereEqualTo("barangay", App.user.getBarangay()).whereGreaterThanOrEqualTo("dateAdded", tsList.get(0))
                .whereLessThanOrEqualTo("dateAdded", tsList.get(1)).orderBy("dateAdded", Query.Direction.DESCENDING).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            ArrayList<Child> arrayList = new ArrayList<>();
                            ArrayList<Child> filteredList = new ArrayList<>();
                            for (QueryDocumentSnapshot doc: task.getResult()){
                                Child child = doc.toObject(Child.class);
                                child.setId(doc.getId());
                                arrayList.add(child);
                            }
                            ArrayList<Child> childList = new ArrayList<>();
                            childList = RemoveDuplicates.removeDuplicates(arrayList);

                            filteredList = getMalList(childList);
                            setTempEmailData(filteredList);

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(GSBList.this, "Failed to get all users", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}