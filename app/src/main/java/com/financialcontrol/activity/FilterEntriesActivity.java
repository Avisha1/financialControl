package com.financialcontrol.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.financialcontrol.R;
import com.financialcontrol.data.AppData;
import com.financialcontrol.data.AppData.AccountType;
import com.financialcontrol.dataObjects.Account;
import com.financialcontrol.dataObjects.Payment;
import com.financialcontrol.dataObjectsAdapters.DatePickerDialogFragment;
import com.financialcontrol.dataObjectsAdapters.DatePickerDialogFragment.GetChosenDateListener;
import com.financialcontrol.utils.DBUtils;
import com.financialcontrol.utils.DateUtils;
import com.financialcontrol.utils.DateUtils.FormatString;

import java.util.ArrayList;
import java.util.Date;

public class FilterEntriesActivity extends AppCompatActivity implements GetChosenDateListener {

	private enum DialogMode{
		ACCOUNTS,PAYMENTS
	}

	RadioGroup typeRadioGroup;
	ArrayList<Account>allAccountsList;
	ArrayList<String>AccountsForDialog;

	ArrayList<Payment>allPaymentsList;
	ArrayList<String> PaymnetsForDialog;

	ArrayAdapter<String>accountsAdapter,paymentsAdapter;

	Button paymentsBtn, accountsBtn,startDateBtn,endDateBtn;
	long account,payment;
	Date startDate,endDate;
	String startDateStr,endDateStr;
	AccountType accType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filter_entries);

		init();
		setDefaultValues();
/*
		Bundle bundle = getIntent().getExtras();
		if(bundle != null){
			accType = AppData.getTypeByInt(bundle.getInt("Type"));
			if (accType == AccountType.INCOME)
				typeRadioGroup.check(R.id.fe_income_rb);
			else if (accType == AccountType.OUTCOME)
				typeRadioGroup.check(R.id.fe_expense_rb);
			else 
				typeRadioGroup.check(R.id.fe_allTypes_rb);
			
			if (bundle.containsKey("Account")){
				account = bundle.getLong("Account");
				
				String accName = getAccountNameById(account);
				accountsBtn.setText(accName.toString());
				
			}
			if (bundle.containsKey("Payment")){
				payment = bundle.getLong("Payment");
				paymentsBtn.setText(getPaymentNameById(payment).toString());
			}
		}
*/

	}

	private void init(){
		accountsBtn = (Button) findViewById(R.id.fe_account_btn);
		paymentsBtn = (Button) findViewById(R.id.fe_payment_btn);
		startDateBtn = (Button) findViewById(R.id.fe_date_from);
		endDateBtn = (Button) findViewById(R.id.fe_date_to);

		initAccounts();
		initPayments();
		initRadioGroup();
	}

	private void setDefaultValues(){
		accountsBtn.setText(getResources().getString(R.string.pick_acccount));
		paymentsBtn.setText(getResources().getString(R.string.pick_payment));
		startDateBtn.setText(getResources().getString(R.string.start_date));
		endDateBtn.setText(getResources().getString(R.string.end_date));
		startDate = endDate = DateUtils.getCurrentDate();
		startDateStr = endDateStr = "";
		typeRadioGroup.check(R.id.fe_allTypes_rb);
		account = payment = -1;
		accType = AccountType.ALL;
	}

	private void initAccounts(){
		allAccountsList = DBUtils.getAccounts(this, null, true);
		AccountsForDialog = new ArrayList<String>();	
	}

	private void initPayments(){
		PaymnetsForDialog = new ArrayList<String>();
		allPaymentsList = DBUtils.getPaymentMethods(this);
		for (Payment pay : allPaymentsList) 
			PaymnetsForDialog.add(pay.getName());
	}

	private void initRadioGroup(){

		typeRadioGroup = (RadioGroup) findViewById(R.id.fe_type_rg);
		typeRadioGroup.check(R.id.fe_allTypes_rb);
		typeRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.fe_expense_rb){
					accType = AccountType.EXPENSE;
				}
				else if (checkedId == R.id.fe_income_rb){
					accType = AccountType.INCOME;
				}
				else{ 
					// all entries
					accType = AccountType.ALL;
				}
				accountsBtn.setText(getResources().getString(R.string.pick_acccount));
			}
		});
	}

	public void SelectAccount (View view){
		showDialog(DialogMode.ACCOUNTS);
	}

	public void SelectPayment (View view){
		showDialog(DialogMode.PAYMENTS);
	}

	private void showDialog (final DialogMode mode){
		final String[] dialogList;
		if (mode == DialogMode.ACCOUNTS){
			initAccountList();
			dialogList = new String[AccountsForDialog.size()];
			int i=0;
			for (String acc : AccountsForDialog) {
				dialogList[i++] = acc;
			}
		}
		else{
			// payments
			dialogList = new String[PaymnetsForDialog.size()];
			int i=0;
			for (String pay : PaymnetsForDialog){
				dialogList[i++] = pay;
			}
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getTitle(mode));
		builder.setSingleChoiceItems(dialogList, -1, new OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int pos) {
				String selection = dialogList[pos];
				if (mode == DialogMode.ACCOUNTS){
					getAccountIdByName(selection);
					accountsBtn.setText(selection.toString());
				}
				else{
					getPaymentIdByName(selection);
					paymentsBtn.setText(selection.toString());
				}
				dialog.dismiss();
			}

		});
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	private String getTitle(DialogMode mode) {
		String title = "";
		if (mode == DialogMode.ACCOUNTS)
			title = getResources().getString(R.string.pick_acccount);
		else
			title = getResources().getString(R.string.pick_payment);
		return title;
	}

	private void initAccountList() {
		AccountsForDialog = new ArrayList<String>();
		if (accType == AccountType.EXPENSE){
			for (Account acc : allAccountsList){
				if(acc.getAccountType() == AccountType.EXPENSE)
					AccountsForDialog.add(acc.getName());
			}
		}
		else if (accType == AccountType.INCOME){
			for (Account acc : allAccountsList){
				if(acc.getAccountType() == AccountType.INCOME)
					AccountsForDialog.add(acc.getName());
			}
		}
		else {
			// All
			for (Account acc : allAccountsList) 
				AccountsForDialog.add(acc.getName());
		}
	}

	private void getAccountIdByName(String name){
		long id = -1;
		for (int i=0; i<allAccountsList.size() && id==-1; i++){
			Account acc = allAccountsList.get(i);
			if (acc.getName().equals(name))
				id = acc.getId();
		}
		account = id;
	}
