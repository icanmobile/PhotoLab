package com.icanmobile.photolab.data.gson.model;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;

/**
 * Created by JONG HO BAEK on 25,February,2019
 * email: icanmobile@gmail.com
 *
 * ActivityModel class
 * This class includes the activity class and resource information.
 * Related methods are changeActivity() of BaseActivity class.
 * This class object will be created by GSon library with below Json format:
 * {
 *   "ME": "model/activity_camera.json",
 *   "NAME": "@string/activity_camera",
 *   "CLASS_NAME": "com.icanmobile.photolab.ui.camera.CameraActivity",
 *   "LAYOUT": "@layout/activity_camera",
 *
 *   "BACKGROUND": "@drawable/wall_background",
 *   "ACTION_BACK_KEY": "EXIT",
 *   "FORWARD_EXIT_ANIM": "@anim/left_out",
 *   "BACKWARD_ENTER_ANIM": "@anim/left_in"
 * }
 */
public class ActivityModel extends BaseModel {

    private static final String FIELD_ME = "ME";
    private static final String FIELD_NAME = "NAME";
    private static final String FIELD_CLASS_NAME = "CLASS_NAME";
    private static final String FIELD_LAYOUT = "LAYOUT";
    private static final String FIELD_BACKGROUND = "BACKGROUND";
    private static final String FIELD_ACTION_BACK_KEY = "ACTION_BACK_KEY";
    private static final String FIELD_FORWARD_ENTER_ANIM = "FORWARD_ENTER_ANIM";
    private static final String FIELD_FORWARD_EXIT_ANIM = "FORWARD_EXIT_ANIM";
    private static final String FIELD_BACKWARD_ENTER_ANIM = "BACKWARD_ENTER_ANIM";
    private static final String FIELD_BACKWARD_EXIT_ANIM = "BACKWARD_EXIT_ANIM";

    // current Json file
    @SerializedName(FIELD_ME)
    private String me;

    // activity name for analytics data
    @SerializedName(FIELD_NAME)
    private String name;

    // activity class for Java reflection
    @SerializedName(FIELD_CLASS_NAME)
    private String className;

    // activity layout
    @SerializedName(FIELD_LAYOUT)
    private String layout;

    // background resource
    @SerializedName(FIELD_BACKGROUND)
    private String background;

    // action for back key
    @SerializedName(FIELD_ACTION_BACK_KEY)
    private String actionBackKey;

    // custom forward enter animation resource
    @SerializedName(FIELD_FORWARD_ENTER_ANIM)
    private String forwardEnterAnim;

    // custom forward exit animation resource
    @SerializedName(FIELD_FORWARD_EXIT_ANIM)
    private String forwardExitAnim;

    // custom backward enter animation resource
    @SerializedName(FIELD_BACKWARD_ENTER_ANIM)
    private String backwardEnterAnim;

    // custom backward exit animation resource
    @SerializedName(FIELD_BACKWARD_EXIT_ANIM)
    private String backwardExitAnim;

    /**
     * getter for current Json file
     */
    public String me() {
        return this.me;
    }

    /**
     * getter for activity name for analytics data
     */
    public String name() {
        return this.name;
    }
    public Integer name(@NonNull Context context) {
        return this.name != null ? getResId(context, this.name) : null;
    }

    /**
     * getter for activity class for Java reflection
     */
    public String className() {
        return this.className;
    }

    /**
     * getter activity layout
     */
    public String layout() {
        return this.layout;
    }
    public Integer layout(@NonNull Context context) {
        return this.layout != null ? getResId(context, this.layout) : null;
    }

    /**
     * getter for background resource
     */
    public String background() {
        return this.background;
    }
    public Integer background(@Nonnull Context context) {
        return this.background != null ? getResId(context, this.background) : null;
    }

    /**
     * getter for action for back key
     */
    public String actionBackKey() {
        return this.actionBackKey;
    }

    /**
     * getter for custom forward enter animation resource
     */
    public String forwardEnterAnim() {
        return this.forwardEnterAnim;
    }
    public Integer forwardEnterAnim(@Nonnull Context context) {
        return this.forwardEnterAnim != null ? getResId(context, this.forwardEnterAnim) : null;
    }

    /**
     * getter for custom forward exit animation resource
     */
    public String forwardExitAnim() {
        return this.forwardExitAnim;
    }
    public Integer forwardExitAnim(@Nonnull Context context) {
        return this.forwardExitAnim != null ? getResId(context, this.forwardExitAnim) : null;
    }

    /**
     * getter for custom backward enter animation resource
     */
    public String backwardEnterAnim() {
        return this.backwardEnterAnim;
    }
    public Integer backwardEnterAnim(@Nonnull Context context) {
        return this.backwardEnterAnim != null ? getResId(context, this.backwardEnterAnim) : null;
    }

    /**
     * getter for custom backward exit animation resource
     */
    public String backwardExitAnim() {
        return this.backwardExitAnim;
    }
    public Integer backwardExitAnim(@Nonnull Context context) {
        return this.backwardExitAnim != null ? getResId(context, this.backwardExitAnim) : null;
    }


    @NonNull
    @Override
    public String toString() {
        StringBuilder sbuilder = new StringBuilder();
        if (this.me != null)                appendString(sbuilder, FIELD_ME + " = " + this.me);
        if (this.name != null)              appendString(sbuilder, FIELD_NAME + " = " + this.name);
        if (this.className != null)         appendString(sbuilder, FIELD_CLASS_NAME + " = " + this.className);
        if (this.layout != null)            appendString(sbuilder, FIELD_LAYOUT + " = " + this.layout);
        if (this.background != null)        appendString(sbuilder, FIELD_BACKGROUND + " = " + this.background);
        if (this.actionBackKey != null)     appendString(sbuilder, FIELD_ACTION_BACK_KEY + " = " + this.actionBackKey);
        if (this.forwardEnterAnim != null)  appendString(sbuilder, FIELD_FORWARD_ENTER_ANIM + " = " + this.forwardEnterAnim);
        if (this.forwardExitAnim != null)   appendString(sbuilder, FIELD_FORWARD_EXIT_ANIM + " = " + this.forwardExitAnim);
        if (this.backwardEnterAnim != null) appendString(sbuilder, FIELD_BACKWARD_ENTER_ANIM + " = " + this.backwardEnterAnim);
        if (this.backwardExitAnim != null)  appendString(sbuilder, FIELD_BACKWARD_EXIT_ANIM + " = " + this.backwardExitAnim);
        return sbuilder.toString();
    }
}
