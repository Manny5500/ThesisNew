package com.example.myapplication;

import java.util.ArrayList;

public class RemoveDuplicates{
    public static ArrayList<Child> removeDuplicates(ArrayList<Child> arrayList){
        ArrayList<Child> filteredArrayList = new ArrayList<>();
        for(Child child: arrayList){
            boolean isDuplicate = false;
            String fullname = child.getChildFirstName()+child.getChildMiddleName()+child.getChildLastName();
            String gmail = child.getGmail();
            for(Child child2: filteredArrayList){
                String fullname2 = child2.getChildFirstName()+child2.getChildMiddleName()+child2.getChildLastName();
                String gmail2 = child2.getGmail();
                if(filteredArrayList.contains(child2) &&
                        fullname.equals(fullname2) && gmail.equals(gmail2)){
                    isDuplicate = true;
                }
            }
            if(!isDuplicate){
                filteredArrayList.add(child);
            }
        }
        return filteredArrayList;
    }

    public static ArrayList<Child> rDGmail(ArrayList<Child> arrayList){
        ArrayList<Child> filteredArrayList = new ArrayList<>();
        for(Child child: arrayList){
            boolean isDuplicate = false;
            String gmail = child.getGmail();
            for(Child child2: filteredArrayList){
                String gmail2 = child2.getGmail();
                if(filteredArrayList.contains(child2) && gmail.equals(gmail2)){
                    isDuplicate = true;
                }
            }
            if(!isDuplicate){
                filteredArrayList.add(child);
            }
        }
        ArrayList<Child> supFA = new ArrayList<>();
        for(Child child: filteredArrayList){
            boolean isGmailVal = !child.getGmail().equals("N/A");
            if(isGmailVal){
                supFA.add(child);
            }
        }
        return supFA;
    }
}
