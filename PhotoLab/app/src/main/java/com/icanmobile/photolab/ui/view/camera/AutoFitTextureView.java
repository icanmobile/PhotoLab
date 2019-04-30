package com.icanmobile.photolab.ui.view.camera;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

/**
 * Created by JONG HO BAEK on 22,February,2019
 * email: icanmobile@gmail.com
 *
 * AutoFitTextureView class
 * This class supports a texture view for camera preview screen.
 * CameraView control sets the screen size info to this class in order to support the full screen preview.
 */
public class AutoFitTextureView extends TextureView {

    private static final String TAG = AutoFitTextureView.class.getSimpleName();

    public AutoFitTextureView(Context context) {
        this(context, null);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    private int mRatioWidth = 0;
    private int mRatioHeight = 0;
    private int mScreenWidth = 0;
    private int mScreenHeight = 0;

    /**
     * Sets the aspect ratio for this view. The size of the view will be measured based on the ratio
     * calculated from the parameters and save the input screen width and height values.
     * @param width
     * @param height
     * @param screenWidth
     * @param screenHeight
     */
    public void setAspectRatio(int width, int height, int screenWidth, int screenHeight) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }

        mRatioWidth = width;
        mRatioHeight = height;
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height);
        }
        else {  // set screen width and height for full screen preview
            setMeasuredDimension(mScreenWidth, mScreenHeight);
        }
    }
}
