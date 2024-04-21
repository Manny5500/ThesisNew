package com.example.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class BDUtils {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context context;
    String barangay;

    int num;
    String type;
    Dialog dialog;
    public BDUtils(Context context, String barangay, int num, String type, Dialog dialog){
        this.context = context;
        this.barangay = barangay;
        this.num = num;
        this.type = type;
        this.dialog = dialog;
    }
    public void updateBar(){
        Map<String, Object> bMap = new HashMap<>();
        if(type.equals("pop")){
            bMap.put("population", num);
        } else if (type.equals("EC")) {
            bMap.put("estimatedChildren", num);
        }
        db.collection("barangay").document(barangay).update(bMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context, "Data updated successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed to save changes", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
