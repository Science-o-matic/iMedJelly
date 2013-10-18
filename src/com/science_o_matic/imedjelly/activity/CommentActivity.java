package com.science_o_matic.imedjelly.activity;

import java.io.IOException;

import org.apache.http.client.methods.HttpRequestBase;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.science_o_matic.imedjelly.R;
import com.science_o_matic.imedjelly.data.ApiClient;
import com.science_o_matic.imedjelly.data.JsonObject;
import com.science_o_matic.imedjelly.data.TaskLoader;

public class CommentActivity extends Activity {
	private Context mContext;
	private TaskLoader mLoader = null;
	private long mId;
	
	private String mStatus;
	private String mError;
	
	private EditText mUserView;
	private EditText mEmailView;
	private RatingBar mRatingBar;
	private EditText mCommentView;
	private Button mSendButton;
	
	private RatingBar.OnRatingBarChangeListener mRatingWatcher;
	private TextWatcher mTextWatcher;

	protected void sendComment(boolean retry) {
		final String user = mUserView.getText().toString();
		final String email = mEmailView.getText().toString();
		final double rating = mRatingBar.getRating();
		final String comment = mCommentView.getText().toString();
		final boolean retrySend = retry;
		// Send feedback.
		mLoader = new TaskLoader(mContext) {
			@Override
			protected void onPostExecute(Task[] results){
				if (results[0].status==405 && retrySend) {
					sendComment(false);
					return;
				}
				
				String result = getResult(results);
				if (!result.equals(TaskLoader.Task.SUCCESS)) {
					Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
				}
				else if (results[0].status == 405){
					String msg = "Already commented";
					Toast.makeText(mContext, getString(R.string.feedback_failed)+msg,
							Toast.LENGTH_SHORT).show();
				}
				else if (mStatus != null) {
					if(mStatus.equals("OK")) {
						Toast.makeText(mContext, getString(R.string.feedback_success),
								Toast.LENGTH_SHORT).show();
					}
					else if(mStatus.equals("ERROR") && mError != null) {
						Toast.makeText(mContext, getString(R.string.feedback_failed)+mError,
								Toast.LENGTH_SHORT).show();
					}
				}
				finish();
			}
		};
		ApiClient api = mLoader.getApiClient();
		HttpRequestBase request = api.postFeedback(mId, user, email, rating, comment);
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Initialize.
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		mContext = getBaseContext();
		setContentView(R.layout.activity_comment);
		if(extras == null) return;
		// Get controls.
		mUserView = ((EditText) findViewById(R.id.name));
		mEmailView = ((EditText) findViewById(R.id.email));
		mRatingBar = ((RatingBar) findViewById(R.id.rating));
		mCommentView = ((EditText) findViewById(R.id.comment));
		mSendButton = (Button) findViewById(R.id.feedback_send);
		// Watchers.
		mRatingWatcher = new RatingBar.OnRatingBarChangeListener() {
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				String user = mUserView.getText().toString();
        		String email = mEmailView.getText().toString();
        		String comment = mCommentView.getText().toString();
        		//mSendButton.setEnabled(!user.isEmpty() && !email.isEmpty() && rating != 0 && !comment.isEmpty());
        		mSendButton.setEnabled(!comment.isEmpty());
			}
		};
		mTextWatcher = new TextWatcher(){
	        public void afterTextChanged(Editable s) {
	        	String user = mUserView.getText().toString();
        		String email = mEmailView.getText().toString();
        		double rating = mRatingBar.getRating();
        		String comment = mCommentView.getText().toString();
        		//mSendButton.setEnabled(!user.isEmpty() && !email.isEmpty() && rating != 0 && !comment.isEmpty());
        		mSendButton.setEnabled(!comment.isEmpty());
	        }
	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        public void onTextChanged(CharSequence s, int start, int before, int count){}
	    };		
		mUserView.addTextChangedListener(mTextWatcher);
		mEmailView.addTextChangedListener(mTextWatcher);
		mRatingBar.setOnRatingBarChangeListener(mRatingWatcher);
		mCommentView.addTextChangedListener(mTextWatcher);
		mSendButton.setEnabled(false);
		
		// Get beach id.
		mId = extras.getLong("id");
		mSendButton.setOnClickListener(new OnClickListener() {
        	@Override
			public void onClick(View v) {
        		sendComment(true);
			}
        });
	}

/*
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
	*/

}