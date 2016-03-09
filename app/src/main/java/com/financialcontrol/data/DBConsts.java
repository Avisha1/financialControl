package com.financialcontrol.data;

public class DBConsts {
	
	//make access to database tables name and columns name from one place, so misspelling mistakes can be avoided
	public static final String DATABASE_NAME = "FinacialDB.db";

	public static final String Accounts_TABLE = "Accounts";
	public static final String Entries_TABLE = "Entries";
	public static final String Payment_TABLE = "Payments";
	
	
	public static final String ACC_ID = "ACC_ID";
	public static final String ACC_Name = "ACC_Name";
	public static final String ACC_TypeCode = "ACC_TypeCode";
	
	
	public static final String ENT_ID = "ENT_ID";
	public static final String ENT_AccId = "ENT_AccId";
	public static final String ENT_Sum = "ENT_Sum";
	public static final String ENT_Date = "ENT_Date";
	public static final String ENT_Type = "ENT_Type";
	public static final String ENT_Image = "ENT_Image";
	public static final String ENT_PayMethodId = "ENT_PayMethodId";
	public static final String ENT_Payments = "ENT_Payments";
	public static final String ENT_Notes = "ENT_Notes";
	public static final String ENT_PaymentCount = "ENT_PaymentCount";
	
	
	public static final String PAY_ID = "PAY_ID";
	public static final String PAY_Name = "PAY_Name";

	
}
