package com.science_o_matic.imedjelly.activity;

import java.io.Closeable;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public abstract class SimpleLoader<T extends Closeable> extends AsyncTaskLoader<T> {
	private T mItem;

	public SimpleLoader(Context context) {
		super(context);
	}

	/* Runs on a worker thread. */
    @Override
    public abstract T loadInBackground();

    /* Runs on the UI thread. */
    @Override
    public void deliverResult(T item) {
        if (isReset()) {
        	closeItem(item);
            return;
        }
        T oldItem = mItem;
        mItem = item;
        if (isStarted()) {
            super.deliverResult(item);
        }
        if (oldItem != item) {
        	closeItem(oldItem);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mItem != null) {
            deliverResult(mItem);
        }
        if (takeContentChanged() || mItem == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(T item) {
        closeItem(item);
    }

    @Override
    protected void onReset() {
        super.onReset();
        // Ensure the loader is stopped.
        onStopLoading();
        // Close item.
        closeItem(mItem);
        mItem = null;
    }

    void closeItem(T item) {
    	try {
        	// Close the item.
        	if (item != null) {
        		item.close();
        	}
        } catch(Exception e) {
        	// Nothing.
        }
    }
}