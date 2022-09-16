package com.miji.solar.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.miji.solar.R;
import com.miji.solar.fragment.TabFragment1;
import com.miji.solar.fragment.TabFragment2;
import com.miji.solar.fragment.TabFragment3;
import com.miji.solar.fragment.TabFragment4;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3, R.string.tab_text_4};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        //return PlaceholderFragment.newInstance(position);

        switch(position) {
            case 0 :
                TabFragment1 tab1 = new TabFragment1();
                return tab1;

            case 1 :
                TabFragment2 tab2 = new TabFragment2();
                return tab2;

            case 2 :
                TabFragment3 tab3 = new TabFragment3();
                return tab3;

            case 3 :
                TabFragment4 tab4 = new TabFragment4();
                return tab4;

            default :
                return null;
        }

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 4;
    }
}