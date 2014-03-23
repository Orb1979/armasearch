package com.orb.arma.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
public class PagerAdapter extends FragmentPagerAdapter implements
        ViewPager.OnPageChangeListener, ActionBar.TabListener {

    private SparseArray<Fragment> mFragments = new SparseArray<Fragment>();
    private ViewPager mPager;
    private ActionBar mActionBar;
    private Fragment mPrimaryItem;
    private int currentPageId = 0;
    private int prevPageId = 0;

    public int getCurrentPageId() {
        return currentPageId;
    }

    public int getPrevPageId() {
        return prevPageId;
    }

    public PagerAdapter(FragmentManager fm, ViewPager vp, ActionBar ab) {
        super(fm);
        mPager = vp;
        mPager.setAdapter(this);
        mPager.setOnPageChangeListener(this);
        mActionBar = ab;
    }

    public void addTab(Fragment frag, int position, String tabTitle) {
        mFragments.put(position, frag);
        mActionBar.addTab(mActionBar.newTab().setTabListener(this).setText(tabTitle));
        this.notifyDataSetChanged();
    }

    public void removeTab(int index) {
        mFragments.remove(index);
        mActionBar.removeTab(mActionBar.getTabAt(index));
        this.notifyDataSetChanged();
    }

    public void switchToTab(int id){
        mPager.setCurrentItem(id);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        mPrimaryItem = (Fragment) object;
    }

    @Override
    public int getItemPosition(Object object) {
        if (object == mPrimaryItem) {
            return POSITION_UNCHANGED;
        }
        return POSITION_NONE;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        //keep track of the last opened tab / page
        prevPageId = currentPageId;
        currentPageId = tab.getPosition();
        mPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) { }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) { }

    @Override
    public void onPageScrollStateChanged(int arg0) { }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) { }

    @Override
    public void onPageSelected(int position) {
        mActionBar.setSelectedNavigationItem(position);
    }

    //This method removes the pages from ViewPager
    public void removePages() {
        mActionBar.removeAllTabs();
        mPager.removeAllViews();
        mFragments.clear();
        //make this to update the pager
        mPager.setAdapter(null);
        mPager.setAdapter(this);
    }
}
