package com.science_o_matic.imedjelly.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

public class BeachesDataSource {

	private SQLiteDatabase database;
	private BeachesSQLiteHelper dbHelper;

	private String[] allColumns = { 
		BeachesSQLiteHelper.BEACHES_COLUMN_ID,
		BeachesSQLiteHelper.BEACHES_COLUMN_NAME,
		BeachesSQLiteHelper.BEACHES_COLUMN_API_ID,
		BeachesSQLiteHelper.BEACHES_COLUMN_LATITUDE,
		BeachesSQLiteHelper.BEACHES_COLUMN_LONGITUDE,
		BeachesSQLiteHelper.BEACHES_COLUMN_JELLYFISH_STATUS,
		BeachesSQLiteHelper.BEACHES_COLUMN_MUNICIPALITY_NAME
	};

	public BeachesDataSource(Context context) {
	    dbHelper = new BeachesSQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Beach createBeach(String name, long api_id,
			double latitude, double longitude,
			String jellyfish_status, String municipality_name) {
		// Insert into database.
		ContentValues values = new ContentValues();
		values.put(BeachesSQLiteHelper.BEACHES_COLUMN_NAME, name);
		values.put(BeachesSQLiteHelper.BEACHES_COLUMN_API_ID, api_id);
		values.put(BeachesSQLiteHelper.BEACHES_COLUMN_LATITUDE, latitude);
		values.put(BeachesSQLiteHelper.BEACHES_COLUMN_LONGITUDE, longitude);
		values.put(BeachesSQLiteHelper.BEACHES_COLUMN_JELLYFISH_STATUS, jellyfish_status);
		values.put(BeachesSQLiteHelper.BEACHES_COLUMN_MUNICIPALITY_NAME, municipality_name);
		long id = database.insert(BeachesSQLiteHelper.TABLE_BEACHES, null, values);
		// Build beach.
		Beach beach = new Beach();
		beach.setId(id);
		beach.setName(name);
		beach.setApi_Id(api_id);
		beach.setLatitude(latitude);
		beach.setLongitude(longitude);
		beach.setJellyfish_Status(jellyfish_status);
		beach.setMunicipality_Name(municipality_name);
		return beach;
	}

	public Beach insertBeach(Beach beach) {
		// Insert into database.
		ContentValues values = new ContentValues();
		values.put(BeachesSQLiteHelper.BEACHES_COLUMN_NAME, beach.getName());
		values.put(BeachesSQLiteHelper.BEACHES_COLUMN_API_ID, beach.getApi_Id());
		values.put(BeachesSQLiteHelper.BEACHES_COLUMN_LATITUDE, beach.getLatitude());
		values.put(BeachesSQLiteHelper.BEACHES_COLUMN_LONGITUDE, beach.getLongitude());
		values.put(BeachesSQLiteHelper.BEACHES_COLUMN_JELLYFISH_STATUS, beach.getJellyfish_Status());
		values.put(BeachesSQLiteHelper.BEACHES_COLUMN_MUNICIPALITY_NAME, beach.getMunicipality_Name());
		long id = database.insert(BeachesSQLiteHelper.TABLE_BEACHES, null, values);
		beach.setId(id);
		return beach;
	}

	public void deleteBeach(Beach beach) {
		long id = beach.getId();
		database.delete(BeachesSQLiteHelper.TABLE_BEACHES,
			BeachesSQLiteHelper.BEACHES_COLUMN_ID + " = " + id, null);
	}

	private Beach cursorToBeach(Cursor cursor) {
		Beach beach = new Beach();
		beach.setId(cursor.getLong(0));
		beach.setName(cursor.getString(1));
		beach.setApi_Id(cursor.getLong(2));
		beach.setLatitude(cursor.getDouble(3));
		beach.setLongitude(cursor.getDouble(4));
		beach.setJellyfish_Status(cursor.getString(5));
		beach.setMunicipality_Name(cursor.getString(6));
		return beach;
	}
	
	public List<Beach> getAllBeaches() {
		List<Beach> beaches = new ArrayList<Beach>();
		// Create cursor.
		Cursor cursor = database.query(BeachesSQLiteHelper.TABLE_BEACHES,
				allColumns, null, null, null, null, null);
		// Traverse cursor.
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Beach beach = cursorToBeach(cursor);
			beaches.add(beach);
			cursor.moveToNext();
		}
		cursor.close();
		return beaches;
	}
	
