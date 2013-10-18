package com.science_o_matic.imedjelly.activity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.science_o_matic.imedjelly.R;
import com.science_o_matic.imedjelly.data.ApiRequest;
import com.science_o_matic.imedjelly.data.BeachesDataSource;

public class SplashActivity extends Activity {
	private static final String TAG = "SplashActivity";

	private BeachesLoader mLoader = null;

	protected void setLanguage(SharedPreferences.Editor editor) {
		String langCode = Locale.getDefault().getISO3Language();
		if (langCode == "en"){
			langCode = "en";
		}
		else if (langCode == "cat" || langCode == "ca"){
			langCode = "ca";
		}
		else if (langCode == "es"){
			langCode = "es";
		}
		else{
			langCode = "es";
		}
		editor.putString("langCode", langCode);
	}

	protected void setDeviceId(SharedPreferences.Editor editor) {
		TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		String uuid = tManager.getDeviceId();
		editor.putString("device", uuid);
	}
	
	protected void setNewUser(SharedPreferences.Editor editor) {
		editor.putBoolean("newUser", true);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		// Set preferences.
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		if(!preferences.getBoolean("firstTime", false)) {
			setDeviceId(editor);
			editor.putBoolean("firstTime", true);
		}
		setLanguage(editor);
		setNewUser(editor);
		editor.commit();
		// Load beaches.
		mLoader = new BeachesLoader(SplashActivity.this);
		mLoader.execute();
	}
	
	public class NotificationLoader extends AsyncTask<Void, Integer, String>{
		private Context mContext = null;
		private ApiRequest mApi = null;
		private HttpGet mRequest = null;
		private String mNotification = null;

		public NotificationLoader(Context context) {
			this.mContext = context;
			this.mApi = new ApiRequest(mContext);
			this.mRequest = mApi.getRequest(mApi.getNotificationURL());
		}
		
		public String parseNotificationJson(JsonReader reader) throws IOException{
			String result = null;
			reader.beginObject();
			while (reader.hasNext()) {
				String root = reader.nextName();
				if (root.equals("notification") && reader.peek() != JsonToken.NULL) {
					result = reader.nextString();
				}
				else {
					reader.skipValue();
				}
			}
			// End transaction.
			reader.endObject();
			return result;
		}
		
		@Override
		protected String doInBackground(Void... voids) {
			Log.d(TAG, "doInBackground");
			try {
				// Perform request.
				HttpResponse response = mApi.performRequest(mRequest);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200) {
					// Get body.
					HttpEntity entity = response.getEntity();
					InputStream inStream = entity.getContent();
					BufferedInputStream bufferedStream = new BufferedInputStream(inStream);
					InputStreamReader streamReader = new InputStreamReader(bufferedStream, "UTF-8");
					// Parse JSON.
					JsonReader reader = new JsonReader(streamReader);
					try {
						mNotification = parseNotificationJson(reader);
					}
					finally {
						reader.close();
					}
				}
				else if (statusCode != 200) {
					String code = String.valueOf(statusCode);
					Log.d(TAG, "Unable to retrieve data: " + code);
					Toast.makeText(mContext, code, Toast.LENGTH_SHORT).show();
				}
			}
			catch (Exception e) {
				Log.d(TAG, e.getLocalizedMessage());
			}
			return null;
		}

		@Override
		protected void onPostExecute(String results){
			Intent intent = new Intent(mContext, TabFragmentActivity.class);
			if(mNotification != null) {
				intent.putExtra("notification", mNotification);
			}
			Log.d(TAG, "onPostExecute");
			startActivity(intent);
			finish();
		}
	}
	
	public class BeachesLoader extends AsyncTask<Void, Integer, String>{
		private Context mContext = null;
		private ApiRequest mApi = null;
		private HttpGet mRequest = null;
		
		private NotificationLoader mLoader = null;

		public BeachesLoader(Context context) {
			this.mContext = context;
			this.mApi = new ApiRequest(mContext);
			this.mRequest = mApi.getRequest(mApi.getAllBeachesServiceURL());
		}
		
		@Override
		protected String doInBackground(Void... voids) {
			Log.d(TAG, "doInBackground");
			try {
				// Perform request.
				HttpResponse response = mApi.performRequest(mRequest);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200) {
					// Get body.
					HttpEntity entity = response.getEntity();
					InputStream inStream = entity.getContent();
					BufferedInputStream bufferedStream = new BufferedInputStream(inStream);
					InputStreamReader streamReader = new InputStreamReader(bufferedStream, "UTF-8");
					// Parse JSON.
					JsonReader reader = new JsonReader(streamReader);
					try {
						BeachesDataSource dataSource = new BeachesDataSource(mContext);
						dataSource.parseBeachesJson(reader);
					}
					finally {
						reader.close();
					}
				}
				else if (statusCode != 200) {
					String code = String.valueOf(statusCode);
					Log.d(TAG, "Unable to retrieve data: " + code);
					Toast.makeText(mContext, code, Toast.LENGTH_SHORT).show();
				}
			}
			catch (Exception e) {
				Log.d(TAG, e.getLocalizedMessage());
			}
			return null;
		}

		@Override
		protected void onPostExecute(String results){
			// Load notification.
			mLoader = new NotificationLoader(SplashActivity.this);
			mLoader.execute();
		}
	}
}