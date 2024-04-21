package com.example.myapplication;


import android.graphics.drawable.Drawable;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;


public class SR_PdfUtils {
    private int estimatedChildren;
    private int population;

    String barangay;
    private Rectangle customsize;

    Drawable drawable;

    SRDPPdf srdpPdf;

    public SR_PdfUtils(Rectangle customsize, String barangay,
                       int estimatedChildren, int population,
                       SRDPPdf srdpPdf, Drawable drawable){
        this.customsize = customsize;
        this.barangay = barangay;
        this.estimatedChildren = estimatedChildren;
        this.population = population;
        this.srdpPdf = srdpPdf;
        this.drawable = drawable;
    }

    public byte[] PdfSetter(){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        pdfContentMaker(byteArrayOutputStream);
        byte[] pdfBytes = byteArrayOutputStream.toByteArray();
        return pdfBytes;
    }

    public void pdfContentMaker(ByteArrayOutputStream byteArrayOutputStream){
        try {
            int childrenMeasured = Integer.parseInt(srdpPdf.sumData[0][3]);
            String[] totalHeader = {"PSGC:", "0403416019","Total WFA:", srdpPdf.sumData[0][3],
                    "Total HFA:", srdpPdf.sumData[0][3], "Total WFL/H:", srdpPdf.sumData[0][3], "Birth to 5 years",
            "F1K", "#IP Children"};
            String[] header1 = {"ACRONYMS & ABBREVATIONS", " 0-5 Months", "6- 11 Months", "12-23 Months",
            "24-35 Months", "36-47 Months", "48-59 Months", "0-59 Months", "0-23 Months", "Boys", "Girls", "Total"};
            String[] header2 = {"Boys", "Girls", "Total"};
            String[] header2_1 = {"Total", "Prev"};

            String[] masterHeader1 = {"Province: ",   "Laguna", "Regn:", "IV-A CALABARZON", "OPT Plus Coverage:",
                    SRPDF_CU.percentage(childrenMeasured,estimatedChildren)};
            String[] masterHeader2 = {"Barangay: ", barangay, "Total Popn Barangay:", String.valueOf(population)};
            String[] masterHeader3 = {"Municipality: ", "MAGDALENA", "Estimated Popn of Children 0-59 mos in Barangay:", String.valueOf(estimatedChildren)};

            // Create a PDF document
            Document document = new Document(customsize);
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();
            //header

            BaseColor textColor = SRPDF_CU.getBaseColor("#ffffff");
            BaseColor textColor2 = SRPDF_CU.getBaseColor("#000000");
            BaseColor yellowColor = SRPDF_CU.getBaseColor("#fffcae");
            BaseColor grayColor = SRPDF_CU.getBaseColor("#D3D3D3");
            BaseColor darkerGray = SRPDF_CU.getBaseColor("#C0C0C0");
            BaseColor darkestGray = SRPDF_CU.getBaseColor("#808080");
            BaseColor blueColor = SRPDF_CU.getBaseColor("#2600FF");
            BaseColor peachColor = SRPDF_CU.getBaseColor("#d39885");

            PdfPTable tableheader = new PdfPTable(26);
            tableheader.setWidthPercentage(100);

            //--header 1st row
            for(int i=0; i<6; i++){
                SRPDF_CU.addCellColTH(tableheader, " ", textColor, textColor2, 0);
            }
            int k=0;
            for(int i=0; i<2; i++){
                SRPDF_CU.addCellColTH(tableheader, masterHeader1[0+i+k],textColor, textColor2, 2 );
                SRPDF_CU.addCellColTHBL(tableheader, masterHeader1[1+i+k],textColor, textColor2, 3 );
                k++;
            }
            SRPDF_CU.addCellColTH(tableheader, masterHeader1[4],textColor, textColor2, 2 );
            SRPDF_CU.addCellColTHBL(tableheader, masterHeader1[5],textColor, textColor2, 2 );
            SRPDF_CU.addCellCRTH(tableheader, "Total # indigenous Preschool Children Measured\n\n 0-59 mos:  0", textColor, textColor2, 3,2);
            SRPDF_CU.addCellCRTHLogo(tableheader, "", textColor, textColor2, 3,3, drawable);

            //--header 2nd row
            k=0;
            for(int i=0; i<2; i++){
                SRPDF_CU.addCellColTH(tableheader, masterHeader2[i+0+k], textColor, textColor2, 4);
                SRPDF_CU.addCellColTHBL(tableheader, masterHeader2[i+1+k], textColor, textColor2, 3);
                k++;
            }
            for(int i=0; i<6; i++){
                SRPDF_CU.addCellColTH(tableheader, "", textColor, textColor,0);
            }

            //--header 3rd row
            k=0;
            for(int i=0; i<2; i++){
                SRPDF_CU.addCellColTH(tableheader, masterHeader3[i+0+k], textColor, textColor2, 4);
                SRPDF_CU.addCellColTHBL(tableheader, masterHeader3[i+1+k], textColor, textColor2, 3);
                k++;
            }
            for(int i=0; i<9; i++){
                SRPDF_CU.addCellColTH(tableheader, "", textColor, textColor,0);
            }

            document.add(tableheader);

            PdfPTable table = new PdfPTable(26);
            table.setWidthPercentage(100);
            float[] columnWidths = {12.11f,
                    3.17f, 3.17f, 3.17f,
                    3.17f, 3.17f, 3.17f,
                    3.17f, 3.17f, 3.17f,
                    3.17f, 3.17f, 3.17f,
                    3.17f, 3.17f, 3.17f,
                    3.17f, 3.17f, 3.17f,
                    3.76f, 6.80f,
                    3.76f, 6.80f,
                    3.17f, 3.17f, 3.17f};
            table.setWidths(columnWidths);



            //table-header - PSGC
            SRPDF_CU.addCellColTH(table, totalHeader[0], textColor, textColor2, 2);
            SRPDF_CU.addCellColTHBL(table, totalHeader[1], textColor, textColor2,4);
            SRPDF_CU.addCellColTH(table, "", textColor, textColor, 4);
            k=0;
            for(int i=0; i<3; i++){
                SRPDF_CU.addCellColor(table, totalHeader[i+2+k], grayColor, textColor2, 2);
                SRPDF_CU.addCellColor(table, totalHeader[i+3+k], yellowColor);
                k++;
            }
            SRPDF_CU.addCellColorCenter(table, totalHeader[8], textColor2, textColor, 2, 0, 1);
            SRPDF_CU.addCellColorCenter(table, totalHeader[9], grayColor, textColor2, 2, 0, 1);
            SRPDF_CU.addCellColorCenter(table, totalHeader[10], textColor, textColor2, 11,0, 1);


            //table-header - ACRO
            SRPDF_CU.addCellColorCenter(table,header1[0], textColor, blueColor,  0, 2, 0);
            for(int i=1; i<7; i++){
                SRPDF_CU.addCellColorCenter(table,header1[i], textColor, textColor2, 3, 0, 0);
            }
            SRPDF_CU.addCCCReg(table,header1[7], textColor2, textColor, 2);
            SRPDF_CU.addCCCReg(table,header1[8], grayColor, textColor2, 2);
            for(int i=9; i<12; i++){
                SRPDF_CU.addCellwRotates(table,header1[i], textColor, textColor2,  2);
            }

            //table-header - Boys Girls
            for(int i=0; i<6; i++){
                for(int j=0; j<3; j++){
                    SRPDF_CU.addCellGender(table, header2[j], textColor2);
                }
            }

            for(int i=0; i<2; i++){
                for(int j=0; j<2; j++){
                    if(i==0){
                        SRPDF_CU.addCellPrev(table,header2_1[j], textColor2, textColor);
                    }else{
                        SRPDF_CU.addCellPrev(table,header2_1[j], grayColor, textColor2);
                    }
                }
            }


            //table-data
            for(int i=0; i<13; i++){
                for(int j=0; j<26; j++){
                    if(i<4 || (i>7 && i<13)){
                        if((j==19 || j==20)){
                            SRPDF_CU.addCellReg(table,srdpPdf.masterData[i][j], textColor2, textColor);
                        } else if (j==21 || j==22) {
                            SRPDF_CU.addCellReg(table,srdpPdf.masterData[i][j], grayColor, textColor2);
                        }else{
                            if(j==0){
                                SRPDF_CU.addCellRegLeft(table, srdpPdf.masterData[i][j], textColor, textColor2);
                            }else{
                                SRPDF_CU.addCellReg(table, srdpPdf.masterData[i][j], textColor, textColor2);
                            }

                        }
                    }
                    else if(i>3 && i<8){
                        if((j==19 || j==20)){
                            SRPDF_CU.addCellReg(table,srdpPdf.masterData[i][j], darkestGray, textColor2);
                        } else if (j==21 || j==22) {
                            SRPDF_CU.addCellReg(table,srdpPdf.masterData[i][j], darkerGray, textColor2);
                        }else{
                            if(j==0){
                                SRPDF_CU.addCellRegLeft(table, srdpPdf.masterData[i][j], grayColor, textColor2);
                            }else{
                                SRPDF_CU.addCellReg(table, srdpPdf.masterData[i][j], grayColor, textColor2);
                            }
                        }
                    }
                    else {
                        SRPDF_CU.addCellReg(table, srdpPdf.masterData[i][j], textColor, textColor2);
                    }
                }
            }


            for(int i=0; i<20; i++){
                if(i==0){
                    SRPDF_CU.addCellRegRight(table,srdpPdf.tfAges[i], textColor2, textColor);
                } else if(i>0 && i<19){
                    SRPDF_CU.addCellReg(table,srdpPdf.tfAges[i], textColor2, textColor);
                } else{
                    SRPDF_CU.addCell(table, "", darkestGray, textColor, 7, 0);
                }
            }

            int[] colspan = {7, 10, 6};

            String[] sumHeader = {"Summary of Children covered by e-OPT Plus", "Mothers/Caregivers Summary", "Data Summary" };
            for(int i=0; i<3; i++){
                    SRPDF_CU.addCellTitle(table, sumHeader[i], peachColor, textColor2, colspan[i]+1);
            }

            for(int i=0; i<5; i++){
                for(int j=0; j<6; j++){
                        if(j==0 || j==2){
                            if(i>2){
                                SRPDF_CU.addCell(table, srdpPdf.sumData[i][j],grayColor,textColor2,colspan[Math.round(j/2)]);
                            }else{
                                SRPDF_CU.addCell(table, srdpPdf.sumData[i][j],textColor,textColor2,colspan[Math.round(j/2)]);
                            }
                        } else if (j==4) {
                            SRPDF_CU.addCell(table, srdpPdf.sumData[i][j],textColor,textColor2,6);
                        }

                        else{
                            if(i>2 && j<4){
                                SRPDF_CU.addCellCenter(table, srdpPdf.sumData[i][j],grayColor,textColor2,0);
                            }else{
                                SRPDF_CU.addCellCenterLast(table, srdpPdf.sumData[i][j],textColor,textColor2,0);
                            }
                        }
                }
            }

            document.add(table);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
