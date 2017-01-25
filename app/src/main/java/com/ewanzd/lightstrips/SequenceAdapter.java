package com.ewanzd.lightstrips;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 *
 */
public class SequenceAdapter extends ArrayAdapter<Sequence> {

    //private static final int layoutResourceId = R.layout.main_row;

    /**
     *
     * @param context
     * @param items
     */
    public SequenceAdapter(Context context, List<Sequence> items) {
        super(context, R.layout.main_row, items);
    }

    /**
     * Fill View with sequence data.
     * @param position Current position.
     * @param convertView View to fill.
     * @param parent Parent ViewGroup.
     * @return Filled View.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // inflate layout
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.main_row, parent, false);
        }

        // get object
        Sequence item = getItem(position);

        // fill layout elements
        TextView txv_name = (TextView)convertView.findViewById(R.id.txv_sequenceName);
        txv_name.setText(item.getName());

        // set object as tag
        convertView.setTag(item);

        // return View
        return convertView;
    }
}
