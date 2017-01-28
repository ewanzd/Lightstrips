package com.ewanzd.lightstrips;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class SequenceItemAdapter extends ArrayAdapter<SequenceItem> {

    private SparseBooleanArray mSelectedItemsIds;
    private LayoutInflater inflater;

    public SequenceItemAdapter(Context context, List<SequenceItem> items) {
        super(context, R.layout.row_sequence, items);

        mSelectedItemsIds = new SparseBooleanArray();
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        final ViewHolder holder;

        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.row_sequence, null);
            holder.layout = (RelativeLayout) view.findViewById(R.id.row_sequence);
            holder.circle = (RelativeLayout)view.findViewById(R.id.color_fill_circle);
            holder.txv_color_name = (TextView) view.findViewById(R.id.color_name);
            holder.txv_time = (TextView) view.findViewById(R.id.time);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        SequenceItem item = getItem(position);
        if(item.getColor() != 0) holder.circle.setBackgroundColor(item.getColor());
        holder.txv_color_name.setText(String.format("#%06X", 0xFFFFFF & item.getColor()));
        String time_text = String.format("%1$d ms", item.getTime() * 100);
        holder.txv_time.setText(time_text);
        return view;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    @Override
    public void clear() {
        super.clear();

        mSelectedItemsIds.clear();
        notifyDataSetChanged();
    }

    private class ViewHolder {
        RelativeLayout layout;
        RelativeLayout circle;
        TextView txv_color_name;
        TextView txv_time;
    }
}