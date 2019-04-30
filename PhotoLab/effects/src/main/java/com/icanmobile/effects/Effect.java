package com.icanmobile.effects;

import android.graphics.Bitmap;

import javax.annotation.Nonnull;

import androidx.annotation.NonNull;

/**
 * Created by JONG HO BAEK on 20,February,2019
 * email: icanmobile@gmail.com
 */
public interface Effect {

    /**
     * filter kind
     */
    enum filter {
        FILTER_LDR(1);

        int num;
        filter(int num) {
            this.num = num;
        }
        int num() {
            return this.num;
        }
    }

    /**
     * Callback interface for the responses of Native Effect Engine
     */
    interface Callback {
        void onError(final String error);
        void onResult(Bitmap effectedBmp);
    }

    /**
     * init native effect engine
     * @param filter the selected filter
     */
    void init(@NonNull Effect.filter filter);

    /**
     * deinit native effect engine
     */
    void deinit();

    /**
     * apply the effect to the bitmap
     * @param bitmap the source bitmap
     */
    void apply(@Nonnull Bitmap bitmap);

    /**
     * apply the effect to the bitmap and set the callback object for response from native effect engine.
     * @param bitmap the source bitmap
     * @param callback the callback method
     */
    void applyForResult(@Nonnull Bitmap bitmap, Effect.Callback callback);

    /**
     * report the progress status
     * @return the progress value (0 ~ 100)
     */
    int progress();
}
