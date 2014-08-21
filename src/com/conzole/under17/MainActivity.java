package com.conzole.under17;

import java.util.ArrayList;
import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.os.Bundle;

public class MainActivity extends ActionBarActivity implements PlaceholderFragment.ActivityControl {	

	static final String PUBLISHED_DATES = "published_dates";
	
	private ViewPager mPager;
	private ScreenSlidePagerAdapter mPagerAdapter;
	private ArrayList<String> mPublishedDates;
	
	public void showProgress(boolean bShow){
		LinearLayout pb = (LinearLayout)findViewById(R.id.progressBarContainer);
		if(bShow && pb != null){
			pb.setVisibility(View.VISIBLE);
		}
		
		if(!bShow && pb != null){
			pb.setVisibility(View.GONE);
		}
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
     	ParseAnalytics.trackAppOpened(getIntent());
        
     	final LinearLayout pb = (LinearLayout)findViewById(R.id.progressBarContainer);
     	
        initializePager();
        if(savedInstanceState != null){
        	mPublishedDates = savedInstanceState.getStringArrayList(PUBLISHED_DATES);
        	if(mPublishedDates == null){
        		getPublishedDates(pb);
        	}
        	else{
        		showProgress(false);
        		intializeAdapter();
        	}
        }
        else{
        	getPublishedDates(pb);
        }
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
    
    	savedInstanceState.putStringArrayList(PUBLISHED_DATES, mPublishedDates);
    	
    	super.onSaveInstanceState(savedInstanceState);
    }
    
	@Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);
		
		if(savedInstanceState != null){
        	mPublishedDates = savedInstanceState.getStringArrayList(PUBLISHED_DATES);
		}
	}
    
    private void initializePager(){
    	mPager = (ViewPager) findViewById(R.id.container);
		mPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int pos) {
				if(mPagerAdapter != null){
					String curDate = mPagerAdapter.getDate(pos);
					String appName = getString(R.string.app_name);
					android.support.v7.app.ActionBar actionBar = getSupportActionBar();
					actionBar.setTitle(appName + "   [ " + curDate + " ]");
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
    }
    
    private void intializeAdapter(){
    	if(mPagerAdapter == null){
    		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), mPublishedDates);
    	}
    	if(mPager == null){
    		initializePager();
    	}
    	
    	mPager.setAdapter(mPagerAdapter);
    	if(mPublishedDates != null && mPublishedDates.size() > 0){
    		int len = mPublishedDates.size();
    		mPager.setCurrentItem(len - 1);
    	}
    }
    
    private void getPublishedDates(final LinearLayout pb){
    	
    	showProgress(true);
    	
    	ParseQuery<ParseObject> query = ParseQuery.getQuery("published");
    	query.fromLocalDatastore();
    	query.orderByAscending("date");
    	query.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> dateList, ParseException exp) {	
				mPublishedDates = new ArrayList<String>();
				for(ParseObject obj : dateList){
					String pbDate = obj.getString("date");
					mPublishedDates.add(pbDate);
				}
				if(mPublishedDates.size() > 0){
					showProgress(false);
				}
				intializeAdapter();
				
				getPublishedDatesQuery(pb);
			}
		});
    }
    
    private void getPublishedDatesQuery(final LinearLayout pb){
    	ParseQuery<ParseObject> query = ParseQuery.getQuery("published");
    	query.orderByAscending("date");
    	query.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> dateList, ParseException exp) {	
				showProgress(false);
				try {
					ParseObject.unpinAll("PUBLISHED_DATES");
				} catch (ParseException e) {  }
				
				ParseObject.pinAllInBackground("PUBLISHED_DATES", dateList);
				pb.setVisibility(View.GONE);
				ArrayList<String> publishedDates = new ArrayList<String>();
				for(ParseObject obj : dateList){
					String pbDate = obj.getString("date");
					publishedDates.add(pbDate);
				}
				if(!compareLists(publishedDates)){
					mPublishedDates = publishedDates;
					mPagerAdapter = null;
					intializeAdapter();
					mPagerAdapter.notifyDataSetChanged();
				}	
				
				showProgress(false);
			}
		});
    }
    
    private boolean compareLists(ArrayList<String> publishedDates){
    	boolean areEqual = true;
    	if(mPublishedDates == null || publishedDates.size() != mPublishedDates.size()){
    		return false;
    	}
    	
    	for(String obj : publishedDates){
    		if(!mPublishedDates.contains(obj)){
    			areEqual = false;
    			break;
    		}
    	}
    	
    	return areEqual;
    }

	@Override
	public void openMoreDialog(String shareData, String link) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		DialogFragment dialog = new MoreDialog(shareData, link);
        dialog.show(ft, "MoreDialog");
	}
	
}
