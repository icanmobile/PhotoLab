package com.icanmobile.photolab.ui.base;

import com.icanmobile.photolab.di.ApplicationComponent;
import com.icanmobile.photolab.di.DaggerApplicationComponent;

import javax.annotation.Nonnull;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;

/**
 * Created by JONG HO BAEK on 21,February,2019
 * email: icanmobile@gmail.com
 *
 * BaseApplication class
 *
 * This class is extended from DaggerApplicaiton class and the application class are extended from this class.
 * This class implements applicationInjector() method for Dagger graph.
 */
public abstract class BaseApplication extends DaggerApplication {

    private static final String TAG = BaseApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        ApplicationComponent component = DaggerApplicationComponent.builder().application(this).build();
        inject(component);
        return component;
    }

    /**
     * the application class object injection
     * @param appComponent
     */
    protected abstract void inject(@Nonnull ApplicationComponent appComponent);
}
