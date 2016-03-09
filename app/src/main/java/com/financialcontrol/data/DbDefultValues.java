package com.financialcontrol.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import com.financialcontrol.R;
import com.financialcontrol.activity.App;
import com.financialcontrol.data.AppData.AccountType;
import com.financialcontrol.dataObjects.Account;
import com.financialcontrol.dataObjects.Payment;


public class DbDefultValues {

	private static ArrayList<Account> DefAccountsSet = new ArrayList<Account>();
	private static ArrayList<Payment> PayMethSet = new ArrayList<Payment>();

	private DbDefultValues(){}

	private static void InitPayMethods()
	{
		List<String> PayMethods = Arrays.asList(App.getContext().getResources().getStringArray(R.array.default_pay_methods));
		for (String payment : PayMethods)
		{
			PayMethSet.add(new Payment(payment));
		}
	}

	private static void InitAccounts(){
		List<String>outcomeList = Arrays.asList(App.getContext().getResources().getStringArray(R.array.default_accounts_expense));
		for (String name : outcomeList) 
		{
			DefAccountsSet.add(new Account(name, AccountType.EXPENSE));
		}
		
		List<String>incomeList = Arrays.asList(App.getContext().getResources().getStringArray(R.array.default_accounts_income));
		for (String name : incomeList)
		{
			DefAccountsSet.add(new Account(name, AccountType.INCOME));
		}
	}

	public static ArrayList<Payment> GetPaymentsTypes()
	{
		if(PayMethSet.isEmpty())
			InitPayMethods();

		return PayMethSet;
	}


	public static ArrayList<Account> GetDefultAccounts()
	{
		if(DefAccountsSet.isEmpty())
			InitAccounts();

		return DefAccountsSet;
	}


}
