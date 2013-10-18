package com.science_o_matic.imedjelly.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Base64;

public class Client {
	private int TIMEOUT = 3000;

	protected String mServer = null;
	private String mAuthorization = null;
	
	public Client(String server) {
		mServer = server;
	}

	public void setAuthentication(String username, String password) {
		byte[] bytes = (username + ":" + password).getBytes();
		String credentials = Base64.encodeToString(bytes, (Base64.DEFAULT));
		credentials = credentials.trim();
		mAuthorization = "Basic " + credentials;
	}

	public void setToken(String token) {
		mAuthorization = "Token " + token;
	}

	public HttpGet getRequest(String url) {
		HttpGet request = new HttpGet(url);
		request.setHeader("Referer", mServer);
		if(mAuthorization!=null) {
			request.addHeader("Authorization", mAuthorization);
		}
		return request;
	}

	public HttpPost postRequest(String url) {
		HttpPost request = new HttpPost(url);
		request.setHeader("Referer", mServer);
		if(mAuthorization!=null) {
			request.addHeader("Authorization", mAuthorization);
		}
		return request;
	}

	public HttpResponse performRequest(HttpUriRequest request) throws IOException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpParams params = httpclient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, TIMEOUT);
		return httpclient.execute(request);
	}

	public HttpPost makeTokenAuth(String email, String password) {
		String url = mServer + "/token-auth/";
		HttpPost request= postRequest(url);
		request.addHeader("Content-Type", "application/x-www-form-urlencoded");
		try {
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("username", email));
			pairs.add(new BasicNameValuePair("password", password));
			UrlEncodedFormEntity body = new UrlEncodedFormEntity(pairs, "UTF-8");
			request.setEntity(body);
		}
		catch(Exception e) {}
		return request;
	}
}