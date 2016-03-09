package com.financialcontrol.activity;

import java.util.ArrayList;

import com.financialcontrol.R;
import com.financialcontrol.data.AppData.AccountType;
import com.financialcontrol.dataObjects.Account;
import com.financialcontrol.dataObjects.PreferenceParams;
import com.financialcontrol.dataObjectsAdapters.AccountAdapter;
import com.financialcontrol.utils.AppUtils;
import com.financialcontrol.utils.DBUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class AccountFragment extends ListFragment implements OnItemLongClickListener, OnItemClickListener{

	public AccountAdapter adapter;
	ArrayList<Account>Account_List;
	private boolean isFromMainPage;
	private PreferenceParams settings;

	public interface OnItemChosenListener{
		public void onItemChosen(Account account);
	}
	
	public AccountFragment(Context context,AccountType type,boolean isMainPage) {
		this.isFromMainPage = isMainPage;
		if (type == AccountType.INCOME)
			Account_List = DBUtils.getAccounts(getActivity(), AccountType.INCOME);
		else 
			Account_List = DBUtils.getAccounts(getActivity(), AccountType.EXPENSE);

		adapter = new AccountAdapter(context, R.layout.single_item_object, Account_List);
		setListAdapter(adapter);

	}

	OnItemChosenListener listener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try{
			listener = (OnItemChosenListener) activity;	
		} catch (ClassCastException ex){

		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_accounts_fragment, container,false);
		SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String sort_by = SP.getString("pref_sort_method", "30");
		settings = new PreferenceParams(sort_by);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (isFromMainPage){
			getListView().setOnItemClickListener(this);
			getListView().setOnItemLongClickListener(this);
		}
		else{
			getListView().setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Account acc = (Account) getListAdapter().getItem(position);
					listener.onItemChosen(acc);

				}
			});
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Account acc = (Account) getListAdapter().getItem(position);
		final long ACC_ID = acc.getId();
		String ACC_name = acc.getName();
		boolean hasEntries = DBUtils.hasAccountGotEntries(getActivity(), ACC_ID);

		if (hasEntries){
			AppUtils.ShowEntries(getActivity(), ACC_ID, -1, acc.getAccountType(),settings.getStartDate(),settings.getEndDate());
		}
		else
		{
			String message = getResources().getString(R.string.no_entries);
			Toast.makeText(getActivity(), message+ACC_name, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

		Account accToDelete = adapter.getItem(position);
		final long ACC_ID = accToDelete.getId();
		DeleteAccountDialogFragment dialog = new DeleteAccountDialogFragment(ACC_ID, position);
		dialog.show(getActivity().getFragmentManager(), "DeleteAccountDialog");
		return false;
	}

	public class DeleteAccountDialogFragment extends DialogFragment{

		private long ACC_ID;
		private int position;

		public DeleteAccountDialogFragment(long ACC_ID, int position) {
			this.ACC_ID = ACC_ID;
			this.position = position;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			super.onCreateDialog(savedInstanceState);
			AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
			dialog.setTitle(R.string.account_delete);
			String DeleteMessage = "";
			if(DBUtils.IsActiveAccount(getActivity(), ACC_ID))
				DeleteMessage = getResources().getString(R.string.delete_non_empty_account);
			else
				DeleteMessage = getResources().getString(R.string.delete_empty_account);
			dialog.setMessage(DeleteMessage)
			.setCancelable(false)
			.setPositiveButton(R.string.delete,new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					boolean IsDelete = DBUtils.deleteAccount(getActivity(), ACC_ID);

					if(IsDelete)
					{
						AppUtils.showToast(getActivity(), getResources().getString(R.string.account_successfully_deleted), false);
						Account acc = adapter.getItem(position);
						adapter.remove(acc);
						adapter.notifyDataSetChanged();
					}

				}
			})
			.setNegativeButton(R.string.cancel, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();

				}
			});
			return dialog.create();

		}
	}
}