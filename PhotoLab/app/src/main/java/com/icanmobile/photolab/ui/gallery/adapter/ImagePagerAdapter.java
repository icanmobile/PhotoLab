package com.icanmobile.photolab.ui.gallery.adapter;

import com.icanmobile.photolab.ui.gallery.fragment.ImageFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;

/**
 * Created by JONG HO BAEK on 22,February,2019
 * email: icanmobile@gmail.com
 *
 * ImagePagerAdapter class
 */
public class ImagePagerAdapter extends FragmentStatePagerAdapter {

    private List<String> pagerItems;

    public ImagePagerAdapter(Fragment fragment, List<String> pagerItems) {
        // Note: Initialize with the child fragment manager.
        super(fragment.getChildFragmentManager());
        this.pagerItems = new ArrayList<>(pagerItems);
    }

    @Override
    public int getCount() {
        return pagerItems.size();
    }

    @Override
    public Fragment getItem(int position) {
        return ImageFragment.newInstance(pagerItems.get(position));
    }
}
