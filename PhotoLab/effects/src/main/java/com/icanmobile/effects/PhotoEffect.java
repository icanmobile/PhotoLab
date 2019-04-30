package com.icanmobile.effects;

import android.graphics.Bitmap;
import android.util.Log;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

/**
 * Created by JONG HO BAEK on 20,February,2019
 * email: icanmobile@gmail.com
 */
@Singleton
public class PhotoEffect implements Effect {

    private static final String TAG = PhotoEffect.class.getSimpleName();

    @Inject
    public PhotoEffect(){}


    /**
     * init native effect engine
     * @param filter the selected filter
     */
    @Override
    public void init(@NonNull Effect.filter filter) {
        initEngine(filter.num());
    }

    /**
     * deinit native effect engine
     */
    @Override
    public void deinit() {
        deinitEngine();
    }

    /**
     * apply the effect to the bitmap
     * @param bitmap the source bitmap
     */
    @Override
    public void apply(@Nonnull Bitmap bitmap) {
        applyEffect(bitmap);
    }


    /**
     * apply the effect to the bitmap and set the callback object for response from native effect engine.
     * @param bitmap the source bitmap
     * @param callback the callback method
     */
    static Effect.Callback mCallback;
    @Override
    public void applyForResult(@Nonnull Bitmap bitmap, Effect.Callback callback) {
        mCallback = callback;
        applyEffect(bitmap);
    }

    /**
     * report the progress status
     * @return the progress value (0 ~ 100)
     */
    @Override
    public int progress() {
        return progressEffect();
    }



    //region NDK_Effects Library and Native Interface
    static {
        System.loadLibrary("ndk_effects");
    }

    /**
     * init command with filter
     * @param filter the filter number
     */
    @Keep
    private static native void initEngine(int filter);

    /**
     * deinit command
     */
    @Keep
    private static native void deinitEngine();

    /**
     * apply effect command
     * @param bitmap
     */
    @Keep
    private static native void applyEffect(Bitmap bitmap);

    /**
     * get progress status command
     * @return
     */
    @Keep
    private static native int progressEffect();

    /**
     * effect result callback from native effect engine after processing.
     * @param effectedBmp the processed bitmap
     */
    @Keep
    private static void onEffectResult(@Nonnull Bitmap effectedBmp) {
        Log.d(TAG, "##### PhotoEffect : onEffectResult : effectedBmp = " + effectedBmp);
        Log.d(TAG, "##### PhotoEffect : onEffectResult : mCallback = " + mCallback);
        if (mCallback != null) {
            mCallback.onResult(effectedBmp);
            mCallback = null;
        }
    }
    //endregion
}
