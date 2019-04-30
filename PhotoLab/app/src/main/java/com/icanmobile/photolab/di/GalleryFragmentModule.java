package com.icanmobile.photolab.di;

import com.icanmobile.photolab.ui.gallery.fragment.GridFragment;
import com.icanmobile.photolab.ui.gallery.fragment.ImageFragment;
import com.icanmobile.photolab.ui.gallery.fragment.ImagePagerFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by JONG HO BAEK on 21,February,2019
 * email: icanmobile@gmail.com
 *
 * GalleryFragmentModule for Dagger2
 * This class supports all fragments of Gallery.
 */
@Module
public abstract class GalleryFragmentModule {

    @ContributesAndroidInjector
    abstract GridFragment bindGridFragment();

    @ContributesAndroidInjector
    abstract ImagePagerFragment bindImagePagerFragment();

    @ContributesAndroidInjector
    abstract ImageFragment bindImageFragment();
}
