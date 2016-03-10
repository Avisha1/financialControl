package com.financialcontrol.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.financialcontrol.R;
import com.financialcontrol.activity.AccountFragment.OnItemChosenListener;
import com.financialcontrol.data.AppData;
import com.financialcontrol.data.AppData.AccountType;
import com.financialcontrol.dataObjects.Account;
import com.financialcontrol.dataObjectsAdapters.AccountDialogFragment;
import com.financialcontrol.dataObjectsAdapters.AccountDialogFragment.AddNewAccountListener;
import com.financialcontrol.dataObjectsAdapters.ViewPagerAdapter;

public class AccountsTabActivity extends AppCompatActivity implements AddNewAccountListener, OnItemChosenListener{

	ActionBar actionBar;
	ViewPager viewPager;
	ActionBar.Tab incomeTab, expenseTab;
	AccountFragment incomeFragment;
	AccountFragment expenseFragment;
	Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_accounts_tab);

		Bundle bundle = getIntent().getExtras();
		boolean isFromMainPage = false;
		if (bundle != null)
			isFromMainPage = bundle.getBoolean("IsFromMainPage");

		incomeFragment = new AccountFragment(this, AccountType.INCOME, isFromMainPage);
		expenseFragment = new AccountFragment(this, AccountType.EXPENSE, isFromMainPage);

		//ActionBar
		actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);

		viewPager = (ViewPager) findViewById(R.id.pager);

		FragmentManager fm = getSupportFragmentManager();

		//Listener
		ViewPager.SimpleOnPageChangeListener listener = new SimpleOnPageChangeListener(){
			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
				actionBar.setSelectedNavigationItem(position);
			}
		};
		//Assign Listener
		viewPager.setOnPageChangeListener(listener);

		//adapter
		ViewPagerAdapter adapter = new ViewPagerAdapter(fm, incomeFragment, expenseFragment, isFromMainPage);
		viewPager.setAdapter(adapter);

		//Tab Listener
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
				viewPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

			}

			@Override
			public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

			}
		};

		//Create Tabs and set listener
		incomeTab = actionBar.newTab().setText(getResources().getString(R.string.income));
		expenseTab = actionBar.newTab().setText(getResources().getString(R.string.expense));

		incomeTab.setTabListener(tabListener);
		expenseTab.setTabListener(tabListener);

		actionBar.addTab(incomeTab);
		actionBar.addTab(expenseTab);

		//		initialiseTabs();		

	}
	/*
	private void initialiseTabs(){
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		incomeTab = actionBar.newTab().setText(getResources().getString(R.string.income));
		expenseTab = actionBar.newTab().setText(getResources().getString(R.string.expense));

		incomeTab.setTabListener(new TabListener_old(incomeFragment));
		expenseTab.setTabListener(new TabListener_old(expenseFragment));

		actionBar.addTab(incomeTab);
		actionBar.addTab(expenseTab);
	}
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_accounts_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		else if (id == R.id.action_add){
			AccountDialogFragment dialog = new AccountDialogFragment();

			dialog.show(getFragmentManager(), "dialog");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDialogPositiveClick(Account account) {
		AccountType type = account.getAccountType();
		if (type == AccountType.INCOME){
			incomeFragment.adapter.add(account);
		}
		else{
			expenseFragment.adapter.add(account);
		}

	}

	@Override
	public void onItemChosen(Account account) {
		Intent goBackWithResult = new Intent();
		long id = account.getId();
		goBackWithResult.putExtra("AccountId", id);
		AccountType type = account.getAccountType();
		goBackWithResult.putExtra("Type", AppData.getTypeAsInt(type));
		setResult(RESULT_OK,goBackWithResult);
		finish();
	}


}