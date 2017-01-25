package com.ewanzd.lightstrips;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Danilo on 24.01.2017.
 */

public class SequenceItemAdapter extends BaseAdapter {

    private Context context;
    private List<SequenceItem> items;

    public SequenceItemAdapter(Context context, List<SequenceItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if(convertView == null) {

            // get components by findViewById and link to holder

            SequenceItem row_pos = items.get(position);

            // add data to holder

            convertView.setTag(holder);

        }

        return convertView;
    }

    private class ViewHolder {

    }
}