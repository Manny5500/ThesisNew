package com.example.myapplication;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class PR_PdfUtils {
    private String[] status_array;
    private String methodType;
    private ArrayList<BarangayModel> arrayList;
    private Rectangle customsize;
    private String type;
    public PR_PdfUtils(String[] status_array, String methodType,
                       ArrayList<BarangayModel> arrayList,
                       Rectangle customsize, String type) {
        this.status_array = status_array;
        this.methodType = methodType;
        this.arrayList = arrayList;
        this.customsize = customsize;
        this.type = type;
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
            //header
            PdfContentUtils.addText("Region IVA: CALABARZON", document);
            PdfContentUtils.addText("MUNICIPALITY OF MAGDALENA", document, true);
            PdfContentUtils.addText("PROVINCE: LAGUNA", document);
            PdfContentUtils.addText("OPERATION TIMBANG PLUS 2023", document);
            PdfContentUtils.addText(type.toUpperCase(), document, true);
            PdfContentUtils.addText("PREVALANCE AND NUMBER OF AFFECTED CHILDREN UNDER FIVE, BY BARANGAY", document);
            PdfContentUtils.addText("\n", document);

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            float[] columnWidths = {10f, 24f, 15f, 17f, 17f, 17f};
            table.setWidths(columnWidths);

            BaseColor baseColor = PdfContentUtils.getBaseColor("#a6aa91");
            //table-header
            PdfContentUtils.addCell(table, "Rank", baseColor, BaseColor.BLACK,0,2);
            PdfContentUtils.addCell(table, "Barangay", baseColor, BaseColor.BLACK,0,2);
            PdfContentUtils.addCell(table, "0-59 Months OPT Plus Coverage (%)", baseColor, BaseColor.BLACK, 0, 2);
            PdfContentUtils.addCell(table, "" + methodType, PdfContentUtils.getBaseColor("#c4c6bb"), BaseColor.BLACK, 3,1);
            PdfContentUtils.addCell(table, "Normal" + " (%)", baseColor, BaseColor.BLACK);
            PdfContentUtils.addCell(table, status_array[0] + " + " + status_array[1] + " (%)", baseColor, BaseColor.BLACK);
            PdfContentUtils.addCell(table, "Number of " + status_array[0] + " + " + status_array[1] , baseColor, BaseColor.BLACK);
            baseColor = PdfContentUtils.getBaseColor("#c4c6bb");
            //table-data
            for(BarangayModel barangayModel: arrayList){
                int position = arrayList.indexOf(barangayModel) + 1;
                PdfContentUtils.addCell(table,"" + position );
                PdfContentUtils.leftAlignedCell(table, "" + barangayModel.getBarangay());
                PdfContentUtils.addCell(table,"" + PdfContentUtils.percentage(barangayModel.getTotalAssess(),barangayModel.getEstimatedChildren()) + "%" );
                PdfContentUtils.addCell(table,"" + PdfContentUtils.percentage(barangayModel.getNormal(), barangayModel.getTotalAssess())+ "%" );
                PdfContentUtils.specificCell(table, "" + PdfContentUtils.percentage(barangayModel.getTotalCase(),barangayModel.getTotalAssess()) + "%", baseColor);
                PdfContentUtils.addCell(table,"" + barangayModel.getTotalCase());
            }
            document.add(table);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
