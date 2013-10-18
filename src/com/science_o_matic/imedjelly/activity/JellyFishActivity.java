package com.science_o_matic.imedjelly.activity;

import android.app.Activity;
import android.os.Bundle;

import com.science_o_matic.imedjelly.application.MainApplication;
import com.science_o_matic.imedjelly.util.Util;

public class JellyFishActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			// Get jelly fish link.
			int id = (int) extras.getLong("id");
			String link = MainApplication.sJellyFishesLink[id];
			int layout = Util.getResourceId(this, link, "layout");
			setContentView(layout);
		}
	}
}