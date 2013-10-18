package com.science_o_matic.imedjelly.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.science_o_matic.imedjelly.R;
import com.science_o_matic.imedjelly.activity.TreatmentActivity;
import com.science_o_matic.imedjelly.adapter.StringListAdapter;
import com.science_o_matic.imedjelly.application.MainApplication;

public class TreatmentFragment extends ListFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_treatment, container, false);
        return view;
	}
	
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();
        if (activity != null) {
        	View header = activity.getLayoutInflater().inflate(R.layout.fragment_treatment_header, null);
        	final ListView view = (ListView) activity.findViewById(android.R.id.list);
        	view.addHeaderView(header);
        	ListAdapter listAdapter = new StringListAdapter(activity, getTreatments());
        	view.setAdapter(listAdapter);
        }
    }
    
    public List<String> getTreatments() {
    	List<String> treatments = new ArrayList<String>();
    	for(int i = 0; i < MainApplication.sTreatments.length; i++) {
    		treatments.add(getString(MainApplication.sTreatments[i]));
    	}
    	return treatments;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
        	if(position > 0) {
	            // Start treatment activity.
	            Context context = activity.getBaseContext();
	        	Intent intent = new Intent(context, TreatmentActivity.class)
	        		.putExtra("id", position-1);
	        	startActivity(intent);
        	}
        }
    }
}
