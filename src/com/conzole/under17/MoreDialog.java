package com.conzole.under17;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MoreDialog extends DialogFragment {

	private String mShareData;
	private String mLink;
	
	public MoreDialog(String shareData, String link){
		mShareData = shareData;
		mLink = link;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    View dialogView = inflater.inflate(R.layout.more_dialog, null);
	    Button openBtn = (Button)dialogView.findViewById(R.id.openBtn);
	    openBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openLink(mLink);
			}
		});	    
	    
	    Button shareBtn = (Button)dialogView.findViewById(R.id.shareBtn);
	    shareBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				shareNews(mShareData);
			}
		});	
	    
	    builder.setView(dialogView);      
	    return builder.create();
	}
	
	private void openLink(String link){
		
		this.dismiss();
		
		Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(link));
		startActivity(intent);
    }
	
	private void shareNews(String data){
		this.dismiss();
		
    	Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
    	sharingIntent.setType("text/plain");
    	
    	sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, data);
    	
    	startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }
}
