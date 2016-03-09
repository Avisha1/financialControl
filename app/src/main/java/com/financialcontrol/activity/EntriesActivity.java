package com.financialcontrol.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.financialcontrol.R;
import com.financialcontrol.data.AppData;
import com.financialcontrol.data.AppData.AccountType;
import com.financialcontrol.dataObjects.Account;
import com.financialcontrol.dataObjects.Entry;
import com.financialcontrol.dataObjects.Payment;
import com.financialcontrol.dataObjects.PreferenceParams;
import com.financialcontrol.dataObjectsAdapters.EntryAdapter;
import com.financialcontrol.utils.AppUtils;
import com.financialcontrol.utils.DBUtils;
import com.financialcontrol.utils.DateUtils;

import java.util.ArrayList;


public class EntriesActivity extends AppCompatActivity implements OnItemClickListener, OnItemLongClickListener {

	ListView lstViewEntry;
	EntryAdapter adapter;
	ArrayList<Entry> list;
	TextView sumEntryTV,titleEntriesTV;
	AlertDialog.Builder dialog;
	AlertDialog alertDialog;
	// For account filter
	AccountType type;
	String account;
	// For payment filter
	String payment;

	long acc_id,pay_id;

	//dates
	String StartDate,EndDate;
	PreferenceParams settings;

