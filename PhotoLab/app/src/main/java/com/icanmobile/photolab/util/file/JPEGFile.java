package com.icanmobile.photolab.util.file;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;

/**
 * Created by JONG HO BAEK on 22,February,2019
 * email: icanmobile@gmail.com
 */
public class JPEGFile implements PhotoFile {

    private static final String TAG = JPEGFile.class.getSimpleName();

    //region singleton pattern
    public static volatile JPEGFile sInstance;
    public static JPEGFile getInstance() {
        if (sInstance == null) {
            synchronized(JPEGFile.class) {
                if (sInstance == null) sInstance = new JPEGFile();
            }
        }
        return sInstance;
    }
    private JPEGFile(){}
    //endregion


    private static final String JPEG_EXT = ".jpg";

    /**
     * get the private storage folder path of application
     * @param context the context
     * @return the private storage folder path.
     */
    @Override
    public String getStorageDir(@NonNull Context context) {
        return context.getExternalFilesDir(null).getAbsolutePath();
    }

    /**
     * get new photo file name based on date and time information
     * @param context the context
     * @return the new file name
     */
    @Override
    public String getNewName(@NonNull Context context) {
        return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + JPEG_EXT;
    }

    /**
     * save a photo file using the input bitmap
     * @param context the context
     * @param bitmap the input bitmap
     */
    @Override
    public void save(@NonNull Context context, @NonNull Bitmap bitmap) {
        try {
            OutputStream fOut;
            String name = getNewName(context);
            File file = new File(getStorageDir(context), getNewName(context));
            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);

            fOut.flush();
            fOut.close();

            Toast.makeText(context, "Save " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * delete a photo file
     * @param filePath the file path
     */
    @Override
    public void delete(@NonNull String filePath) {
        File jpgFile = new File(filePath);
        if (jpgFile.exists()) jpgFile.delete();
    }
}
