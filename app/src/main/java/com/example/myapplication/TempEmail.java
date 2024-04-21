package com.example.myapplication;

public class TempEmail {
    String gmail;
    String parentFirstName;

    String parentMiddleName;
    String parentLastName;

    String sitio;
    String monthlyIncome;
    String houseNumber;
    String belongtoIP;

    String barangay;



    String contactNo;

    public TempEmail(){

    }
    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }
    public String getBarangay() {
        return barangay;
    }

    public void setBarangay(String barangay) {
        this.barangay = barangay;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getParentFirstName() {
        return parentFirstName;
    }

    public void setParentFirstName(String parentFirstName) {
        this.parentFirstName = parentFirstName;
    }

    public String getParentMiddleName() {
        return parentMiddleName;
    }

    public void setParentMiddleName(String parentMiddleName) {
        this.parentMiddleName = parentMiddleName;
    }

    public String getParentLastName() {
        return parentLastName;
    }

    public void setParentLastName(String parentLastName) {
        this.parentLastName = parentLastName;
    }

    public String getFullName(){
        return parentFirstName + " " + parentMiddleName + " " + parentLastName;
    }

    public String getSitio() {
        return sitio;
    }

    public void setSitio(String sitio) {
        this.sitio = sitio;
    }

    public String getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(String monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getBelongtoIP() {
        return belongtoIP;
    }

    public void setBelongtoIP(String belongtoIP) {
        this.belongtoIP = belongtoIP;
    }

}
