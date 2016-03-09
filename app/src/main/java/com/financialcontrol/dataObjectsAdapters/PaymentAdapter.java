package com.financialcontrol.dataObjectsAdapters;

import java.util.ArrayList;

import com.financialcontrol.R;
import com.financialcontrol.dataObjects.Payment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PaymentAdapter extends ArrayAdapter<Payment> {



	private Context context;
	private ArrayList<Payment>list;
	private int resourceId;

	private class viewHolder{
		RelativeLayout pay_layout;
		TextView pay_name;	
	}

	public PaymentAdapter(Context context, int resource, ArrayList<Payment> list) {
		super(context, resource, list);
		this.context = context;
		this.resourceId = resource;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Payment getItem(int position) {
		return list.get(position);
	}

	@Override
	public int getPosition(Payment item) {
		return list.indexOf(item);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;

		viewHolder holder = null;
		Drawable background = null;

		Payment payment = list.get(position);

		if(row == null)
		{
			holder = new viewHolder();
			row = LayoutInflater.from(context).inflate(resourceId, parent, false);

			holder.pay_layout = (RelativeLayout) row.findViewById(R.id.single_item_layout);
			holder.pay_name = (TextView) row.findViewById(R.id.txt_item_name);

			row.setTag(holder);
		}
		else
			holder = (viewHolder) row.getTag();

		background =  context.getResources().getDrawable(R.drawable.rounded_corners_blue);

		holder.pay_layout.setBackground(background);

		holder.pay_name.setText(payment.getName());

		return row;
	}

}
