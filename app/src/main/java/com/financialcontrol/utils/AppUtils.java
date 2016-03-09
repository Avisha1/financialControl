package com.financialcontrol.utils;

import java.util.Locale;

import com.financialcontrol.activity.EntriesActivity;
import com.financialcontrol.data.AppData;
import com.financialcontrol.data.AppData.AccountType;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import android.app.Activity;

public class AppUtils {

	public static final String FORMAT_FLOAT_TWO_DECIMALS = "%.2f";
	/*
	public static String objToStr(Object obj)
	{
		return objToStr(obj, "");
	}

	public static String objToStr(Object obj, String defult)
	{
		if(obj == null)
			return defult;

		if(obj instanceof String)
			return (String)obj;

		String str;
		try
		{
			str = String.format("s", obj);
		}
		catch(Exception ex){
			return defult;
		}
		return str;
	}

	public static int objToInt(Object obj)
	{
		return objToInt(obj, -1);
	}

	public static int objToInt(Object obj, int defult)
	{
		if(obj == null)
			return defult;

		if(obj instanceof Integer)
			return ((Integer)obj).intValue();

		if(obj instanceof String)
			return parseInt(obj);
		try
		{
			int temp = ((Integer)obj).intValue();
			return temp;
		}
		catch (Exception ex)
		{
			return defult;
		}
	}

	private static int parseInt(Object obj)
	{
		int ans = -1;

		try
		{
			int temp = Integer.parseInt(obj.toString());
			ans = temp;
		}
		catch(Exception ex)
		{
			return ans;
		}

		return ans;
	}

	public static double objToDouble(Object obj)
	{
		return objToDouble(obj, -1.0);
	}

	public static double objToDouble(Object obj, double defult)
	{
		if(obj == null)
			return defult;

		try
		{
			Double d = Double.valueOf(objToStr(obj, "-1.0"));
			return d;
		}
		catch(Exception ex)
		{
			return defult;
		}
	}

	public static long objToLong(Object obj)
	{
		return objToLong(obj, -1);
	}

	public static long objToLong(Object obj, long defult)
	{
		if(obj == null)
			return defult;

		try
		{
			Long d = Long.valueOf(objToStr(obj, "-1.0"));
			return d;
		}
		catch(Exception ex)
		{
			return defult;
		}

	}

	public static java.util.Date objToDate(Object obj)
	{
		try
		{
			return toDate( obj );
		}
		catch( ParseException pe )
		{
			pe.printStackTrace();
			return null;
		}
	}

	private static java.util.Date toDate( Object value ) throws ParseException
	{
		if( value == null ) return null;        
		if( value instanceof java.util.Date ) return (java.util.Date)value;
		if( value instanceof String )
		{
			if( "".equals( value ) ) return null;
			return  DateUtils.OUT_DATE_FORMAT.parse( (String)value ) ;
		}

		return  DateUtils.OUT_DATE_FORMAT.parse( value.toString() );
	}

	public static java.sql.Date objToSqlDate( Object value )
	{
		try
		{
			return toSqlDate( value );
		}
		catch( ParseException pe )
		{
			pe.printStackTrace();
			return null;
		}
	}
	/*
	public static java.sql.Date toSqlDate( Object value ) throws ParseException
	{
		if( value == null ) return null;        
		if( value instanceof java.sql.Date ) return (java.sql.Date)value;
		if( value instanceof String )
		{
			if( "".equals( value ) ) return null;
			return new java.sql.Date( DateUtils.OUT_DATE_FORMAT.parse( (String)value ).getTime() );
		}

		return new java.sql.Date( DateUtils.OUT_DATE_FORMAT.parse( value.toString() ).getTime() );
	}
	 */
	/*public static Date strintToDate(String strDate)
	{
		SimpleDateFormat ft = new SimpleDateFormat(Formats.NORAML_DATE);

		Date date = new Date();

		try { 
			date = ft.parse(strDate); 
	        //System.out.println("Parsed date : " + t); 
	    } catch (ParseException e) { 
	        //System.out.println("Not able to parse " + ft);
	    	Log.w("Foramt LOG: ", "Could not parse " + strDate + " to date.");
	    }

		return date;
	}*/
	/*
	public static String getCurrentDate(boolean isExtEnded)
	{
		return new SimpleDateFormat(
				(isExtEnded)? DateUtils.EXTENDED_DATE : DateUtils.NORMAL_DATE)
				.format(new java.util.Date().getTime());	
	}
	 */

	public static String reverseString(String str)
	{
		return new StringBuffer(str).reverse().toString();
	}

	public static String formatTwoDeciamlToStr(Double Value)
	{
		return formatTwoDeciamlToStr(Value, FORMAT_FLOAT_TWO_DECIMALS);
	}

	public static String formatTwoDeciamlToStr(Double Value, String Foramt )
	{
		String Result = String.format(Locale.getDefault(), Foramt, Value);

		return Result;
	}

	public static Double formatTwoDeciamlToDouble(Double Value)
	{
		String Temp = formatTwoDeciamlToStr(Value);

		return Double.parseDouble(Temp);
	}

	public static void showToast(Context context, String message, boolean longMessage)
	{
		Toast toast = Toast.makeText(context, message,
				(longMessage)? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0, 0);

		toast.show();
	}

	public static void hideSoftKeyboard(Activity activity)
	{
		InputMethodManager input = 
				(InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		input.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}

	public static void ShowEntries(Context context,long acc_id,long pay_id,
							AccountType type, String StartDate, String EndDate){
		Intent showEntries = putExtrasToIntent(context, acc_id, pay_id, type, StartDate, EndDate);
		context.startActivity(showEntries);
	}
	
	public static Intent putExtrasToIntent(Context context,long acc_id,long pay_id,
			AccountType type, String StartDate, String EndDate){
		Intent showEntries = new Intent(context, EntriesActivity.class);
		showEntries.putExtra("Account", acc_id);
		showEntries.putExtra("Payment", pay_id );
		showEntries.putExtra("Type", AppData.getTypeAsInt(type));
		showEntries.putExtra("StartDate", StartDate);
		showEntries.putExtra("EndDate", EndDate);
		return showEntries;
	}


}
