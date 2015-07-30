package com.science_o_matic.imedjelly.activity;
 
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.science_o_matic.imedjelly.R;
import com.science_o_matic.imedjelly.adapter.NavigationAdapter;
import com.science_o_matic.imedjelly.fragment.AboutFragment;
import com.science_o_matic.imedjelly.fragment.BeachesFragment;
import com.science_o_matic.imedjelly.fragment.BeachesMapFragment;
import com.science_o_matic.imedjelly.fragment.CommunityFragment;
import com.science_o_matic.imedjelly.fragment.JellyFishesFragment;
import com.science_o_matic.imedjelly.fragment.PredictionFragment;
import com.science_o_matic.imedjelly.fragment.TreatmentFragment;
import com.science_o_matic.imedjelly.util.Util;

public class MainActivity extends DrawerActivity {
	CursorManager mCursorManager;
	FragmentManager mFragmentManager;
	
	// Navigation drawer.
	private ArrayList<DrawerItem> mMenuItems;
	private TypedArray mNavIcons;
	private String[] mNavTitles;
	private NavigationAdapter mNavAdapter;
	private ListView mDrawerList;
	
	final int RQS_GooglePlayServices = 1;
	
	// Fragment identifiers.
	private final int MAP_ID = 0;
	private final int PREDICTION_ID = 1;
	private final int BEACHES_ID = 2;
	private final int JELLYFISHES_ID = 3;
	private final int TREATMENT_ID = 4;
	private final int ABOUT_ID = 5;
	private final int COMMUNITY_ID = 6;
	
	// State.
	private int mPlayServices;
	private int mIndex;
	private Fragment mCurrentFragment;
	
	private Menu mMenu;
	
	protected void setLanguage(SharedPreferences.Editor editor) {
		String langCode = Locale.getDefault().getISO3Language();
		if (langCode == "en"){
			langCode = "en";
		}
		else if (langCode == "cat" || langCode == "ca"){
			langCode = "ca";
		}
		else if (langCode == "es"){
			langCode = "es";
		}
		else{
			langCode = "es";
		}
		editor.putString("langCode", langCode);
	}

	protected void setDeviceId(SharedPreferences.Editor editor) {
		TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		String uuid = tManager.getDeviceId();
		editor.putString("device", uuid);
	}
	
	protected void setNewUser(SharedPreferences.Editor editor) {
		editor.putBoolean("newUser", true);
	}
	
	protected void initialize() {
		// Set preferences.
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		if(!preferences.getBoolean("firstTime", false)) {
			setDeviceId(editor);
			editor.putBoolean("firstTime", true);
		}
		setLanguage(editor);
		setNewUser(editor);
		editor.commit();
	}
	
	protected void setFragment(int index) {
		Fragment fragment = null;
	    switch(index) {
	        case MAP_ID:
	            fragment = new BeachesMapFragment();
	            break;
	        case PREDICTION_ID :
	            fragment = new PredictionFragment();
	            break;
	        case BEACHES_ID:
	            fragment = new BeachesFragment(mBeachItemSelected);
	            break;
	        case JELLYFISHES_ID:
	            fragment = new JellyFishesFragment();
	            break;
	        case TREATMENT_ID:
	            fragment = new TreatmentFragment();
	            break;
	        case ABOUT_ID:
	            fragment = new AboutFragment();
	            break;
	        case COMMUNITY_ID:
	            fragment = new CommunityFragment();
	            break;
	    }
	    mIndex = index;
	    mCurrentFragment = fragment;
	    setTitle(mNavTitles[index]);
	    if (mMenu != null) {
	    	mMenu.findItem(R.id.action_zone).setVisible(mIndex == MAP_ID || mIndex == PREDICTION_ID);
	    	mMenu.findItem(R.id.action_jellyfish).setVisible(mIndex == PREDICTION_ID);
	    }
	    mFragmentManager.beginTransaction()
	        .replace(R.id.content, fragment)
	        .commit();
	}
	
	protected void showFragment(int index, Activity activity) {
		if(index == MAP_ID && mPlayServices != ConnectionResult.SUCCESS) {
    		// Show error
    		Dialog dialog = GooglePlayServicesUtil.getErrorDialog(mPlayServices, activity, RQS_GooglePlayServices);
    		if(dialog != null) {
    	        dialog.show();
    	    }
    		index = BEACHES_ID;
    	}
		// Set current fragment.
		setFragment(index);
    	closeDrawer();
	}

