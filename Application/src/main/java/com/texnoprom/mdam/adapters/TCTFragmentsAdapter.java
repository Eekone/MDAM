package com.texnoprom.mdam.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.texnoprom.mdam.fragments.TCTFragment;

import java.util.ArrayList;
import java.util.List;


public class TCTFragmentsAdapter extends FragmentPagerAdapter {

    private TCTFragment mCurrentFragment;
    public final List<TCTFragment> fragmentsList = new ArrayList<>();

    public void addFragment(String link) {
        fragmentsList.add(TCTFragment.newInstance(link));
    }


    public TCTFragmentsAdapter(FragmentManager fm) {
        super(fm);
    }

    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            mCurrentFragment = ((TCTFragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentsList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentsList.size();
    }

    @Override
    public void destroyItem(ViewGroup viewPager, int position, Object object) {
        viewPager.removeView((View) object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Составной запрос";
            case 1:
                return "Типы параметров";
            default:
                return "???";
        }
    }
}
