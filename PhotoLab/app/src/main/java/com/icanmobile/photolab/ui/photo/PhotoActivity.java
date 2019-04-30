package com.icanmobile.photolab.ui.photo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.icanmobile.photolab.R;
import com.icanmobile.photolab.data.gson.model.ButtonModel;
import com.icanmobile.photolab.data.gson.model.CameraArgumentModel;
import com.icanmobile.photolab.ui.base.BaseActivity;
import com.icanmobile.photolab.util.AppConst;
import com.icanmobile.photolab.viewmodel.ViewModelFactory;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.icanmobile.photolab.util.AppConst.ARGUMENT_MODEL;
import static com.icanmobile.photolab.util.AppConst.Json.CAMERA_ACTIVITY_JSON;
import static com.icanmobile.photolab.util.AppConst.Json.GALLERY_ACTIVITY_JSON;
import static com.icanmobile.photolab.util.AppConst.Json.PHOTO_ACTIVITY_JSON;


/**
 * Created by JONG HO BAEK on 21,February,2019
 * email: icanmobile@gmail.com
 *
 * PhotoActivity class
 * This activity supports to display the original or effect processed bitmap.
 * This activity is working with PhotoViewModel class and it updates the UI based on the LiveData of PhotoViewModel class.
 */
public class PhotoActivity extends BaseActivity
    implements View.OnClickListener {

    private static final String TAG = PhotoActivity.class.getSimpleName();

    //region ViewModelFactory and PhotoViewModel objects.
    @Inject
    ViewModelFactory mViewModelFactory;
    PhotoViewModel mViewModel;
    //endregion

    @BindView(R.id.photo) ImageView mPhoto;
    @BindView(R.id.toggle) ImageView mToggle;
    @BindView(R.id.save) ImageView mSave;
    @BindView(R.id.gallery) ImageView mGallery;
    @BindView(R.id.progressBar) ProgressBar mProgressBar;

    /**
     * create method
     * prepare buttons and observers of ViewModel.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set layout which is from "model/activity_photo.json" file.
        setContentView(mActivityModel.layout(this));

        setUnBinder(ButterKnife.bind(this));

        // set the resources and actions of buttons
        setButtons(mDataManager.getButtonModel(this, mActivityModel.me()));

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(PhotoViewModel.class);

        // photo bitmap observer to update photo view with original and effect processed bitmap.
        final Observer<Bitmap> photoBitmapObserver = new Observer<Bitmap>() {
            @Override
            public void onChanged(@Nullable final Bitmap bmp) {
                // Update the UI, in this case, a TextView.
                mPhoto.setImageBitmap(bmp);
            }
        };
        mViewModel.getPhotoBitmap().observe(this, photoBitmapObserver);

        // progress status observer to update progress bar.
        final Observer<Integer> photoEffectProgress = new Observer<Integer>() {
            @Override
            public void onChanged(Integer progress) {
                if (progress > 0 && mProgressBar.getVisibility() == View.INVISIBLE)
                    mProgressBar.setVisibility(View.VISIBLE);

                mProgressBar.setProgress(progress);

                if (progress >= 100) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        };
        mViewModel.getEffectProgress().observe(this, photoEffectProgress);

        // set photo file path
        mViewModel.setPhotoFile(this, capturedPhoto);
    }




    /**
     * get default json file.
     * When the input intent doesn't include ArgumentsModel object as extras, we need the default json file string because of the activity creation.
     * When other applications launch PhotoLab app, the intent will not include the ArgumentsModel as extras.
     */
    private static final String defaultJson = PHOTO_ACTIVITY_JSON.file();
    @Override
    protected String getDefaultJson() {
        return defaultJson;
    }


    /**
     * override getArguments() method in order to use CameraArgumentModel class.
     * get captured photo path from CameraActivity using CameraArgumentModel class.
     */
    private String capturedPhoto;
    @Override
    protected void getArguments() {
        Intent intent = getIntent();
        boolean bSuccess = false;
        if (intent != null) {
            Bundle args = intent.getExtras();
            if (args != null) {
                CameraArgumentModel argumentModel = (CameraArgumentModel) args.getSerializable(ARGUMENT_MODEL);
                if (argumentModel != null) {
                    mActivityModel = mDataManager.getActivityModel(this, argumentModel.json());
                    capturedPhoto = argumentModel.photoNmae();
                    bSuccess = true;
                }
            }
        }

        if (!bSuccess)
            mActivityModel = mDataManager.getActivityModel(this, getDefaultJson());
    }

    /**
     * when back key is clicked, call changeActivity method with actionBackKey string which includes Json file.
     */
    @Override
    public void onBackPressed() {
        if (mActivityModel.actionBackKey() != null)
            changeActivity(mActivityModel.actionBackKey(), AppConst.TransitionDir.TRANSACTION_DIR_BACKWARD);
        else
            super.onBackPressed();
    }


    /**
     * set the information of buttons such as resources and actions which are defined by Json file.
     * @param buttonModel the ButtonModel class object
     */
    private void setButtons(ButtonModel buttonModel) {
        if (buttonModel != null) {
            ButtonModel.Buttons buttons = buttonModel.buttons();
            if (buttons != null) {
                if (buttons.size() > 0) {
                    // toggle button
                    Glide.with(this)
                            .load(buttons.get(0).image(this))
                            .into(mToggle);
                    mToggle.setTag(buttons.get(0).action());
                } else if (buttons.size() > 1) {
                    // save button
                    Glide.with(this)
                            .load(buttons.get(1).image(this))
                            .into(mGallery);
                    mGallery.setTag(buttons.get(1).action());
                } else if (buttons.size() > 2) {
                    // gallery button
                    Glide.with(this)
                            .load(buttons.get(2).image(this))
                            .into(mSave);
                    mSave.setTag(buttons.get(2).action());
                }
            }
        }

        mToggle.setOnClickListener(this);
        mSave.setOnClickListener(this);
        mGallery.setOnClickListener(this);
    }

    /**
     * buttons click method
     * @param view the button view
     */
    @Override
    public void onClick(View view) {
        if (mProgressBar != null && mProgressBar.getVisibility() == View.VISIBLE)
            return;

        switch (view.getId()) {
            case R.id.toggle:
                mViewModel.toggle();
                break;

            case R.id.save:
                {
                    mViewModel.saveEffectBmp(this);
                    String action = view.getTag() != null ? (String)view.getTag() : CAMERA_ACTIVITY_JSON.file();
                    changeActivity(action, AppConst.TransitionDir.TRANSACTION_DIR_BACKWARD);
                }
                break;

            case R.id.gallery:
                {
                    String action = view.getTag() != null ? (String)view.getTag() : GALLERY_ACTIVITY_JSON.file();
                    changeActivity(action, AppConst.TransitionDir.TRANSACTION_DIR_FORWARD);
                }
                break;
        }
    }
}
