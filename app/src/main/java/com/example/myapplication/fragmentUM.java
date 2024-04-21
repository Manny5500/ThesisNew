package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class fragmentUM extends Fragment  {
    private RecyclerView umrecyclerView;
    private UMAdapter adapter;
    private SearchView searchView;
    private List<ComponentModel> originalComponents;

    MaterialAutoCompleteTextView userPicker;

    FirebaseFirestore db;
    int whiteColor;
    View view;

    String userType = "BNS";
    String[] userList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        FirebaseApp.initializeApp(requireContext());
        whiteColor = ContextCompat.getColor(context, R.color.viola);
        adapter = new UMAdapter(requireContext(), new ArrayList<>());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_u_m, container, false);

        db = FirebaseFirestore.getInstance();
        umrecyclerView = view.findViewById(R.id.recycler);
        userPicker = view.findViewById(R.id.userPicker);

        if(userType.equals("BNS")){
            userType = "personnel";
        }

        userList = new String[]{"BNS", "parent", "Request for Deletion", "Archive", "Verify"};
        FormUtils.setAdapter(userList, userPicker, requireContext());
        userPicker.setText("BNS", false);


        SearchView searchView = view.findViewById(R.id.searchView);
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


        userPickerEvent();

        return view;
    }

    public void userPickerEvent(){
        userPicker.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                userType = (String) parent.getItemAtPosition(position);
                if(userType.equals("BNS")){
                    userType = "personnel";
                }
                Populate();
            }
        });
    }
    public void Populate(){
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    ArrayList<User> arrayList = new ArrayList<>();

                    for (QueryDocumentSnapshot doc: task.getResult()){
                        User child = doc.toObject(User.class);
                        child.setId(doc.getId());
                        arrayList.add(child);
                    }

                    ArrayList<User> filteredUser = new ArrayList<>();

                    for(User user:arrayList){

                        String isArchive = user.getIsArchive();
                        String userCat = user.getUser();
                        String delRequest = user.getDeletionRequest();
                        String userVerified = user.getVerified();
                        //null check
                        if(isArchive==null){
                            isArchive = "";
                        }
                        if(userCat==null){
                            userCat="";
                        }
                        if(delRequest==null){
                            delRequest = "";
                        }
                        if(userVerified==null){
                            userVerified = "";
                        }


                        if(userType.equals(userList[3])){
                            if(isArchive.equals("Yes")){
                                filteredUser.add(user);
                            }
                        }
                        if(userCat.equals(userType)){
                            if(!isArchive.equals("Yes")){
                                filteredUser.add(user);
                            }
                        }
                        if(userType.equals(userList[2])){
                            if(delRequest.equals("true")){
                                filteredUser.add(user);
                            }
                        }

                        if(userType.equals(userList[4]) ){
                            if(userVerified.equals("No") && user.getUser().equals("personnel")){
                                filteredUser.add(user);
                            }
                        }

                    }


                    adapter = new UMAdapter(getContext(), filteredUser);
                    umrecyclerView.setAdapter(adapter);
                    adapter.setOnItemClickListener(new UMAdapter.OnItemClickListener() {
                        @Override
                        public void onClick(User child) {
                            App.user = child;
                            Intent intent = new Intent(requireContext(), UMEdit.class);
                            intent.putExtra("role", userType);
                            startActivity(intent);
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireContext(), "Failed to get all users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        Populate();
    }
}