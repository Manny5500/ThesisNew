package com.example.myapplication;


public class ChildModel {

    String childName, id;

    public ChildModel(){

    }

    public String getChildName(){
        return childName;
    }


    public String getId(){
        return id;
    }

    public void setChildName(String childName){this.childName = childName;}
    public void setId(String id) {
        this.id = id;
    }
}