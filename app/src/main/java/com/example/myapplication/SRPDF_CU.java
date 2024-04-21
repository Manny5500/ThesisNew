package com.example.myapplication;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

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

public class SRPDF_CU {
    public static void addCell(PdfPTable table, String text, BaseColor backgroundColor, BaseColor textColor) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, textColor)));
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(4);
        cell.setBorderColor(BaseColor.BLACK);
        table.addCell(cell);
    }

    public static void addCell(PdfPTable table, String text, BaseColor backgroundColor, BaseColor textColor, int colspan, int rowspan) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, textColor)));
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(4);
        cell.setColspan(colspan);
        cell.setRowspan(rowspan);
        cell.setBorderColor(BaseColor.BLACK);
        table.addCell(cell);
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


    public static String percentage(int number1, int number2){
        float result = (float) number1 / number2;
        float percentage = result * 100.0f;
        return String.format("%.2f", percentage) + " %";
    }

    public static void addCell(PdfPTable table, String text){
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(2);
        table.addCell(cell);
    }
    public static void specificCell(PdfPTable table, String text, BaseColor cellColor){
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setBackgroundColor(cellColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(4);
        table.addCell(cell);
    }
    public static void leftAlignedCell(PdfPTable table, String text){
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(4);
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
        PdfPCell cell = new PdfPCell(new Paragraph(text, new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, textColor)));
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPaddingTop(4);
        cell.setPaddingBottom(4);
        cell.setPaddingLeft(0);
        cell.setPaddingRight(0);
        cell.setColspan(colspan);
        cell.setBorder(Rectangle.TOP | Rectangle.LEFT | Rectangle.BOTTOM);
        table.addCell(cell);
    }
    public static void addCellTitle(PdfPTable table, String text, BaseColor backgroundColor, BaseColor textColor, int colspan) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, textColor)));
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(4);
        cell.setColspan(colspan);
        cell.setBorderColor(BaseColor.BLACK);
        table.addCell(cell);
    }



    public static void addCellR(PdfPTable table, String text, BaseColor backgroundColor, BaseColor textColor, int rowspan) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, textColor)));
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPaddingTop(4);
        cell.setPaddingBottom(4);
        cell.setPaddingLeft(0);
        cell.setPaddingRight(0);
        cell.setRowspan(rowspan);
        cell.setBorder(Rectangle.TOP | Rectangle.LEFT | Rectangle.BOTTOM);
        table.addCell(cell);
    }

    public static void addCellwRotates(PdfPTable table, String text, BaseColor backgroundColor, BaseColor textColor, int rowspan) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, textColor)));
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPaddingTop(4);
        cell.setPaddingBottom(4);
        cell.setPaddingLeft(4);
        cell.setPaddingRight(4);
        cell.setRowspan(rowspan);
        cell.setBorder(Rectangle.TOP | Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.RIGHT);
        cell.setRotation(90);
        table.addCell(cell);
    }

    public static void addCellGender(PdfPTable table, String text, BaseColor textColor){
        PdfPCell cell = new PdfPCell(new Paragraph(text, new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, textColor)));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(4);
        cell.setPaddingRight(0);
        cell.setPaddingLeft(0);
        table.addCell(cell);
    }

    public static void addCellPrev(PdfPTable table, String text, BaseColor backgroundColor, BaseColor textColor){
        PdfPCell cell = new PdfPCell(new Paragraph(text, new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, textColor)));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(4);
        cell.setPaddingRight(0);
        cell.setPaddingLeft(0);
        cell.setBackgroundColor(backgroundColor);
        cell.setBorderColor(textColor);
        table.addCell(cell);
    }

    public static void addCellTH(PdfPTable table, String text){
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(2);
        table.addCell(cell);
    }

    public static void addCellColTH(PdfPTable table, String text, BaseColor backgroundColor, BaseColor textColor, int colspan) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, textColor)));
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(2);
        cell.setColspan(colspan);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
    }

    public static void addCellCRTH(PdfPTable table, String text, BaseColor backgroundColor, BaseColor textColor, int colspan, int rowspan) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, textColor)));
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(4);
        cell.setColspan(colspan);
        cell.setRowspan(rowspan);
        cell.setBorderColor(BaseColor.GRAY);
        table.addCell(cell);
    }

    public static void addCellColTHBL(PdfPTable table, String text, BaseColor backgroundColor, BaseColor textColor, int colspan) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, textColor)));
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(2);
        cell.setColspan(colspan);
        cell.setBorder(Rectangle.BOTTOM);
        table.addCell(cell);
    }

    public static void addCellCRTHLogo(PdfPTable table, String text, BaseColor backgroundColor, BaseColor textColor, int colspan, int rowspan, Drawable drawable) throws BadElementException, IOException {
        PdfPCell cell = new PdfPCell(new Paragraph(text, new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, textColor)));
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(4);
        cell.setColspan(colspan);
        cell.setRowspan(rowspan);
        cell.setBorder(Rectangle.NO_BORDER);

        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        Image img = Image.getInstance(byteArray);
        Phrase phrase = new Phrase("OPT PLUS 2024", new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, textColor));
        cell.addElement(img);
        cell.addElement(phrase);
        table.addCell(cell);
    }

    public static void addCellColor(PdfPTable table, String text, BaseColor backgroundColor, BaseColor textColor, int colspan) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, textColor)));
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setColspan(colspan);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(2);
        table.addCell(cell);
    }

    public static void addCellColor(PdfPTable table, String text, BaseColor backgroundColor){
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(2);
        table.addCell(cell);
    }

    public static void addCellColorCenter(PdfPTable table, String text, BaseColor backgroundColor, BaseColor textColor, int colspan, int rowspan, int fontstyle) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, new Font(Font.FontFamily.HELVETICA, 11, fontstyle, textColor)));
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPaddingTop(4);
        cell.setPaddingBottom(4);
        cell.setPaddingLeft(0);
        cell.setPaddingRight(0);
        cell.setColspan(colspan);
        cell.setRowspan(rowspan);
        cell.setBorder(Rectangle.TOP | Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.RIGHT);
        table.addCell(cell);
    }
    public static void addCCCReg(PdfPTable table, String text, BaseColor backgroundColor, BaseColor textColor, int colspan) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, textColor)));
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPaddingTop(4);
        cell.setPaddingBottom(4);
        cell.setPaddingLeft(0);
        cell.setPaddingRight(0);
        cell.setColspan(colspan);
        cell.setBorder(Rectangle.TOP | Rectangle.LEFT | Rectangle.BOTTOM);
        table.addCell(cell);
    }
    public static void addCellReg(PdfPTable table, String text, BaseColor backgroundColor, BaseColor textColor){
        PdfPCell cell = new PdfPCell(new Phrase(text, new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, textColor)));
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorderColor(textColor);
        cell.setPadding(2);
        table.addCell(cell);
    }
    public static void addCellRegLeft(PdfPTable table, String text, BaseColor backgroundColor, BaseColor textColor){
        PdfPCell cell = new PdfPCell(new Phrase(text, new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, textColor)));
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorderColor(textColor);
        cell.setPadding(2);
        table.addCell(cell);
    }
    public static void addCellRegRight(PdfPTable table, String text, BaseColor backgroundColor, BaseColor textColor){
        PdfPCell cell = new PdfPCell(new Phrase(text, new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, textColor)));
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorderColor(textColor);
        cell.setPadding(2);
        table.addCell(cell);
    }
    public static void addCellCenter(PdfPTable table, String text, BaseColor backgroundColor, BaseColor textColor, int colspan) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, textColor)));
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPaddingTop(4);
        cell.setPaddingBottom(4);
        cell.setPaddingLeft(0);
        cell.setPaddingRight(0);
        cell.setColspan(colspan);
        cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
        table.addCell(cell);
    }
    public static void addCellCenterLast(PdfPTable table, String text, BaseColor backgroundColor, BaseColor textColor, int colspan) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, textColor)));
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPaddingTop(4);
        cell.setPaddingBottom(4);
        cell.setPaddingLeft(0);
        cell.setPaddingRight(0);
        cell.setColspan(colspan);
        cell.setBorder(Rectangle.TOP  | Rectangle.BOTTOM | Rectangle.RIGHT);
        table.addCell(cell);
    }



}
