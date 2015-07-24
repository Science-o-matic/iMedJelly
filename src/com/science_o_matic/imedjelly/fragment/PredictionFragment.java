package com.science_o_matic.imedjelly.fragment;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.science_o_matic.imedjelly.R;
import com.science_o_matic.imedjelly.activity.DataSource;
import com.science_o_matic.imedjelly.data.Table;

public class PredictionFragment extends Fragment {
	
	private WebView mWebView;
	private ProgressBar mProgressBar;
	private Activity mActivity;
	private List<ContentValues> mZones;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_prediction, container, false);
		mWebView = (WebView) view.findViewById(R.id.webkit);
		mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);
		
		/* Load zones */
		mZones = DataSource.getTableItems(mActivity, Table.zone);
		
		showDayDialog();
		return view;
	}

	private ContentValues searchZone(String code) {
		for (ContentValues zone: mZones) {
			if (zone.getAsString("code").equals(code)) {
				return zone;
			}
		}
		return null;
	}
	public void showDayDialog() {
		Resources res = getResources();
		final String[] days = res.getStringArray(R.array.days_array);

		/* Show dialog */
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		builder.setTitle(res.getString(R.string.select_day));
    	builder.setItems(days, new DialogInterface.OnClickListener() {
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    	    	Toast.makeText(mActivity, days[which], Toast.LENGTH_LONG).show();
    	    	
    	    	// TODO: Por ahora solo funciona para BCN y para Pelagia noctiluca
    	    	ContentValues zone = searchZone("BCN");
    	    	configurePredictionMap(which, zone.getAsInteger("zoneId"), "Pelagia noctiluca");
    	    }
    	});
    	builder.show();
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void configurePredictionMap(int dayIndex, Integer zoneId, String jellyFishName) {
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		mWebView.getSettings().setJavaScriptEnabled(Boolean.TRUE);
		mWebView.getSettings().setBuiltInZoomControls(Boolean.TRUE);
		mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebView.getSettings().setUseWideViewPort(true);

		DataSource source = new DataSource(getActivity(), Table.prediction);
		source.openRead();

		ContentValues prediction = source.getItem("zoneId=? and day=? and jellyFishName=?",
				new String[] { Integer.toString(zoneId), "day-" + dayIndex, jellyFishName });
		source.close();

		mWebView.loadUrl(prediction.getAsString("url"));
		mWebView.setWebViewClient(new WebViewClient() {
			/*
			 * evita que los enlaces se abran fuera nuestra app en el navegador
			 * de android
			 */
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}
		});
		
		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int progress) {
				mProgressBar.setProgress(0);
				mProgressBar.setVisibility(View.VISIBLE);
				mActivity.setProgress(progress * 1000);
				mProgressBar.incrementProgressBy(progress);
				if (progress == 100) {
					mProgressBar.setVisibility(View.GONE);
				}
			}
		});
	}
	
	@Override
	public void onAttach(Activity activity) {
		mActivity  = activity;
		super.onAttach(activity);
	}
}