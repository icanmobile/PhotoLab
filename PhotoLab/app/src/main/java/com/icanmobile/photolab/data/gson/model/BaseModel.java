package com.icanmobile.photolab.data.gson.model;

import android.content.Context;

import com.icanmobile.photolab.util.res.AppResManager;

import java.io.Serializable;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;

/**
 * Created by JONG HO BAEK on 25,February,2019
 * email: icanmobile@gmail.com
 *
 * BaseModel class
 * This is base class for Json model files.
 * This class supports the functionality which converts the string to resource id using AppResourceManager class.
 */
public abstract class BaseModel implements Serializable {

    /**
     * getter to convert string to resource id
     * @param context the caller context
     * @param data the string type resource data such as @drawable/image, @color/red, or @string/title
     * @return resource id value
     */
    public int getResId(@NonNull Context context, String data) {
        return AppResManager.getInstance().getResId(context, data);
    }

    /**
     * line separator method for toString method of sub classes.
     * @param sbuilder the StringBuilder class object.
     * @param value the string value
     */
    protected void appendString(@Nonnull StringBuilder sbuilder, String value) {
        sbuilder.append(value + System.getProperty("line.separator"));
    }
}
