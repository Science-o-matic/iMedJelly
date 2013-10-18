package com.science_o_matic.imedjelly.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;

import com.science_o_matic.imedjelly.R;

public class DrawerActivity extends ActionBarActivity {
	// Layout.
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;

	// View.
	private View mDrawerView;
	
	// Action Bar.
	ActionBar mActionBar;
	
	// Flags.
	private boolean mCloseOnBack;

	public void setupDrawer(int layoutId, int icon, int viewId) {
		// Get drawer.
		mDrawerLayout = (DrawerLayout) findViewById(layoutId);
		// Set the ActionBarDrawerToggle.
		mDrawerToggle = new ActionBarDrawerToggle(
        	this,
        	mDrawerLayout,
        	icon,
        	R.string.drawer_open,
        	R.string.drawer_close
		);
		mDrawerLayout.setDrawerListener(mDrawerToggle);
        // Enable home button.
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        // Style.
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // View.
        mDrawerView = (ListView) findViewById(viewId);
        // Set flags.
        mCloseOnBack = false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}
 
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    	mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	if (mDrawerToggle.onOptionsItemSelected(item)) {
    		return true;
    	}
    	return super.onOptionsItemSelected(item);
	}

	public void setCloseOnBack(boolean closeOnBack) {
		mCloseOnBack = closeOnBack;
	}

	public void disableDrawer() {
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		mDrawerToggle.setDrawerIndicatorEnabled(false);
	}

	public void enableDrawer() {
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
		mDrawerToggle.setDrawerIndicatorEnabled(true);
	}

	public void hideDrawer() {
		mDrawerLayout.setVisibility(View.GONE);
	}

	public void showDrawer() {
		mDrawerLayout.setVisibility(View.VISIBLE);
	}
	
	public void setTitle(String title) {
		mActionBar.setTitle(title);
	}
	
	public boolean openDrawer() {
		boolean result = false;
		if (!isDrawerOpen()) {
			mDrawerLayout.openDrawer(mDrawerView);
			result = true;
		}
		return result;
	}

	public boolean closeDrawer() {
		boolean result = false;
		if (isDrawerOpen()) {
			mDrawerLayout.closeDrawer(mDrawerView);
			result = true;
		}
		return result;
	}
	
	public boolean isDrawerOpen() {
		return mDrawerLayout.isDrawerOpen(mDrawerView);
	}

	@Override
	public void onBackPressed() {
		if (mCloseOnBack || !closeDrawer()) {
			super.onBackPressed();
		}
	}
}
