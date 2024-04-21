package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class barangayAnalytics extends AppCompatActivity {

    FirebaseFirestore db;
    ArrayList<Child> childrenList;

    RecyclerView barangayAnalyticsRecycler;

    ArrayList<Timestamp> timestampArrayList;

    private barangayAnalyticsAdapter bAAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barangay_analytics);


        timestampArrayList = getIntent().getParcelableArrayListExtra("timestampArrayList");
        barangayAnalyticsRecycler = findViewById(R.id.barangayAnalyticsRecycler);

        db = FirebaseFirestore.getInstance();
        getBarangayData();
    }
    private void getBarangayData(){
        db.collection("children").whereGreaterThanOrEqualTo("dateAdded", timestampArrayList.get(0))
                .whereLessThanOrEqualTo("dateAdded", timestampArrayList.get(1))
                .whereArrayContainsAny("statusdb",
                        Arrays.asList("Underweight", "Severe Underweight",
                                "Overweight", "Obese",
                                "Wasted", "Severe Wasted",
                                "Stunted", "Severe Stunted"))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                           childrenList = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                Child child = doc.toObject(Child.class);
                                child.setId(doc.getId());
                                childrenList.add(child);
                            }
                            int [] malnutrition_perbarangay = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
                            int[] index = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23};
                            String[] barangay = getResources().getStringArray(R.array.barangay);
                            Map<String, Map<String, Integer>> myMap = new HashMap<>();
                            for(int i = 0; i<malnutrition_perbarangay.length;i++){
                                malnutrition_perbarangay[i] =  getBarangayCount(childrenList, barangay[i]);
                                myMap.put(barangay[i], getSitioCount(childrenList, barangay[i]));
                            }

                            RankBarangay(malnutrition_perbarangay, index, myMap);

                        } else {
                            Toast.makeText(barangayAnalytics.this, "Failed to get children data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(barangayAnalytics.this, "Failed to get children data" + e, Toast.LENGTH_SHORT).show();
                        Log.d("Firestore Error" ,"Failed to get children data" + e);
                    }
                });
    }

    public int getBarangayCount(ArrayList<Child> childrenList, String barangay){
        int count = 0;
        for(Child child: childrenList){
            if(child.getBarangay().equals(barangay)){
                count++;
            }
        }
        return count;
    }

    public Map<String, Integer> getSitioCount(ArrayList<Child> childrenList, String barangay){
        String[] sitioList = SitioUtils.getSitioList(barangay);
        Map<String,Integer> sitioMap = new HashMap<>();

        for(String sitio:sitioList){
            int count = 0;
            for(Child child:childrenList){
                if(child.getBarangay().equals(barangay) &&
                        child.getSitio().equals(sitio)){
                    count++;
                }
            }
            sitioMap.put(sitio, count);
        }
        return sitioMap;
    }

    private void RankBarangay(int[] malnutrition_perbarangay, int[] index, Map<String, Map<String,Integer>> myMap){
        String[] barangay = getResources().getStringArray(R.array.barangay);
        ArrayList<BarangayModel> barangayList = new ArrayList<>();
        for (int i = 0; i < malnutrition_perbarangay.length - 1; i++) {
            for (int j = i + 1; j < malnutrition_perbarangay.length; j++) {
                if (malnutrition_perbarangay[i] > malnutrition_perbarangay[j]) {
                    // Swap malnutrition_rate[i] and malnutrition_rate[j]
                    int tempRate = malnutrition_perbarangay[i];
                    malnutrition_perbarangay[i] = malnutrition_perbarangay[j];
                    malnutrition_perbarangay[j] = tempRate;

                    // Swap index[i] and index[j]
                    int tempIndex = index[i];
                    index[i] = index[j];
                    index[j] = tempIndex;
                }
            }
        }

        int k=0;
        for(int i=23; i>=0; i--){
            BarangayModel barangayNow = new BarangayModel();
            barangayNow.setBarangay(barangay[index[i]]);
            barangayNow.setRank(k+1);
            barangayNow.setTotalCase(malnutrition_perbarangay[i]);
            barangayNow.setSitioMap(myMap.get(barangay[index[i]]));
            barangayList.add(barangayNow);
            k++;
        }

        bAAdapter = new barangayAnalyticsAdapter(barangayAnalytics.this, barangayList);
        barangayAnalyticsRecycler.setAdapter(bAAdapter);

    }


}