package com.conzole.under17;

import java.util.ArrayList;
import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ParseQueryAdapter extends BaseAdapter {

	static final String TABLE_NAME = "news";
	
	private String mDate;
	private ArrayList<NewsModel> mDataList;
	private Activity mActivity;
	private LayoutInflater mInflater;
	private OnLoadListener mLoadListner;
	private OnActionListener mActionListner;
	
	public ParseQueryAdapter(OnLoadListener listner, Activity act,  String date)
	{
		mDataList = new ArrayList<NewsModel>();
		mDate = date;
		mActivity = act;
		mLoadListner = listner;
		
		mInflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		queryData();
	}
	
	public ParseQueryAdapter(Activity act,  String date, ArrayList<NewsModel> dataList)
	{
		mDataList = new ArrayList<NewsModel>();
		mDate = date;
		mActivity = act;
		
		mInflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		mDataList = dataList;
	}
	
	public ArrayList<NewsModel> getData(){
		return mDataList;
	}
	
	private void queryData(){
		ParseQuery<ParseObject> query = ParseQuery.getQuery(TABLE_NAME);
        query.whereEqualTo("date", mDate);
        query.orderByAscending("priority");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> dataList, ParseException e) {
                if (e == null) {
                    fillData(dataList);
                } else {
                	
                	Toast toast = Toast.makeText(mActivity, "Network Error", Toast.LENGTH_SHORT);
                	toast.show();
                }
                
                if(mLoadListner != null){
                	mLoadListner.onLoaded();
                }
            }
        });
	}
	
	private void fillData(List<ParseObject> dataList){

		mDataList.clear();
		int position = 1;
		
		for(ParseObject object : dataList){
			String subject = object.getString("subject");
	    	String data = object.getString("data");
	    	String link = object.getString("link");
	    	String id = object.getObjectId();
	    	
	    	NewsModel model = new NewsModel();
	    	model.id = id;
	    	model.position = position;
	    	model.date = mDate;
	    	model.subject = subject;
	    	model.data = data;
	    	model.link = link;
	    	
	    	mDataList.add(model);
	    	position++;
		}
		
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {

		return mDataList.size();
	}

	@Override
	public Object getItem(int position) {
		
		NewsModel item = mDataList.get(position);		
		return item;
	}

	@Override
	public long getItemId(int position) {

		NewsModel item = mDataList.get(position);
		return item.position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View vi = convertView;
        ViewHolder holder;
        
		if (vi == null) {
			vi = mInflater.inflate(R.layout.fragment_newsitem, null);
			
			holder = new ViewHolder();
			holder.position = (TextView)vi.findViewById(R.id.postext);
			holder.subject = (TextView)vi.findViewById(R.id.subjecttext);
			holder.data = (TextView)vi.findViewById(R.id.newsdata);
			holder.more = (ImageButton)vi.findViewById(R.id.moreButton);
			
			vi.setTag(holder);
    	}
		else {
			holder=(ViewHolder)vi.getTag();
		}
        
		final NewsModel item = mDataList.get(position);
			
		holder.position.setText(("" + item.position + "."));
		holder.subject.setText(item.subject);
		holder.data.setText(item.data);
		
		holder.more.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mActionListner != null){
					mActionListner.onOpenMore(item);
				}
			}
		});
    	  
		holder.subject.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openLink(item.link);
			}
    	});
    	  
    	holder.data.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				openLink(item.link);
			}
    	});
		
		return vi;
	}
	
	private void openLink(String link){
		if(mActivity != null){
			Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(link));
			mActivity.startActivity(intent);
		}
    }
	
	public void addLoadListener(OnLoadListener listner){
		mLoadListner = listner;
	}
	
	public void addActionListener(OnActionListener listner){
		mActionListner = listner;
	}
	
	public static class NewsModel{
		public String id;
		public int position;
		public String date;
		public String subject;
		public String data;
		public String link;
	}
	
	public static class ViewHolder{
        
		public TextView position;
        public TextView subject;
        public TextView data; 
        public ImageButton more;
    }
	
	public abstract static interface OnActionListener{		
		
		public abstract void onOpenLink(NewsModel model);
		
		public abstract void onOpenMore(NewsModel model);
	}
	
	public abstract static interface OnLoadListener{
		
		public abstract void onLoaded();
	}
}
