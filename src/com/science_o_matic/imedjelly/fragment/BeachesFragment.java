package com.science_o_matic.imedjelly.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.science_o_matic.imedjelly.R;
import com.science_o_matic.imedjelly.activity.BeachActivity;
import com.science_o_matic.imedjelly.activity.DataSource;
import com.science_o_matic.imedjelly.adapter.GenericListAdapter;
import com.science_o_matic.imedjelly.data.Table;

public class BeachesFragment extends ListFragment {
	private Context mContext;
	private int mZoneId = -1;
	private String mMunicipality = null;
	private OnItemSelectedListener mListener;
	private int mTitleResource;

	public BeachesFragment(OnItemSelectedListener listener) {
		mZoneId = -1;
		mMunicipality = null;
		mListener = listener;
		mTitleResource = R.string.geo_zone;
	}

	public BeachesFragment(OnItemSelectedListener listener, int zoneId) {
		mZoneId = zoneId;
		mMunicipality = null;
		mListener = listener;
		mTitleResource = R.string.geo_municipality;
	}
	
	public BeachesFragment(OnItemSelectedListener listener, int zoneId, String municipality) {
		mZoneId = zoneId;
		mMunicipality = municipality;
		mListener = listener;
		mTitleResource = R.string.geo_beach;
	}
	
	public void setZone(int zoneId) {
		mZoneId = zoneId;
	}

	public void setMunicipality(String municipality) {
		mMunicipality = municipality;
	}

	public int getZone() {
		return mZoneId;
	}
	
	public String getMunicipality() {
		return mMunicipality;
	}
	
	public int getTitleResource() {
		return mTitleResource;
	}
	
	public List<ContentValues> getMunicipalities() {
		List<ContentValues> result = new ArrayList<ContentValues>();
    	DataSource source = new DataSource(getActivity().getApplicationContext(), Table.beach);
    	source.openRead();
    	Cursor cursor = source.getDistinctCursor(
    		new String[]{"municipalityName"},
    		"zoneId=?",
    		new String[]{Integer.toString(mZoneId)},
    		"municipalityName"
    	);
    	cursor.moveToFirst();
    	while (!cursor.isAfterLast()) {
    		ContentValues values = new ContentValues();
			DatabaseUtils.cursorRowToContentValues(cursor, values);
    		result.add(values);
    		cursor.moveToNext();
    	}
    	source.close();
    	return result;
	}
	
	private void setJellyFishStatus(ImageView view, String status) {
		Resources res = mContext.getResources();
		if (status.equals("NO_WARNING")) {
			view.setImageResource(R.drawable.no_warning_small);
			view.setContentDescription(res.getString(R.string.no_warning));
		}
		else if (status.equals("LOW_WARNING")) {
			view.setImageResource(R.drawable.low_warning_small);
			view.setContentDescription(res.getString(R.string.low_warning));
		}
		else if (status.equals("HIGH_WARNING")) {
			view.setImageResource(R.drawable.high_warning_small);
			view.setContentDescription(res.getString(R.string.high_warning));
		}
		else if (status.equals("VERY_HIGH_WARNING")) {
			view.setImageResource(R.drawable.very_high_warning_small);
			view.setContentDescription(res.getString(R.string.very_high_warning));
		}
	}

	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();
        mContext = activity.getApplicationContext();
        GenericListAdapter<ContentValues> listAdapter;
        if (mZoneId == -1) {
	        // Zones list.
	        List<ContentValues> zones = DataSource.getTableItems(mContext, Table.zone);
	        listAdapter = new GenericListAdapter<ContentValues>(
	         		activity,
	         		zones,
	         		R.layout.item_string_list,
	         		new int[] {R.id.list_label}
	         	) {
	         		@Override
	         		public void setView(ContentValues values, View[] views) {
	         			((TextView) views[0]).setText(values.getAsString("name"));
	         		}
	         	};
        }
        else if (mMunicipality == null) {
        	// Municipality list.
        	List<ContentValues> municipalities = getMunicipalities();
	        listAdapter = new GenericListAdapter<ContentValues>(
	         		activity,
	         		municipalities,
	         		R.layout.item_string_list,
	         		new int[] {R.id.list_label}
         	) {
         		@Override
         		public void setView(ContentValues values, View[] views) {
         			((TextView) views[0]).setText(values.getAsString("municipalityName"));
         		}
         	};
        }
        else {
        	// Beaches list.
        	List<ContentValues> beaches = DataSource.getTableItems(mContext, Table.beach, "municipalityName=?", new String[]{mMunicipality});
	        listAdapter = new GenericListAdapter<ContentValues>(
	         		activity,
	         		beaches,
	         		R.layout.item_beach_list,
	         		new int[] {R.id.list_label, R.id.jellyfish_status}
         	) {
         		@Override
         		public void setView(ContentValues values, View[] views) {
         			((TextView) views[0]).setText(values.getAsString("name"));
         			setJellyFishStatus(((ImageView) views[1]), values.getAsString("jellyFishStatus")); 
         		}
         	};
        }
        setListAdapter(listAdapter);
    }

	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
		mListener.onItemSelected(l, v, position, id);
    }
	
	public BeachesFragment selectItem(int position) {
		BeachesFragment fragment = null;
		ListAdapter listAdapter = getListAdapter();
	    if (mZoneId == -1) {
	    	ContentValues zone = (ContentValues) listAdapter.getItem(position);
	    	fragment = new BeachesFragment(mListener, zone.getAsInteger("zoneId"));
	    }
	    else if (mMunicipality == null) {
	    	ContentValues municipality = (ContentValues) listAdapter.getItem(position);
	    	fragment = new BeachesFragment(mListener, mZoneId, municipality.getAsString("municipalityName"));
	    }
	    else {
	    	ContentValues beach = (ContentValues) listAdapter.getItem(position);
	    	long id = beach.getAsInteger("id");
	    	Intent intent = new Intent(mContext, BeachActivity.class)
	    		.putExtra("id", id);
	    	startActivity(intent);
	    }
	    return fragment;
	}

	public BeachesFragment unselectItem() {
		BeachesFragment fragment = null;
		if (mMunicipality != null) {
			fragment = new BeachesFragment(mListener, mZoneId);
		}
		else if (mZoneId != -1) {
			fragment = new BeachesFragment(mListener);
		}
		return fragment;
	}
}
