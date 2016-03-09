package com.financialcontrol.dataObjects;
import com.financialcontrol.utils.*;

public class PreferenceParams {

	//input
	private String SortBy;
	//private String BillingDate;

	//output
	private String StartDate = "";
	private String EndDate = "";
	private String RepresentedStr = "";
	
	//TBD
	//More params to come according to preferences page

	public PreferenceParams (String Sort_By)
	{
		SortBy = Sort_By;

		init();
	}

	private void init()
	{	
		if(IsValidSortBy(SortBy))
		{
			String [] arrDatesFilterStr = DateUtils.getDateStartEnd(SortBy);
			StartDate = arrDatesFilterStr[0];
			EndDate = arrDatesFilterStr[1];
			RepresentedStr = arrDatesFilterStr[2];
			//else, values will be empty strings
		}
		
		//TBD
	}

	public String getStartDate()
	{
		return StartDate;
	}

	public String getEndDate()
	{
		return EndDate;
	}

	public String getRepresentedString_ToMainPage()
	{
		return RepresentedStr;
	}

	private boolean IsValidSortBy(String val)
	{
		//365 year, 30 month, 7 week, 1 today
		return (val.equals(DateUtils.NONE) || val.equals(DateUtils.YEAR) || 
				val.equals(DateUtils.MONTH) || val.equals(DateUtils.WEEK) || 
				val.equals(DateUtils.TODAY) || val.equals(DateUtils.PAY_DAY)|| val.equals(DateUtils.RANGE));
	}

}
