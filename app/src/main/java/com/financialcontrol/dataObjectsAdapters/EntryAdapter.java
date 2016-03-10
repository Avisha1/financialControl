package com.financialcontrol.dataObjectsAdapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.financialcontrol.R;
import com.financialcontrol.dataObjects.Entry;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;

import java.util.ArrayList;

public class EntryAdapter extends ArrayAdapter<Entry> {

    Context context;
    ArrayList<Entry> list;
    int resourceId;

    public EntryAdapter(Context context, int resource, ArrayList<Entry> list) {
        super(context, resource, list);
        this.context = context;
        this.list = list;
        this.resourceId = resource;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Entry getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getPosition(Entry item) {
        return list.indexOf(item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        viewHolder holder = null;
        Drawable background = null;

        Entry ent = list.get(position);

        if (row == null) {
            holder = new viewHolder();
            row = LayoutInflater.from(context).inflate(resourceId, parent, false);
            holder.ent_layout = (RelativeLayout) row.findViewById(R.id.single_entry_layout);
            holder.ent_date = (TextView) row.findViewById(R.id.entry_date);
            holder.ent_acc = (TextView) row.findViewById(R.id.entry_account);
            holder.ent_ammount = (TextView) row.findViewById(R.id.entry_sum);
            holder.imageView = (ImageView) row.findViewById(R.id.imageView);
            row.setTag(holder);
        } else
            holder = (viewHolder) row.getTag();

        int entType = ent.getType();

        if (entType == 1) {
            background = ContextCompat.getDrawable(context, R.drawable.rounded_corners_green);
            holder.imageView.setImageDrawable(
                    new IconicsDrawable(context)
                            .icon(MaterialDesignIconic.Icon.gmi_balance_wallet)
                            .color(ContextCompat.getColor(context, R.color.green))
                            .sizeDp(48)
            );
        } else if (entType == 0) {
            background = ContextCompat.getDrawable(context, R.drawable.rounded_corners_red);
            holder.imageView.setImageDrawable(
                    new IconicsDrawable(context)
                            .icon(MaterialDesignIconic.Icon.gmi_balance_wallet)
                            .color(ContextCompat.getColor(context, R.color.red))
                            .sizeDp(24)
            );
        }
        holder.ent_layout.setBackground(background);

        holder.ent_date.setText(ent.getDate());
        holder.ent_acc.setText(ent.getAccountName());
        holder.ent_ammount.setText(String.valueOf(ent.getAmmount()));


        return row;
    }

    private static class viewHolder {
        RelativeLayout ent_layout;
        TextView ent_date;
        TextView ent_acc;
        TextView ent_ammount;
        ImageView imageView;
    }


}
