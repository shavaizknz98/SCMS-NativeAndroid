package com.example.scms;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {

    int mNoOfTabs;

    public PagerAdapter(FragmentManager fm, int NumberOfTabs){
        super(fm);
        this.mNoOfTabs = NumberOfTabs;
    }


    @Override
    public int getCount() {
        return mNoOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                LoginTab tab1 = new LoginTab();
                return tab1;
            case 1:
                SignupTab tab2 = new SignupTab();
                return tab2;
                default:
                    return null;
        }

    }

}
