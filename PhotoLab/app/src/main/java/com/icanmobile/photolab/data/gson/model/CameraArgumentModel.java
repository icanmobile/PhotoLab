package com.icanmobile.photolab.data.gson.model;

import javax.annotation.Nonnull;

/**
 * Created by JONG HO BAEK on 25,February,2019
 * email: icanmobile@gmail.com
 *
 * CameraArgumentModel class
 * This class extended from ArgumentModel class and added photoName member variable.
 * After taking photo, CameraActivity has to share the photo absolute path with PhotoActivity.
 * When activities are changed, this class object will be delivered to PhotoActivity object.
 */
public class CameraArgumentModel extends ArgumentModel {

    /**
     * call super class with json file and transition direction information
     * @param json the activity json file
     * @param dir the transition direction
     * @param photoName the still shot photo path
     */
    public CameraArgumentModel(@Nonnull String json, @Nonnull String dir, String photoName) {
        super(json, dir);
        this.photoName = photoName;
    }

    /**
     * photo absolution path
     */
    private String photoName;
    public String photoNmae() {
        return photoName;
    }
}
