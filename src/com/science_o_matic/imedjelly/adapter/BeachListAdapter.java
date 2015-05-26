package com.science_o_matic.imedjelly.adapter;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.science_o_matic.imedjelly.R;
import com.science_o_matic.imedjelly.data.Beach;

public class BeachListAdapter extends BaseAdapter {

    private class ViewHolder {
        public TextView textView;
        public ImageView jellyfishStatus;
    }
    
    private Context mContext;
    private List<Beach> mBeaches;
    private LayoutInflater mInflater;
    
    public BeachListAdapter(Context context, List<Beach> beaches) {
    	mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mBeaches = beaches;
    }
    
    @Override
    public int getCount() {
    	return mBeaches.size();
    }

    @Override
    public Object getItem(int position) {
        if (mBeaches != null && position >= 0 && position < getCount()) {
            return mBeaches.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (mBeaches != null && position >= 0 && position < getCount()) {
            return mBeaches.get(position).getId();
        }
        return 0;
    }
	private void setJellyFishStatus(ImageView view, String status) {
		Resources res = mContext.getResources();
		if (status.equals("NO_WARNING")) {
			view.setImageResource(R.drawable.no_warning_small);
			view.setContentDescription(res.getString(R.string.no_warning));
		}
		else if (status.equals("LOW_WARNING")) {
			view.setImageResource(R.drawable.low_warning_small);
			view.setContentDescription(res.getString(R.string.low_warning));
		}
		else if (status.equals("HIGH_WARNING")) {
			view.setImageResource(R.drawable.high_warning_small);
			view.setContentDescription(res.getString(R.string.high_warning));
		}
		else if (status.equals("VERY_HIGH_WARNING")) {
			view.setImageResource(R.drawable.very_high_warning_small);
			view.setContentDescription(res.getString(R.string.very_high_warning));
		}
	}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;      
        if (view == null) {
            view = mInflater.inflate(R.layout.item_beach_list, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.list_label);
            viewHolder.jellyfishStatus = (ImageView) view.findViewById(R.id.jellyfish_status);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }
        Beach beach = mBeaches.get(position);
        viewHolder.textView.setText(beach.getName());
        setJellyFishStatus(viewHolder.jellyfishStatus, beach.getJellyfish_Status()); 
        return view;
    }
}