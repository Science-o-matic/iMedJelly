package com.science_o_matic.imedjelly.activity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.SparseArray;

public class CursorManager implements LoaderManager.LoaderCallbacks<Cursor> {
	private Context mContext;

	// Entries.
	private SparseArray<CursorEntry> mEntries = null;
	
	// Loader manager.
	LoaderManager mLoaderManager;
	LoaderCallbacks<Cursor> mCallbacks = this;

	public class CursorEntry {
		DataSource mSource;
		SimpleCursorAdapter mAdapter;

		String[] mFields = null;
		String mSelection = null;
		String[] mArgs = null;
		String mOrder = null;

		public CursorEntry(DataSource source, SimpleCursorAdapter adapter) {
			mSource = source;
			mAdapter = adapter;
		}

		public void setQuery(String[] fields, String selection,
				String[] args, String order) {
			mFields = fields;
			mSelection = selection;
			mArgs = args;
			mOrder = order;
		}
	}

	public CursorManager(Context context, LoaderManager manager) {
		mContext = context;
		mEntries = new SparseArray<CursorEntry>();
		mLoaderManager = manager;
	}

	public void clear() {
		for (int i=0; i<mEntries.size(); i++) {
			CursorEntry entry = mEntries.valueAt(i);
			entry.mSource.close();
		}
	}

	public CursorEntry getEntry(int id) {
		return mEntries.get(id);
	}

	public CursorEntry addEntry(int id, DataSource source, SimpleCursorAdapter adapter) {
		CursorEntry entry = new CursorEntry(source, adapter);
		entry.mSource.openRead();
		mEntries.put(id, entry);
		return entry;
	}

	public void addCursor(int id,
			DataSource source, SimpleCursorAdapter adapter,
			String[] fields, String selection, String[] args, String order) {
		CursorEntry entry = addEntry(id, source, adapter);
		entry.setQuery(fields, selection, args, order);
		mLoaderManager.initLoader(id, null, mCallbacks);
	}

	public void restartCursor(int id,
			String[] fields, String selection, String[] args, String order) {
		CursorEntry entry = getEntry(id);
		entry.setQuery(fields, selection, args, order);
		mLoaderManager.restartLoader(id, null, mCallbacks);
	}

	public void restartCursor(int id) {
		mLoaderManager.restartLoader(id, null, mCallbacks);
	}

	public SimpleCursorAdapter makeAdapter(int layout, String[] from, int[] to) {
		return new SimpleCursorAdapter(
			mContext,
			layout,
	 		null,
	 		from,
	 		to,
	 		0);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorEntry entry = mEntries.get(id);
		return new DataSourceLoader(
			mContext,
			entry.mSource,
			entry.mFields,
			entry.mSelection,
			entry.mArgs,
			entry.mOrder
		);
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// Data is available, associate the cursor with the adapter.
		CursorEntry entry = mEntries.get(loader.getId());
		entry.mAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// Remove references to a non available cursor.
		CursorEntry entry = mEntries.get(loader.getId());
		entry.mAdapter.swapCursor(null);
	}
}