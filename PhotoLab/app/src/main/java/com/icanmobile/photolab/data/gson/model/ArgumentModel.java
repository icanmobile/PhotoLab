package com.icanmobile.photolab.data.gson.model;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;

/**
 * Created by JONG HO BAEK on 25,February,2019
 * email: icanmobile@gmail.com
 *
 * ArgumentModel class
 * This class includes an json file and transition direction information which will be delivered to next activity.
 * Related methods are getArguments() and getNextArguments() of BaseActivity class.
 */
public class ArgumentModel extends BaseModel {

    private static final String FIELD_JSON = "JSON";
    private static final String FIELD_TRANSITION_DIR = "TRANSITION_DIR";

    // json file
    @SerializedName(FIELD_JSON)
    private String json;

    // transition direction for changing activity
    @SerializedName(FIELD_TRANSITION_DIR)
    private String transitionDir;


    public ArgumentModel(@Nonnull String json, @Nonnull String dir) {
        this.json = json;
        this.transitionDir = dir;
    }

    /**
     * getter for json file
     */
    public String json() {
        return this.json;
    }

    /**
     * getter for transition direction
     */
    public String transitionDir() {
        return this.transitionDir;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sbuilder = new StringBuilder();
        if (this.json != null)          appendString(sbuilder, FIELD_JSON + " = " + this.json);
        if (this.transitionDir != null) appendString(sbuilder, FIELD_TRANSITION_DIR + " = " + this.transitionDir);
        return sbuilder.toString();
    }
}
