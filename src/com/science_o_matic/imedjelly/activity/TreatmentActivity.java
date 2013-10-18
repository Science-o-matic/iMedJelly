package com.science_o_matic.imedjelly.activity;

import android.app.Activity;
import android.os.Bundle;

import com.science_o_matic.imedjelly.application.MainApplication;
import com.science_o_matic.imedjelly.util.Util;

public class TreatmentActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			// Get treatment link.
			int id = (int) extras.getInt("id");
			String link = MainApplication.sTreatmentsLink[id];
			int layout = Util.getResourceId(this, link, "layout");
			setContentView(layout);
		}
	}
}