package com.project.splitz;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Joseph Ang on 30/6/2017.
 */

public class GroupInfoPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    public GroupInfoPagerAdapter(FragmentManager fm1, int NumOfTabs) {
        super(fm1);
        this.mNumOfTabs = NumOfTabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                GeneralInfoFragment GeneralInfoTab = new GeneralInfoFragment();
                return GeneralInfoTab;
            case 1:
                UserInfoFragment UserInfoTab = new UserInfoFragment();
                return UserInfoTab;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
