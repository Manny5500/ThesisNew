package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class fragment_addDataNew extends Fragment {

    View view;

    TextInputEditText parentFirstName, parentMiddleName, parentLastName,
            gmail, houseNumber;


    MaterialAutoCompleteTextView belongAC, incomeAC, sitioAC;

    Button next;

    String pFVal, pMVal, pLVal, gMVal, hNVal, bEVal, iNVaL, sIVal;
    FirebaseFirestore db;

    private boolean isReturningFromActivity = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_data_new, container, false);
        db = FirebaseFirestore.getInstance();
        parentFirstName = view.findViewById(R.id.textParentFirstName);
        parentMiddleName = view.findViewById(R.id.textParentMiddleName);
        parentLastName = view.findViewById(R.id.textParentLastName);
        gmail = view.findViewById(R.id.textGmail);
        houseNumber = view.findViewById(R.id.textHouseNumber);
        belongAC = view.findViewById(R.id.textBelong);
        incomeAC = view.findViewById(R.id.textIncome);
        sitioAC = view.findViewById(R.id.spinnerSitio);
        next = view.findViewById(R.id.btnNext);

        String[] belongs = getResources().getStringArray(R.array.yes_or_no);
        String[] monthlyIncome = {"Less than 9,100", "9,100 to 18,200", "18,200 to 36,400",
                "36,400 to 63,700", "63,700 to 109,200", "109,200 to 182,000", "Above 182,000"};

        FormUtils.setAdapter(SitioUtils.getSitioList(App.user.getBarangay()), sitioAC, requireContext());
        FormUtils.setAdapter(belongs,belongAC, requireContext());
        FormUtils.setAdapter(monthlyIncome, incomeAC, requireContext());

        showYesNoDialog();
        nextEvent();

        return view;
    }

    private  void nextEvent(){
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAllTextInputData();
                boolean isFormValid = FormUtils.validateForm_Parent( pFVal, pMVal, pLVal, gMVal, "09229591835",requireContext());

                if (isFormValid) {
                    savetoTempEmail();
                } else {
                    Toast.makeText(getContext(), "Please fill out all fields and provide valid information", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




    private void showYesNoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Confirmation");
        builder.setMessage("Is the parent have already data?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(requireContext(), SearchParent.class));
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setAllTextInputData(){
        pFVal = parentFirstName.getText().toString().trim();
        pMVal = parentMiddleName.getText().toString().trim();
        pLVal = parentLastName.getText().toString().trim();
        gMVal = gmail.getText().toString().trim();
        hNVal = houseNumber.getText().toString().trim();
        bEVal = belongAC.getText().toString().trim();
        iNVaL = incomeAC.getText().toString().trim();
        sIVal = sitioAC.getText().toString().trim();
    }
    private void clearText(){
        parentFirstName.setText("");
        parentMiddleName.setText("");
        parentLastName.setText("");
        gmail.setText("");
        houseNumber.setText("");
        belongAC.setText("");
        incomeAC.setText("");
        sitioAC.setText("");
    }

    private Map<String, Object> createMap() {
        Map<String, Object> parent = new HashMap<>();
        parent.put("parentFirstName", pFVal);
        parent.put("parentMiddleName", pMVal);
        parent.put("parentLastName", pLVal);
        parent.put("gmail", gMVal);
        parent.put("houseNumber", hNVal);
        parent.put("monthlyIncome", iNVaL);
        parent.put("sitio", sIVal);
        parent.put("belongtoIP", bEVal);
        parent.put("barangay", App.user.getBarangay());
        return parent;
    }

    private void setParentData (){
        TempEmail tempEmail = new TempEmail();
        tempEmail.setGmail(gMVal);
        tempEmail.setParentFirstName(pFVal);
        tempEmail.setParentMiddleName(pMVal);
        tempEmail.setParentLastName(pLVal);
        tempEmail.setHouseNumber(hNVal);
        tempEmail.setMonthlyIncome(iNVaL);
        tempEmail.setSitio(sIVal);
        tempEmail.setBarangay(App.user.getBarangay());
        tempEmail.setBelongtoIP(bEVal);

        App.tempEmail = tempEmail;
    }
    private void savetoTempEmail() {
        Map<String, Object> parent = createMap();
        clearText();

        db.collection("tempEmail").document(gMVal)
                .set(parent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        setParentData();
                        startActivity(new Intent(requireContext(), AddDataWithParent.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Failed to parent data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    

}