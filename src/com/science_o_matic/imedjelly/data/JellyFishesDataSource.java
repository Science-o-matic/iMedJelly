package com.science_o_matic.imedjelly.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.JsonReader;
import android.util.JsonToken;

public class JellyFishesDataSource {

	private SQLiteDatabase database;
	private JellyFishesSQLiteHelper dbHelper;
	Context mContext = null;

	private String[] allColumns = { 
		JellyFishesSQLiteHelper.JELLYFISH_COLUMN_ID,
		JellyFishesSQLiteHelper.JELLYFISH_COLUMN_TITLE,
		JellyFishesSQLiteHelper.JELLYFISH_COLUMN_NAME,
		JellyFishesSQLiteHelper.JELLYFISH_COLUMN_SUBNAME,
		JellyFishesSQLiteHelper.JELLYFISH_COLUMN_LEVEL,
		JellyFishesSQLiteHelper.JELLYFISH_COLUMN_DANGER,
		JellyFishesSQLiteHelper.JELLYFISH_COLUMN_ENVIRONMENT,
		JellyFishesSQLiteHelper.JELLYFISH_COLUMN_FREQUENCY,
		JellyFishesSQLiteHelper.JELLYFISH_COLUMN_CHARACTERISTICS,
		JellyFishesSQLiteHelper.JELLYFISH_COLUMN_PICTURE,
	};

	public JellyFishesDataSource(Context context) {
	    dbHelper = new JellyFishesSQLiteHelper(context);
	    mContext = context;
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public JellyFish createJellyFish(String title, String name,
			String subname, String level,
			String danger, String environment,
			String frequency, String characteristics,
			int picture) {
		// Insert into database.
		ContentValues values = new ContentValues();
		values.put(JellyFishesSQLiteHelper.JELLYFISH_COLUMN_TITLE, title);
		values.put(JellyFishesSQLiteHelper.JELLYFISH_COLUMN_NAME, name);
		values.put(JellyFishesSQLiteHelper.JELLYFISH_COLUMN_SUBNAME, subname);
		values.put(JellyFishesSQLiteHelper.JELLYFISH_COLUMN_LEVEL, level);
		values.put(JellyFishesSQLiteHelper.JELLYFISH_COLUMN_DANGER, danger);
		values.put(JellyFishesSQLiteHelper.JELLYFISH_COLUMN_ENVIRONMENT, environment);
		values.put(JellyFishesSQLiteHelper.JELLYFISH_COLUMN_FREQUENCY, frequency);
		values.put(JellyFishesSQLiteHelper.JELLYFISH_COLUMN_CHARACTERISTICS, characteristics);
		values.put(JellyFishesSQLiteHelper.JELLYFISH_COLUMN_PICTURE, picture);
		long id = database.insert(JellyFishesSQLiteHelper.TABLE_JELLYFISHES, null, values);
		// Build jellyfish.
		JellyFish jellyfish = new JellyFish();
		jellyfish.setId(id);
		jellyfish.setTitle(title);
		jellyfish.setName(name);
		jellyfish.setSubname(subname);
		jellyfish.setLevel(level);
		jellyfish.setDanger(danger);
		jellyfish.setEnvironment(environment);
		jellyfish.setFrequency(frequency);
		jellyfish.setCharacteristics(characteristics);
		jellyfish.setPicture(picture);
		return jellyfish;
	}

	public JellyFish insertJellyFish(JellyFish jellyfish) {
		// Insert into database.
		ContentValues values = new ContentValues();
		values.put(JellyFishesSQLiteHelper.JELLYFISH_COLUMN_TITLE, jellyfish.getTitle());
		values.put(JellyFishesSQLiteHelper.JELLYFISH_COLUMN_NAME, jellyfish.getName());
		values.put(JellyFishesSQLiteHelper.JELLYFISH_COLUMN_SUBNAME, jellyfish.getSubname());
		values.put(JellyFishesSQLiteHelper.JELLYFISH_COLUMN_LEVEL, jellyfish.getLevel());
		values.put(JellyFishesSQLiteHelper.JELLYFISH_COLUMN_DANGER, jellyfish.getDanger());
		values.put(JellyFishesSQLiteHelper.JELLYFISH_COLUMN_ENVIRONMENT, jellyfish.getEnvironment());
		values.put(JellyFishesSQLiteHelper.JELLYFISH_COLUMN_FREQUENCY, jellyfish.getFrequency());
		values.put(JellyFishesSQLiteHelper.JELLYFISH_COLUMN_CHARACTERISTICS, jellyfish.getCharacteristics());
		values.put(JellyFishesSQLiteHelper.JELLYFISH_COLUMN_PICTURE, jellyfish.getPicture());
		long id = database.insert(JellyFishesSQLiteHelper.TABLE_JELLYFISHES, null, values);
		jellyfish.setId(id);
		return jellyfish;
	}

	public void deleteJellyFish(JellyFish jellyfish) {
		long id = jellyfish.getId();
		database.delete(JellyFishesSQLiteHelper.TABLE_JELLYFISHES,
				JellyFishesSQLiteHelper.JELLYFISH_COLUMN_ID + " = " + id, null);
	}

	private JellyFish cursorToJellyFish(Cursor cursor) {
		JellyFish jellyfish = new JellyFish();
		jellyfish.setId(cursor.getLong(0));
		jellyfish.setTitle(cursor.getString(1));
		jellyfish.setName(cursor.getString(2));
		jellyfish.setSubname(cursor.getString(3));
		jellyfish.setLevel(cursor.getString(4));
		jellyfish.setDanger(cursor.getString(5));
		jellyfish.setEnvironment(cursor.getString(6));
		jellyfish.setFrequency(cursor.getString(7));
		jellyfish.setCharacteristics(cursor.getString(8));
		jellyfish.setPicture(cursor.getInt(9));
		return jellyfish;
	}
	
	public List<JellyFish> getAllJellyFishes() {
		List<JellyFish> jellyfishes = new ArrayList<JellyFish>();
		// Create cursor.
		Cursor cursor = database.query(JellyFishesSQLiteHelper.TABLE_JELLYFISHES,
				allColumns, null, null, null, null, null);
		// Traverse cursor.
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			JellyFish jellyfish = cursorToJellyFish(cursor);
			jellyfishes.add(jellyfish);
			cursor.moveToNext();
		}
		cursor.close();
		return jellyfishes;
	}
	
