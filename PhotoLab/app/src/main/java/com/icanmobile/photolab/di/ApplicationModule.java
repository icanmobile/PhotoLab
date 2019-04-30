package com.icanmobile.photolab.di;


import com.icanmobile.effects.Effect;
import com.icanmobile.effects.PhotoEffect;
import com.icanmobile.photolab.data.AppDataManager;
import com.icanmobile.photolab.data.DataManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by JONG HO BAEK on 21,February,2019
 * email: icanmobile@gmail.com
 *
 * ApplicationModule class for Dagger2
 * This class supports DataManager and Effect classes.
 */
@Singleton
@Module(includes = ViewModelModule.class)
public class ApplicationModule {

    @Singleton
    @Provides
    DataManager provideDataManager(AppDataManager appDataManager) {
        return appDataManager;
    }

    @Singleton
    @Provides
    Effect provideEffect(PhotoEffect photoEffect) {
        return photoEffect;
    }
}
