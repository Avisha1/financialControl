package com.financialcontrol.dataObjectsAdapters;

import com.financialcontrol.activity.AccountFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

	
	final int pageCount = 2;
	Bundle info;
	boolean isFromMainPage;
	AccountFragment incomeFragment;
	AccountFragment expenseFragment;
	
	public ViewPagerAdapter(FragmentManager fm,AccountFragment incomeFragment,AccountFragment expenseFragment, boolean isFromMainPage) {
		super(fm);
		this.isFromMainPage = isFromMainPage;
		this.incomeFragment = incomeFragment;
		this.expenseFragment = expenseFragment;
	}

	@Override
	public Fragment getItem(int pos) {
		info = new Bundle();
		switch(pos){
		case 0:
			info.putInt("currentPage", pos++);
			incomeFragment.setArguments(info);
			return incomeFragment;
		case 1:
			info.putInt("currentPage", pos++);
			expenseFragment.setArguments(info);
			return expenseFragment;
		}
		return null;
	}

	@Override
	public int getCount() {
		return pageCount;
	}

	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}
}