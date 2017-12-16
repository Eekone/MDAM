package com.texnoprom.mdam.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.texnoprom.mdam.fragments.TCPFragment;

import java.util.ArrayList;
import java.util.List;


public class MMPRFragmentsAdapter extends FragmentPagerAdapter {

    private TCPFragment mCurrentFragment;
    public final List<TCPFragment> fragmentsList = new ArrayList<>();

    public void addFragment(String name, String type, int command, int device, int firstReg, int regCount) {
        fragmentsList.add(TCPFragment.newInstance(name, type, command, device, firstReg, regCount));
    }

    public void resetDevice(int device) {
        for (TCPFragment fr : fragmentsList) {
            fr.device = device;
        }
    }

    public MMPRFragmentsAdapter(FragmentManager fm) {
        super(fm);
    }

    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            mCurrentFragment = ((TCPFragment) object);
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
        if (position > fragmentsList.size())
            return "?";
        return fragmentsList.get(position).getArguments().getString("category");
    }
}
