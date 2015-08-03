package com.science_o_matic.imedjelly.activity;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.science_o_matic.imedjelly.R;
import com.science_o_matic.imedjelly.data.ApiClient;
import com.science_o_matic.imedjelly.data.JsonObject;
import com.science_o_matic.imedjelly.data.Table;
import com.science_o_matic.imedjelly.data.TaskLoader;

public class SplashActivity extends Activity {
	private ZonesLoader mZonesLoader = null;
	private BeachesLoader mBeachesLoader = null;
	private NotificationsLoader mNotificationsLoader = null;
	private PredictionsLoader mPredictionsLoader = null;

	private int[] mZonesIds;
	private boolean mExistsPrediction = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		// Load zones.
		mZonesLoader = new ZonesLoader(SplashActivity.this);
		mZonesLoader.execute();
	}
	
	public class NotificationsLoader extends TaskLoader implements JsonObject {
		private Context mContext = null;
		private ApiClient mApi = null;
		private String mNotification = null;
	
		public NotificationsLoader(Context context) {
			super(context);
			mContext = context;
			mApi = new ApiClient(context);
		}
	
		public void execute() {
			super.execute(new Task(mApi.getNotifications(), this));
		}
		
		@Override
		public void parseJson(JsonReader reader) throws IOException {
			reader.beginObject();
			while (reader.hasNext()) {
				String root = reader.nextName();
				if (root.equals("notification") && reader.peek() != JsonToken.NULL) {
					mNotification = reader.nextString();
				}
				else {
					reader.skipValue();
				}
			}
			// End transaction.
			reader.endObject();
		}
	
		@Override
		protected void onPostExecute(Task[] tasks){
			String result = getResult(tasks);
			if (!result.equals(Task.SUCCESS)) {
				Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
			}			
			Intent intent = new Intent(mContext, MainActivity.class);
			if (mNotification != null) {
				intent.putExtra("notification", mNotification);
			}
			intent.putExtra("existsPrediction", mExistsPrediction);
			startActivity(intent);
			finish();
		}
	}

	public class BeachesLoader extends TaskLoader {
		private Context mContext = null;
		private ApiClient mApi = null;
		private int[] mZones;
	
		public BeachesLoader(Context context, int[] zones) {
			super(context);
			mContext = context;
			mApi = new ApiClient(context);
			mZones = zones;
		}
	
		public void execute() {
			Task[] beachTasks = new Task[mZones.length];
			for (int i=0; i<mZones.length; i++) {
				beachTasks[i] = new Task(
					mApi.getBeaches(mZones[i]),
					new BeachDataSource(mContext, Table.beach, mZones[i])
				);
			}
			super.execute(beachTasks);
		}
		
		public class BeachDataSource extends DataSource {
			private int mZoneId;
	
			public BeachDataSource(Context context, Table table, int zoneId) {
				super(context, table);
				mZoneId = zoneId;
			}
	
			@Override
			public void parseJson(JsonReader reader) throws IOException {
				// Start transaction.
				openWrite();
				delete("zoneId=?", new String[]{Integer.toString(mZoneId)});
				mDatabase.beginTransaction();
				try {
					reader.beginObject();
					while (reader.hasNext()) {
						String field = reader.nextName();
						if (field.equals("beaches")) {
							reader.beginArray();
							while (reader.hasNext()) {
								ContentValues values = new ContentValues();
								values.put("zoneId", mZoneId);
								// Parse object.
								reader.beginObject();
								while (reader.hasNext()) {
									parseJsonField(reader, values);
								}
								// Insert object.
								insert(values);
								reader.endObject();
							}
							reader.endArray();
						}
					}
					reader.endObject();
					mDatabase.setTransactionSuccessful();
				} finally {
					// End transaction.
					mDatabase.endTransaction();
					close();
				}
			}
		}
	
		@Override
		protected void onPostExecute(Task[] tasks){
			String result = getResult(tasks);
			if (result.equals(Task.SUCCESS)) {
				mPredictionsLoader = new PredictionsLoader(mContext, mZonesIds);
				mPredictionsLoader.execute();
			}
			else {
				Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
				Intent intent = new Intent(mContext, MainActivity.class);
				startActivity(intent);
				finish();
			}
		}
	}
	
	public class ZonesLoader extends TaskLoader {
		private Context mContext = null;
		private ApiClient mApi = null;

		public ZonesLoader(Context context) {
			super(context);
			mContext = context;
			mApi = new ApiClient(context);
		}

		public void execute() {
			DataSource zonesSource = new ZoneDataSource(mContext, Table.zone);
			super.execute(new Task(mApi.getZones(), zonesSource));
		}
		
		public class ZoneDataSource extends DataSource {
			public ZoneDataSource(Context context, Table table) {
				super(context, table);
			}

			@Override
			public void parseJson(JsonReader reader) throws IOException {
				// Start transaction.
				openWrite();
				deleteAll();
				mDatabase.beginTransaction();
				try {
					reader.beginObject();
					while (reader.hasNext()) {
						String field = reader.nextName();
						if (field.equals("zones")) {
							loadRows(reader);
						}
					}
					reader.endObject();
					mDatabase.setTransactionSuccessful();
				} finally {
					// End transaction.
					mDatabase.endTransaction();
					close();
				}
			}
		}
		
		public int[] getZonesId() {
			List<ContentValues> zones = DataSource.getTableItems(mContext, Table.zone);
			int zonesNum = zones.size();
			int[] result = new int[zonesNum];
			int current = 0;
			for (ContentValues zone: zones) {
				result[current] = zone.getAsInteger("zoneId");
				current ++;
			}
			return result;
		}

		@Override
		protected void onPostExecute(Task[] tasks) {
			String result = getResult(tasks);
			if (result.equals(Task.SUCCESS)) {
				mZonesIds = getZonesId();
				mBeachesLoader = new BeachesLoader(mContext, mZonesIds);
				mBeachesLoader.execute();
			} else {
				Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
				Intent intent = new Intent(mContext, MainActivity.class);
				startActivity(intent);
				finish();
			}
		}
	}
	
	public class PredictionsLoader extends TaskLoader {
		private Context mContext = null;
		private ApiClient mApi = null;
		private int[] mZones;

		public PredictionsLoader(Context context, int[] zones) {
			super(context);
			mContext = context;
			mApi = new ApiClient(context);
			mZones = zones;
		}
		
		public void execute() {
			Task[] predictionTasks = new Task[mZones.length];
			for (int i=0; i<mZones.length; i++) {
				predictionTasks[i] = new Task(
					mApi.getPredictions(mZones[i]),
					new PredictionDataSource(mContext, Table.prediction, mZones[i])
				);
			}
			super.execute(predictionTasks);
		}
		
		public class PredictionDataSource extends DataSource {
			private int mZoneId;
			
			public PredictionDataSource(Context context, Table table, int zoneId) {
				super(context, table);
				mZoneId = zoneId;
			}
			
			@Override
			public void parseJson(JsonReader reader) throws IOException {
				openWrite();
				delete("zoneId=?", new String[]{Integer.toString(mZoneId)});
				mDatabase.beginTransaction();
				try {
					reader.beginObject();
					
					while(reader.hasNext()) {
						mExistsPrediction = true;
						String field = reader.nextName();
						reader.beginArray();
						while (reader.hasNext()) {
							ContentValues values = new ContentValues();
							values.put("day", field);
							values.put("zoneId", mZoneId);
							// Parse object.
							reader.beginObject();
							while (reader.hasNext()) {
								parseJsonField(reader, values);
							}
							// Insert object.
							insert(values);
							reader.endObject();
						}
						reader.endArray();
					}
					
					reader.endObject();
					mDatabase.setTransactionSuccessful();
				} finally {
					mDatabase.endTransaction();
					close();
				}
				
			}
		}
		
		@Override
		protected void onPostExecute(Task[] tasks) {
			String result = getResult(tasks);
			if (result.equals(Task.SUCCESS)) {
				mNotificationsLoader = new NotificationsLoader(mContext);
				mNotificationsLoader.execute();
			} else {
				Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
				Intent intent = new Intent(mContext, MainActivity.class);
				startActivity(intent);
				finish();
			}
		}
	}
}