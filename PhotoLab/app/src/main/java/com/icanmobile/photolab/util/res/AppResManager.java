package com.icanmobile.photolab.util.res;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

import androidx.annotation.NonNull;

/**
 * Created by JONG HO BAEK on 25,February,2019
 * email: icanmobile@gmail.com
 *
 * AppResManager class
 * This class implements the methods to control the resources.
 */

@Singleton
public class AppResManager implements ResManager {


    //region singleton pattern
    public static volatile AppResManager sInstance;
    public static AppResManager getInstance() {
        if (sInstance == null) {
            synchronized (AppResManager.class) {
                if (sInstance == null) sInstance = new AppResManager();
            }
        }
        return sInstance;
    }
    private AppResManager(){}
    //endregion


    /**
     * get resource id
     * @param context the context
     * @param data the resource string data such as "@drawable/image".
     * @return the resource id value.
     */
    public int getResId(@NonNull Context context, @NonNull String data) {
        int delimiter = data.indexOf('/');
        if (delimiter == -1 ) {
            return 0;
        }
        String type = data.substring(1, delimiter);
        String name = data.substring(delimiter + 1);
        return getResId(context, name, type);
    }
    private int getResId(@NonNull Context context, @NonNull String name, @NonNull String type) {
        return context.getResources().getIdentifier(name, type, context.getPackageName());
    }


    /**
     * load json model using GSon library.
     * @param context the context
     * @param json the json file such as "model/activity_camera.json"
     * @param classOfT the model class
     * @param <T> the generic type of model class
     * @return the model class object
     */
    public <T> T loadJsonModel(@Nonnull Context context, @Nonnull String json, Class<T> classOfT) {
        String data = getTextFromAssets(context, json);
        if (data == null || data.length() == 0) return null;

        Gson gson = new Gson();
        try {
            return gson.fromJson(data, classOfT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get strings of the json file in assets folder.
     * @param context the context
     * @param fileName the json file name as "model/activity_camera.json"
     * @return the strings in the json file
     */
    private String getTextFromAssets(@Nonnull Context context, @Nonnull String fileName) {
        StringBuilder ret = new StringBuilder();
        if (hasAssetFile(context, fileName)) {
            try {
                InputStream is = context.getAssets().open(fileName);
                InputStreamReader inputStreamReader = new InputStreamReader(is);
                BufferedReader f = new BufferedReader(inputStreamReader);
                String line = f.readLine();
                while (line != null) {
                    ret.append(line);
                    line = f.readLine();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ret.toString();
        }
        return ret.toString();
    }

    /**
     * check file exit in assets folder
     * @param context the context
     * @param fileName the file name
     * @return the file exit or not
     */
    private boolean hasAssetFile(@NonNull Context context, @NonNull String fileName){
        if (context == null || fileName == null || fileName.length() == 0) return false;
        InputStream is = null;
        try {
            is = context.getResources().getAssets().open(fileName);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
