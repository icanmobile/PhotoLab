package com.icanmobile.photolab.ui.photo;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.icanmobile.effects.Effect;
import com.icanmobile.effects.PhotoEffect;
import com.icanmobile.photolab.util.file.JPEGFile;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by JONG HO BAEK on 24,February,2019
 * email: icanmobile@gmail.com
 *
 * PhotoViewModel class
 * This class saves the original and effect processed bitmaps.
 * Although device orientation is changed, the bitmaps are just saved in this class because this class is extended by ViewModel class.
 */
public class PhotoViewModel extends ViewModel
    implements Effect.Callback {

    private static final String TAG = PhotoViewModel.class.getSimpleName();

    private final Effect mEffect;

    @Inject
    public PhotoViewModel(PhotoEffect effect){
        mEffect = effect;
    }

    /**
     * save photo file abstract path
     * when the photo file is changed, call prepareBitmap() method in order to prepare new original and processed bitmaps.
     */
    private String mFile;
    public String getPhotoFile() {
        return mFile;
    }
    public void setPhotoFile(@NonNull Context context, String photoFile) {
        boolean isChanged = (mFile != null && mFile.equals(photoFile)) ? false : true;
        mFile = photoFile;
        prepareBitmaps(context, photoFile, isChanged);
    }

    /**
     * load the original bitmap and start the photo effect with progress status.
     * @param context the context
     * @param photoFile the photo file absolute path
     * @param isChanged the status as photo file is changed
     */
    private void prepareBitmaps(@NonNull Context context, String photoFile, boolean isChanged) {
        // photo file is changed
        if (isChanged) {
            // load new bitmap
            Glide.with(context)
                    .asBitmap()
                    .load(mFile)
                    .listener(new RequestListener<Bitmap>() {
                                  @Override
                                  public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Bitmap> target, boolean b) {
                                      return false;
                                  }

                                  @Override
                                  public boolean onResourceReady(Bitmap bitmap, Object o, Target<Bitmap> target, DataSource dataSource, boolean b) {
                                      // save the original bitmap
                                      setOriginalBmp(bitmap.copy(bitmap.getConfig(), true));

                                      // update photo view of Activity
                                      getPhotoBitmap().setValue(getOriginalBmp());

                                      // init and apply photo effect
                                      mEffect.init(Effect.filter.FILTER_LDR);
                                      mEffect.applyForResult(bitmap, PhotoViewModel.this);

                                      // update the progress status
                                      updateProgress();
                                      return false;
                                  }
                              }
                    ).submit();
        }
        // photo file is not changed, update photo view of Activity with current bitmaps.
        else {
            if (mIsEffected)
                getPhotoBitmap().setValue(getEffectedBmp());
            else
                getPhotoBitmap().setValue(getOriginalBmp());
        }
    }


    /**
     * This method will set the bitmap value to photo view of Activity
     */
    private boolean mIsEffected;
    public void toggle() {
        if (mIsEffected)
            getPhotoBitmap().setValue(getOriginalBmp());
        else
            getPhotoBitmap().setValue(getEffectedBmp());
        mIsEffected ^= true;
    }

    /**
     * Bitmap Live Data for photo view of Activity
     */
    private MutableLiveData<Bitmap> mPhotoBitmap;
    public MutableLiveData<Bitmap> getPhotoBitmap() {
        if (mPhotoBitmap == null)
            mPhotoBitmap = new MutableLiveData<>();
        return mPhotoBitmap;
    }

    /**
     * the original bitmap
     */
    private Bitmap mOriginalBmp;
    public Bitmap getOriginalBmp() {
        return mOriginalBmp;
    }
    public void setOriginalBmp(Bitmap originalBmp) {
        mOriginalBmp = originalBmp;
    }

    /**
     * the effect processed bitmap
     */
    private Bitmap mEffectedBmp;
    public Bitmap getEffectedBmp() {
        return mEffectedBmp;
    }
    public void setEffectedBmp(Bitmap effectedBmp) {
        mEffectedBmp = effectedBmp;
    }

    /**
     * save effect processed bitmap to JPEG file
     * @param context the context
     */
    public void saveEffectBmp(@Nonnull Context context) {
        JPEGFile.getInstance().save(context, mEffectedBmp);
    }

    /**
     * Progress Status Live Data for progressBar of Activity
     */
    private MutableLiveData<Integer> mEffectProgress;
    public MutableLiveData<Integer> getEffectProgress() {
        if (mEffectProgress == null)
            mEffectProgress = new MutableLiveData<>();
        return mEffectProgress;
    }

    /**
     * start to update the progress status
     */
    private void updateProgress() {
        Observable.fromCallable(() -> {
            int progress;
            do {
                progress = mEffect.progress();
                getEffectProgress().postValue(progress);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (progress < 100);
            return true;
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe((result) -> {
            mEffect.deinit();
        });
    }


    //region Effect.Callback interface
    /**
     * reported error from the effect engine.
     * @param error
     */
    @Override
    public void onError(@Nonnull String error) {
        Log.d(TAG, "##### onError : error = " + error);
    }

    /**
     * After effect processing, this method will be called with the result bitmap.
     * @param effectedBmp the result bitmap of effect processing
     */
    @Override
    public void onResult(@Nonnull Bitmap effectedBmp) {
        mIsEffected = true;

        // set the effect processed bitmap
        setEffectedBmp(effectedBmp);

        // post the effect processed bitmap
        getPhotoBitmap().postValue(getEffectedBmp());

        // delete temp file after creating the effect processed photo.
        // we have the original and effect processed bitmaps in this class.
        JPEGFile.getInstance().delete(getPhotoFile());
    }
    //endregion
}
