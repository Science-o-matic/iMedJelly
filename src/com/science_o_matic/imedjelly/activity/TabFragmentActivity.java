package com.science_o_matic.imedjelly.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.widget.TabHost;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.science_o_matic.imedjelly.R;
import com.science_o_matic.imedjelly.fragment.AboutFragment;
import com.science_o_matic.imedjelly.fragment.BeachesFragment;
import com.science_o_matic.imedjelly.fragment.BeachesMapFragment;
import com.science_o_matic.imedjelly.fragment.CommunityFragment;
import com.science_o_matic.imedjelly.fragment.JellyFishesFragment;
import com.science_o_matic.imedjelly.fragment.TreatmentFragment;
import com.science_o_matic.imedjelly.util.Util;

public class TabFragmentActivity extends FragmentActivity {
	private FragmentTabHost mTabHost = null;
	private FragmentManager mManager = null;
	private String mCurrentTab = null;
	
	final int RQS_GooglePlayServices = 1;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_tab);
        // Create tab host.
        Resources res = getResources();
        mManager = getSupportFragmentManager();
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, mManager, R.id.tabcontent);
        // Create beaches map tab.
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    	if(status == ConnectionResult.SUCCESS) {
	        // Add beaches map tab.
	        Intent intentBeachesMap = new Intent().setClass(this, BeachesMapFragment.class);
	        mTabHost.addTab(mTabHost
	        		.newTabSpec(res.getString(R.string.map_view_label))
	        		.setIndicator("", res.getDrawable(R.drawable.ic_tab_map))
	        		.setContent(intentBeachesMap),
	        		BeachesMapFragment.class, null);
    	}
    	else {
    		Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, RQS_GooglePlayServices);
    		if(dialog != null) {
                dialog.show();                
            }
    	}
        // Add beaches tab.
        Intent mIntentBeaches = new Intent().setClass(this, BeachesFragment.class);
        mTabHost.addTab(mTabHost
        		.newTabSpec(res.getString(R.string.beaches_view_label))
        		.setIndicator("", res.getDrawable(R.drawable.ic_tab_beach))
        		.setContent(mIntentBeaches),
        		BeachesFragment.class, null);
        // Add jelly fishes tab.
        Intent intentJellyFishes = new Intent().setClass(this, BeachesMapFragment.class);
        mTabHost.addTab(mTabHost
        		.newTabSpec(res.getString(R.string.jellyfishes_view_label))
        		.setIndicator("", res.getDrawable(R.drawable.ic_tab_jellyfish))
        		.setContent(intentJellyFishes),
                JellyFishesFragment.class, null);
        // Add treatment tab.
        Intent intentTreatment = new Intent().setClass(this, TreatmentFragment.class);
        mTabHost.addTab(mTabHost
        		.newTabSpec(res.getString(R.string.treatment_view_label))
        		.setIndicator("", res.getDrawable(R.drawable.ic_tab_treatment))
        		.setContent(intentTreatment),
        		TreatmentFragment.class, null);
        // Add about tab.
        Intent intentAbout = new Intent().setClass(this, AboutFragment.class);
        mTabHost.addTab(mTabHost
        		.newTabSpec(res.getString(R.string.about_view_label))
        		.setIndicator("", res.getDrawable(R.drawable.ic_tab_about))
        		.setContent(intentAbout),
        		AboutFragment.class, null);
        // Add community tab.
        Intent intentCommunity = new Intent().setClass(this, CommunityFragment.class);
        mTabHost.addTab(mTabHost
        		.newTabSpec(res.getString(R.string.community_view_label))
        		.setIndicator("", res.getDrawable(R.drawable.ic_tab_community))
        		.setContent(intentCommunity),
        		CommunityFragment.class, null);
        // Set listener to control back stack.
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
        	@Override
			public void onTabChanged(String tabId) {
        		// Clean back stack.
	        	while(mManager.getBackStackEntryCount() > 0) {
	        		mManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
	        	}
        		// Restore if needed the beaches fragment.
        		if(tabId == getResources().getString(R.string.beaches_view_label)) {
        			mManager.beginTransaction()
	            	.replace(R.id.tabcontent, new BeachesFragment(), null)
	            	.addToBackStack(null)
	            	.commit();
        		}
        		mCurrentTab = tabId;
			}
        });
        
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
        	String notification = extras.getString("notification");
        	if(notification != null) {
        		Util.ShowNotification(this,
    				getResources().getString(R.string.notification_title),
    				notification);
        	}
		}
	}

	@Override
    public void onBackPressed() {
		int label = R.string.community_view_label;
		if(mCurrentTab == getResources().getString(label)) {
			CommunityFragment fragment = (CommunityFragment) mManager.findFragmentByTag(mCurrentTab);
			if(fragment.goBack()) {
				return;
			}
		}
		super.onBackPressed();
    }

	@Override
    public void onDestroy() {
        super.onDestroy();
        mTabHost = null;
    }
}
