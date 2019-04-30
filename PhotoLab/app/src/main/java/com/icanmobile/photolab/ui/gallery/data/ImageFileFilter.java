package com.icanmobile.photolab.ui.gallery.data;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import javax.annotation.Nonnull;

/**
 * Created by JONG HO BAEK on 22,February,2019
 * email: icanmobile@gmail.com
 *
 * ImageFileFilter class
 * this class supports the filter functionality for the specific file extensions such as "jpg", "png", and "bmp"
 */
public class ImageFileFilter implements FileFilter {

    private static final String TAG = ImageFileFilter.class.getSimpleName();

    /**
     * whether searching the sub directory or not.
     */
    private final boolean allowDirectories;


    public ImageFileFilter() {
        this(true);
    }
    public ImageFileFilter(boolean allowDirectories) {
        this.allowDirectories = allowDirectories;
    }

    /**
     * check the specific file exist or not
     * @param f the input directory
     * @return
     */
    @Override
    public boolean accept(@Nonnull File f) {
        if (f.isHidden() || !f.canRead()) return false;

        if (f.isDirectory())
            return checkDirectory(f);

        return checkFileExtension(f);
    }

    /**
     * check file extension
     * @param f the file
     * @return
     */
    private boolean checkFileExtension(@Nonnull File f) {
        String ext = getFileExtension(f);
        if (ext == null) return false;

        try {
            if (SupportedFileFormat.valueOf(ext.toUpperCase()) != null)
                return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
        return false;
    }

    /**
     * get file extension from the inpout file
     * @param f the input file
     * @return
     */
    public String getFileExtension(@Nonnull File f ) {
        return getFileExtension( f.getName() );
    }
    public String getFileExtension(@Nonnull String fileName ) {
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            return fileName.substring(i+1);
        } else
            return null;
    }


    /**
     * check the direction whether it includes the specific file or not.
     * @param dir the directory
     * @return the status of the specific file exist.
     */
    private boolean checkDirectory(@Nonnull File dir) {
        if (!allowDirectories) return false;

        final ArrayList<File> subDirs = new ArrayList<>();
        int nums = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isFile())
                    return checkFileExtension(file);
                else if (file.isDirectory()) {
                    subDirs.add(file);
                    return false;
                }
                else
                    return false;
            }
        }).length;

        if (nums > 0)
            return true;

        for (File subDir : subDirs) {
            if (checkDirectory(subDir)) {
                return true;
            }
        }
        return false;
    }



    /**
     * Files formats currently supported by Library
     */
    public enum SupportedFileFormat
    {
        JPG("jpg"),
        JPEG("jpeg");

        private String filesuffix;

        SupportedFileFormat(@Nonnull String filesuffix ) {
            this.filesuffix = filesuffix;
        }

        public String getFilesuffix() {
            return filesuffix;
        }
    }
}
