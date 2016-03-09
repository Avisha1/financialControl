package com.financialcontrol.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.financialcontrol.R;
import com.financialcontrol.activity.App;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class DateUtils {

	public static final String NORMAL_DATE = "dd/MM/yyyy";
	public static final String US_DATE = "MM/dd/yyyy";
	public static final String EXTENDED_DATE = "yyyy/MM/dd H:mm:ss";
	public static final String DB_DATE = "yyyy-MM-dd";

	public enum FormatString{
		LOCAL_FORMAT, US_FORMAT, DB_FORMAT;
	}
	
	public static SimpleDateFormat LOCAL_FORMAT = new SimpleDateFormat(NORMAL_DATE);
	public static SimpleDateFormat US_FORMAT = new SimpleDateFormat(US_DATE);
	public static SimpleDateFormat DB_FORMAT = new SimpleDateFormat(DB_DATE);
	
	//need to make better way to save it
	public final static String NONE = "-1";
	public final static String YEAR = "365";
	public final static String MONTH = "30";
	public final static String WEEK = "7";
	public final static String TODAY = "1";
	public final static String PAY_DAY = "2";
	public final static String RANGE = "100";

	//Get current time (For quick entry and basic initialize for calendars
	public static Date getCurrentDate(){
		return new Date(System.currentTimeMillis());
	}
	
	//Return current date in format: DD/MM/YYYY
	public static String getCurrentDateForShow(){
		return LOCAL_FORMAT.format(getCurrentDate());
	}
	
	// Converting Date object to String object according to given Format
	public static String convertDateToString(Date date, FormatString formatString){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		
		String DateStrResult = "";
		if (formatString == FormatString.LOCAL_FORMAT)
			DateStrResult = LOCAL_FORMAT.format(c.getTime());
		else if (formatString == FormatString.US_FORMAT)
			DateStrResult = US_FORMAT.format(c.getTime());
		else
			DateStrResult = DB_FORMAT.format(c.getTime());
		
		return DateStrResult;
	}
	
	// Converting String object to Date object according to given format
	public static Date convertStringToFormattedDate(String dateToConvert, FormatString wantedFormat){
		Date date = new Date();
		try{
			if (wantedFormat == FormatString.DB_FORMAT)
				date = DB_FORMAT.parse(dateToConvert);
			else if (wantedFormat == FormatString.LOCAL_FORMAT)
				date = LOCAL_FORMAT.parse(dateToConvert);
			else
				date = US_FORMAT.parse(dateToConvert);
		}
		catch (ParseException e){}
		return date;
	}
	
	// Convert 1 string date format to other using the 2 methods above
	public static String convertStringDateToString (FormatString source,String dateToConvert,FormatString target){
		String convertedDate = "";
		Date convertedString = convertStringToFormattedDate(dateToConvert, source);
		convertedDate = convertDateToString(convertedString, target);
		return convertedDate;
	}
	
	//Converting DB date value to Date object for parsing in UI
	public static Date convertDBStringToDate (String dateToConvert){
		Date date = new Date();
		try{
			date = DB_FORMAT.parse(dateToConvert);
		}
		catch (ParseException e){}
		return date;
	}

	//Converting DB date to Local format
	public static String convertDBDateToLocal (String dateStr){
		Date date = new Date();

		try
		{
			date = DB_FORMAT.parse(dateStr);
		}
		catch (ParseException e){}

		return LOCAL_FORMAT.format(date);
	}
	
	//Converting DB date to US format
	public static String convertSystemDateToUS (String dateStr){
		Date date = new Date();

		try
		{
			date = DB_FORMAT.parse(dateStr);
		}
		catch (ParseException e){}

		return US_FORMAT.format(date);
	}

	//Return Date value after adding 1 month
	public static Date addMonthToDate(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, 1);
		return calendar.getTime();
	}
	
	public static String[] getDateStartEnd(String param)
	{
		//0 - first date, 1 - last date, 2 - represented string
		String [] resArr = new String[3];

		Calendar calendar = Calendar.getInstance(Locale.getDefault());

		Integer CurrentYear = calendar.get(Calendar.YEAR);
		Integer CurrentMonth = calendar.get(Calendar.MONTH) + 1; // Note: zero based!
		Integer CurrentDayInMonth = calendar.get(Calendar.DAY_OF_MONTH);

		if (param.equals(NONE)){
			resArr[0] = "";
			resArr[1] = "";
			resArr[2] = App.getContext().getResources().getString(R.string.all);
		}
		else if(param.equals(YEAR))
		{
			calendar.set(CurrentYear, 0, 1);
			resArr[0] = DB_FORMAT.format(calendar.getTime());

			calendar.set(CurrentYear, 11, 31);
			resArr[1] = DB_FORMAT.format(calendar.getTime());
			String title = App.getContext().getResources().getString(R.string.filter_year);
			resArr[2] = title + " " + CurrentYear.toString();
		}
		else if(param.equals(MONTH))
		{
			// -1, because its zero based
			calendar.set(CurrentYear, CurrentMonth - 1, 1);
			resArr[0] = DB_FORMAT.format(calendar.getTime());

			calendar.set(CurrentYear, CurrentMonth - 1, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			resArr[1] = DB_FORMAT.format(calendar.getTime());
			String title = App.getContext().getResources().getString(R.string.filter_month);
			resArr[2] = title + " " + CurrentMonth.toString() + "/" + CurrentYear.toString();
		}
		else if(param.equals(WEEK))
		{
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			resArr[0] = DB_FORMAT.format(calendar.getTime());

			calendar.add(Calendar.DATE, 6);
			//calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
			resArr[1] = DB_FORMAT.format(calendar.getTime());
			String title = App.getContext().getResources().getString(R.string.filter_week);
			resArr[2] = title;

		}
		else if(param.equals(TODAY))
		{
			calendar.set(CurrentYear, CurrentMonth - 1, CurrentDayInMonth);

			resArr[0] = resArr[1] = DB_FORMAT.format(calendar.getTime());
			String title = App.getContext().getResources().getString(R.string.filter_day);
			resArr[2] = title;
		}
		else if (param.equals(PAY_DAY))
		{
			SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(App.getContext());
			String startDate = SP.getString("pref_sort_by_day", "1");
			calendar.set(CurrentYear, CurrentMonth-1, Integer.valueOf(startDate));
			Date start = calendar.getTime();
			resArr[0] = convertDateToString(start, FormatString.DB_FORMAT);
			calendar.add(Calendar.MONTH, 1);
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			Date end = calendar.getTime();
			resArr[1] = convertDateToString(end, FormatString.DB_FORMAT);
	//		String title = App.getContext().getResources().getString(R.string.filter_payday);
			String title = convertDateToString(start, FormatString.LOCAL_FORMAT) + " - " + 
							convertDateToString(end, FormatString.LOCAL_FORMAT);
			resArr[2] = title;
		}
		else
		{
			//RANGE
			resArr[0] = "";
			resArr[1] = "";
			String title = App.getContext().getResources().getString(R.string.filter_dates);
			resArr[2] = title;
		}

		return resArr;
	}

}
