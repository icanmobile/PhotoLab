package com.icanmobile.photolab;

import com.icanmobile.photolab.di.ApplicationComponent;
import com.icanmobile.photolab.ui.base.BaseApplication;

/**
 * Created by JONG HO BAEK on 21,February,2019
 * email: icanmobile@gmail.com
 *
 * PhotoLabApplication class
 * This class implements inject() method which is defined by BaseApplication class.
 */
public class PhotoLabApplication extends BaseApplication {

    @Override
    protected void inject(ApplicationComponent component) {
        component.inject(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
