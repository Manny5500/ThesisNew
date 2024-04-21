package com.example.myapplication;

public class User {

    private String firstName;
    private String middleName;
    private String email;
    private String birthdate;
    private String sex;
    private String barangay;

    private String lastName;
    private String user;
    private String contact;

    private String id;

    private String imageUrl;

    private String deletionRequest;



    private String isArchive;

    private String verified;


    public User(){}


    public User(String firstName, String middleName, String lastName, String email, String birthdate,
                String sex, String barangay, String user, String contact, String verified) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.birthdate = birthdate;
        this.sex = sex;
        this.barangay = barangay;
        this.user = user;
        this.contact = contact;
        this.verified = verified;
    }


    public String getVerified(){return verified;}
    public void setVerified(String verified){this.verified = verified;}
    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBarangay() {
        return barangay;
    }

    public void setBarangay(String barangay) {
        this.barangay = barangay;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public String getDeletionRequest(){
        return deletionRequest;
    }

    public void setDeletionRequest(String deletionRequest) {
        this.deletionRequest = deletionRequest;
    }

    public String getIsArchive() {
        return isArchive;
    }

    public void setIsArchive(String isArchive) {
        this.isArchive = isArchive;
    }


}
