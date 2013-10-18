package com.science_o_matic.imedjelly.activity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.science_o_matic.imedjelly.data.GenericSQLite;
import com.science_o_matic.imedjelly.data.JsonObject;
import com.science_o_matic.imedjelly.data.Table;

public class DataSource implements JsonObject {
	protected Context mContext;
	protected Table mTable;
	protected GenericSQLite mSqlite;
	protected SQLiteDatabase mDatabase;
	private String mKey = null;

	public DataSource(Context context, Table table) {
		mContext = context;
		mTable = table;
		mSqlite = new GenericSQLite(context);
		mKey = table.getPrimaryKey();
	}

	public void openRead() throws SQLException {
		mDatabase = mSqlite.getReadableDatabase();
	}

	public void openWrite() throws SQLException {
		mDatabase = mSqlite.getWritableDatabase();
	}

	public void close() {
		mSqlite.close();
	}

	public Cursor getDistinctCursor(String[] fields, String selection, String[] args, String order) {
		if (fields == null) {
			fields = mTable.mFieldsName;
		}
		return mDatabase.query(
			true,
			mTable.mName,
			fields,
			selection,
			args,
			null,		// Group by.
			null,		// Having.
			order,		// Order by.
			null		// Limit.
		);
	}

	public Cursor getCursor(String[] fields, String selection, String[] args, String order) {
		if (fields == null) {
			fields = mTable.mFieldsName;
		}
		return mDatabase.query(
			mTable.mName,
			fields,
			selection,
			args,
			null,		// Group by.
			null,		// Having.
			order,		// Order by.
			null		// Limit.
		);
	}

	public Cursor getCursorByKey(String[] fields, String key) {
		Cursor cursor = getCursor(
			fields,
			mKey + " = \"?\"",
			new String[] { key },
			null
		);
		cursor.moveToFirst();
		return (!cursor.isAfterLast())? cursor: null;
	}

	public ContentValues getItem(String selection, String[] args) {
		Cursor cursor = getCursor(null, selection, args, null);
		cursor.moveToFirst();
		if (cursor.isAfterLast()) {
			return null;
		}
		ContentValues values = new ContentValues();
		DatabaseUtils.cursorRowToContentValues(cursor, values);
		return values;
	}
	
	public List<ContentValues> getItems(String selection, String[] args) {
		List<ContentValues> result = new ArrayList<ContentValues>();
		Cursor cursor = getCursor(null, selection, args, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ContentValues values = new ContentValues();
			DatabaseUtils.cursorRowToContentValues(cursor, values);
			result.add(values);
			cursor.moveToNext();
		}
		return result;
	}

	public Cursor rawQuery(String sql, String[] args) {
		return mDatabase.rawQuery(sql, args);
	}

	public long insert(ContentValues values) {
		return mDatabase.insert(mTable.mName, null, values);
	}

	public int update(ContentValues values, String whereClause, String[] whereArgs) {
		return mDatabase.update(mTable.mName, values, whereClause, whereArgs);
	}
	
	public int delete(String whereClause, String[] whereArgs) {
		return mDatabase.delete(mTable.mName, whereClause, whereArgs);
	}

	public void deleteAll(){
		mDatabase.delete(mTable.mName, null, null);
	}

	public String parseJsonArrayField(JsonReader reader) throws IOException {
		StringBuilder s = new StringBuilder();
		s.append("|");
		reader.beginArray();
		while (reader.hasNext()) {
			JsonToken token = reader.peek();
			if (token == JsonToken.NUMBER) {
				s.append(String.valueOf(reader.nextInt()));
				s.append("|");
			} else if (token == JsonToken.STRING) {
				s.append(reader.nextString());
				s.append("|");
			} else {
				reader.skipValue();
			}
		}
		reader.endArray();
		return s.toString();
	}

	public void parseJsonField(JsonReader reader, ContentValues values) throws IOException {
		// Identify field.
		String fieldName = reader.nextName();
		int fieldIndex = mTable.searchField(fieldName);
		// Ignore unknown fields.
		if (fieldIndex == -1) {
			reader.skipValue();
			return;
		}
		// Get name and definition.
		String name = mTable.mFieldsName[fieldIndex];
		String definition = mTable.mFieldsDefinition[fieldIndex];
		JsonToken token = reader.peek();
		// Number field.
		if (definition.startsWith("integer")) {
			if (token == JsonToken.NUMBER) {
				values.put(name, reader.nextInt());
			} else if (token == JsonToken.STRING) {
				values.put(name, Integer.valueOf(reader.nextString()));
			} else if (token == JsonToken.BOOLEAN) {
				values.put(name, reader.nextBoolean()? 1: 0);
			} else {
				throw new IOException("Cannot convert value");
			}
		}
		// String field.
		else if (definition.startsWith("text")) {
			if (token == JsonToken.NUMBER) {
				values.put(name, String.valueOf(reader.nextInt()));
			} else if (token == JsonToken.STRING) {
				values.put(name, reader.nextString());
			} else if (token == JsonToken.BOOLEAN) {
				values.put(name, reader.nextBoolean()? "true": "false");
			} else if (token == JsonToken.BEGIN_ARRAY) {
				values.put(name, parseJsonArrayField(reader));
			} else {
				throw new IOException("Cannot convert value");
			}
		}
		// Real field.
		else if (definition.startsWith("real")) {
			if (token == JsonToken.NUMBER) {
				values.put(name, reader.nextDouble());
			} else if (token == JsonToken.STRING) {
				values.put(name, Double.valueOf(reader.nextString()));
			} else if (token == JsonToken.BOOLEAN) {
				values.put(name, reader.nextBoolean()? 1.0: 0.0);
			} else {
				throw new IOException("Cannot convert value");
			}
		}
		else {
			throw new IOException("Field definition unkwnown");
		}
	}
	
	public void loadRows(JsonReader reader) throws IOException {
		reader.beginArray();
		while (reader.hasNext()) {
			ContentValues values = new ContentValues();
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

	public void parseJson(JsonReader reader) throws IOException {
		// Start transaction.
		openWrite();
		mDatabase.beginTransaction();
		try {
			deleteAll();
			loadRows(reader);
			mDatabase.setTransactionSuccessful();
		} finally {
			// End transaction.
			mDatabase.endTransaction();
			close();
		}
	}

	public void loadData(String data) {
		try {
			byte[] bytes = data.getBytes("UTF-8");
			InputStream stream = new ByteArrayInputStream(bytes);
			InputStreamReader streamReader = new InputStreamReader(stream, "UTF-8");
			JsonReader reader = new JsonReader(streamReader);
			parseJson(reader);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static List<ContentValues> getTableItems(Context context, Table table) {
		DataSource source = new DataSource(context, table);
		source.openRead();
		final List<ContentValues> result = source.getItems(null, null);
		source.close();
		return result;
	}
	
	public static List<ContentValues> getTableItems(Context context, Table table, String selection, String[] args) {
		DataSource source = new DataSource(context, table);
		source.openRead();
		final List<ContentValues> result = source.getItems(selection, args);
		source.close();
		return result;
	}
}
