package com.icanmobile.photolab.util.file;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

/**
 * Created by JONG HO BAEK on 22,February,2019
 * email: icanmobile@gmail.com
 *
 * PhotoFile Interface
 * This interface defines the methods of file.
 */
public interface PhotoFile {

    /**
     * get the private storage folder path of application
     * @param context the context
     * @return the private storage folder path.
     */
    String getStorageDir(@NonNull Context context);

    /**
     * get new photo file name based on date and time information
     * @param context the context
     * @return the new file name
     */
    String getNewName(@NonNull Context context);

    /**
     * save a photo file using the input bitmap
     * @param context the context
     * @param bitmap the input bitmap
     */
    void save(@NonNull Context context, @NonNull Bitmap bitmap);

    /**
     * delete a photo file
     * @param filePath the file path
     */
    void delete(@NonNull String filePath);
}
