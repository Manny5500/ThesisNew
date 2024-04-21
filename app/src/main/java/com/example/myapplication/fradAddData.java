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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class fradAddData extends Fragment {
    View view;

    private ArrayList<User> userlist;
    private String personnelgmail, userid;

    TextInputEditText childFirstName, childMiddleName, childLastName,
            parentFirstName, parentMiddleName, parentLastName,
            gmail, houseNumber, height, weight, bdate, expectedDate;

    MaterialAutoCompleteTextView sexAC, belongAC, incomeAC, sitio;
    ArrayList<String> statusdb;
    Button submit;
    private FirebaseFirestore db;
    String barangays, sexchose, belongchose, dateString;
    SimpleDateFormat dateFormat;
    private int bdatevalue, monthdiff;
    private double height_true_val, weight_true_val;
    String childFirstNameValue, childMiddleNameValue, childLastNameValue,
            parentFirstNameValue, parentMiddleNameValue, parentLastNameValue,
            gmailValue, houseNumberValue, bdateValue, expectedDateValue,
            sexACValue, belongACValue, heightValue, weightValue, incomeVal, sitioVal;

    boolean isRelated = false;
    ArrayList<String> status = new ArrayList<>();
    String tempEmailVal;

    String myBarangay;

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_frad_add_data, container, false);


        height_true_val = 0;


        personnelgmail = ((PersonnelActivity) getActivity()).email;
        userid = ((PersonnelActivity) getActivity()).userid;
        userlist = new ArrayList<>();

        String[] barangay = getResources().getStringArray(R.array.barangay);
        String[] sex = getResources().getStringArray(R.array.sex);
        String[] belongs = getResources().getStringArray(R.array.yes_or_no);
        String[] monthlyIncome = {"Less than 9,100", "9,100 to 18,200", "18,200 to 36,400",
                "36,400 to 63,700", "63,700 to 109,200", "109,200 to 182,000", "Above 182,000"};

        db = FirebaseFirestore.getInstance();

        childFirstName = view.findViewById(R.id.textChildfirstName);
        childMiddleName = view.findViewById(R.id.textChildMiddleName);
        childLastName = view.findViewById(R.id.textChildLastName);
        parentFirstName = view.findViewById(R.id.textParentFirstName);
        parentMiddleName = view.findViewById(R.id.textParentMiddleName);
        parentLastName = view.findViewById(R.id.textParentLastName);
        gmail = view.findViewById(R.id.textGmail);
        houseNumber = view.findViewById(R.id.textHouseNumber);
        height = view.findViewById(R.id.textHeight);
        weight = view.findViewById(R.id.textWeight);
        bdate = view.findViewById(R.id.textBdate);
        expectedDate = view.findViewById(R.id.textExpectedDate);
        sexAC = view.findViewById(R.id.textPregnant);
        belongAC = view.findViewById(R.id.textBelong);
        submit = view.findViewById(R.id.btnSubmit);
        incomeAC = view.findViewById(R.id.textIncome);
        sitio = view.findViewById(R.id.spinnerSitio);

        FormUtils.setAdapter(sex, sexAC, requireContext());
        FormUtils.setAdapter(belongs, belongAC, requireContext());
        FormUtils.setAdapter(monthlyIncome, incomeAC, requireContext());
        FormUtils.setAdapter(SitioUtils.getSitioList(App.user.getBarangay()), sitio, requireContext());

        FormUtils.dateClicked(bdate, requireContext());
        FormUtils.dateClicked(expectedDate, requireContext());



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMonthdiff();
                setAllTextInputData();
                boolean isFormValid = FormUtils.validateForm(childFirstNameValue, childMiddleNameValue, childLastNameValue,
                        parentFirstNameValue, parentMiddleNameValue, parentLastNameValue,
                        gmailValue, houseNumberValue, bdateValue, expectedDateValue,
                        sexACValue, belongACValue, heightValue, weightValue, monthdiff, sitioVal, requireContext());


                if (isFormValid) {
                    checkName();
                } else {
                    Toast.makeText(getContext(), "Please fill out all fields and provide valid information", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private void setMonthdiff() {
        dateString = bdate.getText().toString();
        FormUtils formUtils = new FormUtils();
        Date parsedDate = formUtils.parseDate(dateString);

        if (parsedDate != null) {
            monthdiff = formUtils.calculateMonthsDifference(parsedDate);
        } else {
            Toast.makeText(requireContext(), "Failed to parse the date", Toast.LENGTH_SHORT).show();
        }
    }

    private void setAllTextInputData() {
        //the only diff of edit here is incomeVal
        childFirstNameValue = childFirstName.getText().toString().trim();
        childMiddleNameValue = childMiddleName.getText().toString().trim();
        childLastNameValue = childLastName.getText().toString().trim();
        parentFirstNameValue = parentFirstName.getText().toString().trim();
        parentMiddleNameValue = parentMiddleName.getText().toString().trim();
        parentLastNameValue = parentLastName.getText().toString().trim();
        gmailValue = gmail.getText().toString().trim();
        houseNumberValue = houseNumber.getText().toString().trim();
        bdateValue = bdate.getText().toString().trim();
        expectedDateValue = expectedDate.getText().toString().trim();
        sexACValue = sexAC.getText().toString().trim();
        belongACValue = belongAC.getText().toString().trim();
        heightValue = height.getText().toString().trim();
        weightValue = weight.getText().toString().trim();
        incomeVal = incomeAC.getText().toString().trim();
        sitioVal = sitio.getText().toString().trim();
    }

    private void AddNewFirestore(String barangayString) {
        Map<String, Object> user = createMap(barangayString);
        db.collection("children").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                clearInputs();
                if(tempEmailVal==null){
                    savetoTempEmail();
                }
                savetoHistory();
                Toast.makeText(getContext(), "Form submitted successfully!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireContext(), "Failed to add user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Map<String, Object> createMap(String barangayString) {
        status.clear();
        setStatuses();
        Map<String, Object> user = new HashMap<>();
        user.put("childFirstName", childFirstNameValue);
        user.put("childMiddleName", childMiddleNameValue);
        user.put("childLastName", childLastNameValue);
        user.put("parentFirstName", parentFirstNameValue);
        user.put("parentMiddleName", parentMiddleNameValue);
        user.put("parentLastName", parentLastNameValue);
        user.put("gmail", gmailValue);
        user.put("houseNumber", houseNumberValue);
        user.put("height", height_true_val);
        user.put("weight", weight_true_val);
        user.put("birthDate", bdateValue);
        user.put("belongtoIP", belongACValue);
        user.put("barangay", barangayString);
        user.put("sitio", sitioVal);
        user.put("sex", sexACValue);
        user.put("expectedDate", expectedDateValue);
        user.put("statusdb", statusdb);
        user.put("status", status);
        user.put("dateAdded", DateParser.getCurrentTimeStamp());
        user.put("monthAdded", DateParser.getMonthYear());
        user.put("monthlyIncome", incomeVal);
        return user;
    }

    private void setStatuses() {
        height_true_val = Double.parseDouble(heightValue);
        weight_true_val = Double.parseDouble(weightValue);
        statusdb = FindStatusWFA.CalculateMalnourished(requireContext(), monthdiff, weight_true_val, height_true_val, sexACValue);
        String[] individualTest = FindStatusWFA.individualTest(requireContext(), monthdiff, weight_true_val, height_true_val, sexACValue);

        for (String status_element : individualTest) {
            status.add(status_element);
        }
    }

    private void getBarangay() {
        db.collection("users").document(userid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                AddNewFirestore(String.valueOf(documentSnapshot.getString("barangay")));
                myBarangay = String.valueOf(documentSnapshot.getString("barangay"));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireContext(), "Failed to save changes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearInputs() {
        childFirstName.setText("");
        childMiddleName.setText("");
        childLastName.setText("");
        parentFirstName.setText("");
        parentMiddleName.setText("");
        parentLastName.setText("");
        gmail.setText("");
        houseNumber.setText("");
        height.setText("");
        weight.setText("");
        bdate.setText("");
        expectedDate.setText("");
        sexAC.setText("");
        belongAC.setText("");
        incomeAC.setText("");
        sitio.setText("");
    }



    private void savetoHistory() {
        String child_full_name = childFirstNameValue + " " + childMiddleNameValue + " " + childLastNameValue;
        String date_now = FormUtils.getCurrentDate();
        DocumentReference childDocRef = db.collection("children_historical").document(child_full_name);
        DocumentReference dateExaminedDocRef = childDocRef.collection("dates").document(date_now);
        Map<String, Object> childData = new HashMap<>();
        childData.put("height", height_true_val);
        childData.put("weight", weight_true_val);
        childData.put("dateid", Integer.parseInt(date_now));
        childData.put("status", status);
        childData.put("statusdb", statusdb);

        dateExaminedDocRef.set(childData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext(), "Added Successfully to the History", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void savetoTempEmail() {
        Map<String, Object> tempEmail = new HashMap<>();
        tempEmail.put("parentFirstName", parentFirstNameValue);
        tempEmail.put("parentMiddleName", parentMiddleNameValue);
        tempEmail.put("parentLastName", parentLastNameValue);
        tempEmail.put("houseNumber", houseNumberValue);
        tempEmail.put("sitio", sitioVal);
        tempEmail.put("monthlyIncome", incomeVal);
        tempEmail.put("barangay", myBarangay);
        tempEmail.put("belongtoIP", belongACValue);


        db.collection("tempEmail").document(gmailValue)
                .set(tempEmail)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Email submitted successfully!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Failed to add user", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getTempEmail() {
        db.collection("tempEmail").document(gmailValue).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    TempEmail tempEmail = documentSnapshot.toObject(TempEmail.class);
                    tempEmail.setGmail(documentSnapshot.getId());
                    tempEmailVal = tempEmail.getGmail();
                    if(checkEmail(tempEmail)){
                        getBarangay();
                    }
                } else {
                    getBarangay();
                }
            }
        });
    }

    private boolean checkEmail(TempEmail tempEmail){
        boolean check = true;
        if(tempEmail!=null){
            boolean isParentFirstNameMatch = parentFirstNameValue.equals(tempEmail.getParentFirstName());
            boolean isParentMiddleNameMatch = parentMiddleNameValue.equals(tempEmail.getParentMiddleName());
            boolean isParentLastNameMatch = parentLastNameValue.equals(tempEmail.getParentLastName());
            if(isParentFirstNameMatch && isParentMiddleNameMatch && isParentLastNameMatch){
                check = true;
            }else{
                Toast.makeText(requireContext(), "Email and Parent Name are not matched", Toast.LENGTH_SHORT).show();
                check = false;
            }
        }
        return check;
    }

    private void checkName(){
        boolean isMiddleNameMatch = parentMiddleNameValue.equals(childMiddleNameValue);
        boolean isLastNameMatch = parentLastNameValue.equals(childLastNameValue);
        if(isRelated || (isMiddleNameMatch && isLastNameMatch)){
            isRelated = true;
            getTempEmail();
        }else{
            showYesNoDialog();
        }
    }

    private void showYesNoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Confirmation");
        builder.setMessage("Is this child related to parent/guardian");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               isRelated = true;
                dialog.dismiss();
                getTempEmail();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               isRelated = false;
                dialog.dismiss();
            }
        });

        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}