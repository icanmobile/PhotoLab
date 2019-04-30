package com.icanmobile.photolab.util;

/**
 * Created by JONG HO BAEK on 23,February,2019
 * email: icanmobile@gmail.com
 */
public class AppConst {

    //region Json file information
    public enum Json {
        EXIT("EXIT"),
        CAMERA_ACTIVITY_JSON("model/activity_camera.json"),
        PHOTO_ACTIVITY_JSON("model/activity_photo.json"),
        GALLERY_ACTIVITY_JSON("model/activity_gallery.json");

        String jsonFile;
        Json(String json) {
            jsonFile = json;
        }
        public String file() {
            return jsonFile;
        }
    }
    //endregion


    //region ArgumentModel information
    public static final String ARGUMENT_MODEL = "ARGUMENT_MODEL";
    public enum TransitionDir {
        TRANSACTION_DIR_NONE,
        TRANSACTION_DIR_FORWARD,
        TRANSACTION_DIR_BACKWARD,
        TRANSACTION_DIR_FORWARD_ADD,
        TRANSACTION_DIR_BACKWARD_ADD
    }
    //endregion
}