	public void setSelectedItem(int index) {
		mDrawerList.setItemChecked(index, true);
	}

	protected void createMenuView() {
		// Set adapter to view.
		mDrawerList = (ListView) findViewById(R.id.drawer_left);
		mMenuItems = new ArrayList<DrawerItem>();
		mNavIcons = getResources().obtainTypedArray(R.array.navigation_icons);			
        mNavTitles = getResources().getStringArray(R.array.navigation_options);
        mMenuItems = new ArrayList<DrawerItem>();
        for(int i=0; i<mNavIcons.length(); i++) {
        	mMenuItems.add(new DrawerItem(mNavTitles[i], mNavIcons.getResourceId(i, -1)));
        }
        mNavAdapter = new NavigationAdapter(this, mMenuItems);
        mDrawerList.setAdapter(mNavAdapter);
		// Update list on navigation drawer item click.
		final Activity activity = this;
		mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	showFragment(position, activity);
            }
        });
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mFragmentManager = getSupportFragmentManager();
		// Check google play services.
		mPlayServices = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		// Set drawer.
		setupDrawer(R.id.drawer_layout, R.drawable.ic_drawer, R.id.drawer_left);
		setCloseOnBack(true);
		// Create cursor manager.
		mCursorManager = new CursorManager(this, getSupportLoaderManager());
		// Initialize.
		createMenuView();
		initialize();
		// Show notifications.
		Bundle extras = getIntent().getExtras();
        if(extras != null) {
        	String notification = extras.getString("notification");
        	if(notification != null) {
        		Util.ShowNotification(this,
    				getResources().getString(R.string.notification_title),
    				notification);
        	}
		}
        // Show fragment.
        showFragment(MAP_ID, this);
    }
	
	OnItemSelectedListener mBeachItemSelected = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if (mIndex == BEACHES_ID) {
				BeachesFragment fragment = ((BeachesFragment) mCurrentFragment).selectItem(position);
				if (fragment != null) {
					mFragmentManager.beginTransaction()
			        	.replace(R.id.content, fragment, null)
			        	.addToBackStack(null)
			        	.commit();
					mCurrentFragment = fragment;
					setTitle(getResources().getString(fragment.getTitleResource()));
				}
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
		
	};

	@Override
	public void onBackPressed() {
		if (super.isDrawerOpen()) {
			super.onBackPressed();
		}
		else {
			if (mIndex == BEACHES_ID) {
				BeachesFragment fragment = ((BeachesFragment) mCurrentFragment).unselectItem();
				if (fragment != null) {
					mFragmentManager.beginTransaction()
			        	.replace(R.id.content, fragment, null)
			        	.addToBackStack(null)
			        	.commit();
					mCurrentFragment = fragment;
					setTitle(getResources().getString(fragment.getTitleResource()));
					return;
				}
			}
			else if (mIndex == COMMUNITY_ID) {
				CommunityFragment fragment = (CommunityFragment) mCurrentFragment;
				if (fragment.goBack()) {
					return;
				}
			}
			super.openDrawer();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent e) {
	    if (keyCode == KeyEvent.KEYCODE_MENU) {
	    	if (isDrawerOpen()) {
	    		closeDrawer();
	    	}
	    	else {
	    		openDrawer();
	    	}
	    	return true;
	    }
	    return super.onKeyDown(keyCode, e);
	}

	@Override
	protected void onDestroy() {
		mCursorManager.clear();
		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    		case R.id.action_zone:
    			if (mIndex == MAP_ID) {
    				((BeachesMapFragment) mCurrentFragment).showZoneDialog();
    			} else if(mIndex == PREDICTION_ID) {
    				((PredictionFragment) mCurrentFragment).showZoneDialog();
    			}
    			break;
    		case R.id.action_jellyfish:
				if(mIndex == PREDICTION_ID) {
    				((PredictionFragment) mCurrentFragment).showPredictionDialog(getResources());
    			}
    			break;
    		default:
	            return super.onOptionsItemSelected(item);
    	}
    	return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.map_actions, menu);
		mMenu = menu;
		mMenu.findItem(R.id.action_zone).setVisible(mIndex == MAP_ID || mIndex == PREDICTION_ID);
		mMenu.findItem(R.id.action_jellyfish).setVisible(mIndex == PREDICTION_ID);
	    return super.onCreateOptionsMenu(menu);
	}
}

