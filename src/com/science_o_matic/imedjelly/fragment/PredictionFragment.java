package com.science_o_matic.imedjelly.fragment;

import java.util.ArrayList;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.science_o_matic.imedjelly.R;
import com.science_o_matic.imedjelly.activity.DataSource;
import com.science_o_matic.imedjelly.data.Table;

public class PredictionFragment extends Fragment {

	private WebView mWebView;
	private ProgressBar mProgressBar;
	private Activity mActivity;
	private List<ContentValues> mZones;
	private Integer mZoneId;
	private List<ContentValues> mPredictions;
	private List<ContentValues> mPredictionsDay0;
	private List<ContentValues> mPredictionsDay1;
	private List<ContentValues> mPredictionsDay2;
	private List<ContentValues> mJellyfishesToList;
	private ContentValues mJellyfish;
	private RadioGroup mRadioGroupDays;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_prediction, container, false);
		mWebView = (WebView) view.findViewById(R.id.webkit);
		mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);

		/* Load zones */
		mZones = DataSource.getTableItems(mActivity, Table.zone, "predictionAvailable = ?", new String[] { "1" });

		mRadioGroupDays = (RadioGroup) view.findViewById(R.id.radioGroupDays);
		mRadioGroupDays.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (mJellyfish != null) {
					switch (checkedId) {
					case R.id.todayButton:
						mJellyfishesToList = mPredictionsDay0;
						break;

					case R.id.tomorrowButton:
						mJellyfishesToList = mPredictionsDay1;
						break;

					case R.id.aftertomorrowButton:
						mJellyfishesToList = mPredictionsDay2;
						break;

					default:
						break;
					}
					showPredictionDialog(getResources());
				}}
		});

		configureParameters();
		return view;
	}

	private void configureParameters() {
		Resources res = getResources();
		if (mZoneId == null) {
			showZoneDialog(res);
		} else if (mJellyfish == null) {
			collectPredictionsByDay();
			configureDayButtons();
			showPredictionDialog(res);
		} else {
			configurePredictionMap(mJellyfish);
		}
	}

	public void showPredictionDialog(Resources res) {
		int numPredictions = mJellyfishesToList.size();
		String[] names = new String[numPredictions];
		int current = 0;
		for (ContentValues prediction : mJellyfishesToList) {
			names[current] = prediction.getAsString("jellyFishName");
			current++;
		}
		// Show dialog.
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		builder.setTitle(res.getString(R.string.select_jellyfish));
		builder.setItems(names, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mJellyfish = mJellyfishesToList.get(which);
				configureParameters();
			}
		});
		builder.show();
	}

	private void configureDayButtons() {
		RadioButton button = (RadioButton) mRadioGroupDays.getChildAt(0);
		if(!mPredictionsDay0.isEmpty()) {
			mJellyfishesToList = mPredictionsDay0;
			button.setEnabled(true);
			button.setChecked(true);
		} else {
			button.setEnabled(false);
		}
		
		button = (RadioButton) mRadioGroupDays.getChildAt(1);
		if(!mPredictionsDay1.isEmpty()) {
			button.setEnabled(true);
			if(mJellyfishesToList == null) {
				mJellyfishesToList = mPredictionsDay1;
				button.setChecked(true);
			}
		} else {
			button.setEnabled(false);
		}
		
		button = (RadioButton) mRadioGroupDays.getChildAt(2);
		if(!mPredictionsDay2.isEmpty()) {
			button.setEnabled(true);
			if(mJellyfishesToList == null) {
				mJellyfishesToList = mPredictionsDay2;
				button.setChecked(true);
			}
		} else {
			button.setEnabled(false);
		}
	}

	private void collectPredictionsByDay() {
		mJellyfishesToList = null;
		mPredictionsDay0 = new ArrayList<ContentValues>();
		mPredictionsDay1 = new ArrayList<ContentValues>();
		mPredictionsDay2 = new ArrayList<ContentValues>();

		DataSource source = new DataSource(getActivity(), Table.prediction);
		source.openRead();
		mPredictions = source.getDistinctItems(new String[] { "day", "jellyFishName", "url" }, "zoneId=?",
				new String[] { Integer.toString(mZoneId) }, "day, jellyFishName");
		source.close();

		for (ContentValues prediction : mPredictions) {
			String predictionDay = prediction.getAsString("day");
			if(predictionDay.equals("day-0")){
				mPredictionsDay0.add(prediction); 
			} else if (predictionDay.equals("day-1")) {
				mPredictionsDay1.add(prediction);
			} else {
				mPredictionsDay2.add(prediction);
			}
		}
	}

	private void showZoneDialog(Resources res) {
		// Get zones names.
		int numZones = mZones.size();
		String[] names = new String[numZones];
		int current = 0;
		for (ContentValues zone : mZones) {
			names[current] = zone.getAsString("name");
			current++;
		}
		// Show dialog.
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		builder.setTitle(res.getString(R.string.select_zone));
		builder.setItems(names, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ContentValues zone = mZones.get(which);
				mZoneId = zone.getAsInteger("zoneId");
				configureParameters();
			}
		});
		builder.show();
	}

	public void showZoneDialog() {
		Resources res = getResources();
		mJellyfish = null;
		showZoneDialog(res);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void configurePredictionMap(ContentValues prediction) {
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		mWebView.getSettings().setJavaScriptEnabled(Boolean.TRUE);
		mWebView.getSettings().setBuiltInZoomControls(Boolean.TRUE);
		mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebView.getSettings().setUseWideViewPort(true);
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
		mActivity = activity;
		super.onAttach(activity);
	}
}