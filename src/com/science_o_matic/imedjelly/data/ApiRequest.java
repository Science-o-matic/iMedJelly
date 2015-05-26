package com.science_o_matic.imedjelly.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.science_o_matic.imedjelly.R;

public class ApiRequest {
	private Context mContext = null;
	private String mUsername = null;
	private String mPassword = null;
	private String mServerPrefix = null;

	public ApiRequest(Context context) {
		mContext = context;
		mUsername = context.getString(R.string.ServerUsername); 
		mPassword = context.getString(R.string.ServerPassword);
		//mServerPrefix = mContext.getResources().getString(R.string.DevPrefix);
		mServerPrefix = mContext.getResources().getString(R.string.ProdPrefix);
	}
	
	public HttpGet getRequest(String url) {
		// Set authentication.
		String base64EncodedCredentials = Base64.encodeToString(
				(mUsername + ":" + mPassword).getBytes(), (Base64.DEFAULT));
		base64EncodedCredentials = base64EncodedCredentials.trim();
		// Return request.
		HttpGet get = new HttpGet(url);
		get.addHeader("Authorization", "Basic " + base64EncodedCredentials);
		return get;
	}

	public HttpPost postRequest(String url) {
		// Set authentication.
		String base64EncodedCredentials = Base64.encodeToString(
				(mUsername + ":" + mPassword).getBytes(), (Base64.DEFAULT));
		base64EncodedCredentials = base64EncodedCredentials.trim();
		// Return request.
		HttpPost post = new HttpPost(url);
		post.addHeader("Authorization", "Basic " + base64EncodedCredentials);
		return post;
	}

	public HttpResponse performRequest(HttpUriRequest request) {
		HttpResponse response = null;
		HttpClient httpclient = new DefaultHttpClient();
		try {
			response = httpclient.execute(request);
		}
		catch(Exception e) 	{}
		return response;
	}

	public String getAllBeachesServiceURL() {
		return mServerPrefix + mContext.getString(R.string.AllBeachesService);
	}
	
	public String getBeachServiceURL(long api_id) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		String uuid = preferences.getString("device", null);
		String language = preferences.getString("langCode", null);
		return mServerPrefix
				+ mContext.getString(R.string.BeachService)
				+ String.valueOf(api_id) 
				+ "/lang/" + language + "/device/" + uuid;
	}
	
	public String getFeedbackServiceURL(long api_id) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		String uuid = preferences.getString("device", null);
		String language = preferences.getString("langCode", null);
		return mServerPrefix
				+ mContext.getString(R.string.FeedbackService)
				+ String.valueOf(api_id) 
				+ "/deviceId/" + uuid + "/lang/" + language;
	}
	
	public String getObservationServiceURL(long api_id) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		String uuid = preferences.getString("device", null);
		return mServerPrefix
				+ mContext.getString(R.string.ObservationService)
				+ String.valueOf(api_id) 
				+ "/deviceId/" + uuid;
	}
	
	public String getNotificationURL() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		String uuid = preferences.getString("device", null);
		String language = preferences.getString("langCode", null);
		return mServerPrefix
				+ mContext.getString(R.string.NotificationService)
				+ "lang/" + language + "/device/" + uuid;
	}
	
	public String getRegisterURL() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		String uuid = preferences.getString("device", null);
		String language = preferences.getString("langCode", null);
		return mServerPrefix
				+ mContext.getString(R.string.RegisterService)
				+ "lang/" + language + "/device/" + uuid;
	}
	
	public HttpPost makeFeedback(long api_id, String name, String email,
			double rating, String comment) {
		String url = getFeedbackServiceURL(api_id);
		HttpPost post = postRequest(url);
		try {
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("name", name));
			pairs.add(new BasicNameValuePair("email", email));
			pairs.add(new BasicNameValuePair("rating", String.valueOf((int) rating)));
			pairs.add(new BasicNameValuePair("comment", comment));
			UrlEncodedFormEntity body = new UrlEncodedFormEntity(pairs, "UTF-8");
			post.addHeader("Content-Type", "application/x-www-form-urlencoded");
			post.setEntity(body);
		}
		catch(Exception e) {}
		return post;
	}
	
	public HttpPost makeObservation(long api_id, String jellyFishName,
			String observations, File image) {
		String url = getObservationServiceURL(api_id);
		HttpPost post = postRequest(url);
		try {
			MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			entity.addPart("jellyFishName", new StringBody(jellyFishName));
			entity.addPart("observations", new StringBody(observations));
			entity.addPart("image", new FileBody(image));
			post.setEntity(entity);
		}
		catch(Exception e) {}
		return post;
	}
	
	public HttpPost makeRegistration(String username, String password, String email) {
		String url = getRegisterURL();
		HttpPost post = postRequest(url);
		try {
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("deviceFamily", "android"));
			pairs.add(new BasicNameValuePair("appVersion", "1"));
			pairs.add(new BasicNameValuePair("userName", username));
			pairs.add(new BasicNameValuePair("password", password));
			pairs.add(new BasicNameValuePair("email", email));
			UrlEncodedFormEntity body = new UrlEncodedFormEntity(pairs, "UTF-8");
			post.addHeader("Content-Type", "application/x-www-form-urlencoded");
			post.setEntity(body);
		}
		catch(Exception e) {}
		return post;
	}
}