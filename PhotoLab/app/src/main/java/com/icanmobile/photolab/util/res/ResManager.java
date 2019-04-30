package com.icanmobile.photolab.util.res;

import android.content.Context;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;

/**
 * Created by JONG HO BAEK on 25,February,2019
 * email: icanmobile@gmail.com
 *
 * ResManager Interface
 * This interface defines the methods of resources.
 */
public interface ResManager {

    /**
     * get resource id
     * @param context the context
     * @param data the resource string data such as "@drawable/image".
     * @return the resource id value.
     */
    int getResId(@NonNull Context context, @NonNull String data);

    /**
     * load json model using GSon library.
     * @param context the context
     * @param json the json file such as "model/activity_camera.json"
     * @param classOfT the model class
     * @param <T> the generic type of model class
     * @return the model class object
     */
    <T> T loadJsonModel(@Nonnull Context context, @Nonnull String json, Class<T> classOfT);
}
