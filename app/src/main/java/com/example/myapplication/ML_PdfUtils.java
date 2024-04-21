package com.example.myapplication;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ML_PdfUtils{
    Rectangle customsize;
    Drawable drawable;
    String barangay;
    String month;
    private ArrayList<Child> arrayList;
    public ML_PdfUtils(Rectangle customsize, ArrayList<Child> arrayList, Drawable drawable, String barangay, String month){
        this.customsize = customsize;
        this.arrayList = arrayList;
        this.drawable = drawable;
        this.barangay = barangay;
        this.month = month;

    }
    public byte[] PdfSetter(){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        pdfContentMaker(byteArrayOutputStream);
        byte[] pdfBytes = byteArrayOutputStream.toByteArray();
        return pdfBytes;
    }


    public void pdfContentMaker(ByteArrayOutputStream byteArrayOutputStream){
        try {
            // Create a PDF document
            Document document = new Document(customsize);
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();

            PdfPTable tableHead = new PdfPTable(25);
            tableHead.setWidthPercentage(100);

            int border1 = Rectangle.TOP |   Rectangle.LEFT;
            int border2 = Rectangle.TOP;
            int border3 =  Rectangle.TOP |  Rectangle.RIGHT;
            int border4 = Rectangle.NO_BORDER;
            int border5 = Rectangle.LEFT | Rectangle.RIGHT;
            int border6 = Rectangle.RIGHT;
            int border7 = Rectangle.LEFT;

            int[] borderArr = {border1, border2, border2, border2, border3};
            int[] colSpanArr = {5, 15, 1, 2, 2};
            int[]  rowsSpanArr = {1, 1, 1, 1, 2};

            BaseColor yellowColor = PdfContentUtils.getBaseColor("#fffcae");
            BaseColor greenColor = PdfContentUtils.getBaseColor("#77DD77");
            BaseColor redColor = PdfContentUtils.getBaseColor("#F8858B");
            BaseColor orangeColor = PdfContentUtils.getBaseColor("#FFAC4A");

            String[] headerArr1 = {
                    "", "Community Level e-OPT PLUS Tool", "", month, ""
            };

            for(int i=0; i<4; i++){
                PdfContentUtils.addCellML(tableHead, headerArr1[i],
                        yellowColor, BaseColor.BLACK, colSpanArr[i], rowsSpanArr[i], 6, 1, border4, 14
                        );
            }

            PdfContentUtils.addCellMLLogo(tableHead, "",
                    yellowColor, BaseColor.BLACK, 2, 2, 6, 1, border4, 14, drawable
            );

            borderArr = new int[] {border7, border4, border4, border4, border4};
            headerArr1 = new String[] {"", "For a maximum of 100 children in a small or medium sized barangay, use this file for a purok, section or part of the barangay",
                    "", "", ""};
            for(int i=0; i<4; i++){
                PdfContentUtils.addCellML(tableHead, headerArr1[i],
                        yellowColor, BaseColor.BLACK, colSpanArr[i], rowsSpanArr[i], 4, 0, border4, 9
                );
            }
            borderArr = new int[] {border7, border4, border6, border4, border6};
            headerArr1 = new String[] {"", "WEIGHT FOR AGE, HEIGHT FOR AGE & WEIGHT FOR LENGTH STATUS",
                     "Region: IVA CALABARZON", "", ""};
            colSpanArr = new int[] {5, 15, 5, 0, 0};
            for(int i=0; i<3; i++){
                PdfContentUtils.addCellML(tableHead, headerArr1[i],
                        yellowColor, BaseColor.BLACK, colSpanArr[i], rowsSpanArr[i], 4, 1, border4, 12
                );
            }

            PdfContentUtils.addCellML(tableHead, "",
                    BaseColor.DARK_GRAY, BaseColor.WHITE, 25, 1, 4, 1, border4, 11
            );
            PdfContentUtils.addCellML(tableHead, " ",
                    BaseColor.DARK_GRAY, BaseColor.WHITE, 25, 1, 4, 1, border4, 9
            );

            BaseColor purpleColor = PdfContentUtils.getBaseColor("#f0e4f4");

            int[] colSpanArr2 = {7, 5, 1, 5, 1, 5, 1};

            String[] headerArr2 = {"",
                    "Barangay: " + barangay ,  "", "Municipality: MAGDALENA", "","Province: Laguna", ""
            };
            for(int i=0; i<7; i++){
                PdfContentUtils.addCellML(tableHead, headerArr2[i],
                       purpleColor, BaseColor.BLACK, colSpanArr2[i], 1, 4, 1, border4, 12
                );
            }



            document.add(tableHead);


            // Create a table with 3 columns
            PdfPTable table = new PdfPTable(14);
            table.setWidthPercentage(100);
            float[] columnWidths = {12f, 25f, 40f, 40f, 15f,
                    15f, 18f, 18f, 12f, 12f,
            13f, 14f, 14f, 14f};
            table.setWidths(columnWidths);

            // Add table headers
            String hexColor = "#2596be";
            int red = Integer.valueOf(hexColor.substring(1, 3), 16);
            int green = Integer.valueOf(hexColor.substring(3, 5), 16);
            int blue = Integer.valueOf(hexColor.substring(5, 7), 16);
            BaseColor baseColor = new BaseColor(red, green, blue);



            PdfContentUtils.addCellML(table, "Child Seq", baseColor, BaseColor.WHITE);
            PdfContentUtils.addCellML(table, "Address or Location of Residence", baseColor, BaseColor.WHITE);
            PdfContentUtils.addCellML(table, "Name of mother or caregiver", baseColor, BaseColor.WHITE);
            PdfContentUtils.addCellML(table, "Full Name of Child", baseColor, BaseColor.WHITE);
            PdfContentUtils.addCellML(table, "Belong to IP Group", baseColor, BaseColor.WHITE);
            PdfContentUtils.addCellML(table, "Sex", baseColor, BaseColor.WHITE);
            PdfContentUtils.addCellML(table, "Date of Birth", baseColor, BaseColor.WHITE);
            PdfContentUtils.addCellML(table, "Actual Date of Weighing", baseColor, BaseColor.WHITE);
            PdfContentUtils.addCellML(table, "Height", baseColor, BaseColor.WHITE);
            PdfContentUtils.addCellML(table, "Weight", baseColor, BaseColor.WHITE);
            PdfContentUtils.addCellML(table, "Age In Mos", BaseColor.BLACK, BaseColor.WHITE);
            PdfContentUtils.addCellML(table, "WFA Status", BaseColor.BLACK, BaseColor.WHITE);
            PdfContentUtils.addCellML(table, "HFA Status", BaseColor.BLACK, BaseColor.WHITE);
            PdfContentUtils.addCellML(table, "WFL/H Status", BaseColor.BLACK, BaseColor.WHITE);



            // Add sample data
            for(Child child: arrayList){
                String middleName = child.getChildMiddleName();
                String parentmiddleName = child.getParentMiddleName();
                if(middleName.equals("NA")){
                    middleName = "";
                }
                if(parentmiddleName.equals("NA")){
                    parentmiddleName = "";
                }
                int position = arrayList.indexOf(child) + 1;
                table.addCell(new Phrase("" + position));
                table.addCell(new Phrase("" + child.getBarangay()));
                table.addCell(new Phrase(""+ child.getParentFirstName()
                        + " " + parentmiddleName + " " + child.getParentLastName()));
                table.addCell(new Phrase(""+ child.getChildFirstName()
                        + " " + middleName + " " + child.getChildLastName()));
                table.addCell(new Phrase(""+child.getBelongtoIP()));
                table.addCell(new Phrase(genderShort(child.getSex())));
                table.addCell(new Phrase(""+child.getBirthDate()));
                SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy");
                table.addCell(new Phrase(""+sdf.format(child.getDateAdded())));
                table.addCell(new Phrase("" + child.getHeight() ));
                table.addCell(new Phrase("" + child.getWeight() ));
                table.addCell(new Phrase(""+ getMonth(child)));

                String WFA = getWFAStatus(child);
                String HFA = getHFAStatus(child);
                String WFH = getWFHStatus(child);


                if(WFA.equals("N") || WFA.equals("")){
                    PdfContentUtils.addCell(table, WFA, greenColor, BaseColor.BLACK);
                }else if( WFA.equals("UW")){
                    PdfContentUtils.addCell(table, WFA, yellowColor, BaseColor.BLACK);
                }else if(WFA.equals("SUW")){
                    PdfContentUtils.addCell(table, WFA, redColor, BaseColor.BLACK);
                }else if(WFA.equals("OW")){
                    PdfContentUtils.addCell(table, WFA, orangeColor, BaseColor.BLACK);
                }else{
                    PdfContentUtils.addCell(table, WFA, BaseColor.WHITE, BaseColor.BLACK);
                }

                if(HFA.equals("N") || HFA.equals("") || HFA.equals("T")){
                    PdfContentUtils.addCell(table, HFA, greenColor, BaseColor.BLACK);
                }else if(HFA.equals("ST")){
                    PdfContentUtils.addCell(table, HFA, yellowColor, BaseColor.BLACK);
                }else if(HFA.equals("SST")){
                    PdfContentUtils.addCell(table, HFA, redColor, BaseColor.BLACK);
                }else{
                    PdfContentUtils.addCell(table, HFA, BaseColor.WHITE, BaseColor.BLACK);
                }

                if(WFH.equals("N") || WFH.equals("")){
                    PdfContentUtils.addCell(table, WFH, greenColor, BaseColor.BLACK);
                }else if(WFH.equals("MW")){
                    PdfContentUtils.addCell(table, WFH, yellowColor, BaseColor.BLACK);
                }else if(WFH.equals("SW")){
                    PdfContentUtils.addCell(table, WFH, redColor, BaseColor.BLACK);
                }else if(WFH.equals("OW") || WFH.equals("OB")){
                    PdfContentUtils.addCell(table, WFH, orangeColor, BaseColor.BLACK);
                }else{
                    PdfContentUtils.addCell(table, WFH, BaseColor.WHITE, BaseColor.BLACK);
                }

            }

            // Add the table to the document
            document.add(table);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getMonth(Child child){
        FormUtils formUtils = new FormUtils();
        Date parsedDate = formUtils.parseDate(child.getBirthDate());
        int monthdiff = 0;
        if (parsedDate != null) {
            monthdiff = formUtils.calculateMonthsDifference(parsedDate);
        }

        return monthdiff;
    }

    public String getWFAStatus(Child child){
        String WFAStatus = "";
        if(child.getStatus().get(0).equals("Normal") || child.getStatus().get(0).equals("")){
            WFAStatus = "N";
        } else if (child.getStatus().get(0).equals("Overweight")) {
            WFAStatus = "OW";
        } else if (child.getStatus().get(0).equals("Underweight")){
            WFAStatus = "UW";
        } else if(child.getStatus().get(0).equals("Severe Underweight")){
            WFAStatus = "SUW";
        }
        return WFAStatus;
    }

    public String getHFAStatus(Child child){
        String WFAStatus = "";
        if(child.getStatus().get(1).equals("Normal") ||
        child.getStatus().get(1).equals("")){
            WFAStatus = "N";
        } else if (child.getStatus().get(1).equals("Stunted")) {
            WFAStatus = "ST";
        } else if (child.getStatus().get(1).equals("Severe Stunted")){
            WFAStatus = "SST";
        } else if(child.getStatus().get(1).equals("Tall")){
            WFAStatus = "T";
        }
        return WFAStatus;
    }

    public String getWFHStatus(Child child){
        String WFAStatus = " ";
        if(child.getStatus().get(2).equals("Normal") || child.getStatus().get(2).equals("")){
            WFAStatus = "N";
        } else if (child.getStatus().get(2).equals("Wasted")) {
            WFAStatus = "MW";
        } else if (child.getStatus().get(2).equals("Severe Wasted")){
            WFAStatus = "SW";
        } else if(child.getStatus().get(2).equals("Overweight")){
            WFAStatus = "OW";
        } else if (child.getStatus().get(2).equals("Obese")) {
            WFAStatus = "OB";
        }
        return WFAStatus;
    }

    public String genderShort(String gender){
        if(gender.equals("Male")){
            return "M";
        }else{
            return "F";
        }
    }

}
