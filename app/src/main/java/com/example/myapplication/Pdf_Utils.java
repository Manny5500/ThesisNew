package com.example.myapplication;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Pdf_Utils {

    ContentResolver resolver;
    byte[] byteArray;
    Context context;
    String filename;
    public Pdf_Utils(ContentResolver resolver, byte[] byteArray, Context context, String filename){
        this.resolver = resolver;
        this.byteArray = byteArray;
        this.context = context;
        this.filename = filename;
    }

    public void savePDFToStorage() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "NutriAssist");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + File.separator + "NutriAssist");

        Uri externalUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL);
        Uri pdfUri = resolver.insert(externalUri, values);
        try {
            if (pdfUri != null) {
                resolver.openOutputStream(pdfUri).write(byteArray);
                Toast.makeText(context, "PDF Saved Successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Error Creating PDF", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error Saving PDF", Toast.LENGTH_SHORT).show();
        }
    }

}
