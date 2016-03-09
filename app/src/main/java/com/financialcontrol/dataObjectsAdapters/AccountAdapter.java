package com.financialcontrol.dataObjectsAdapters;

import java.util.ArrayList;

import com.financialcontrol.R;
import com.financialcontrol.data.AppData.AccountType;
import com.financialcontrol.dataObjects.Account;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class AccountAdapter extends ArrayAdapter<Account> {
	

	private Context context;
	private ArrayList<Account>list;
	private int resourceId;
	
	private class viewHolder{
		RelativeLayout acc_layout;
		TextView acc_name;	
	}

	public AccountAdapter(Context context, int resource, ArrayList<Account> list) {
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
	public Account getItem(int position) {
		return list.get(position);
	}
	
	@Override
	public int getPosition(Account item) {
		return list.indexOf(item);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		
		viewHolder holder = null;
		Drawable background = null;
		
		Account Acc = list.get(position);
		
		if(row == null)
		{
			holder = new viewHolder();
			row = LayoutInflater.from(context).inflate(resourceId, parent, false);
			
			holder.acc_layout = (RelativeLayout) row.findViewById(R.id.single_item_layout);
			holder.acc_name = (TextView) row.findViewById(R.id.txt_item_name);
			
			row.setTag(holder);
		}
		else
			holder = (viewHolder) row.getTag();
		
		
		AccountType Account_type = Acc.getAccountType();
		
		if(Account_type == AccountType.INCOME)
			background =  context.getResources().getDrawable(R.drawable.rounded_corners_green);
		else
			background = context.getResources().getDrawable(R.drawable.rounded_corners_red);
		
		holder.acc_layout.setBackground(background);

		holder.acc_name.setText(Acc.getName());

		return row;
	}
	
	



}
