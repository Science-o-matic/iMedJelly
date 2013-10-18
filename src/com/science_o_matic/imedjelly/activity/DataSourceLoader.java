package com.science_o_matic.imedjelly.activity;

import android.content.Context;
import android.database.Cursor;

public class DataSourceLoader extends SimpleLoader<Cursor> {
	private DataSource mDataSource;
	String[] mFields;
	String mSelection;
	String[] mArgs;
	String mOrder;

	public DataSourceLoader(Context context, DataSource dataSource,
		String[] fields, String selection, String[] args, String order) {
		super(context);
		mDataSource = dataSource;
		mFields = fields;
		mSelection = selection;
		mArgs = args;
		mOrder = order;
	}

	@Override
	public Cursor loadInBackground() {
		return mDataSource.getCursor(
			mFields,
			mSelection,
			mArgs,
			mOrder
		);
	}
}
