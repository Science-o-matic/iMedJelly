package com.science_o_matic.imedjelly.activity;

import java.io.File;
import java.io.IOException;

import org.apache.http.client.methods.HttpRequestBase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.science_o_matic.imedjelly.R;
import com.science_o_matic.imedjelly.data.ApiClient;
import com.science_o_matic.imedjelly.data.JsonObject;
import com.science_o_matic.imedjelly.data.TaskLoader;

public class CameraUploadActivity extends Activity {
	private Context mContext;
	private TaskLoader mLoader = null;
	private long mId;
	private Uri mImageUri = null;
	private final static int TAKE_PICTURE = 1;
	
	private String mStatus;
	private String mError;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		setContentView(R.layout.activity_observation);
		mContext = getBaseContext();
		if(extras != null) {			
			// Get beach id.
			mId = extras.getLong("id");
			ImageView photo = (ImageView) findViewById(R.id.image_upload);
			photo.setOnClickListener(new OnClickListener() {
	        	@Override
				public void onClick(View v) {
	        		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
	        	    File file = new File(Environment.getExternalStorageDirectory(), "JellyFish.jpg");
	        	    mImageUri = Uri.fromFile(file);
	        	    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
	        	    startActivityForResult(intent, TAKE_PICTURE);
				}
	        });
			Button send = (Button) findViewById(R.id.observation_send);
			send.setOnClickListener(new OnClickListener() {
	        	@Override
				public void onClick(View v) {
	        		Spinner spinner = (Spinner) findViewById(R.id.jellyFishName);
	        		String jellyFishName = String.valueOf(spinner.getSelectedItem());
	        		String comment = ((EditText) findViewById(R.id.comment)).getText().toString();
	        		// Post photo.
	    			mLoader = new TaskLoader(mContext) {
	    				@Override
	    				protected void onPostExecute(Task[] results){
	    					String result = getResult(results);
	    					if (!result.equals(TaskLoader.Task.SUCCESS)) {
	    						Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
	    					}
	    					else if (mStatus != null) {
	    						if(mStatus.equals("OK")) {
		    						Toast.makeText(mContext, getString(R.string.observation_success),
		    								Toast.LENGTH_SHORT).show();
		    					}
		    					else if(mStatus.equals("ERROR") && mError != null) {
		    						Toast.makeText(mContext, getString(R.string.observation_failed)+mError,
		    								Toast.LENGTH_SHORT).show();
		    					}
	    					}
	    					finish();
	    				}
	    			};
	    			ApiClient api = mLoader.getApiClient();
	    			File file = null;
	    			if (mImageUri != null) {
	    				file = new File(mImageUri.getPath());
	    			}
	    			HttpRequestBase request = api.postObservation(mId, jellyFishName, comment, file);
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
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    switch (requestCode) {
	    case TAKE_PICTURE:
	        if (resultCode == Activity.RESULT_OK) {
	            getContentResolver().notifyChange(mImageUri, null);
	            ImageView imageView = (ImageView) findViewById(R.id.image_upload);
	            try {
	            	int height = imageView.getHeight();
	            	int width = imageView.getWidth();
	            	Bitmap bitmap = android.provider.MediaStore.Images.Media
	            			.getBitmap(getContentResolver(), mImageUri);
	            	Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
	                imageView.setImageBitmap(scaledBitmap);
	            } catch (Exception e) {
	                Log.e("Camera failed: ", e.toString());
	            }
	        }
	    }
	}

/*
	public class PhotoSender extends AsyncTask<Void, Integer, String>{
		private Context mContext = null;
		private ApiRequest mApi = null;
		private HttpPost mRequest = null;
		
		private String mStatus = null;
		private String mError = null;

		public PhotoSender(Context context, long api_id,
				String jellyFishName, String comment, File image) {
			this.mContext = context;
			mApi = new ApiRequest(mContext);
			mRequest = mApi.makeObservation(api_id, jellyFishName, comment, image);
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
					Toast.makeText(mContext, getString(R.string.observation_success),
							Toast.LENGTH_SHORT).show();
				}
				else if(mStatus.equals("ERROR") && mError != null) {
					Toast.makeText(mContext, getString(R.string.observation_failed)+mError,
							Toast.LENGTH_SHORT).show();
				}
				// Go back.
				finish();
			}
		}
	}
	*/
}