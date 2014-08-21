package com.conzole.under17;

import java.util.ArrayList;

import com.conzole.under17.ParseQueryAdapter.NewsModel;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

public class PlaceholderFragment extends Fragment {
	
	static final String TABLE_NAME = "news";
	static final String CURRENT_DATE = "current_date";
	private String mDate;
	
	private ParseAdapterStore mStore;
	private ActionBarActivity mActivity;
	private ParseQueryAdapter.NewsModel mCurrentModel;
	
	public PlaceholderFragment() {
		
    }
		
	public void setDate(String dtString){
		mDate = dtString;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		
		menu.addSubMenu("Open");
		menu.addSubMenu("Share");
	}
	

	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (ActionBarActivity)activity;
    }
	
	@Override
    public void onSaveInstanceState(Bundle savedInstanceState){
    
		savedInstanceState.putString(CURRENT_DATE, mDate);
		
    	super.onSaveInstanceState(savedInstanceState);
    }
	
	private String getShareData(ParseQueryAdapter.NewsModel object){
		String subject = object.subject;
  	  	String data = object.data;
  	    String link = object.link;
  	    
  	    return subject + " : " + data + "\n\n Link : " + link + "\n\n By under17";
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	readCurrentDate(savedInstanceState);
    	
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        final ListView list = (ListView) rootView.findViewById(R.id.newsView);
        
        final LinearLayout pbCont = (LinearLayout)rootView.findViewById(R.id.progressFragContainer);
        
        ParseQueryAdapter adapter = null;
        
        FragmentManager fm = getActivity().getSupportFragmentManager();
        mStore = (ParseAdapterStore) fm.findFragmentByTag(mDate);
        if(mStore == null){
        	ParseQueryAdapter.OnLoadListener listner = new ParseQueryAdapter.OnLoadListener() {
				
				@Override
				public void onLoaded() {
					pbCont.setVisibility(View.GONE);
				}
			};
			pbCont.setVisibility(View.VISIBLE);
        	adapter = new ParseQueryAdapter(listner, getActivity(), mDate);
        	mStore = new ParseAdapterStore(adapter.getData());
	        fm.beginTransaction().add(mStore, mDate).commit();
        }
        else{
        	adapter = new ParseQueryAdapter(getActivity(), mDate, mStore.getData());
        }
        
        adapter.addActionListener(new ParseQueryAdapter.OnActionListener() {
			
			@Override
			public void onOpenMore(NewsModel model) {
				mCurrentModel = model;
				getActivity().openContextMenu(list);
			}
			
			@Override
			public void onOpenLink(NewsModel model) {
				
			}
		});
        
        list.setAdapter(adapter);
        
        registerForContextMenu(list);
        
        return rootView;
    }
    
    private void shareNews(String data){
    	
    	Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
    	sharingIntent.setType("text/plain");
    	
    	sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, data);
    	
    	startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }
    
    private void openLink(String link){
		if(mActivity != null){
			Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(link));
			mActivity.startActivity(intent);
		}
    }    
    
    private void readCurrentDate(Bundle savedInstanceState){
    	if(savedInstanceState != null){
    		mDate = savedInstanceState.getString(CURRENT_DATE);
    	}
    }
    
    public static interface ActivityControl{
    	
    	void openContextMenu(View v);
    	
    	void openMoreDialog(String shareData, String link);
    }
    
    @Override
	public boolean onContextItemSelected(MenuItem menuItem) {
    	if(mCurrentModel != null && mCurrentModel.date == mDate){
    		ParseQueryAdapter.NewsModel item = mCurrentModel;
    		CharSequence title = menuItem.getTitle();
    		if(title == "Open"){
    			openLink(item.link);
    		}
			if(title == "Share"){
    			shareNews(getShareData(item));
    		}
			
			return true;
    	}
		return false;
	}

	public class ParseAdapterStore extends Fragment{
    	
    	private ArrayList<ParseQueryAdapter.NewsModel> mData;
    	
    	public ParseAdapterStore(){
    		
    	}
    	
    	public ParseAdapterStore(ArrayList<ParseQueryAdapter.NewsModel> data){
    		mData = data;
    	}
    	
    	public ArrayList<ParseQueryAdapter.NewsModel> getData(){
    		return mData;
    	}
    	
    	@Override
    	public void onCreate(Bundle savedInstanceState) {
    	    super.onCreate(savedInstanceState);
    	    
    	    setRetainInstance(true);
    	  }
    }
}
