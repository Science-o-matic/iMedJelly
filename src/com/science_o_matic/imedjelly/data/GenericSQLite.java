package com.science_o_matic.imedjelly.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GenericSQLite extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "yourchoice.db";
	private static final int DATABASE_VERSION = 1;

	public GenericSQLite(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void createTable(Table table, SQLiteDatabase db) {
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE TABLE IF NOT EXISTS "+table.mName+"(");
		for(int i=0; i<table.mFieldsName.length; i++){
			if(i>0) sql.append(",");
			sql.append(table.mFieldsName[i]+" "+table.mFieldsDefinition[i]);
		}
		sql.append(");");
		db.execSQL(sql.toString());
	}

	public void dropTable(Table table, SQLiteDatabase db) {
		StringBuilder sql = new StringBuilder();
		sql.append("DROP TABLE IF EXISTS "+table.mName+";");
		db.execSQL(sql.toString());
	}

	public void createDatabase(SQLiteDatabase db) {
		createTable(Table.zone, db);
		createTable(Table.beach, db);
	}

	public void dropDatabase(SQLiteDatabase db) {
		dropTable(Table.zone, db);
		dropTable(Table.beach, db);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		dropDatabase(db);
		createDatabase(db);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		dropDatabase(db);
		createDatabase(db);
	}
}