	public List<String> getAllMunicipalities(){
		List<String> municipalities = new ArrayList<String>();
		// Create cursor.
		String[] columns = {BeachesSQLiteHelper.BEACHES_COLUMN_MUNICIPALITY_NAME};
		Cursor cursor = database.query(true, BeachesSQLiteHelper.TABLE_BEACHES, 
				columns, null, null, null, null, null, null);
		// Traverse cursor.
		cursor.moveToFirst();
		while (!cursor.isAfterLast()){
			municipalities.add(cursor.getString(0));
			cursor.moveToNext();
		}
		cursor.close();
		return municipalities;
	}
	
	public List<Beach> getMunicipalityBeaches(String municipality){
		List<Beach> beaches = new ArrayList<Beach>();
		// Create cursor.
		Cursor cursor = database.query(true, BeachesSQLiteHelper.TABLE_BEACHES, 
				allColumns,
				BeachesSQLiteHelper.BEACHES_COLUMN_MUNICIPALITY_NAME + " = \"" + municipality + "\"",
				null, null, null, null, null);
		// Traverse cursor.
		cursor.moveToFirst();
		while (!cursor.isAfterLast()){
			Beach beach = cursorToBeach(cursor);
			beaches.add(beach);
			cursor.moveToNext();
		}
		cursor.close();
		return beaches;
	}
	
	public int getBeachAPIid(String beachname){
		int beachAPIid = -1;
		String[] columns = {BeachesSQLiteHelper.BEACHES_COLUMN_API_ID};
		Cursor cursor = database.query(true, BeachesSQLiteHelper.TABLE_BEACHES,
				columns,
				BeachesSQLiteHelper.BEACHES_COLUMN_NAME + " = \"" + beachname + "\"",
				null, null, null, null, null);
		// Traverse cursor.
		cursor.moveToFirst();
		while (!cursor.isAfterLast()){
			beachAPIid = cursor.getInt(0);
			cursor.moveToNext();
		}
		cursor.close();
		return beachAPIid;
	}
	
	public void deleteAllBeaches(){
		database.delete(BeachesSQLiteHelper.TABLE_BEACHES, null, null);
	}
	
	public void parseBeachesJson(JsonReader reader) throws IOException{
		open();
		deleteAllBeaches();
		reader.beginObject();
		Beach beach = new Beach();
		// Start transaction.
		while (reader.hasNext()) {
			beach.clean();
			String root = reader.nextName();
			if (root.equals("beaches") && reader.peek() != JsonToken.NULL) {
				reader.beginArray();
				while (reader.hasNext()) {
					// Fill beach data.
					reader.beginObject();
					while (reader.hasNext()) {
						String item = reader.nextName();
						if (item.equals("name")) {
							beach.setName(reader.nextString());
						}
						else if (item.equals("id")) {
							beach.setApi_Id(reader.nextInt());
						}
						else if (item.equals("latitude")) {
							beach.setLatitude(reader.nextDouble());
						}
						else if (item.equals("longitude")) {
							beach.setLongitude(reader.nextDouble());
						}
						else if (item.equals("jellyFishStatus")) {
							beach.setJellyfish_Status(reader.nextString());
						}
						else if (item.equals("municipalityName")) {
							beach.setMunicipality_Name(reader.nextString());
						}
						else {
							reader.skipValue();
						}
					}
					reader.endObject();
					// Insert beach.
					insertBeach(beach);
				}
				reader.endArray();
			}
			else {
				reader.skipValue();
			}
		}
		// End transaction.
		reader.endObject();
		close();
	}
}
