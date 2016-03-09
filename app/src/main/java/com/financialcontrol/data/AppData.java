package com.financialcontrol.data;

import com.financialcontrol.R;
import com.financialcontrol.activity.App;

public class AppData {

	//income = 1, expense = 0, ALL = 2; for all entries
	public enum AccountType{
		INCOME,EXPENSE,ALL;
	}
	
	public static String getTypeString(AccountType type){
		String str = "";
		
		if (type == AccountType.INCOME)
			str = App.getContext().getResources().getString(R.string.income);
		else if (type == AccountType.EXPENSE)
			str = App.getContext().getResources().getString(R.string.expense);
		else 
			str = App.getContext().getResources().getString(R.string.all);
		
		return str;
	}
	
	public static String getTypeString(int type){
		String str = "";
		
		if (type == 1)
			str = App.getContext().getResources().getString(R.string.income);
		else //if (type == 0)
			str = App.getContext().getResources().getString(R.string.expense);
		
		return str;
	}
	
	public static AccountType getTypeByInt (int type){
		AccountType accType = null;
		
		if (type == 1)
			accType = AccountType.INCOME;
		else if (type == 0)
			accType = AccountType.EXPENSE;
		else
			accType = AccountType.ALL;
		return accType;
	}
	
	public static int getTypeAsInt (AccountType type){
		if (type == AccountType.INCOME)
			return 1;
		else if (type == AccountType.EXPENSE)
			return 0;
		else 
			return 2;
	}
	

}
