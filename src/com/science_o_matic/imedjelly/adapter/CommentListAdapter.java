package com.science_o_matic.imedjelly.adapter;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.science_o_matic.imedjelly.R;
import com.science_o_matic.imedjelly.data.BeachDescription;
import com.science_o_matic.imedjelly.data.Comment;

public class CommentListAdapter extends BaseAdapter {

    private class ViewHolder {
        public TextView username;
        public TextView date;
        public TextView text;
    }
    
    private List<Comment> mComments;
    private LayoutInflater mInflater;
    private Format mDateFormat;
    
    public CommentListAdapter(Context context, List<Comment> comments) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mComments = comments;
        mDateFormat = new SimpleDateFormat(BeachDescription.DateFormat, Locale.ENGLISH);
    }
    
    @Override
    public int getCount() {
    	return mComments.size();
    }

    @Override
    public Object getItem(int position) {
        if (mComments != null && position >= 0 && position < getCount()) {
            return mComments.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (mComments != null && position >= 0 && position < getCount()) {
            return position;
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;      
        if (view == null) {
            view = mInflater.inflate(R.layout.item_comment_list, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.username = (TextView) view.findViewById(R.id.list_label);
            viewHolder.date = (TextView) view.findViewById(R.id.list_date);
            viewHolder.text = (TextView) view.findViewById(R.id.list_text);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }
        Comment comment = mComments.get(position);
        if(comment != null) {
        	viewHolder.username.setText(comment.getUsername());
        	String dateText = "";
        	Date date = comment.getDate();
        	if (date != null) {
        		dateText = mDateFormat.format(date);
        	}
        	viewHolder.date.setText(dateText);
        	viewHolder.text.setText(comment.getText());
        }
        return view;
    }
}