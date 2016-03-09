package com.financialcontrol.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.financialcontrol.R;
import com.financialcontrol.data.AppData.AccountType;
import com.financialcontrol.dataObjects.PreferenceParams;
import com.financialcontrol.utils.AppConsts;
import com.financialcontrol.utils.AppUtils;
import com.financialcontrol.utils.DBUtils;

public class MainPageActivity extends AppCompatActivity {

	Button incomeDetails,expenseDetails;
	TextView filterTitle,incomeTV,expenseTV,incomeSumTV,expenseSumTV;
	RelativeLayout incomeRL,expenseRL;
	PreferenceParams settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_page);
		initialize();
	}

	private void initialize() {
		//Initialize income view
		View incomeView = findViewById(R.id.mp_income);
		incomeTV = (TextView) incomeView.findViewById(R.id.tl_title);
		incomeTV.setText(getResources().getString(R.string.income));
		incomeTV.setTextColor(getResources().getColor(R.color.green));
		incomeSumTV = (TextView) incomeView.findViewById(R.id.tl_sum);
		incomeDetails = (Button) incomeView.findViewById(R.id.tl_expand_btn);
		incomeDetails.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppUtils.ShowEntries(MainPageActivity.this, -1, -1, AccountType.INCOME,settings.getStartDate(),settings.getEndDate());
			}
		});

		//Initialize expense view
		View expenseView = findViewById(R.id.mp_expense);
		expenseTV = (TextView) expenseView.findViewById(R.id.tl_title);
		expenseTV.setText(getResources().getString(R.string.expense));
		expenseTV.setTextColor(getResources().getColor(R.color.red));
		expenseSumTV = (TextView) expenseView.findViewById(R.id.tl_sum);
		expenseDetails = (Button) expenseView.findViewById(R.id.tl_expand_btn);
		expenseDetails.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppUtils.ShowEntries(MainPageActivity.this, -1, -1, AccountType.EXPENSE,settings.getStartDate(),settings.getEndDate());
			}
		});
		filterTitle = (TextView) findViewById(R.id.mp_title);

	}

	@Override
	protected void onResume() {
		SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

		String sort_by = SP.getString("pref_sort_method", "30");
		settings = new PreferenceParams(sort_by);
		
		Double sum = 0.0;
		
		filterTitle.setText(settings.getRepresentedString_ToMainPage());
		sum = DBUtils.getAccountSum(this, AccountType.INCOME, -1,-1, AppConsts.EntryActivityMode.FILTER,
				settings.getStartDate(), settings.getEndDate());
		incomeSumTV.setText(String.valueOf(sum));

		sum = DBUtils.getAccountSum(this, AccountType.EXPENSE, -1,-1, AppConsts.EntryActivityMode.FILTER,
				settings.getStartDate(), settings.getEndDate());
		expenseSumTV.setText(String.valueOf(sum));
		super.onResume();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_page, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		int id = item.getItemId();
		/*if (id == R.id.action_show_all) {
			AppUtils.ShowEntries(MainPageActivity.this, -1, -1, AccountType.ALL, settings.getStartDate(), settings.getEndDate());
			return true;
		}*/
		if (id == R.id.action_settings){
			Intent showPrefs = new Intent(MainPageActivity.this, SettingsActivity.class);
			startActivity(showPrefs);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void AddEntryBtn (View view){
		Intent AddNewEntry = new Intent(MainPageActivity.this, AddingEntryActivity.class);
		startActivity(AddNewEntry);
	}

	public void ShowPayMethodsBtn (View view){
		Intent ShowPayMethods = new Intent(MainPageActivity.this, PaymentsActivity.class);
		startActivity(ShowPayMethods);
	}

	public void ShowAccountsBtn (View view){
		Intent ShowAccounts = new Intent(MainPageActivity.this, AccountsTabActivity.class);
		ShowAccounts.putExtra("IsFromMainPage", true);
		startActivity(ShowAccounts);
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle(getResources().getString(R.string.leaving_app_title));
		dialogBuilder.setMessage(getResources().getString(R.string.leaving_app_message));
		dialogBuilder.setPositiveButton(getResources().getString(R.string.exit), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(MainPageActivity.this, getResources().getString(R.string.goodbye), Toast.LENGTH_SHORT).show();
				finish();
			}
		});
		dialogBuilder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();	
			}
		});
		AlertDialog alertDialog = dialogBuilder.create();
		alertDialog.show();
	}
}
