package com.science_o_matic.imedjelly.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.science_o_matic.imedjelly.R;
import com.science_o_matic.imedjelly.activity.JellyFishActivity;
import com.science_o_matic.imedjelly.adapter.JellyFishesGridAdapter;
import com.science_o_matic.imedjelly.application.MainApplication;
import com.science_o_matic.imedjelly.data.JellyFish;

public class JellyFishesFragment extends Fragment {
	private GridView mGridView;
    private JellyFishesGridAdapter mGridAdapter;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_jellyfishes, container, false);
        // Store a pointer to the GridView that powers this grid fragment.
        mGridView = (GridView) view.findViewById(R.id.grid_view);
        return view;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();
        if (activity != null) {
            mGridAdapter = new JellyFishesGridAdapter(activity, MainApplication.sJellyFishes);
            if (mGridView != null) {
                mGridView.setAdapter(mGridAdapter);
            }
            // Setup our onItemClickListener.
            mGridView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    onGridItemClick((GridView) parent, view, position, id);
                }
            });
        }
    }
    
    public void onGridItemClick(GridView g, View v, int position, long id) {
        Activity activity = getActivity();
        if (activity != null) {
        	JellyFish jellyfish = (JellyFish) mGridAdapter.getItem(position);
        	// Start jellyfish activity.
        	Context context = activity.getBaseContext();
        	Intent intent = new Intent(context, JellyFishActivity.class)
        		.putExtra("id", jellyfish.getId());
        	startActivity(intent);
        }
    } 
}
