package com.project.splitz;

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
                GroupsFragment GroupsTab = new GroupsFragment();
                return GroupsTab;
            case 1:
                FriendsFragment FriendsTab = new FriendsFragment();
                return FriendsTab;
            case 2:
                ExpensesFragment ExpensesTab = new ExpensesFragment();
                return ExpensesTab;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
