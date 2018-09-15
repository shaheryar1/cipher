package com.eg.cipher;

/**
 * Created by EG on 9/15/2018.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                TextImageFragment tab1 = new TextImageFragment();
                return tab1;
            case 1:
                VideoFragment tab2 = new VideoFragment();
                return tab2;
//            case 2:
//                SoundFragment tab3 = new SoundFragment();
//                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}