	EntriesActivity context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entries);

		initViewParams();

		initDatabaseValues(getIntent());
	}


	private void initViewParams(){
		context = this;
		dialog = new AlertDialog.Builder(this);
		sumEntryTV = (TextView) findViewById(R.id.sumEntryTV);
		titleEntriesTV = (TextView) findViewById(R.id.titleEntriesTV);
		lstViewEntry = (ListView) findViewById(R.id.entryLV);
	}

	private void initDatabaseValues(Intent intent){
		initBundleParams(intent);
		setTitles();
		initListView();
		initSum();
	}
	private void initBundleParams (Intent intent){
		if (intent == null){
			acc_id = -1;
			pay_id = -1;
			type = AccountType.ALL;
			StartDate = "";
			EndDate = "";
		}
		else{
			Bundle bundle = intent.getExtras();

			acc_id = bundle.getLong("Account");
			pay_id = bundle.getLong("Payment");
			type = AppData.getTypeByInt(bundle.getInt("Type"));
			StartDate = bundle.getString("StartDate");
			EndDate = bundle.getString("EndDate");
			if (acc_id > 0){
				Account acc = DBUtils.getAccount(this, acc_id);
				account = acc.getName();
			}
			if (pay_id > 0){
				Payment pay = DBUtils.getPaymentMethod(this, pay_id);
				payment = pay.getName();
			}
		}
	}

	private void initListView(){
		list = DBUtils.getEntries(this, type, acc_id,pay_id,StartDate,EndDate);
		adapter = new EntryAdapter(this, R.layout.single_item_entry, list);
		lstViewEntry.setAdapter(adapter);

		lstViewEntry.setOnItemClickListener(this);

		lstViewEntry.setOnItemLongClickListener(this);
	}
	private void setTitles(){
		String newLine = System.getProperty("line.separator");
		StringBuilder ab = new StringBuilder();
		ab.append(GetTypeTitle());

		if (acc_id > 0){
			ab.append(newLine);
			ab.append(account);
		}
		if (pay_id > 0){
			ab.append(newLine);
			ab.append(payment);
		}
		if (!StartDate.equals("")){
			ab.append(newLine);
			ab.append(GetDatesTitle());
		}

		titleEntriesTV.setText(ab.toString());
	}

	private String GetTypeTitle(){
		String str = "";
		if (type == AccountType.INCOME){
			str = (getResources().getString(R.string.income));
		}
		else if (type == AccountType.EXPENSE){
			str = (getResources().getString(R.string.expense));
		}
		else
			str = (getResources().getString(R.string.all));

		return str;
	}

	private String GetDatesTitle(){
		String dateStr = "";
		if (!StartDate.equals("")){
			if (StartDate.equals(EndDate))
				dateStr = DateUtils.convertDBDateToLocal(StartDate);
			else
				dateStr = DateUtils.convertDBDateToLocal(StartDate) + "-"
						+ DateUtils.convertDBDateToLocal(EndDate);
		}
		return dateStr;
	}

	@Override
	protected void onResume() {
		super.onResume();
		Entry.sortEntryList(list);
	}

	/*
	private EntryActivityMode getModeByInt(int mode){

		EntryActivityMode entryActivityMode;

		if (mode == EntryActivityMode.ALL_ENTRIES.GetMode())
			entryActivityMode = EntryActivityMode.ALL_ENTRIES;
		else
			entryActivityMode = EntryActivityMode.FILTER;
		return entryActivityMode;
	}

	private String getActivityTitle()
	{
		String ResultTitle;
		if(entryActivityMode == EntryActivityMode.OUTCOME_MODE)
		{
			ResultTitle = getResources().getString(R.string.expense);
		}
		else if(entryActivityMode == EntryActivityMode.INCOME_MODE)
		{
			ResultTitle = getResources().getString(R.string.income);
		}
		else if(entryActivityMode == EntryActivityMode.ALL_ENTRIES_FROM_ACCOUNT)
		{
			ResultTitle = AppData.getTypeString(type) + ":" + account.toString();
		}
		else if (entryActivityMode == EntryActivityMode.ALL_ENTRIES_FROM_PAYMENT)
		{
			ResultTitle = payment.toString();
		}
		else
		{
			ResultTitle = getResources().getString(R.string.all_entries);
		}

		return ResultTitle;

	}
	 */
	/*
	private void clearEntryList(){
		acc_id = -1;
		pay_id = -1;
		StartDate = "";
		EndDate = "";
		initFilterParameters();
		adapter.notifyDataSetChanged();
		initSum();
	}
	 */
	private void initSum()
	{
		double sum = 0;
		for (Entry entry : list) {
			int ent_type = entry.getType();

			if (ent_type==1)
				sum = sum + entry.getAmmount();
			else
				sum = sum - entry.getAmmount();
		}

		sumEntryTV.setText(AppUtils.formatTwoDeciamlToStr(sum));
		if (sum < 0)
			sumEntryTV.setTextColor(getResources().getColor(R.color.red));
	}

	/*@Override
	public void onBackPressed() {
		Intent goBackWithResult = new Intent(this, MainPage.class);

		//goBackWithResult.putExtra("IsChanged", IsEntryChanged);

		//setResult(RESULT_OK,goBackWithResult);
		finish();d
		//startActivity(goBackWithResult);

		//super.onBackPressed();
	};*/


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.entries, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.filter_entries) { //StartActivityForResult
			Intent openFilterEntries = new Intent(EntriesActivity.this, FilterEntriesActivity.class);
			if (acc_id > 0){
				openFilterEntries.putExtra("Account", acc_id);
				openFilterEntries.putExtra("Type", AppData.getTypeAsInt(type));
			}
			if (pay_id > 0)
				openFilterEntries.putExtra("Payment", pay_id);
			openFilterEntries.putExtra("Type",AppData.getTypeAsInt(type));
			startActivityForResult(openFilterEntries, 1);
			return true;
		}
		else if (id == R.id.clearAll){
			initDatabaseValues(null);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if(resultCode == RESULT_OK){
				initDatabaseValues(data);
			}
		}
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		ContextThemeWrapper ctw = new ContextThemeWrapper( context, android.R.style.Theme_Holo_Light_Dialog );
		final Dialog AboutDialog = new Dialog(ctw);

		AboutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		AboutDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		AboutDialog.setContentView(R.layout.dialog_entry_info);

		TextView ammount = (TextView) AboutDialog.findViewById(R.id.se_ammount_input);
		TextView date = (TextView) AboutDialog.findViewById(R.id.fe_date_tv_input);
		TextView account = (TextView) AboutDialog.findViewById(R.id.fe_account_tv_input);
		TextView payMethod = (TextView) AboutDialog.findViewById(R.id.se_pay_type_input);
		TextView payments = (TextView) AboutDialog.findViewById(R.id.se_payments_input);
		TextView notes = (TextView) AboutDialog.findViewById(R.id.se_notes_input);
		TextView type = (TextView) AboutDialog.findViewById(R.id.se_type_input);
		Entry ent = list.get(position);
		ammount.setText(String.valueOf(ent.getAmmount()));
		date.setText(ent.getDate());
		account.setText(ent.getAccountName());
		payMethod.setText(ent.getPaymentName());
		payments.setText(ent.getPaymentDisplay());
		notes.setText(ent.getNotes());
		type.setText(AppData.getTypeString(ent.getType()));

		AboutDialog.setCanceledOnTouchOutside(true);
		AboutDialog.show();

		/*
		dialog = new AlertDialog.Builder(new ContextThemeWrapper(this,android.R.style.Theme_Holo_Light));
		Entry ent = list.get(position);
		LayoutInflater inflater = (EntriesActivity.this).getLayoutInflater();
		View v = inflater.inflate(R.layout.single_entry_info, null);
		dialog.setView(v);

		TextView ammount = (TextView) v.findViewById(R.id.se_ammount_input);
		TextView date = (TextView) v.findViewById(R.id.fe_account_tv);
		TextView account = (TextView) v.findViewById(R.id.fe_date_tv);
		TextView payMethod = (TextView) v.findViewById(R.id.se_pay_type_input);
		TextView payments = (TextView) v.findViewById(R.id.se_payments_input);
		TextView notes = (TextView) v.findViewById(R.id.se_notes_input);
		TextView type = (TextView) v.findViewById(R.id.se_type_input);

		ammount.setText(String.valueOf(ent.getAmmount()));
		date.setText(ent.getDate());
		account.setText(ent.getAccountName());
		payMethod.setText(ent.getPaymentName());
		payments.setText(ent.getPaymentDisplay());
		notes.setText(ent.getNotes());
		type.setText(AppData.getTypeString(ent.getType()));


		alertDialog = dialog.create();
		alertDialog.setCanceledOnTouchOutside(true);
		alertDialog.show();
		 */

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
		dialog = new AlertDialog.Builder(EntriesActivity.this);
		dialog.setMessage(getResources().getString(R.string.confirm_delete));
		dialog.setPositiveButton(getResources().getString(R.string.delete), new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Entry deleteEntry = list.get(position);

				DBUtils.deleteEntry(EntriesActivity.this, deleteEntry.getId());			
				list.remove(position);
				adapter.notifyDataSetChanged();
				initSum();
			}
		});
		dialog.setNegativeButton(getResources().getString(R.string.cancel), new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		alertDialog = dialog.create();
		alertDialog.show();

		return true;
	}


}
