package com.science_o_matic.imedjelly.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.science_o_matic.imedjelly.R;

public class PredictionFragment extends Fragment {
	
	private WebView mWebView;
	private ProgressBar mProgressBar;
	private Activity mActivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_prediction, container, false);
		mWebView = (WebView) view.findViewById(R.id.webkit);
		mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);
		showDayDialog();
		return view;
	}

	public void showDayDialog() {
		Resources res = getResources();
		// Get zones names.
//		int numZones = mZones.size();
		final String[] days = {"Hoy", "Mañana", "Pasado mañana"};
//		int current = 0;
//		for (ContentValues zone: mZones) {
//			names[current] = zone.getAsString("name");
//			current ++;
//		}
		// Show dialog.
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		builder.setTitle(res.getString(R.string.select_day));
    	builder.setItems(days, new DialogInterface.OnClickListener() {
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    	    	Toast.makeText(mActivity, days[which], Toast.LENGTH_LONG).show();
    	    	showPredictionMap(which);
//    	    	ContentValues zone = mZones.get(which);
//    	    	String zoneCode = zone.getAsString("code");
//    	    	setZoneCode(zoneCode);
//    	    	ZoneConfig config = searchZoneConfig(zoneCode);
//    	    	if (config != null) {
//    	    		moveMap(config);
//    	    	}
//    	    	configureBeaches(zone.getAsInteger("zoneId"));
    	    }

    	});
    	builder.show();
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void showPredictionMap(int which) {
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		mWebView.getSettings().setJavaScriptEnabled(Boolean.TRUE);
		mWebView.getSettings().setBuiltInZoomControls(Boolean.TRUE);
		mWebView.getSettings().setLoadWithOverviewMode(true); 
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.loadUrl("http://ygneo.cartodb.com/viz/265316b4-bc14-11e4-8c7f-0e018d66dc29/embed_map");
		mWebView.setWebViewClient(new WebViewClient(){
			/* evita que los enlaces se abran fuera nuestra app en el navegador de android */
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}
		});
	}
	
	@Override
	public void onAttach(Activity activity) {
		mActivity  = activity;
		super.onAttach(activity);
	}
}
