package com.science_o_matic.imedjelly.fragment;

import java.io.IOException;
import java.util.Stack;
import org.apache.http.client.methods.HttpRequestBase;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.science_o_matic.imedjelly.R;
import com.science_o_matic.imedjelly.data.ApiClient;
import com.science_o_matic.imedjelly.data.JsonObject;
import com.science_o_matic.imedjelly.data.TaskLoader;

public class CommunityFragment extends Fragment {
	private Context mContext;
	private TaskLoader mLoader = null;
	private WebView mWebView = null;
	private Stack<String> mLinkStack = new Stack<String>();
	private View mView = null;
	
	private String mStatus;
	private String mError;
	
	private boolean getNewUserFlag() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		return preferences.getBoolean("newUser", true);
	}
	
	private void setNewUserFlag(boolean flag) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean("newUser", flag);
		editor.commit();
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = null;
		mContext = getActivity().getBaseContext();    
        if(getNewUserFlag()) {
        	mView = inflater.inflate(R.layout.newuser, container, false);
    		Button button = (Button) mView.findViewById(R.id.newuser_join);
        	button.setOnClickListener(new OnClickListener() {
        		@Override
				public void onClick(View v) {
        			String username = ((EditText) mView.findViewById(R.id.name)).getText().toString();
            		String password = ((EditText) mView.findViewById(R.id.password)).getText().toString();
            		String email = ((EditText) mView.findViewById(R.id.email)).getText().toString();
	        		// New user.
	    			mLoader = new TaskLoader(mContext) {
	    				@Override
	    				protected void onPostExecute(Task[] results){
	    					String result = getResult(results);
	    					if (!result.equals(TaskLoader.Task.SUCCESS)) {
	    						Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
	    					}
	    					else if (mStatus != null) {
	    						if(mStatus.equals("OK")) {
		    						Toast.makeText(mContext, getString(R.string.newuser_success),
		    								Toast.LENGTH_SHORT).show();
		    					}
		    					else if(mStatus.equals("ERROR") && mError != null) {
		    						Toast.makeText(mContext, getString(R.string.newuser_failed)+mError,
		    								Toast.LENGTH_SHORT).show();
		    					}
	    					}
	    				}
	    			};
	    			ApiClient api = mLoader.getApiClient();
	    			HttpRequestBase request = api.postRegistration(username, password, email);
	    			TaskLoader.Task task = mLoader.new Task(request, new JsonObject() {
						@Override
						public void parseJson(JsonReader reader) throws IOException {
							reader.beginObject();
							while (reader.hasNext()) {
								String item = reader.nextName();
								if (item.equals("status") && reader.peek() != JsonToken.NULL) {
									mStatus = reader.nextString();
								}
								else if (item.equals("error") && reader.peek() != JsonToken.NULL) {
									mError = reader.nextString();
								}
								else {
									reader.skipValue();
								}
							}
							reader.endObject();
						}
	    			});
	    			mLoader.execute(task);
				}
            });
        	TextView text = (TextView) mView.findViewById(R.id.newuser_alreadyregistered);
        	text.setOnClickListener(new OnClickListener() {
        		@Override
    			public void onClick(View v) {
        			setNewUserFlag(false);
        			reload();
        		}
        	});
        }
        else {
        	mView = inflater.inflate(R.layout.community, container, false);
    		Resources res = getActivity().getResources();
            String link = res.getString(R.string.community_url);
            mLinkStack.add(link);
            String currentURL = mLinkStack.peek();
            if (currentURL != null) {
            	mWebView = (WebView) mView.findViewById(R.id.webPage);
            	mWebView.setWebViewClient(new WebViewClient(){
                	@Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                		mLinkStack.add(url);
                		return false;
                    }
                });
            	mWebView.getSettings().setJavaScriptEnabled(true);
            	load(currentURL);
            }
        }
        return mView;
    }

	public void reload() {
		CommunityFragment fragment = new CommunityFragment();
        FragmentManager manager = getActivity().getSupportFragmentManager();
        manager.beginTransaction()
        	.replace(R.id.content, fragment, null)
        	.addToBackStack(null)
        	.commit();
	}
	
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();
        if (activity != null) {
        }
    }
    
    public void load(String url) {
    	mWebView.loadUrl(url);
    }
    
    public boolean goBack() {
    	if(mLinkStack.size() > 1) {
    		mLinkStack.pop();
    		load(mLinkStack.peek());
    		return true;
    	}
    	return false;
    }

    /*
    public class NewUserSender extends AsyncTask<Void, Integer, String>{
		private Context mContext = null;
		private ApiRequest mApi = null;
		private HttpPost mRequest = null;
		
		private String mStatus = null;
		private String mError = null;

		public NewUserSender(Context context,
				String username, String password, String email) {
			this.mContext = context;
			mApi = new ApiRequest(mContext);
			mRequest = mApi.makeRegistration(username, password, email);
		}

		@Override
		protected String doInBackground(Void... voids) {
			Log.d(TAG, "doInBackground");
			try {
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
						parseJson(reader);
					}
					finally {
						reader.close();
					}
				}
				else if (statusCode != 200) {
					String code = String.valueOf(statusCode);
					Log.d(TAG, "Unable to send data: " + code);
					Toast.makeText(mContext, code, Toast.LENGTH_SHORT).show();
				}
			}
			catch (Exception e) {
				Log.d(TAG, e.getLocalizedMessage());
			}
			return null;
		}

		private void parseJson(JsonReader reader) throws IOException{
			reader.beginObject();
			while (reader.hasNext()) {
				String item = reader.nextName();
				if (item.equals("status") && reader.peek() != JsonToken.NULL) {
					mStatus = reader.nextString();
				}
				else if (item.equals("error") && reader.peek() != JsonToken.NULL) {
					mError = reader.nextString();
				}
				else {
					reader.skipValue();
				}
			}
			reader.endObject();
		}
		
		@Override
		protected void onPostExecute(String results){
			if(mStatus != null) {
				if(mStatus.equals("OK")) {
					Toast.makeText(mContext, getString(R.string.newuser_success),
							Toast.LENGTH_LONG).show();
					setNewUserFlag(false);
					reload();
				}
				else if(mStatus.equals("ERROR") && mError != null) {
					Toast.makeText(mContext, getString(R.string.newuser_failed)+mError,
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
    */
}
