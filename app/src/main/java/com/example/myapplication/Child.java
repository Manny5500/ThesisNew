package com.example.myapplication;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Child implements Serializable {

    String childName, childFirstName, childMiddleName, childLastName,
            parentFirstName, parentMiddleName, parentLastName,
            id, barangay, parentName, gmail, houseNumber, sex, belongtoIP, birthDate,
            expectedDate, forfeeding, monthlyIncome, forgulayan, phoneNumber, downloadUrl,
            sitio, monthAdded;
    double weight, height;

    String related;



    String dateString;



    ArrayList<String> statusdb;



    ArrayList<String> status;


  Date dateAdded;

    public Child(){

    }


    public String getRelated() {
        return related;
    }

    public void setRelated(String related) {
        this.related = related;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public String dateString(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(dateAdded);
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }
    public ArrayList<String> getStatus() {
        return status;
    }

    public void setStatus(ArrayList<String> status) {
        this.status = status;
    }
    public String getMonthAdded() {
        return monthAdded;
    }

    public void setMonthAdded(String monthAdded) {
        this.monthAdded = monthAdded;
    }
    public String getSitio(){return sitio;}
    public void setSitio(String sitio){this.sitio = sitio; }
    public String getPhoneNumber(){return phoneNumber;}
    public void setPhoneNumber(String phoneNumber){this.phoneNumber = phoneNumber; }
    public String getDownloadUrl(){return downloadUrl;}
    public void setDownloadUrl(String downloadUrl){this.downloadUrl = downloadUrl; }
    public String getForgulayan(){return forgulayan;}
    public void setForgulayan(String forgulayan){this.forgulayan = forgulayan; }
    public String getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(String monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public ArrayList<String> getStatusdb(){return statusdb;}
    public void setStatusdb(ArrayList<String> statusdb){this.statusdb = statusdb; }
    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getChildFirstName() {
        return childFirstName;
    }

    public void setChildFirstName(String childFirstName) {
        this.childFirstName = childFirstName;
    }

    public String getChildMiddleName() {
        return childMiddleName;
    }

    public void setChildMiddleName(String childMiddleName) {
        this.childMiddleName = childMiddleName;
    }

    public String getChildLastName() {
        return childLastName;
    }

    public void setChildLastName(String childLastName) {
        this.childLastName = childLastName;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBarangay() {
        return barangay;
    }

    public void setBarangay(String barangay) {
        this.barangay = barangay;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBelongtoIP() {
        return belongtoIP;
    }

    public void setBelongtoIP(String belongtoIP) {
        this.belongtoIP = belongtoIP;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getExpectedDate() {
        return expectedDate;
    }

    public void setExpectedDate(String expectedDate) {
        this.expectedDate = expectedDate;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getForfeeding() {
        return forfeeding;
    }

    public void setForfeeding(String forfeeding) {
        this.forfeeding = forfeeding;
    }

}