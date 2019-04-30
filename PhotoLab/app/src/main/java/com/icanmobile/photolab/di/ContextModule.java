package com.icanmobile.photolab.di;

import android.app.Application;
import android.content.Context;

import dagger.Binds;
import dagger.Module;

/**
 * Created by JONG HO BAEK on 21,February,2019
 * email: icanmobile@gmail.com
 */
@Module
public abstract class ContextModule {

    @Binds
    abstract Context provideContext(Application application);
}
