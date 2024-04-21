package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class parent_notification extends Fragment implements NotifyAdapter.ItemClickListener {


    private RecyclerView recyclerView;
    private NotifyAdapter adapter;
    View view;

    private String gmail;
    private FirebaseFirestore db;
    private ArrayList<Child> childrenList;

    String item[];
    String name[];
    String pnumber[];

    Boolean isComplete = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_parent_notification, container, false);

        db = FirebaseFirestore.getInstance();

        item = getResources().getStringArray(R.array.malnourished_categories);
        name = getResources().getStringArray(R.array.children_name);
        pnumber = getResources().getStringArray(R.array.phone_number);
        childrenList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recyclerNotification);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));


        gmail = ((ParentActivity)getActivity()).email;
        if(!gmail.isEmpty()) {
            NotificationMaker();
        }
        return view;
    }
    @Override
    public void onItemClick(int position) {
        ComponentModel clickedItem = adapter.getItem(position);
        if (clickedItem != null) {
        }
    }
    public void NotificationMaker(){
        db.collection("children").whereEqualTo("gmail", gmail).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<ComponentModel> notify = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Child child = doc.toObject(Child.class);
                        child.setId(doc.getId());
                        if(child.getForfeeding() != null && child.getForfeeding().equals("Yes")){
                            ComponentModel componentModel = new ComponentModel("Your child is part of the feeding program", R.color.black, R.color.background3);
                            notify.add(componentModel);
                        }
                        childrenList.add(child);
                    }
                    adapter = new NotifyAdapter(getContext(), notify, parent_notification.this::onItemClick);
                    recyclerView.setAdapter(adapter);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireContext(), "Failed to get all users", Toast.LENGTH_SHORT).show();
            }
        });
    }
}