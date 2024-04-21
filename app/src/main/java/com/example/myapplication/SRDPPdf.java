package com.example.myapplication;


import java.util.ArrayList;

public class SRDPPdf {
    ArrayList<Child> childList;
    String[] tfAges = new String[19];
    SRDPSum sSum;
    SRDPConso sConso;
    SRDPList sList;

    int[][] listData;
    int[] consoData;
    String[] consoPerc;

    int[] dataData;
    int[] dataMother;
    int[] dataCount;

    String[][] masterData = new String[13][26];
    String[][] sumData = new String[5][6];

    String [] OPTCat, MotherCat, DataCat;
    String[] category = {
            "WFA - Normal", "WFA - OW", "WFA - UW", "WFA - SUW",
            "HFA - Normal", "HFA - Tall", "HFA - St", "HFA - Sst",
            "WFL/H - Normal", "WFL/H - OW", "WFL/H - Ob", "WFL/H - MW", "WFL/H - Sw"
    };

    public SRDPPdf(ArrayList<Child> childList){
        this.childList = childList;
        setObjects();
        setArrData();
    }
    public String[] getTfAges(){
        String[] tfAgesTemp = new String[19];
        int tfAgesInt[] = sList.tfAges(sList.monthFilter());
        tfAgesTemp[0] = "Total";
        for(int i=1; i<19; i++){
            tfAgesTemp[i] = String.valueOf(tfAgesInt[i-1]);
        }
        return tfAgesTemp;
    }
    public void setObjects(){
        this.sSum = new SRDPSum(childList);
        this.sConso = new SRDPConso(childList);
        this.sList = new SRDPList(childList);

        this.OPTCat = sSum.OPTCat;
        this.MotherCat = sSum.MotherCat;
        this.DataCat = sSum.DataCat;
    }
    public void setArrData(){
        this.listData = sList.countNow(sList.monthFilter());
        this.consoData = sConso.countNow(sConso.monthFilter());
        this.consoPerc = sConso.getPercentage(consoData);
        this.dataData = sSum.countNow(sSum.monthFilter());
        this.dataMother = sSum.countNowMother(sSum.monthFilter());
        this.dataCount = sSum.countNowData(childList);

        setMasterData();
        setSumData();
        this.tfAges = getTfAges();
    }

     public void setMasterData(){
        for(int i=0; i<4; i++){
            int k=0;
            int l=0;
            for(int j=0; j<6; j++){
                //--WFA
                masterData[i][k+1] = String.valueOf(listData[i + l][0]);
                masterData[i][k+2] = String.valueOf(listData[i + l][1]);
                masterData[i][k+3] = String.valueOf(listData[i+l][0] + listData[i+l][1]);


                k = k + 3;
                l = l+4;
            }
        }

         for(int i=4; i<8; i++){
             int k=0;
             int l=20;
             for(int j=0; j<6; j++){
                 //--HFA
                 masterData[i][k+1] = String.valueOf(listData[i + l][0]);
                 masterData[i][k+2] = String.valueOf(listData[i + l][1]);
                 masterData[i][k+3] = String.valueOf(listData[i +l][0] + listData[i+l][1]);

                 k = k + 3;
                 l = l+4;
             }
         }


         for(int i=8; i<13; i++){
             int k=0;
             int l=40;
             for(int j=0; j<6; j++){
                 //--WFH
                 masterData[i][k+1] = String.valueOf(listData[i + l][0]);
                 masterData[i][k+2] = String.valueOf(listData[i + l][1]);
                 masterData[i][k+3] = String.valueOf(listData[i + l][0] + listData[i+l][1]);
                 k = k + 3;
                 l = l+5;
             }
         }

         //--Consolidated
         for(int i=0; i<13; i++){
             int k=0;
             for(int j=19; j<23; j++){
                 if(j % 2 == 0){
                     masterData[i][j] = consoPerc[i];
                 }else{
                     masterData[i][j] = String.valueOf(consoData[i]);
                 }
                 k = k + 13;
             }
         }

         //--IP Children
         for(int i=0; i<13; i++){
             masterData[i][0] = category[i];
             for(int j=23; j<26; j++){
                 masterData[i][j] = "0";
             }
         }


     }

     public void setSumData(){
        for(int i=0; i<5; i++){
            if(i==0){
                sumData[i][0] = "";
                sumData[i][1] = "";
            }else{
                sumData[i][0] = OPTCat[i-1];
                sumData[i][1] = String.valueOf(dataData[i-1]);
            }
        }

        for(int i=0; i<5; i++){
            sumData[i][2] = MotherCat[i];
            sumData[i][3] = String.valueOf(dataMother[i]);
            sumData[i][4] = DataCat[i];
            sumData[i][5] = String.valueOf(dataCount[i]);
        }


     }

}
