package com.financialcontrol.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.financialcontrol.R;
import com.financialcontrol.dataObjects.Payment;
import com.financialcontrol.dataObjects.PreferenceParams;
import com.financialcontrol.dataObjectsAdapters.PaymentAdapter;
import com.financialcontrol.utils.AppUtils;
import com.financialcontrol.utils.DBUtils;

import java.util.ArrayList;

public class PaymentsActivity extends AppCompatActivity implements OnItemClickListener, OnItemLongClickListener {

	private ListView listView;
	private ArrayList<Payment>list;
	public PaymentAdapter adapter;
	private PreferenceParams settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payments);
		listView = (ListView) findViewById(R.id.paymentsListview);
		list = DBUtils.getPaymentMethods(this);

		adapter = new PaymentAdapter(this, R.layout.single_item_object, list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);
		
		SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String sort_by = SP.getString("pref_sort_method", "30");
		settings = new PreferenceParams(sort_by);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.payments_activity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_add) {
			addPaymentDialog();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void addPaymentDialog(){
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle(getResources().getString(R.string.add_payment));
		final EditText inputPayment = new EditText(this);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		inputPayment.setLayoutParams(lp);
		dialog.setView(inputPayment);
		dialog.setPositiveButton(R.string.add, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String name = inputPayment.getText().toString();
				if (name.equals(""))
					dialog.dismiss();
				else{
					Payment payment = new Payment(name);
					long lastRowId = DBUtils.addPaymentMethod(PaymentsActivity.this, name);

					if (lastRowId != -1)
					{
						payment.setId(lastRowId);
						adapter.add(payment);
						adapter.notifyDataSetChanged();
					}
					else{
						Toast.makeText(PaymentsActivity.this, "Could not add paymnet", Toast.LENGTH_SHORT).show();		
					}
					dialog.dismiss();
				}

			}
		});
		dialog.setNegativeButton(R.string.cancel, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();				
			}
		});
		AlertDialog alertDialog = dialog.create();
		alertDialog.setCanceledOnTouchOutside(true);
		alertDialog.show();
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Payment payment = adapter.getItem(position);
		final long pay_id = payment.getId();
		String pay_name = payment.getName();
		boolean hasEntries = DBUtils.hasPayMethodGotEntries(this, pay_id);

		if (hasEntries){
			AppUtils.ShowEntries(PaymentsActivity.this, -1, pay_id, null, settings.getStartDate(),settings.getEndDate());
		}
		else
		{
			String message = getResources().getString(R.string.no_entries);
			Toast.makeText(this, message+pay_name, Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		Payment payment = adapter.getItem(position);
		final long pay_id = payment.getId();
		DeletePaymentDialogFragment dialog = new DeletePaymentDialogFragment(pay_id, position);
		dialog.show(this.getFragmentManager(),"DeletePaymentDialog");
		return false;
	}

	public class DeletePaymentDialogFragment extends DialogFragment{

		private long PAY_ID;
		private int position;

		public DeletePaymentDialogFragment(long PAY_ID, int position) {
			this.PAY_ID = PAY_ID;
			this.position = position;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			super.onCreateDialog(savedInstanceState);
			AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
			dialog.setTitle(R.string.payment_delete);
			String DeleteMessage = "";
			if(DBUtils.IsActivePayMethod(getActivity(), PAY_ID))
				DeleteMessage = getResources().getString(R.string.delete_non_empty_payment);
			else
				DeleteMessage = getResources().getString(R.string.delete_empty_payment);
			dialog.setMessage(DeleteMessage)
			.setCancelable(false)
			.setPositiveButton(R.string.delete,new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					boolean IsDelete = DBUtils.deletePayMethod(getActivity(), PAY_ID);

					if(IsDelete)
					{
						AppUtils.showToast(getActivity(), getResources().getString(R.string.payment_successfully_deleted), false);
						Payment payment = adapter.getItem(position);
						adapter.remove(payment);
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
