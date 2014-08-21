package com.conzole.under17;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter  {

	private ArrayList<String> mPublishedDates;
	
	public ScreenSlidePagerAdapter(FragmentManager fm, ArrayList<String> pubDates) {
        super(fm);
        mPublishedDates = pubDates;
    }

    @Override
    public Fragment getItem(int position) {
    	PlaceholderFragment frag = new PlaceholderFragment();
    	frag.setDate(getDate(position));
    	return frag;
    }

    @Override
    public int getCount() {
        return mPublishedDates.size();
    }

    public String getDate(int pos){
    	
    	return mPublishedDates.get(pos);
    }
}
