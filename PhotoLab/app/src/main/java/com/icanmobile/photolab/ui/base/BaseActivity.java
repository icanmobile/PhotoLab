package com.icanmobile.photolab.ui.base;

import android.content.Intent;
import android.os.Bundle;

import com.icanmobile.photolab.data.DataManager;
import com.icanmobile.photolab.data.gson.model.ActivityModel;
import com.icanmobile.photolab.data.gson.model.ArgumentModel;
import com.icanmobile.photolab.util.AppConst;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import butterknife.Unbinder;
import dagger.android.support.DaggerAppCompatActivity;

import static com.icanmobile.photolab.util.AppConst.ARGUMENT_MODEL;

/**
 * Created by JONG HO BAEK on 21,February,2019
 * email: icanmobile@gmail.com
 *
 * BaseActivity class
 * This class is extended from DaggerAppCompatActivity class and all activity classes are extended from this class.
 * This class supports Butter Knife binder, change activities, and set arguments as intent extras.
 */
public abstract class BaseActivity extends DaggerAppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    @Inject protected DataManager mDataManager;

    //region Butter Knife binder
    private Unbinder mUnBinder;
    public void setUnBinder(Unbinder unBinder) {
        mUnBinder = unBinder;
    }
    //endregion

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check intent
        getArguments();
    }

    /**
     * get arguments for the creating activity
     * this method get ArgumentModel object and create ActivityModel object using json string of ArgumentModel object.
     */
    protected ActivityModel mActivityModel;
    protected void getArguments() {
        Intent intent = getIntent();
        boolean bSuccess = false;
        if (intent != null) {
            Bundle args = intent.getExtras();
            if (args != null) {
                ArgumentModel argumentModel = (ArgumentModel) args.getSerializable(ARGUMENT_MODEL);
                if (argumentModel != null) {
                    mActivityModel = mDataManager.getActivityModel(this, argumentModel.json());
                    bSuccess = true;
                }
            }
        }

        if (!bSuccess)
            mActivityModel = mDataManager.getActivityModel(this, getDefaultJson());
    }

    /**
     * get default json file.
     * When the input intent doesn't include ArgumentsModel object as extras, we need the default json file string because of the activity creation.
     * When other applications launch PhotoLab app, the intent will not include the ArgumentsModel as extras.
     */
    abstract protected String getDefaultJson();


    @Override
    protected void onDestroy() {
        // unbind the butter knife
        if (mUnBinder != null)
            mUnBinder.unbind();

        super.onDestroy();
    }


    /**
     * change activity using Java reflection technique.
     * @param json
     * @param dir
     */
    public void changeActivity(@Nonnull String json, @Nonnull AppConst.TransitionDir dir) {
        if (json == null) return;
        if (dir == null) dir = AppConst.TransitionDir.TRANSACTION_DIR_NONE;

        // exit app
        if (json.equals(AppConst.Json.EXIT.file())) {
            goToNative();
            return;
        }

        ActivityModel activityModel = mDataManager.getActivityModel(this, json);
        try {
            // create a intent with the class name string of next activity.
            Intent intent = new Intent(this, Class.forName(activityModel.className()));

            // set ArgumentsModel as extras.
            Bundle args = new Bundle();
            args.putSerializable(ARGUMENT_MODEL, getNextArguments(json, dir));
            intent.putExtras(args);

            // generate custom transition animations based on the activity json files.
            int enter = 0, exit = 0;
            switch (dir) {
                case TRANSACTION_DIR_FORWARD:
                    {
                        enter = activityModel.forwardEnterAnim(this);
                        exit = mActivityModel.forwardExitAnim(this);
                    }
                    break;
                case TRANSACTION_DIR_BACKWARD:
                    {
                        enter = activityModel.backwardEnterAnim(this);
                        exit = mActivityModel.backwardExitAnim(this);
                    }
                    break;
            }
            Bundle animation = ActivityOptionsCompat.makeCustomAnimation(this, enter, exit).toBundle();

            // call startActivity method
            startActivity(intent, animation);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * get next ArgumentModel object for next activity.
     * the sub activity class can override this method in order to support the customized ArgumentModel such as CameraArugmentModel
     * @param json the json file path
     * @param dir the transition direction
     * @return
     */
    protected ArgumentModel getNextArguments(@Nonnull String json, @Nonnull AppConst.TransitionDir dir){
        return new ArgumentModel(json, dir.name());
    }

    /**
     * go to native screen
     * when changeActivity() method is called with "EXIT" json string, app will go to native screen.
     */
    private void goToNative() {
        moveTaskToBack(true);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}
