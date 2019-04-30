package com.icanmobile.photolab.data.gson.model;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;

/**
 * Created by JONG HO BAEK on 25,February,2019
 * email: icanmobile@gmail.com
 *
 * ButtonModel class
 * This class includes the button information such as image resource and action.
 * This class object will be created by GSon library with below Json format:
 *
 * "BUTTONS": [
 *     {
 *       "IMAGE": "@drawable/res_effect"
 *     },
 *     {
 *       "IMAGE": "@drawable/res_save",
 *       "ACTION": "model/activity_camera.json"
 *     },
 *     {
 *       "IMAGE": "@drawable/res_gallery",
 *       "ACTION": "model/activity_gallery.json"
 *     }
 *   ]
 */
public class ButtonModel implements Serializable, Cloneable {

    private static final String FIELD_BUTTONS = "BUTTONS";

    // button array
    @SerializedName(FIELD_BUTTONS)
    private Buttons buttons;

    // getter for button array
    public Buttons buttons() {
        return this.buttons;
    }

    @NonNull
    @Override
    public String toString() {
        return new StringBuilder().append(buttons.toString()).toString();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return (ButtonModel) super.clone();
    }


    /**
     * Buttons class
     * This class includes button item classes.
     */
    public static class Buttons extends ArrayList<Buttons.Item> implements Cloneable {

        @Override
        public String toString() {
            StringBuilder sbuilder = new StringBuilder();
            for (Item item : this)
                appendString(sbuilder, item.toString());
            return sbuilder.toString();
        }
        private void appendString(StringBuilder builder, String value) {
            builder.append(value + System.getProperty("line.separator"));
        }

        @NonNull
        @Override
        public Buttons clone() {
            return (Buttons) super.clone();
        }


        /**
         * Item class
         *
         * This class includes the button resource and action information.
         */
        private static final String FIELD_IMAGE = "IMAGE";
        private static final String FIELD_ACTION = "ACTION";
        public static class Item extends BaseModel implements Cloneable {

            // image resource
            @SerializedName(FIELD_IMAGE)
            private String image;

            // action
            @SerializedName(FIELD_ACTION)
            private String action;


            /**
             * getter for image resource
             */
            public String image() {
                return this.image;
            }
            public int image(@Nonnull Context context) {
                return this.image != null ? getResId(context, this.image) : null;
            }

            /**
             * getter for action
             */
            public String action() {
                return this.action;
            }


            @NonNull
            @Override
            public String toString() {
                StringBuilder sbuilder = new StringBuilder();
                appendString(sbuilder, FIELD_IMAGE + " : " + this.image);
                appendString(sbuilder, FIELD_ACTION + " : " + this.action);
                return sbuilder.toString();
            }

            @Override
            protected Item clone() throws CloneNotSupportedException {
                return (Item) super.clone();
            }
        }
    }
}