	public void deleteAllJellyFishes(){
		database.delete(JellyFishesSQLiteHelper.TABLE_JELLYFISHES, null, null);
	}
	
	public void parseBeachesJson(JsonReader reader) throws IOException{
		open();
		deleteAllJellyFishes();
		reader.beginObject();
		JellyFish jellyfish = new JellyFish();
		Resources res = mContext.getResources();
		String pkg = mContext.getPackageName();
		// Start transaction.
		while (reader.hasNext()) {
			jellyfish.clean();
			String root = reader.nextName();
			if (root.equals("jellyfishes") && reader.peek() != JsonToken.NULL) {
				reader.beginArray();
				while (reader.hasNext()) {
					// Fill jellyfish data.
					reader.beginObject();
					while (reader.hasNext()) {
						String item = reader.nextName();
						if (item.equals("title")) {
							jellyfish.setTitle(reader.nextString());
						}
						else if (item.equals("name")) {
							jellyfish.setName(reader.nextString());
						}
						else if (item.equals("subname")) {
							jellyfish.setSubname(reader.nextString());
						}
						else if (item.equals("level")) {
							jellyfish.setLevel(reader.nextString());
						}
						else if (item.equals("danger")) {
							jellyfish.setDanger(reader.nextString());
						}
						else if (item.equals("environment")) {
							jellyfish.setEnvironment(reader.nextString());
						}
						else if (item.equals("frequency")) {
							jellyfish.setFrequency(reader.nextString());
						}
						else if (item.equals("characteristics")) {
							jellyfish.setCharacteristics(reader.nextString());
						}
						else if (item.equals("picture")) {
							String src = reader.nextString();
							int picture = res.getIdentifier(src , "drawable", pkg);
							jellyfish.setPicture(picture);
						}
						else {
							reader.skipValue();
						}
					}
					reader.endObject();
					// Insert jellyfish.
					insertJellyFish(jellyfish);
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
