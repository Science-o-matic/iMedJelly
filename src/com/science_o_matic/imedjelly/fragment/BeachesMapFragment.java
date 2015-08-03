package com.science_o_matic.imedjelly.fragment;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.science_o_matic.imedjelly.R;
import com.science_o_matic.imedjelly.activity.BeachActivity;
import com.science_o_matic.imedjelly.activity.DataSource;
import com.science_o_matic.imedjelly.data.Table;

public class BeachesMapFragment extends SupportMapFragment {
	private Activity mActivity;
	private GoogleMap mMap;
	private List<ContentValues> mZones;
	private GoogleMap.OnInfoWindowClickListener mMapListener;
	private Marker mMarker;
	
	class ZoneConfig {
		String mCode;
		float mLatitude;
		float mLongitude;
		float mStartZoom;
		float mEndZoom;
		
		public ZoneConfig(String code, float latitude, float longitude, float startZoom, float endZoom) {
			mCode = code;
			mLatitude = latitude;
			mLongitude = longitude;
			mStartZoom = startZoom;
			mEndZoom = endZoom;
		}
	}
	
	ZoneConfig[] mZoneConfig = {
		new ZoneConfig("BLA", 39.8867539f, 2.9066162f, 4.0f, 7.0f),
		new ZoneConfig("BCN", 41.39479f, 2.1487679f, 4.0f, 7.0f),
		new ZoneConfig("TNZ", 33.7931605f, 9.5607654f, 4.0f, 6.0f),
	};

	protected String getZoneCode() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
		return preferences.getString("zoneCode", null);
	}
	
	protected void setZoneCode(String code) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("zoneCode", code);
		editor.commit();
	}

	private void setMapTransparent(ViewGroup group) {
		int childCount = group.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View child = group.getChildAt(i);
		        if (child instanceof ViewGroup) {
		            setMapTransparent((ViewGroup) child);
		        } else if (child instanceof SurfaceView) {
		            child.setBackgroundColor(0x00000000);
		            group.requestTransparentRegion(child);
	        }
	    }
	}
	
	private ContentValues searchZone(String code) {
		for (ContentValues zone: mZones) {
			if (zone.getAsString("code").equals(code)) {
				return zone;
			}
		}
		return null;
	}
	
	private ZoneConfig searchZoneConfig(String code) {
		for (ZoneConfig config: mZoneConfig) {
			if (config.mCode.equals(code)) {
				return config;
			}
		}
		return null;
	}

	public void showZoneDialog() {
		Resources res = getResources();
		// Get zones names.
		int numZones = mZones.size();
		String[] names = new String[numZones];
		int current = 0;
		for (ContentValues zone: mZones) {
			names[current] = zone.getAsString("name");
			current ++;
		}
		// Show dialog.
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		builder.setTitle(res.getString(R.string.select_zone));
    	builder.setItems(names, new DialogInterface.OnClickListener() {
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    	    	ContentValues zone = mZones.get(which);
    	    	String zoneCode = zone.getAsString("code");
    	    	setZoneCode(zoneCode);
    	    	ZoneConfig config = searchZoneConfig(zoneCode);
    	    	if (config != null) {
    	    		moveMap(config);
    	    	}
    	    	configureBeaches(zone.getAsInteger("zoneId"));
    	    }
    	});
    	builder.show();
	}
	
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	// Create map and overlay layout.
    	View mapView = super.onCreateView(inflater, container, savedInstanceState);
    	RelativeLayout view = new RelativeLayout(mActivity);
    	view.addView(mapView, new RelativeLayout.LayoutParams(-1, -1));
    	View overlayView = inflater.inflate(R.layout.map_overlay, container, false);
    	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
    		LinearLayout.LayoutParams.WRAP_CONTENT,
    		LinearLayout.LayoutParams.WRAP_CONTENT
    	);
    	params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
    	params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
    	view.addView(overlayView, params);
    	setMapTransparent((ViewGroup) view);
    	// Get map.
    	mMap = getMap();
    	mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.setInfoWindowAdapter(null);
        // Listener.
        mMapListener = new OnInfoWindowClickListener() {
    		public void onInfoWindowClick(Marker marker) {
    			// Start beach activity.
    			mMarker = marker;
    			long id = Long.parseLong(marker.getSnippet());
            	Context context = getActivity().getBaseContext();
            	Intent intent = new Intent(context, BeachActivity.class)
            		.putExtra("id", id);
            	startActivity(intent);
    		}
    	};
    	// Select zone.
		mZones = DataSource.getTableItems(mActivity, Table.zone);
		
		// Get current zone.
		String zoneCode = getZoneCode();
		if (zoneCode == null) {
			showZoneDialog();
		} else {
			ContentValues zone = searchZone(zoneCode);
			ZoneConfig config = searchZoneConfig(zoneCode);
			if (config != null) {
				moveMap(config);
			}
			configureBeaches(zone.getAsInteger("zoneId"));
		}

        return view;
    }
    
    public void moveMap(ZoneConfig config) {
    	// Configure map.
        LatLng latlng = new LatLng(config.mLatitude, config.mLongitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, config.mStartZoom));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(config.mEndZoom), 2000, null);
    }

    private int getStatusDrawable(String status) {
    	if (status.equals("NO_WARNING")){
    		return R.drawable.pingreen;
	  	}
	  	else if (status.equals("LOW_WARNING")){
	  		return R.drawable.pingreen;
	  	}
	  	else if (status.equals("HIGH_WARNING")){
	  		return R.drawable.pinyellow;
	  	}
	  	else if (status.equals("VERY_HIGH_WARNING")){
	  		return R.drawable.pinred;
	  	}
	  	else{
	  		return R.drawable.pingray;
	  	}
    }
    
	@Override
	public void onResume() {
		if (mMarker != null) {
			DataSource dataSource = new DataSource(mActivity, Table.beach);
			dataSource.openRead();
			ContentValues beach = dataSource.getItem("id = ?", new String[] { mMarker.getSnippet() });
			dataSource.close();
			mMarker.remove();
			int icon = getStatusDrawable(beach.getAsString("jellyFishStatus"));
			mMap.addMarker(new MarkerOptions()
					.position(new LatLng(beach.getAsDouble("latitude"), beach.getAsDouble("longitude")))
					.icon(BitmapDescriptorFactory.fromResource(icon)).snippet(String.valueOf(beach.getAsInteger("id")))
					.title(beach.getAsString("name")));
		}
		super.onResume();
	}

	private void configureBeaches(int zoneId) {
    	mMap.clear();
    	DataSource source = new DataSource(getActivity(), Table.beach);
    	source.openRead();
    	List<ContentValues> beaches = source.getItems("zoneId=?", new String[]{Integer.toString(zoneId)});
    	source.close();
    	for(ContentValues b: beaches) {
    		int icon = getStatusDrawable(b.getAsString("jellyFishStatus"));
    		mMap.addMarker(new MarkerOptions()
    			.position(new LatLng(b.getAsDouble("latitude"), b.getAsDouble("longitude")))
    			.icon(BitmapDescriptorFactory.fromResource(icon))
    			.snippet(String.valueOf(b.getAsInteger("id")))
    			.title(b.getAsString("name")));
    		mMap.setOnInfoWindowClickListener(mMapListener);
    	}
    }
}
