package com.icanmobile.photolab.ui.gallery.activity;

import android.os.Bundle;

import com.icanmobile.photolab.R;
import com.icanmobile.photolab.ui.base.BaseActivity;
import com.icanmobile.photolab.ui.gallery.fragment.GridFragment;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import butterknife.ButterKnife;

import static com.icanmobile.photolab.util.AppConst.Json.GALLERY_ACTIVITY_JSON;


/**
 * Created by JONG HO BAEK on 22,February,2019
 * email: icanmobile@gmail.com
 *
 * GalleryActivity class
 * This class supports the grid and single style photo browsers.
 * The default fragment is GridFragment class.
 */
public class GalleryActivity extends BaseActivity {

    public static int currentPosition;
    private static final String KEY_CURRENT_POSITION = "com.google.samples.gridtopager.key.currentPosition";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set layout which is from "model/activity_gallery.json" file.
        setContentView(mActivityModel.layout(this));
        setUnBinder(ButterKnife.bind(this));

        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(KEY_CURRENT_POSITION, 0);
            // Return here to prevent adding additional GridFragments when changing orientation.
            return;
        }

        // create and register GridFragment as default
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, new GridFragment(), GridFragment.class.getSimpleName())
                .commit();
    }


    /**
     * get default json file.
     * When the input intent doesn't include ArgumentsModel object as extras, we need the default json file string because of the activity creation.
     * When other applications launch PhotoLab app, the intent will not include the ArgumentsModel as extras.
     */
    private static final String defaultJson = GALLERY_ACTIVITY_JSON.file();
    @Override
    protected String getDefaultJson() {
        return defaultJson;
    }

    /**
     * save the current image position of the image list.
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_POSITION, currentPosition);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
