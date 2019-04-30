package com.icanmobile.photolab.di;

import android.app.Application;

import com.icanmobile.photolab.PhotoLabApplication;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import dagger.android.support.DaggerApplication;

/**
 * Created by JONG HO BAEK on 21,February,2019
 * email: icanmobile@gmail.com
 *
 * ApplicationComponent class for Dagger2
 */
@Singleton
@Component(
        modules = {
                ContextModule.class,
                ApplicationModule.class,
                AndroidSupportInjectionModule.class,
                ActivityModule.class
        })
public interface ApplicationComponent extends AndroidInjector<DaggerApplication> {

    void inject(PhotoLabApplication application);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);
        ApplicationComponent build();
    }
}
