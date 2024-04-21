package com.example.myapplication;

import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;

public class PY_PdfUtils {

    Rectangle customsize;
    String[] value;


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

            String[] label = {"Name", "Sex", "Date of Birth", "Age in month", "Date of Referral",
                    "Name of mother/caregiver", "Registration Number (mother/caregiver)", "Contact Number",
                    "Municipality", "Barangay", "House No"};

            PdfContentUtils.addText("Referral Transfer Form/SAM", document, true, "left");
            for(int i=0; i< label.length; i++){
                PdfContentUtils.addText(label[i]+" : "+value[i], document, false,"left");
            }
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
