package com.example.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FireStoreUtility {

    public  ArrayList<String> cMOC;
    public FireStoreUtility(ArrayList<String> cMOC){
        this.cMOC = cMOC;
    }

    public  void getBarangayStatus(String barangayString, TextView textView, FirebaseFirestore db){
        db.collection("barangay").document(barangayString).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Map<String, Object> data = documentSnapshot.getData();

                    String feedto = String.valueOf(data.get("feedto"));
                    FormUtils formUtils = new FormUtils();
                    Date parsedDate = formUtils.parseDate(feedto);


                    int daydiff = 3;
                    if (parsedDate != null) {
                        daydiff = formUtils.calculateDaysDifference(parsedDate);
                    } else {

                    }
                    if(data!=null && data.containsKey("feedfrom") && daydiff<=0){
                        textView.setTextColor(Color.parseColor("#008000"));

                    }else if(data!=null && data.containsKey("feedfrom") && daydiff>0){
                        resetFeedingDate(barangayString, "", "", db);
                    }

                } else {
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    public   void getBarangayDetails(String barangayString, Dialog dialog, FirebaseFirestore db, Context context){
        db.collection("barangay").document(barangayString).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Map<String, Object> data = documentSnapshot.getData();
                    Button set = dialog.findViewById(R.id.btnSet);
                    Button cancel = dialog.findViewById(R.id.btnCancel);
                    TextInputEditText from = dialog.findViewById(R.id.dateFrom);
                    TextInputEditText to = dialog.findViewById(R.id.dateTo);
                    TextView title = dialog.findViewById(R.id.title);
                    if(data!=null && data.containsKey("feedfrom")){
                        from.setText(String.valueOf(data.get("feedfrom")));
                        to.setText(String.valueOf(data.get("feedto")));
                    }
                    FormUtils.dateClicked(from, context);
                    FormUtils.dateClicked(to, context);

                    set.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String fromValue = from.getText().toString().trim();
                            String toValue = to.getText().toString().trim();
                            setFeedingDate(barangayString, fromValue, toValue, db, context);
                            dialog.dismiss();
                        }
                    });

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                } else {
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle errors here
            }
        });
    }

    private  void setFeedingDate(String barangayString, String fromValue, String toValue, FirebaseFirestore db, Context context){
        Map<String, Object> barangay = new HashMap<>();
        barangay.put("feedfrom", fromValue);
        barangay.put("feedto", toValue);
        db.collection("barangay").document(barangayString).update(barangay).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                settoChildren(barangayString,db, "set");
                Toast.makeText(context, "Saved successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed to save changes", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private  void resetFeedingDate(String barangayString, String fromValue, String toValue, FirebaseFirestore db){
        Map<String, Object> barangay = new HashMap<>();
        barangay.put("feedfrom", fromValue);
        barangay.put("feedto", toValue);
        db.collection("barangay").document(barangayString).update(barangay).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                settoChildren(barangayString,db, "reset");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }
    private  void settoChildren(String barangayString, FirebaseFirestore db, String action){
        CollectionReference collectionRef = db.collection("children");
        Query query = collectionRef
                .whereEqualTo("barangay", barangayString)
                .whereArrayContainsAny("statusdb", Arrays.asList("Underweight", "Wasted", "Stunted"));
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                WriteBatch batch = db.batch();



                FormUtils formUtils = new FormUtils();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    DocumentReference documentRef = collectionRef.document(document.getId());
                    String birthDate = document.getString("birthDate");
                    if(birthDate!=null){
                        Date parsedDate = formUtils.parseDate(document.getString("birthDate"));
                        int monthdiff = 0;
                        if (parsedDate != null) {
                            monthdiff = formUtils.calculateMonthsDifference(parsedDate);
                        }
                        if(action.equals("set") && monthdiff>23) {
                            batch.update(documentRef, "forfeeding", "Yes");
                        } else if (action.equals("reset")) {
                            batch.update(documentRef, "forfeeding", "No");
                        }
                    }
                }

                batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
            }
        });
    }


    private  void setGulayantoChildren(FirebaseFirestore db, String action, String type){
        CollectionReference collectionRef = db.collection("children");
        Query query = null;
        if(type.equals("Parent with more than 1 child")){
            query = collectionRef
                    .whereIn("gmail", cMOC);
        } else if (type.equals("Malnourished")) {
            query = collectionRef
                    .whereArrayContainsAny("statusdb",
                            Arrays.asList("Underweight", "Wasted", "Stunted", "Severe Underweight", "Severe Wasted", "Severe Stunted"));

        } else if (type.equals("Low Income")) {
            query = collectionRef
                    .whereIn("monthlyIncome", Arrays.asList("Less than 9,100", "9,100 to 18,200", "18,200 to 36,400"));

        } else if (type.equals("All Beneficiaries")) {
            query = collectionRef
                    .where(Filter.or(
                            Filter.inArray("monthlyIncome",Arrays.asList("Less than 9,100", "9,100 to 18,200", "18,200 to 36,400")),
                            Filter.arrayContains("statusdb",
                                    Arrays.asList("Underweight", "Wasted", "Stunted", "Severe Underweight", "Severe Wasted", "Severe Stunted"))
                    ));
        }

        if(!query.equals(null)){
            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    WriteBatch batch = db.batch();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        DocumentReference documentRef = collectionRef.document(document.getId());
                        if(action.equals("set")) {
                            batch.update(documentRef, "forgulayan", "Yes");
                        } else if (action.equals("reset")) {
                            batch.update(documentRef, "forgulayan", "No");
                        }
                    }
                    batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
                }
            });
        }


    }

    public   void getGulayanDetails(String gulayanString, Dialog dialog, String type, FirebaseFirestore db, Context context){
        db.collection("gulayan").document(gulayanString).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Map<String, Object> data = documentSnapshot.getData();
                    Button set = dialog.findViewById(R.id.btnSet);
                    Button cancel = dialog.findViewById(R.id.btnCancel);
                    TextInputEditText from = dialog.findViewById(R.id.dateFrom);
                    TextInputEditText to = dialog.findViewById(R.id.dateTo);
                    TextView title = dialog.findViewById(R.id.title);
                    title.setText("Gulayan sa Bakuran");
                    if(data!=null && data.containsKey("from")){
                        from.setText(String.valueOf(data.get("from")));
                        to.setText(String.valueOf(data.get("to")));
                    }
                    FormUtils.dateClicked(from, context);
                    FormUtils.dateClicked(to, context);

                    set.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String fromValue = from.getText().toString().trim();
                            String toValue = to.getText().toString().trim();
                            setGulayDate(gulayanString, fromValue, toValue, type, db, context);
                            dialog.dismiss();
                        }
                    });

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                } else {
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle errors here
            }
        });
    }

    private  void setGulayDate(String gulayanString, String fromValue, String toValue,  String type, FirebaseFirestore db, Context context){
        Map<String, Object> gulayan = new HashMap<>();
        gulayan.put("from", fromValue);
        gulayan.put("to", toValue);

        if(gulayanString.equals("All Beneficiaries")){
            db.collection("gulayan").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("gulayan").document(document.getId()).update(gulayan);
                        }
                        setGulayantoChildren(db, "set", type);
                        Toast.makeText(context, "All documents updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed to update documents", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            db.collection("gulayan").document(gulayanString).update(gulayan).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    setGulayantoChildren(db, "set" , type);
                    Toast.makeText(context, "Saved successfully", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Failed to save changes", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    public  void getGulayanStatus(String gulayanString, TextView textView, FirebaseFirestore db){
        db.collection("gulayan").document(gulayanString).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Map<String, Object> data = documentSnapshot.getData();

                    String feedto = String.valueOf(data.get("to"));
                    FormUtils formUtils = new FormUtils();
                    Date parsedDate = formUtils.parseDate(feedto);


                    int daydiff = 3;
                    if (parsedDate != null) {
                        daydiff = formUtils.calculateDaysDifference(parsedDate);
                    } else {

                    }
                    if(data!=null && data.containsKey("from") && daydiff<=0){
                        textView.setTextColor(Color.parseColor("#008000"));

                    }else if(data!=null && data.containsKey("from") && daydiff>0){
                        resetGulayanDate(gulayanString, "", "", db);
                    }

                } else {
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

    }

    private  void resetGulayanDate(String gulayanString, String fromValue, String toValue, FirebaseFirestore db){
        Map<String, Object> gulayan = new HashMap<>();
        gulayan.put("from", fromValue);
        gulayan.put("to", toValue);
        db.collection("gulayan").document(gulayanString).update(gulayan).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                setGulayantoChildren(db, "reset" , gulayanString);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }
}
