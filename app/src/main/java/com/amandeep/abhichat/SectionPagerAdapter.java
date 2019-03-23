package com.amandeep.abhichat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

class SectionPagerAdapter extends FragmentStatePagerAdapter {
    int tabCount;

    //Constructor to the class
    public SectionPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        //Initializing tab count
        this.tabCount= tabCount;
    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        switch (position) {
            case 0:
                ChatListFragment chatListFragment = new ChatListFragment();
                return chatListFragment;
            case 1:
                AllUserFragmnt allUserFragmnt = new AllUserFragmnt();
                return allUserFragmnt;

            default:
                return null;
        }
    }

    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return tabCount;
    }

}
