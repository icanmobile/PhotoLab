package com.icanmobile.photolab.ui.gallery.data;

import android.annotation.TargetApi;
import android.os.Build;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * Created by JONG HO BAEK on 22,February,2019
 * email: icanmobile@gmail.com
 *
 * StorageGalleryData
 * this class supports to search image files of the app private folder.
 */
public class StorageGalleryData implements GalleryData {

    private static final String TAG = StorageGalleryData.class.getSimpleName();

    //region singleton pattern
    public static volatile StorageGalleryData sInstace;
    public static StorageGalleryData getInstance() {
        if (sInstace == null) {
            synchronized(StorageGalleryData.class) {
                if (sInstace == null) sInstace = new StorageGalleryData();
            }
        }
        return sInstace;
    }
    private StorageGalleryData(){}
    //endregion


    /**
     * get a list of images in root directory
     * @param rootDir the root directory
     * @return the image list
     */
    @TargetApi(Build.VERSION_CODES.N)
    public List<String> getImages(@Nonnull String rootDir) {
        File dir = new File(rootDir);
        HashSet<String> imageFiles = new HashSet<>();
        getImages(dir, imageFiles);

        // photo name is made by data and time strings.
        // so we return the image list which is sorted by descending order.
        List<String> result = new ArrayList<>(imageFiles);
//        Collections.sort(result, Collections.reverseOrder());
        Collections.sort(result, Collections.reverseOrder());
        return result;
    }

    /**
     * add all image files under dir folder recursively.
     * @param dir the root directory
     * @param imageFiles the set for image files.
     */
    private void getImages(@Nonnull File dir, HashSet<String> imageFiles) {
        File[] files = dir.listFiles(new ImageFileFilter());
        for(File file : files) {
            if (file.isFile())
                imageFiles.add(file.getPath());
            else if (file.isDirectory()) {
                getImages(file, imageFiles);
            }
        }
    }
}

