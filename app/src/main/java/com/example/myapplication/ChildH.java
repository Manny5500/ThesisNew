package com.example.myapplication;

import android.content.Context;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ChildH implements Serializable {
    protected double weight, height;


    String id;


    ArrayList<String> statusdb;



    ArrayList<String> status;

    ArrayList<String> statusProgress;


    public ChildH(){};
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getStatusdb() {
        return statusdb;
    }

    public void setStatusdb(ArrayList<String> statusdb) {
        this.statusdb = statusdb;
    }

    public ArrayList<String> getStatus() {
        return status;
    }

    public void setStatus(ArrayList<String> status) {
        this.status = status;
    }



}
