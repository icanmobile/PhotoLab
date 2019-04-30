package com.icanmobile.photolab.data;

import android.content.Context;

import com.icanmobile.photolab.data.gson.model.ActivityModel;
import com.icanmobile.photolab.data.gson.model.ButtonModel;
import com.icanmobile.photolab.util.res.AppResManager;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.NonNull;

/**
 * Created by JONG HO BAEK on 24,February,2019
 * email: icanmobile@gmail.com
 *
 * AppDataManager class
 * This class includes getter methods for Json model classes.
 */
@Singleton
public class AppDataManager implements DataManager {

    @Inject
    public AppDataManager() {
    }

    /**
     * get activity model object
     * @param context the context
     * @param json the activity json file such as "model/activity_camera.json"
     * @return ActivityModel class object
     */
    public ActivityModel getActivityModel(@NonNull Context context, @Nonnull String json) {
        return AppResManager.getInstance().loadJsonModel(context, json, ActivityModel.class);
    }

    /**
     * get button model object
     * @param context the context
     * @param json the activity json file such as "model/activity_camera.json"
     * @return ButtonModel class object
     */
    @Override
    public ButtonModel getButtonModel(@Nonnull Context context, @Nonnull String json) {
        return AppResManager.getInstance().loadJsonModel(context, json, ButtonModel.class);
    }
}
