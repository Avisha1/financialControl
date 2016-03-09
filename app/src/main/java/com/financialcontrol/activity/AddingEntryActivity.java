package com.financialcontrol.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.financialcontrol.R;
import com.financialcontrol.data.AppData;
import com.financialcontrol.data.AppData.AccountType;
import com.financialcontrol.dataObjects.Payment;
import com.financialcontrol.dataObjectsAdapters.DatePickerDialogFragment;
import com.financialcontrol.dataObjectsAdapters.DatePickerDialogFragment.GetChosenDateListener;
import com.financialcontrol.utils.AppUtils;
import com.financialcontrol.utils.DBUtils;
import com.financialcontrol.utils.DateUtils;
import com.financialcontrol.utils.DateUtils.FormatString;

import java.util.ArrayList;
import java.util.Date;



public class AddingEntryActivity extends AppCompatActivity implements GetChosenDateListener {

	// Widgets
	ActionBar actionBar;
	EditText txtAmmountEntered, txtPaymentsEntered, txtDetailsEntered;
	TextView chosenDate,chosenAccount,chosenPayment;
	RadioButton rb_debit, rb_payments;
	RadioGroup rg_transaction;
	Button btnDate, btnAccounts, btnPayments, btnSave, btnCancel;

	double sum = 0.0;
	long accountId = -1;
	long payMethodId = -1;
	String dateToShow = "";
	Date date;
	Transaction transaction;

	AccountType type = AccountType.EXPENSE;
	int payments = 1;

	public enum Transaction{
		DEBIT,PAYMENTS;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_entry);
		initialise();
	}

	private void initialise() {
		actionBar = getActionBar();
		date = new Date();
		txtAmmountEntered = (EditText)findViewById(R.id.ae_et_ammount);
		txtPaymentsEntered = (EditText)findViewById(R.id.ae_et_numPayment);
		txtDetailsEntered = (EditText)findViewById(R.id.ae_et_notes);

		chosenDate = (TextView)findViewById(R.id.ae_tv_date_choice);
		dateToShow = DateUtils.getCurrentDateForShow();
		chosenDate.setText(dateToShow);
		chosenAccount = (TextView) findViewById(R.id.ae_tv_account_choice);
		chosenPayment = (TextView) findViewById(R.id.ae_tv_payment_choice);
		transaction = Transaction.PAYMENTS;
		rg_transaction = (RadioGroup) findViewById(R.id.ae_rg_transaction);

		chosenDate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				pickDate();

			}
		});

		chosenAccount.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				pickAccount();
			}
		});

		chosenPayment.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				pickPayment();
			}

		});

		rg_transaction.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.ae_rb_debit){
					transaction = Transaction.DEBIT;
				}
				else if (checkedId == R.id.ae_rb_payments){
					transaction = Transaction.PAYMENTS;
				}

			}
		});

		//hide keyboard when user touches outside of a view
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.ae_layout);
		layout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event){
				AppUtils.hideSoftKeyboard(AddingEntryActivity.this);
				return false;
			}
		});
	}

	private void pickDate (){
		DatePickerDialogFragment newFragment = DatePickerDialogFragment.newInstance(date);
		newFragment.show(getFragmentManager(), "datePicker");
	}

	private void pickAccount ()
	{		
		Intent pickAccount = new Intent(this, AccountsTabActivity.class);
		startActivityForResult(pickAccount, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if(resultCode == RESULT_OK){

				long id = data.getLongExtra("AccountId", -1);
				int typeInt = data.getIntExtra("Type", -1);
				String acc_name = DBUtils.getAccount(AddingEntryActivity.this, id).getName();

				chosenAccount.setText(acc_name);
				accountId = id;
				type = AppData.getTypeByInt(typeInt);
			}
		}
	}	

	private void pickPayment (){

		final ArrayList<Payment>payList = DBUtils.getPaymentMethods(this);
		String[] payListStr = new String[payList.size()];

		int i=0;
		for (Payment payment : payList)
			payListStr[i++] = payment.getName();

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

		dialogBuilder.setTitle(getResources().getString(R.string.pick_payment));
		dialogBuilder.setSingleChoiceItems(payListStr, -1, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int position) {
				String payment = payList.get(position).getName();
				payMethodId = getPaymentId(payList,payment);
				chosenPayment.setText(payment);
				dialog.dismiss();
			}
		});

		AlertDialog alertDialog = dialogBuilder.create();
		alertDialog.setCanceledOnTouchOutside(true);
		alertDialog.show();

	}

	private long getPaymentId(ArrayList<Payment>payList, String pay_name){
		long id = -1;
		for (int i=0; i<payList.size() && id==-1; i++){
			if (payList.get(i).getName().equals(pay_name)) 
				id = payList.get(i).getId();
		}
		return id;
	}

	public void saveInfoBtn (View view){
		boolean isInfoValid = validateInfo();

		if (isInfoValid){		
			if (transaction == Transaction.PAYMENTS)
				sum = sum / payments;
			for (int i=1; i<=payments; i++){
				DBUtils.addEntry(this, accountId, sum, date, type, payMethodId , payments, txtDetailsEntered.getText().toString(), i);
				date = DateUtils.addMonthToDate(date);
			}

			Toast.makeText(this, getResources().getString(R.string.entry_saved), Toast.LENGTH_SHORT).show();
			goToHomePage();
		}
	}

	private boolean validateInfo() {
		boolean valid = false;
		String sumStr = txtAmmountEntered.getText().toString();
		
		if (sumStr.equals("")) 
			txtAmmountEntered.setError(getResources().getString(R.string.enter_amount));
		else
		{
			sum = Double.parseDouble(sumStr);
			String paymentText = txtPaymentsEntered.getText().toString();
			
			if (!paymentText.equals(""))
				payments = Integer.parseInt(txtPaymentsEntered.getText().toString());
			
			if (accountId != -1 && payMethodId!=-1 && payments>=1)
				valid = true;
			else
			{
				if (accountId == -1)
					chosenAccount.setError(getResources().getString(R.string.pick_acccount));
				if (payMethodId == -1)
					chosenPayment.setError(getResources().getString(R.string.pick_payment));
				if (payments < 1){
					String message = getResources().getString(R.string.wrong_payments);
					Toast.makeText(this,message.toString(), Toast.LENGTH_SHORT).show();
				}
			}
		}
		return valid;
	}

	public void cancelEntryBtn (View view){
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		boolean enteredInfo = checkEnteredValues();

		if (enteredInfo)
			showExitDialog();
		else 
			goToHomePage();
	}

	private boolean checkEnteredValues() {
		String sum = txtAmmountEntered.getText().toString();

		if (!sum.equals(""))
			return true;

		return false;
	}

	private void goToHomePage(){

		finish();
	}
	private void showExitDialog(){
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setMessage(getResources().getString(R.string.changes_will_not_save));
		dialogBuilder.setPositiveButton(getResources().getString(R.string.proceed), new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				goToHomePage();
			}
		});
		dialogBuilder.setNegativeButton(getResources().getString(R.string.cancel), new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		AlertDialog alertDialog = dialogBuilder.create();
		alertDialog.show();
	}

	@Override
	public void onDatePick(Date date,boolean hasStartDate) {
		this.date = date;
		dateToShow = DateUtils.convertDateToString(date, FormatString.LOCAL_FORMAT);
		chosenDate.setText(dateToShow);

	}



}


