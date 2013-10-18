package com.science_o_matic.imedjelly.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class JellyFishesSQLiteHelper extends SQLiteOpenHelper {
	
	public static final String TABLE_JELLYFISHES = "jellyfish";
	public static final String JELLYFISH_COLUMN_ID = "_id";
	public static final String JELLYFISH_COLUMN_TITLE = "title";
	public static final String JELLYFISH_COLUMN_NAME = "name";
	public static final String JELLYFISH_COLUMN_SUBNAME = "subname";
	public static final String JELLYFISH_COLUMN_LEVEL = "level";
	public static final String JELLYFISH_COLUMN_DANGER = "danger";
	public static final String JELLYFISH_COLUMN_ENVIRONMENT = "environment";
	public static final String JELLYFISH_COLUMN_FREQUENCY = "frequency";
	public static final String JELLYFISH_COLUMN_CHARACTERISTICS = "characteristics";
	public static final String JELLYFISH_COLUMN_PICTURE = "picture";
		
	private static final String DATABASE_NAME = "imedjelly.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final String CREATE_TABLE_JELLYFISHES =
			"CREATE TABLE "	+ TABLE_JELLYFISHES + "("
			+ JELLYFISH_COLUMN_ID + " integer primary key autoincrement, "
			+ JELLYFISH_COLUMN_TITLE + " text, "
			+ JELLYFISH_COLUMN_NAME + " text, "
			+ JELLYFISH_COLUMN_SUBNAME + " text, "
			+ JELLYFISH_COLUMN_LEVEL + " text, "
			+ JELLYFISH_COLUMN_DANGER + " text, "
			+ JELLYFISH_COLUMN_ENVIRONMENT + " text, "
			+ JELLYFISH_COLUMN_FREQUENCY + " text, "
			+ JELLYFISH_COLUMN_CHARACTERISTICS + " text, "
			+ JELLYFISH_COLUMN_PICTURE + " integer)";

	private static final String DROP_TABLE_JELLYFISHES =
			"DROP TABLE IF EXISTS " + TABLE_JELLYFISHES;
	
	public JellyFishesSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_JELLYFISHES);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(BeachesSQLiteHelper.class.getName(), newVersion + ", which will destroy all old data");
		db.execSQL(DROP_TABLE_JELLYFISHES);
		db.execSQL(CREATE_TABLE_JELLYFISHES);
	}

}
