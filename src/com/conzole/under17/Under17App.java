package com.conzole.under17;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.PushService;

import android.app.Application;

public class Under17App extends Application {

	@Override
	public void onCreate (){
		super.onCreate();
		
		Parse.enableLocalDatastore(this);
		
		Parse.initialize(this, "nFRohenRl3cSgXMr4QnL2gG5R35xxuid7cpb864q", "feinuclVh5nNJvNeRG7Sd3kMELoZgUfAaljXgAoW");
		
		// Specify an Activity to handle all pushes by default.
		PushService.setDefaultPushCallback(this, MainActivity.class);
		
		ParseUser curUser = ParseUser.getCurrentUser();
		if(curUser == null){
			ParseAnonymousUtils.logIn(new LogInCallback() {
				
				@Override
				public void done(ParseUser arg0, ParseException arg1) {					
					
				}
			});
		}
	}
}

