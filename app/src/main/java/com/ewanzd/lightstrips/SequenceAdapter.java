package com.ewanzd.lightstrips;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Collection;
import java.util.List;

/**
 *
 */
public class SequenceAdapter extends ArrayAdapter<Sequence> {

    private SparseBooleanArray mSelectedItemsIds;
    private LayoutInflater inflater;

    /**
     *
     * @param context
     * @param items
     */
    public SequenceAdapter(Context context, List<Sequence> items) {
        super(context, R.layout.main_row, items);

        mSelectedItemsIds = new SparseBooleanArray();
        inflater = LayoutInflater.from(context);
    }

    /**
     * Fill View with sequence data.
     * @param position Current position.
     * @param view View to fill.
     * @param parent Parent ViewGroup.
     * @return Filled View.
     */
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        final ViewHolder holder;

        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.main_row, null);
            holder.txv_name = (TextView) view.findViewById(R.id.txv_sequenceName);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.txv_name.setText(getItem(position).getName());
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
        TextView txv_name;
    }
}
