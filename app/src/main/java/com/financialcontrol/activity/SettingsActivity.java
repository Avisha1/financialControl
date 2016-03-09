package com.financialcontrol.activity;


import java.util.Date;

import com.financialcontrol.R;
import com.financialcontrol.dataObjectsAdapters.DatePickerDialogFragment;
import com.financialcontrol.dataObjectsAdapters.DatePickerDialogFragment.GetChosenDateListener;
import com.financialcontrol.utils.DateUtils;
import com.financialcontrol.utils.DateUtils.FormatString;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;



public class SettingsActivity extends PreferenceActivity  {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPrefsFragment()).commit();
	}

	public static class MyPrefsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener,GetChosenDateListener, OnPreferenceClickListener{

		private ListPreference filterByPref;
		private ListPreference dayFilterPref;
		private Preference startDateFilter,endDateFilter;
		public Date startDate,endDate;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			addPreferencesFromResource(R.xml.preferences);

			filterByPref = (ListPreference) getPreferenceManager().findPreference("pref_sort_method");
			dayFilterPref = (ListPreference)getPreferenceManager().findPreference("pref_sort_by_day");
			startDateFilter = getPreferenceManager().findPreference("pref_range_start");
			endDateFilter = getPreferenceManager().findPreference("pref_range_end");
			startDate = endDate = DateUtils.getCurrentDate();

			startDateFilter.setOnPreferenceClickListener(this);

			String[]arr = getResources().getStringArray(R.array.sort_entry_by_choice);

			if (filterByPref.getEntry().equals(arr[5]))
				dayFilterPref.setEnabled(true);
			else
				dayFilterPref.setEnabled(false);

			filterByPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					if (newValue.equals(DateUtils.PAY_DAY))
						dayFilterPref.setEnabled(true);
					else
						dayFilterPref.setEnabled(false);

					return true;
				}
			});

			getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

		}

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			updatePreference(findPreference(key));
		}

		private void updatePreference(Preference preference) {
			if (preference instanceof ListPreference){

				ListPreference listPref = (ListPreference)preference;
				listPref.setSummary(listPref.getEntry());
			}
		}
		@Override
		public void onResume() {
			super.onResume();

			for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); ++i) {

				Preference preference = getPreferenceScreen().getPreference(i);

				if (preference instanceof PreferenceGroup) {
					PreferenceGroup preferenceGroup = (PreferenceGroup) preference;
					for (int j = 0; j < preferenceGroup.getPreferenceCount(); ++j) {
						updatePreference(preferenceGroup.getPreference(j));
					}
				}
				else 
					updatePreference(preference);
			}
		}


		@Override
		public boolean onPreferenceClick(Preference preference) {
	/*		String key = preference.getKey();
			if (key.equals("pref_range_start")){
				DatePickerDialogFragment newFragment = DatePickerDialogFragment.newInstance(startDate);
				newFragment.show(getFragmentManager(), "datePicker");
			}
			else if (key.equals("pref_range_end")){
				DatePickerDialogFragment newFragment = DatePickerDialogFragment.newInstance(endDate);
				newFragment.show(getFragmentManager(), "datePicker");
			}
	*/
			return false;
		}

		@Override
		public void onDatePick(Date date, boolean hasStartDate) {
			// TODO Auto-generated method stub
			
		}



		
	}
}
