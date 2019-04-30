package com.icanmobile.photolab.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.Unbinder;
import dagger.android.support.DaggerFragment;

/**
 * Created by JONG HO BAEK on 21,February,2019
 * email: icanmobile@gmail.com
 *
 * BaseFragment class
 * This class is extended from DaggerFragment class and all fragment classes are extended from this class.
 * We can add the scalability of fragment class using this class.
 *
 */
public abstract class BaseFragment extends DaggerFragment {

    private static final String TAG = BaseFragment.class.getSimpleName();

    private BaseActivity mActivity;

    //region Butter Knife binder
    private Unbinder mUnBinder;
    public void setUnBinder(Unbinder unBinder) {
        mUnBinder = unBinder;
    }
    //endregion

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            mActivity = (BaseActivity) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        // unbind the butter knife
        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }
}
