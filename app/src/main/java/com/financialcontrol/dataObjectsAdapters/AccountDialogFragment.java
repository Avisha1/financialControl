package com.financialcontrol.dataObjectsAdapters;

import com.financialcontrol.R;
import com.financialcontrol.data.AppData.AccountType;
import com.financialcontrol.dataObjects.Account;
import com.financialcontrol.utils.DBUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class AccountDialogFragment extends DialogFragment {

	private EditText accountName;
	private RadioGroup accountRadioGroup;
	private AccountType type;
	
	public interface AddNewAccountListener {
		public void onDialogPositiveClick(Account account);
	}
	
	AddNewAccountListener listener;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try{
			listener = (AddNewAccountListener)activity;
		} catch (ClassCastException ex){
			ex.printStackTrace();
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_add_account, null);
		builder.setView(view);
		builder.setTitle(R.string.add_an_account);
		type = AccountType.EXPENSE;
		accountName = (EditText) view.findViewById(R.id.dialog_add_account);
		accountRadioGroup = (RadioGroup) view.findViewById(R.id.radioGroupAddAccount);
		
		accountRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.rb_income)
					type = AccountType.INCOME;
				else 
					type = AccountType.EXPENSE;
			}
		});

		builder.setPositiveButton(R.string.add, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String name = accountName.getText().toString();
				if (name.equals(""))
					dialog.dismiss();
				else{
					Account account = new Account(name, type);
					long lastRowId = DBUtils.addAccount(getActivity(), account.getName(), account.getAccountType());
					
					if(lastRowId != -1)
					{
						account.setId(lastRowId);
						listener.onDialogPositiveClick(account);	
					}
					else{
						Toast.makeText(getActivity(), "Could not add account", Toast.LENGTH_SHORT).show();
					}

					dialog.dismiss();
				}
				
				
			}
		});
		builder.setNegativeButton(R.string.cancel, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		return builder.create();
	}
	
}
