package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PdfContentUtils {


    public static void addCell(PdfPTable table, String text, BaseColor backgroundColor, BaseColor textColor) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, textColor)));
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(6);
        cell.setBorderColor(BaseColor.BLACK);
        table.addCell(cell);
    }

    public static void addCell(PdfPTable table, String text, BaseColor backgroundColor, BaseColor textColor, int colspan, int rowspan) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, textColor)));
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(6);
        cell.setColspan(colspan);
        cell.setRowspan(rowspan);
        cell.setBorderColor(BaseColor.BLACK);
        table.addCell(cell);
    }
    public static void addText(String text, Document document)throws DocumentException {
        Paragraph paragraph = new Paragraph(text, new Font(Font.FontFamily.HELVETICA, 12));
        paragraph.setAlignment(paragraph.ALIGN_CENTER);
        document.add(paragraph);
    }
    public static void addText(String text, Document document, boolean isBold )throws DocumentException {
        Paragraph paragraph = new Paragraph(text, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD));
        paragraph.setAlignment(paragraph.ALIGN_CENTER);
        document.add(paragraph);
    }


    public static void addText(String text, Document document,  boolean isBold, String align)throws DocumentException {
        Paragraph paragraph;
        if(isBold){
            paragraph = new Paragraph(text, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD));
        }else {
            paragraph = new Paragraph(text, new Font(Font.FontFamily.HELVETICA, 12));
       }
        setAlign(paragraph,align);
        document.add(paragraph);
    }

    public static void setAlign(Paragraph paragraph, String align){
        switch (align){
            case "left":
                paragraph.setAlignment(paragraph.ALIGN_LEFT);
                break;
            case "right":
                paragraph.setAlignment(paragraph.ALIGN_RIGHT);
                break;
            case "justify":
                paragraph.setAlignment(paragraph.ALIGN_JUSTIFIED);
                break;
            default:
                paragraph.setAlignment(paragraph.ALIGN_CENTER);
        }

    }


    public static float percentage(int number1, int number2){
        float result = (float) number1 / number2;
        float percentage = Math.round(result * 100.0f);
        return percentage;
    }

    public static void addCell(PdfPTable table, String text){
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(6);
        table.addCell(cell);
    }
    public static void specificCell(PdfPTable table, String text, BaseColor cellColor){
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setBackgroundColor(cellColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(6);
        table.addCell(cell);
    }
    public static void leftAlignedCell(PdfPTable table, String text){
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(6);
        table.addCell(cell);
    }

    public static BaseColor getBaseColor(String hexColor){
        int red = Integer.valueOf(hexColor.substring(1, 3), 16);
        int green = Integer.valueOf(hexColor.substring(3, 5), 16);
        int blue = Integer.valueOf(hexColor.substring(5, 7), 16);
        BaseColor baseColor = new BaseColor(red, green, blue);
        return baseColor;
    }

    public static void addCell(PdfPTable table, String text, BaseColor backgroundColor, BaseColor textColor, int colspan) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, textColor)));
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(6);
        cell.setColspan(colspan);
        cell.setBorderColor(BaseColor.BLACK);
        table.addCell(cell);
    }

    public static void addCellML(PdfPTable table, String text, BaseColor backgroundColor, BaseColor textColor) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, textColor)));
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(6);
        cell.setBorderColor(BaseColor.BLACK);
        table.addCell(cell);
    }
    public static void addCellML(PdfPTable table, String text, BaseColor backgroundColor, BaseColor textColor,
                                 int colspan, int rowspan, int padding, int boldness, int border, int fontsize) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, new Font(Font.FontFamily.HELVETICA, fontsize, boldness, textColor)));
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(padding);
        cell.setColspan(colspan);
        cell.setRowspan(rowspan);
        cell.setBorderColor(BaseColor.BLACK);
        cell.setBorder(border);
        table.addCell(cell);
    }

    public static void addCellMLLogo(PdfPTable table, String text, BaseColor backgroundColor, BaseColor textColor, int colspan, int rowspan,  int padding, int boldness, int border, int fontsize,
                                     Drawable drawable) throws BadElementException, IOException {
        PdfPCell cell = new PdfPCell(new Paragraph(text, new Font(Font.FontFamily.HELVETICA, fontsize, boldness, textColor)));
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(padding);
        cell.setColspan(colspan);
        cell.setRowspan(rowspan);
        cell.setBorderColor(BaseColor.BLACK);
        cell.setBorder(border);

        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        Image img = Image.getInstance(byteArray);
        cell.addElement(img);
        table.addCell(cell);
    }
}
