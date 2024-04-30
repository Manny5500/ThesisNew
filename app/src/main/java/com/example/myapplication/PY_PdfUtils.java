package com.example.myapplication;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;

public class PY_PdfUtils {

    Rectangle customsize;
    String[] value ;
    static BaseColor blueColor, grandblueColor, whiteColor, blackColor;
    public PY_PdfUtils(Rectangle customsize, String[] value){
        this.customsize = customsize;
        this.value = value;
    }

    public byte[] PdfSetter(){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        pdfContentMaker(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    public void pdfContentMaker(ByteArrayOutputStream byteArrayOutputStream){
        try {
            // Create a PDF document
            Document document = new Document(customsize);
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();
            //contents

            PdfContentUtils.addTextRF("Region IVA: CALABARZON", document, 10, 0);
            PdfContentUtils.addTextRF("MUNICIPALITY OF MAGDALENA", document, 10, 1);
            PdfContentUtils.addTextRF("PROVINCE: LAGUNA", document, 10, 0);
            PdfContentUtils.addTextRF("\n", document, 5, 0);


            /*
            String[] label = {"Name", "Sex", "Date of Birth", "Age in month", "Date of Referral",
                    "Name of mother/caregiver", "Registration Number (mother/caregiver)", "Contact Number",
                    "Municipality", "Barangay", "House No"};
            for(int i=0; i< label.length; i++){
                PdfContentUtils.addText(label[i]+" : "+value[i], document, false,"left");
            }*/

            setBaseColor();

            PdfPTable table = new PdfPTable(12);
            table.setWidthPercentage(100);
            SRPDF_CU.addCell(table, "REFERRAL TRANSFER FORM/SAM",  grandblueColor,whiteColor,12, 1);
            addCellLabel(table, "Date of Refferal",  3, 1);
            addCellValue(table, value[0], 9, 1);
            String[] name_label = {"Last Name", "First Name","Middle Name"};
            Child child = App.child;
            String[] name_val = {child.getChildLastName(), child.getChildFirstName(), child.getChildMiddleName()};
            addCellLabel(table, "Name of Child",  3, 2);
            for(int i=0; i<3; i++){
                addCellLabel(table, name_label[i],  3, 1);
            }
            for(int i=0; i<3; i++){
                addCellValue(table, name_val[i],  3, 1);
            }
            String[] second_label = {"Date of Birth", "Age in Months", "Gender"};
            for(int i=0; i<3; i++){
                addCellLabel(table, second_label[i],  4, 1);
            }
            String[] second_val = {child.getBirthDate(), value[1] + " mos.", "Male"};
            for(int i=0; i<3; i++){
               addCellValue(table, second_val[i], 4, 1);
            }
            String[] third_label = {"Height", String.valueOf(child.getHeight()) + " cm", "Weight", String.valueOf(child.getWeight()) + " kg" };
            for(int i=0; i<4; i++){
                if(i%2==0){
                    addCellLabel(table, third_label[i],  3, 1);
                }else{
                   addCellValue(table, third_label[i],  3, 1);
                }
            }
            String[] ff_label = {"Status", "Name of Parent"};
            String statusmaker = "";
            int k=0;
            for(String s: child.getStatusdb()){
                if(k==App.child.getStatusdb().size()-1){
                    statusmaker = statusmaker + s;
                }else{
                    statusmaker = statusmaker + s + ", ";
                }
                k++;
            }

            String[] ff_value = {statusmaker, child.getParentLastName() + ", " + child.getParentFirstName(), ", " + child.getParentMiddleName()};
            for(int i=0; i<2; i++){
                addCellLabel(table, ff_label[i],  3, 1);
                addCellValue(table, ff_value[i],  9, 1);
            }
            String[]  sixth_label = {"Email", child.getGmail(), "Cellphone #", child.getPhoneNumber()};
            for(int i=0; i<4; i++){
                if(i%2==0){
                    addCellLabel(table, sixth_label[i], 3, 1);
                }else{
                   addCellValue(table, sixth_label[i], 3, 1);
                }
            }
            String houseNumber = App.child.getHouseNumber();
            if(houseNumber==null){
                houseNumber = "";
            }
            String sitio = App.child.getSitio();
            if(sitio == null){
                sitio = "";
            }
            String[]  seventh_label = {"Address", houseNumber + " " + sitio + " Brgy. " + App.child.getBarangay() + " Magdalena, Laguna"};
            for(int i=0; i<2; i++){
                if(i==0){
                    addCellLabel(table, seventh_label[i], 3, 1);
                }else {
                   addCellValue(table, seventh_label[i],  9, 1);

                }
            }
            document.add(table);

            PdfContentUtils.addTextRF("powered by NutriAssist", document, 9, 0);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setBaseColor(){
        blueColor = SRPDF_CU.getBaseColor("#DEEBF2");
        grandblueColor = SRPDF_CU.getBaseColor("#51ADE5");
        whiteColor = SRPDF_CU.getBaseColor("#FFFFFF");
        blackColor = SRPDF_CU.getBaseColor("#000000");
    }

    public static void addCellLabel(PdfPTable table, String text, int colspan, int rowspan) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, blackColor)));
        cell.setBackgroundColor(blueColor);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(4);
        cell.setColspan(colspan);
        cell.setRowspan(rowspan);
        cell.setBorderColor(BaseColor.BLACK);
        table.addCell(cell);
    }
    public static void addCellValue(PdfPTable table, String text, int colspan, int rowspan) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, blackColor)));
        cell.setBackgroundColor(whiteColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(4);
        cell.setColspan(colspan);
        cell.setRowspan(rowspan);
        cell.setBorderColor(BaseColor.BLACK);
        table.addCell(cell);
    }

}