/*
	private String getAccountNameById(long id){
		String name = "";
		boolean flag = false;
		for (int i=0; i<allAccountsList.size() && !flag; i++){
			Account acc = allAccountsList.get(i);
			if (acc.getId() == id){
				name = acc.getName();
				flag = true;
			}
		}
		return name;
	}
*/
	private void getPaymentIdByName(String name){
		long id = -1;
		for (int i=0; i<allPaymentsList.size() && id==-1; i++){
			Payment pay = allPaymentsList.get(i);
			if (pay.getName().equals(name))
				id = pay.getId();
		}
		payment = id;
	}
/*
	private String getPaymentNameById(long id){
		String name = "";
		boolean flag = false;
		for (int i=0; i<allPaymentsList.size() && !flag; i++){
			Payment pay = allPaymentsList.get(i);
			if (pay.getId() == id){
				name = pay.getName();
				flag = true;
			}
		}
		return name;
	}
*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.filter_entries, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		if (id == R.id.clearFilter){
			setDefaultValues();
		}
		return super.onOptionsItemSelected(item);
	}

	public void SelectDateFrom (View view){
		pickDate(false);
	}

	public void SelectDateTo (View view){
		pickDate(true);
	}

	private void pickDate (boolean hasRange){
		Date date = null;
		
		if (hasRange)
			date = startDate;
		
		DatePickerDialogFragment newFragment = DatePickerDialogFragment.newInstance(date);
		newFragment.show(getFragmentManager(), "datePicker");
	}

	public void FilterEntries (View view){
		Intent showEntries = new Intent();
		showEntries.putExtra("Account", account);
		showEntries.putExtra("Payment", payment );
		showEntries.putExtra("Type", AppData.getTypeAsInt(accType));
		showEntries.putExtra("StartDate", startDateStr);
		showEntries.putExtra("EndDate", endDateStr);
		setResult(RESULT_OK,showEntries);
		finish();
	}

	@Override
	public void onDatePick(Date date, boolean hasStartDate) {
		if (hasStartDate)
			endDate = date;
		else{
			startDate = date;
			endDate = startDate;
		}

		startDateStr = DateUtils.convertDateToString(startDate,FormatString.DB_FORMAT);
		endDateStr = DateUtils.convertDateToString(endDate,FormatString.DB_FORMAT);
		String startDateToShow = DateUtils.convertDateToString(startDate, FormatString.LOCAL_FORMAT);
		String endDateToShow = DateUtils.convertDateToString(endDate, FormatString.LOCAL_FORMAT);
		startDateBtn.setText(startDateToShow);
		endDateBtn.setText(endDateToShow);	
	}



}
