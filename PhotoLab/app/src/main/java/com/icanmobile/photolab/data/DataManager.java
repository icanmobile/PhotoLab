package com.icanmobile.photolab.data;

import android.content.Context;

import com.icanmobile.photolab.data.gson.model.ActivityModel;
import com.icanmobile.photolab.data.gson.model.ButtonModel;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;

/**
 * Created by JONG HO BAEK on 24,February,2019
 * email: icanmobile@gmail.com
 *
 * DataManager Interface
 * This interface defines public methods of AppDataManaager class.
 */
public interface DataManager {

    /**
     * get activity model object
     * @param context the context
     * @param json the activity json file such as "model/activity_camera.json"
     * @return ActivityModel class object
     */
    ActivityModel getActivityModel(@NonNull Context context, @Nonnull String json);

    /**
     * get button model object
     * @param context the context
     * @param json the activity json file such as "model/activity_camera.json"
     * @return ButtonModel class object
     */
    ButtonModel getButtonModel(@Nonnull Context context, @Nonnull String json);
}
