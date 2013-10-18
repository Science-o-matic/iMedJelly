package com.science_o_matic.imedjelly.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class GenericListAdapter<T> extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<T> mItems;
    private int mLayoutId;
    private int[] mViewId;

    private class ViewHolder {
    	View[] views = null;

    	public ViewHolder(View view) {
    		views = new View[mViewId.length];
    		for(int i=0; i<mViewId.length; i++) {
    			views[i] = view.findViewById(mViewId[i]);
    		}
    	}
    }

    public GenericListAdapter(Context context, List<T> items,
    		int layoutId, int[] viewId) {
        mInflater = (LayoutInflater) context.getSystemService(
        		Context.LAYOUT_INFLATER_SERVICE);
        mItems = items;
        mLayoutId = layoutId;
        mViewId = viewId;
    }

	@Override
    public int getCount() {
    	return mItems.size();
    }

	@Override
    public Object getItem(int position) {
        if (mItems != null && position >= 0 && position < getCount()) {
            return mItems.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("unchecked")
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;
        if (view == null) {
            view = mInflater.inflate(mLayoutId, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }
        T item = mItems.get(position);
        setView(item, viewHolder.views);
        return view;
    }

    public void setView(T item, View[] views) {}
}
