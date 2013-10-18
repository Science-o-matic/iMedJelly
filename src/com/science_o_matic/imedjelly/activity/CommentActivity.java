package com.science_o_matic.imedjelly.activity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import com.science_o_matic.imedjelly.R;
import com.science_o_matic.imedjelly.data.ApiRequest;

public class CommentActivity extends Activity {
	private static final String TAG = "CommentActivity";
	
	private long mApi_id;
	private FeedbackSender mSender = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		setContentView(R.layout.activity_comment);
		if(extras != null) {
			// Get beach id.
			mApi_id = extras.getLong("api_id");
			Button view = (Button) findViewById(R.id.feedback_send);
			view.setOnClickListener(new OnClickListener() {
	        	@Override
				public void onClick(View v) {
	        		String user = ((EditText) findViewById(R.id.name)).getText().toString();
	        		String email = ((EditText) findViewById(R.id.email)).getText().toString();
	        		double rating = ((RatingBar) findViewById(R.id.rating)).getRating();
	        		String comment = ((EditText) findViewById(R.id.comment)).getText().toString();
	        		mSender = new FeedbackSender(getBaseContext(),
	        				mApi_id,
	        				user, email, rating, comment);
	        		mSender.execute();
				}
	        });
		}
	}

	public class FeedbackSender extends AsyncTask<Void, Integer, String>{
		private Context mContext = null;
		private ApiRequest mApi = null;
		private HttpPost mRequest = null;
		
		private String mStatus = null;
		private String mError = null;

		public FeedbackSender(Context context, long api_id,
				String name, String email, double rating, String comment) {
			this.mContext = context;
			mApi = new ApiRequest(mContext);
			mRequest = mApi.makeFeedback(api_id, name, email, rating, comment);
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
				else if (statusCode == 405) {
					// Already commented.
					String msg = "";
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
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
					Toast.makeText(mContext, getString(R.string.feedback_success),
							Toast.LENGTH_SHORT).show();
				}
				else if(mStatus.equals("ERROR") && mError != null) {
					Toast.makeText(mContext, getString(R.string.feedback_failed)+mError,
							Toast.LENGTH_SHORT).show();
				}
				// Go back.
				finish();
			}
		}
	}
}