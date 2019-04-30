package com.icanmobile.photolab.di;

import com.icanmobile.photolab.ui.camera.CameraActivity;
import com.icanmobile.photolab.ui.gallery.activity.GalleryActivity;
import com.icanmobile.photolab.ui.photo.PhotoActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by JONG HO BAEK on 21,February,2019
 * email: icanmobile@gmail.com
 *
 * ActivityModule class for Dagger2
 * This class supports CameraActivity, PhotoActivity, and GalleryActivity classes.
 */
@Module
public abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract CameraActivity bindCameraActivity();

    @ContributesAndroidInjector
    abstract PhotoActivity bindPhotoActivity();

    @ContributesAndroidInjector(modules = {GalleryFragmentModule.class})
    abstract GalleryActivity bindGalleryActivity();
}
