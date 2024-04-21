package com.example.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ParentChildrenList extends Fragment {
    View view;
    FirebaseFirestore db;
    RecyclerView recyclerView;

    private ChildAdapter userAdapter;
    int whiteColor;
    private String  gmail;

    String barangayString = "";

    ArrayList<Child> childrenList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Initialize FirebaseApp when the fragment is attached to a context
        FirebaseApp.initializeApp(requireContext());
        whiteColor = ContextCompat.getColor(context, R.color.viola);
        userAdapter = new ChildAdapter(requireContext(), new ArrayList<>());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_parent_children_list, container, false);


        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recycler);


        gmail = ((ParentActivity)getActivity()).email;

        if(gmail != null){
            Populate();
        }

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
                userAdapter.getFilter().filter(s);
                return true;
            }
        });



        return view;
    }

    private void Populate(){
        db.collection("children").whereEqualTo("gmail", gmail)
                .orderBy("dateAdded", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String gulayanStatus = "";
                            ArrayList<Child> arrayList = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                Child child = doc.toObject(Child.class);
                                child.setId(doc.getId());
                                arrayList.add(child);

                                if(child.getForgulayan()!=null && child.getForgulayan().equals("Yes"))
                                    gulayanStatus = "Yes";
                            }



                            childrenList = RemoveDuplicates.removeDuplicates(arrayList);

                            userAdapter = new ChildAdapter(getContext(), RemoveDuplicates.removeDuplicates(arrayList));
                            recyclerView.setAdapter(userAdapter);
                            userAdapter.setOnItemClickListener(new ChildAdapter.OnItemClickListener() {
                                @Override
                                public void onClick(Child child) {
                                    App.child = child;
                                    startActivity(new Intent(requireContext(), ParentChildrenActivity.class));
                                }
                            });

                            if(gulayanStatus.equals("Yes"))
                                showRecoDialog("gulayan");


                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Failed to get all users", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void showRecoDialog(String type){
        if(isAdded() && requireContext() != null){
            final Dialog dialog = new Dialog(requireContext());
            dialog.setContentView(R.layout.reco_dialog);

            String message = "";

            ConstraintLayout upPanel = dialog.findViewById(R.id.panelUp);
            ImageView imgAct = dialog.findViewById(R.id.imgAct);
            TextView statusText = dialog.findViewById(R.id.status);
            ImageView imgReal = dialog.findViewById(R.id.imgReal);

            if(type.equals("gulayan")) {
                upPanel.setBackgroundColor(Color.parseColor("#32BA7B"));
                message = "You are qualified to Gulayan\n sa Bakuran Program";
                imgReal.setImageResource(R.drawable.vegetable);
                imgAct.setColorFilter(Color.parseColor("#FFEBC3"));
            }



            statusText.setText(message);
            Window window = dialog.getWindow();
            window.setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            dialog.show();
        }
    }

}