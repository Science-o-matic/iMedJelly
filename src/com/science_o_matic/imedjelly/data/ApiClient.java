package com.science_o_matic.imedjelly.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.science_o_matic.imedjelly.R;

public class ApiClient extends Client{
	private Context mContext;
	String uuid;
	String mLanguage;

	public ApiClient(Context context) {
		super(context.getString(R.string.ProdPrefix));
		mContext = context;
		setAuthentication(
			mContext.getString(R.string.ServerUsername),
			mContext.getString(R.string.ServerPassword)
		);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		uuid = preferences.getString("device", null);
		mLanguage = preferences.getString("langCode", null);
	}

	public HttpGet getZones() {
		return getRequest(mServer + "/MEDUSAS/ws/zones");
	}

	public HttpGet getBeaches(int zoneId) {
		return getRequest(mServer + "/MEDUSAS/ws/beaches/locations/zone/" + Integer.toString(zoneId));
	}
	
	public HttpGet getNotifications() {
		String url = mServer + "/MEDUSAS/ws/notification/lang/" + mLanguage + "/device/" + uuid;
		return getRequest(url);
	}
	
	public HttpGet getBeachData(long id) {
		String url = mServer + "/MEDUSAS/ws/beaches/beach/" + String.valueOf(id) +"/lang/" + mLanguage + "/device/" + uuid;
		return getRequest(url);
	}
	
	public HttpGet getPredictions(int zoneId) {
		String url = mServer + "/MEDUSAS/ws/prediction/zones/" + Integer.toString(zoneId) + "/" + mLanguage;
		return getRequest(url);
	}

	public HttpPost postObservation(long id, String jellyFishName, String observations, File image) {
		String url = mServer + "/MEDUSAS/ws/observation/beach/" + String.valueOf(id) + "/deviceId/" + uuid;
		HttpPost post = postRequest(url);
		try {
			MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			entity.addPart("jellyFishName", new StringBody(jellyFishName));
			entity.addPart("observations", new StringBody(observations));
			if (image != null) {
				entity.addPart("image", new FileBody(image));
			}
			post.setEntity(entity);
		}
		catch(Exception e) {}
		return post;
	}
	
	public HttpPost postFeedback(long id, String name, String email, double rating, String comment) {
		String url = mServer + "/MEDUSAS/ws/feedback/beach/" + String.valueOf(id) + "/deviceId/" + uuid + "/lang/" + mLanguage;
		HttpPost post = postRequest(url);
		try {
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("name", name));
			pairs.add(new BasicNameValuePair("email", email));
			if (rating > 0.0) {
				pairs.add(new BasicNameValuePair("rating", String.valueOf((int) rating)));
			}
			pairs.add(new BasicNameValuePair("comment", comment));
			UrlEncodedFormEntity body = new UrlEncodedFormEntity(pairs, "UTF-8");
			post.addHeader("Content-Type", "application/x-www-form-urlencoded");
			post.setEntity(body);
		}
		catch(Exception e) {}
		return post;
	}
	
	public HttpPost postRegistration(String username, String password, String email) {
		String url = mServer + "/MEDUSAS/ws/register/" + "lang/" + mLanguage + "/device/" + uuid;
		HttpPost post = postRequest(url);
		try {
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("deviceFamily", "android"));
			pairs.add(new BasicNameValuePair("appVersion", "1"));
			pairs.add(new BasicNameValuePair("userName", username));
			pairs.add(new BasicNameValuePair("password", password));
			pairs.add(new BasicNameValuePair("email", email));
			
			pairs.add(new BasicNameValuePair("genre", "35"));
			pairs.add(new BasicNameValuePair("wage", "80000"));
			
			UrlEncodedFormEntity body = new UrlEncodedFormEntity(pairs, "UTF-8");
			post.addHeader("Content-Type", "application/x-www-form-urlencoded");
			post.setEntity(body);
		}
		catch(Exception e) {}
		return post;
	}
}
