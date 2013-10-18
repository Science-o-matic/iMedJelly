package com.science_o_matic.imedjelly.fragment;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.science_o_matic.imedjelly.R;
import com.science_o_matic.imedjelly.activity.BeachActivity;
import com.science_o_matic.imedjelly.adapter.BeachListAdapter;
import com.science_o_matic.imedjelly.adapter.StringListAdapter;
import com.science_o_matic.imedjelly.data.Beach;
import com.science_o_matic.imedjelly.data.BeachesDataSource;

public class BeachesFragment extends ListFragment {
	private String mMunicipality = null;

	public void setMunicipality(String municipality) {
		mMunicipality = municipality;
	}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();
        if (activity != null) {
        	BeachesDataSource dataSource = new BeachesDataSource(activity);
        	dataSource.open();
        	ListAdapter listAdapter = null;
        	// Create municipalities list.
        	if(mMunicipality == null) {
        		List<String> municipalities = dataSource.getAllMunicipalities();
        		listAdapter = new StringListAdapter(activity, municipalities);
        	}
        	// Create beaches list.
        	else {
        		List<Beach> beaches = dataSource.getMunicipalityBeaches(mMunicipality);
        		listAdapter = new BeachListAdapter(activity, beaches);
        	}
        	dataSource.close();
            setListAdapter(listAdapter);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        FragmentActivity activity = getActivity();
        if (activity != null) {   
            ListAdapter listAdapter = getListAdapter();
            if(mMunicipality == null) {
            	// Create beaches new fragment.
            	BeachesFragment fragment = new BeachesFragment();
	            String municipality = (String) listAdapter.getItem(position);
	            fragment.setMunicipality(municipality);
	            // Change to display municipality beaches.
	            FragmentManager manager = activity.getSupportFragmentManager();
	            manager.beginTransaction()
	            	.replace(R.id.tabcontent, fragment, null)
	            	.addToBackStack(null)
	            	.commit();
            }
            else {
            	// Start beach activity.
            	Beach beach = (Beach) listAdapter.getItem(position);
            	Context context = activity.getBaseContext();
            	Intent intent = new Intent(context, BeachActivity.class)
            		.putExtra("api_id", beach.getApi_Id());
            	startActivity(intent);
            }
        }
    }
}
