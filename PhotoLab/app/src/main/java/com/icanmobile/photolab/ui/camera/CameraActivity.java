package com.icanmobile.photolab.ui.camera;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.icanmobile.photolab.R;
import com.icanmobile.photolab.data.gson.model.ArgumentModel;
import com.icanmobile.photolab.data.gson.model.CameraArgumentModel;
import com.icanmobile.photolab.ui.base.BaseActivity;
import com.icanmobile.photolab.ui.view.camera.CameraView;
import com.icanmobile.photolab.util.AppConst;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.icanmobile.photolab.util.AppConst.Json.CAMERA_ACTIVITY_JSON;
import static com.icanmobile.photolab.util.AppConst.Json.GALLERY_ACTIVITY_JSON;
import static com.icanmobile.photolab.util.AppConst.Json.PHOTO_ACTIVITY_JSON;

/**
 * Created by JONG HO BAEK on 20,February,2019
 * email: icanmobile@gmail.com
 *
 * CameraActivity class
 * This class supports the custom camera functionality using CameraView control.
 */
public class CameraActivity extends BaseActivity {

    private static final String TAG = CameraActivity.class.getSimpleName();

    @BindView(R.id.camera)
    CameraView mCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set layout which is from "model/activity_camera.json" file.
        setContentView(mActivityModel.layout(this));

        setUnBinder(ButterKnife.bind(this));

        init();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        init();
    }

    /**
     * init process of activity
     */
    private void init() {
        // set camera callback
        mCameraView.setCallback(mCameraCallback);

        // request permission
        requestCameraPermission();
    }

    /**
     * get default json file.
     * When the input intent doesn't include ArgumentsModel object as extras, we need the default json file string because of the activity creation.
     * When other applications launch PhotoLab app, the intent will not include the ArgumentsModel as extras.
     */
    private static final String defaultJson = CAMERA_ACTIVITY_JSON.file();
    @Override
    protected String getDefaultJson() {
        return defaultJson;
    }

    /**
     * override getNextArguments() method of BaseActivity class.
     * When PhotoActivity class will be launched, we will use CameraArugmentModel class object in order to pass the captured phot file.
     * @param json the json file path
     * @param dir the transition direction
     * @return the ArgumentModel or it's extended model object
     */
    @Override
    protected ArgumentModel getNextArguments(@Nonnull String json, @Nonnull AppConst.TransitionDir dir){
        if (json.equals(AppConst.Json.PHOTO_ACTIVITY_JSON.file())) {
            return new CameraArgumentModel(json, dir.name(), mCapturedPhoto);
        }
        return new ArgumentModel(json, dir.name());
    }

    /**
     * when actionBackKey value is defined in "model/activity_camera.json" file,
     * call changeActivity() method with the value.
     */
    @Override
    public void onBackPressed() {
        if (mActivityModel.actionBackKey() != null)
            changeActivity(mActivityModel.actionBackKey(), AppConst.TransitionDir.TRANSACTION_DIR_BACKWARD);
        else
            super.onBackPressed();
    }

    //region CameraView.Callback interface methods
    private String mCapturedPhoto;
    CameraView.Callback mCameraCallback = new CameraView.Callback() {
        /**
         * Camera device error
         * @param error the error
         */
        @Override
        public void onError(@Nonnull final String error) {
            Log.d(TAG, "camera device error = " + error);
        }

        /**
         * completed the still shot
         * @param file the captured file
         */
        @Override
        public void onCaptureCompleted(@Nonnull final String file) {
            mCapturedPhoto = file;
            changeActivity(PHOTO_ACTIVITY_JSON.file(), AppConst.TransitionDir.TRANSACTION_DIR_FORWARD);
        }

        /**
         * clicked the "go to gallery" button
         */
        @Override
        public void goToGallery() {
            changeActivity(GALLERY_ACTIVITY_JSON.file(), AppConst.TransitionDir.TRANSACTION_DIR_FORWARD);
        }

        /**
         * show toast message
         * @param message the message
         */
        @Override
        public void showToast(@Nonnull final String message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(CameraActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });

        }
    };
    //endregion


    //region request camera permission
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    @TargetApi(Build.VERSION_CODES.M)
    private void requestCameraPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle(getString(R.string.request_permission_title));
                alertBuilder.setMessage(getString(R.string.request_permission_desc));
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @TargetApi(Build.VERSION_CODES.M)
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);}});
                AlertDialog alert = alertBuilder.create();
                alert.show();
            }
            else {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mCameraView != null) mCameraView.openCamera();

            }
        }
    }
    //endregion
}
