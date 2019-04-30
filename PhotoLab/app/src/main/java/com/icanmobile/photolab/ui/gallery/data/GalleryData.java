package com.icanmobile.photolab.ui.gallery.data;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * Created by JONG HO BAEK on 22,February,2019
 * email: icanmobile@gmail.com
 *
 * GalleryData interface
 */
public interface GalleryData {

    /**
     * get a list of images in root directory
     * @param rootDir the root directory
     * @return the image list
     */
    List<String> getImages(@Nonnull String rootDir);
}
