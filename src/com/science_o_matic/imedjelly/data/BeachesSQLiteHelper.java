package com.science_o_matic.imedjelly.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BeachesSQLiteHelper extends SQLiteOpenHelper {
	
	public static final String TABLE_BEACHES = "beaches";
	public static final String BEACHES_COLUMN_ID = "_id";
	public static final String BEACHES_COLUMN_NAME = "name";
	public static final String BEACHES_COLUMN_API_ID = "api_id";
	public static final String BEACHES_COLUMN_LATITUDE = "latitude";
	public static final String BEACHES_COLUMN_LONGITUDE = "longitude";
	public static final String BEACHES_COLUMN_JELLYFISH_STATUS = "jellyfish_status";
	public static final String BEACHES_COLUMN_MUNICIPALITY_NAME = "municipality_name";
	
	private static final String DATABASE_NAME = "imedjelly.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final String CREATE_TABLE_BEACHES =
			"CREATE TABLE "	+ TABLE_BEACHES + "("
			+ BEACHES_COLUMN_ID + " integer primary key autoincrement, "
			+ BEACHES_COLUMN_NAME + " text, "
			+ BEACHES_COLUMN_API_ID	+ " integer, "
			+ BEACHES_COLUMN_LATITUDE + " real, "
			+ BEACHES_COLUMN_LONGITUDE + " real, "
			+ BEACHES_COLUMN_JELLYFISH_STATUS + " text, "
			+ BEACHES_COLUMN_MUNICIPALITY_NAME + " text);";

	private static final String DROP_TABLE_BEACHES =
			"DROP TABLE IF EXISTS " + TABLE_BEACHES;
	
	public BeachesSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_BEACHES);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(BeachesSQLiteHelper.class.getName(), newVersion + ", which will destroy all old data");
		db.execSQL(DROP_TABLE_BEACHES);
		db.execSQL(CREATE_TABLE_BEACHES);
	}
}
