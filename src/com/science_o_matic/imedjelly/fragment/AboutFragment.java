package com.science_o_matic.imedjelly.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.science_o_matic.imedjelly.R;

public class AboutFragment extends Fragment {
	private Button button;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about, container, false);
        button = (Button) view.findViewById(R.id.button_call);
        button.setOnClickListener(new OnClickListener() {
        	@Override
			public void onClick(View v) {
        		String number = getString(R.string.about_phone_number);
        		number = number.replaceAll(" ","");
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:"+number));
				startActivity(callIntent);
			}
        });
        return view;
    }
	
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();
        if (activity != null) {
        }
    }
}
