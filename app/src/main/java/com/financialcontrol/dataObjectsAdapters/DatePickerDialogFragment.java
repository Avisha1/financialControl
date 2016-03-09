package com.financialcontrol.dataObjectsAdapters;

import java.util.Calendar;
import java.util.Date;

import com.financialcontrol.R;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

public class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

	public interface GetChosenDateListener{
		public void onDatePick(Date date,boolean hasStartDate);
	}

	GetChosenDateListener listener;
	Long date;

	public static DatePickerDialogFragment newInstance(Date date) {
		DatePickerDialogFragment dpdf = new DatePickerDialogFragment();
		if (date != null){
			Bundle args = new Bundle();
			args.putLong("setDate", date.getTime());
			dpdf.setArguments(args);
		}
		return dpdf;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		if (bundle != null){
			date = bundle.getLong("setDate");
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try{
			listener = (GetChosenDateListener)activity;
		} catch (ClassCastException ex){
			ex.printStackTrace();
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		DatePickerDialog dpdf = new DatePickerDialog(getActivity(),this,year,month,day);
		if (date != null)
			dpdf.getDatePicker().setMinDate(date);
		return dpdf;
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		Calendar c = Calendar.getInstance();
		c.set(year, monthOfYear, dayOfMonth);
		Date date = c.getTime();
		boolean hasDate = false;
		if (this.date !=null)
			hasDate = true;
		listener.onDatePick(date,hasDate);

	}


}
