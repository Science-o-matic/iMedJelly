package com.science_o_matic.imedjelly.fragment;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

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
import com.science_o_matic.imedjelly.data.Beach;
import com.science_o_matic.imedjelly.data.BeachesDataSource;

public class BeachesMapFragment extends SupportMapFragment {
	private GoogleMap mMap;

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
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View view = super.onCreateView(inflater, container, savedInstanceState);
    	setMapTransparent((ViewGroup) view);
		// Get map.
    	mMap = getMap();
        configureMap(mMap);
        return view;
    }
    
    public void configureMap(GoogleMap map) {
    	// Configure map.
        Resources res = getResources();
        float latitude = Float.parseFloat(res.getString(R.string.position_latitude));
        float longitude = Float.parseFloat(res.getString(R.string.position_longitude));
        LatLng latlng = new LatLng(latitude, longitude);
        float startZoom = Float.parseFloat(res.getString(R.string.startZoom));
        float endZoom = Float.parseFloat(res.getString(R.string.endZoom));
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMyLocationEnabled(true);
        map.getUiSettings().setAllGesturesEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, startZoom));
        map.animateCamera(CameraUpdateFactory.zoomTo(endZoom), 2000, null);
        // Configure beaches.
        configureBeaches(map);
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
    
    private void configureBeaches(GoogleMap map) {
    	BeachesDataSource dataSource = new BeachesDataSource(getActivity());
    	dataSource.open();
    	List<Beach> beaches = dataSource.getAllBeaches();
    	dataSource.close();
    	for(Beach b: beaches) {
    		int icon = getStatusDrawable(b.getJellyfish_Status());
    		map.addMarker(new MarkerOptions()
    			.position(new LatLng(b.getLatitude(), b.getLongitude()))
    			.icon(BitmapDescriptorFactory.fromResource(icon))
    			.snippet(String.valueOf(b.getApi_Id()))
    			.title(b.getName()));
    		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
    			public void onInfoWindowClick(Marker marker) {
    				// Start beach activity.
    				long api_id = Long.parseLong(marker.getSnippet());
                	Context context = getActivity().getBaseContext();
                	Intent intent = new Intent(context, BeachActivity.class)
                		.putExtra("api_id", api_id);
                	startActivity(intent);
    			}
    		});
    	}
    }
}
