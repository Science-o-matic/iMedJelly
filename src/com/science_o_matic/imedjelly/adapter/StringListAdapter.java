package com.science_o_matic.imedjelly.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.science_o_matic.imedjelly.R;

public class StringListAdapter extends BaseAdapter {

    private class ViewHolder {
        public TextView textView;
    }
    
    private List<String> mStrings;
    private LayoutInflater mInflater;
    
    public StringListAdapter(Context context, List<String> strings) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mStrings = strings;
    }

    public StringListAdapter(Context context, String[] strings) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mStrings = null;
        if(strings != null) {
        	for(String s: strings) {
        		mStrings.add(s);
        	}
        }
    }
    
    @Override
    public int getCount() {
    	return mStrings.size();
    }

    @Override
    public Object getItem(int position) {
        if (mStrings != null && position >= 0 && position < getCount()) {
            return mStrings.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (mStrings != null && position >= 0 && position < getCount()) {
            return position;
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;      
        if (view == null) {
            view = mInflater.inflate(R.layout.item_string_list, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.list_label);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.textView.setText(mStrings.get(position));
        return view;
    }
}