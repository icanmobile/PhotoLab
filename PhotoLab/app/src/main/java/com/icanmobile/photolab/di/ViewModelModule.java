package com.icanmobile.photolab.di;

import com.icanmobile.photolab.ui.photo.PhotoViewModel;
import com.icanmobile.photolab.viewmodel.ViewModelFactory;

import javax.inject.Singleton;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * Created by JONG HO BAEK on 24,February,2019
 * email: icanmobile@gmail.com
 *
 * ViewModelModule for Dagger2
 * This class supports PhotoViewModel and ViewModelFactory classes.
 */
@Singleton
@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(PhotoViewModel.class)
    abstract ViewModel bindPhotoViewModel(PhotoViewModel photoViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}
