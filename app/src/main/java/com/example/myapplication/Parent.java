package com.example.myapplication;

public class Parent extends User{
    private String employment1;
    private String employment2;
    private String hasPregnant;

    private String monthlyIncome;
    private int familySize;


    public Parent(){
        super();
    }

    public Parent(String employment1, String employment2, String hasPregnant, String monthlyIncome, int familySize){
        super();
        this.employment1 = employment1;
        this.employment2 = employment2;
        this.hasPregnant = hasPregnant;
        this.monthlyIncome = monthlyIncome;
        this.familySize = familySize;

    }

    public String getEmployment1() {
        return employment1;
    }

    public void setEmployment1(String employment1) {
        this.employment1 = employment1;
    }

    public String getEmployment2() {
        return employment2;
    }

    public void setEmployment2(String employment2) {
        this.employment2 = employment2;
    }

    public String getHasPregnant() {
        return hasPregnant;
    }

    public void setHasPregnant(String hasPregnant) {
        this.hasPregnant = hasPregnant;
    }

    public String getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(String monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public int getFamilySize() {
        return familySize;
    }

    public void setFamilySize(int familySize) {
        this.familySize = familySize;
    }


}
