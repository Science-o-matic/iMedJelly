package com.science_o_matic.imedjelly.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.science_o_matic.imedjelly.R;
import com.science_o_matic.imedjelly.data.JellyFish;

public class JellyFishesGridAdapter extends BaseAdapter {

    private class ViewHolder {
    	public ImageView imageView;
    }
    
    private JellyFish[] mJellyFishes;
    private LayoutInflater mInflater;
    
    public JellyFishesGridAdapter(Context context, JellyFish[] jellyfishes) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mJellyFishes = jellyfishes;
    }
    
    @Override
    public int getCount() {
    	return (mJellyFishes != null)? mJellyFishes.length: 0;
    }

    @Override
    public Object getItem(int position) {
        if (mJellyFishes != null && position >= 0 && position < getCount()) {
            return mJellyFishes[position];
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (mJellyFishes != null && position >= 0 && position < getCount()) {
            return mJellyFishes[position].getId();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        View view = convertView;
        ViewHolder viewHolder;
        
        if (view == null) {
            view = mInflater.inflate(R.layout.jellyfishes_grid_layout, parent, false);
            
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) view.findViewById(R.id.grid_image);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }
        // Set jellyfish view.
        JellyFish jellyfish = mJellyFishes[position];
        viewHolder.imageView.setImageResource(jellyfish.getPicture());
        return view;
    }
}
