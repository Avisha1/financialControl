package com.financialcontrol.utils;

import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.util.SparseArray;

import com.financialcontrol.data.*;
import com.financialcontrol.data.AppData.AccountType;
import com.financialcontrol.dataObjects.Account;
import com.financialcontrol.dataObjects.Entry;
import com.financialcontrol.dataObjects.Payment;
import com.financialcontrol.utils.AppConsts.EntryActivityMode;
import com.financialcontrol.utils.DateUtils.FormatString;


public  class DBUtils  {


	private static AppDB dbConnection;
	private static SQLiteDatabase db;

	// init method
	private static SQLiteDatabase getConnection(Context context){
		dbConnection = AppDB.getInstance(context);
		SQLiteDatabase sqld = dbConnection.getWritableDatabase(); 
		return sqld;
	}

	// Add account to DB
	public static long addAccount(Context context, String accountName, AppData.AccountType type)
	{
		db = getConnection(context);

		ContentValues cv = new ContentValues();
		cv.put(DBConsts.ACC_Name, accountName);
		cv.put(DBConsts.ACC_TypeCode, getAccountTypeAsInt(type).toString() );

		long LastRowId = db.insert(DBConsts.Accounts_TABLE, null, cv);

		return LastRowId; 
	}

	//delete account from db
	public static boolean deleteAccount(Context context, long id)
	{
		db = getConnection(context);

		if(!db.isReadOnly())
			db.execSQL("PRAGMA foreign_keys = ON;");

		return db.delete(DBConsts.Accounts_TABLE, DBConsts.ACC_ID + " = " + String.valueOf(id), null) > 0;
	}



	//////////////////////////////
	//////////////////////////////
	////////// 	Entry	//////////
	//////////////////////////////
	//////////////////////////////

	//Adding "quick" entry
	public static void addEntry(Context context, int accountID, double sum)
	{
		addEntry(context, accountID, sum, DateUtils.getCurrentDate());	
	}

	//Adding specific entry (date is not now)
	public static void addEntry(Context context, int accountID, double ammount, Date date)
	{
		db = getConnection(context);

		ContentValues cv = new ContentValues();
		cv.put(DBConsts.ENT_AccId, accountID);
		cv.put(DBConsts.ENT_Sum, ammount);
		cv.put(DBConsts.ENT_Date, DateUtils.convertDateToString(date, FormatString.DB_FORMAT));

		Long newId = db.insert(DBConsts.Entries_TABLE , null, cv);

		/*cv = new ContentValues();
		cv.put("ENT_CODE", newId);
		db.update(DBConsts.Entries_TABLE , cv, DBConsts.ENT_ID + " = " + newId.toString(), null);*/

		//db.close();
	}

	//Add full entry
	public static void addEntry(Context context, long accountID, double ammount, Date date,
			AccountType type, long payMethodId, int payments, String notes, int paymentCount)
	{
		db = getConnection(context);

		db.beginTransaction();

		ContentValues cv = new ContentValues();
		cv.put(DBConsts.ENT_AccId, accountID);
		cv.put(DBConsts.ENT_Sum, ammount);
		cv.put(DBConsts.ENT_Date, DateUtils.convertDateToString(date, DateUtils.FormatString.DB_FORMAT));
		cv.put(DBConsts.ENT_Type, getAccountTypeAsInt(type));
		cv.put(DBConsts.ENT_PayMethodId, payMethodId);
		cv.put(DBConsts.ENT_Payments, payments);
		cv.put(DBConsts.ENT_Notes, notes);
		cv.put(DBConsts.ENT_PaymentCount, paymentCount);

		Long id = db.insert(DBConsts.Entries_TABLE, null, cv);

		if(id > 0)
			db.setTransactionSuccessful();

		cv = new ContentValues();
		cv.put(DBConsts.ENT_ID, id);
		db.update(DBConsts.Entries_TABLE , cv, " ROWID " + " = " + id.toString(), null);

		db.endTransaction();

	}

	public static boolean IsActiveAccount(Context context, Long Acc_Id)
	{
		db = getConnection(context);

		String SqlText = "select * from " + DBConsts.Entries_TABLE + 
				" where " + DBConsts.ENT_AccId + " = " + Acc_Id.toString();

		Cursor c = db.rawQuery(SqlText, null);

		if(c.getCount() > 0)
			return true;

		return false;
	}

	public static boolean IsActivePayMethod (Context context, Long Pay_id)
	{
		db = getConnection(context);

		String SqlText = "select * from " + DBConsts.Entries_TABLE + 
				" where " + DBConsts.ENT_PayMethodId + " = " + Pay_id.toString();

		Cursor c = db.rawQuery(SqlText, null);

		if(c.getCount() > 0)
			return true;

		return false;

	}

	//////////////////////////////
	//////////////////////////////
	/////////  Payment	//////////
	//////////////////////////////
	//////////////////////////////

	// Add payment method
	public static long addPaymentMethod(Context context, String paymentName)
	{
		db = getConnection(context);

		ContentValues cv = new ContentValues();
		cv.put(DBConsts.PAY_Name, paymentName);

		long id = db.insert(DBConsts.Payment_TABLE, null, cv);

		return id;
	}

	// Delete payment
	public static boolean deletePayMethod(Context context, long id)
	{
		db = getConnection(context);

		if(!db.isReadOnly())
			db.execSQL("PRAGMA foreign_keys = ON;");

		return db.delete(DBConsts.Payment_TABLE, DBConsts.PAY_ID + " = " + String.valueOf(id), null) > 0;
	}
	//get all methods - consider using SparseArray
	public static ArrayList<Payment> getPaymentMethods(Context context){
		db = getConnection(context);

		ArrayList<Payment> PayList = new ArrayList<Payment>();

		String SQLText = "select * from " + DBConsts.Payment_TABLE + ";";
		Cursor c = db.rawQuery(SQLText, null);

		while(c.moveToNext())
		{
			int i_id = c.getColumnIndex(DBConsts.PAY_ID);
			int i_name = c.getColumnIndex(DBConsts.PAY_Name);

			Payment pay = new Payment(c.getInt(i_id), c.getString(i_name));

			PayList.add(pay);
		}	
		return PayList;
	}

	// get payment methods - Probably keep this and delete the previous one
	public static SparseArray<String> getPaymentMethod (Context context){
		SparseArray<String>paySarray = new SparseArray<String>();

		db = getConnection(context);

		String SQLText = " select * from " + DBConsts.Payment_TABLE + ";";
		Cursor c = db.rawQuery(SQLText, null);

		while(c.moveToNext())
		{
			int i_id = c.getColumnIndex(DBConsts.PAY_ID);
			int i_name = c.getColumnIndex(DBConsts.PAY_Name);
			paySarray.put(c.getInt(i_id), c.getString(i_name));
		}	
		return paySarray;
	}

	// get payment by id
	public static Payment getPaymentMethod(Context context, long id){

		db = getConnection(context);

		String SQLText = " select * from " + DBConsts.Payment_TABLE +
				" where " + DBConsts.PAY_ID + " = " + String.valueOf(id);
		Cursor c = db.rawQuery(SQLText, null);

		Payment payment = null;

		//supposed to be only one
		while (c.moveToNext()){
			int i_id = c.getColumnIndex(DBConsts.PAY_ID);
			int i_name = c.getColumnIndex(DBConsts.PAY_Name);
			if(c.getLong(i_id) == id)
				payment = new Payment(c.getString(i_name));
		}
		return payment;

	}

	//////////////////////////////
	//////////////////////////////
	/////////  Account	//////////
	//////////////////////////////
	//////////////////////////////

	// Get all accounts
	public static SparseArray<String> getAllAccounts(Context context)
	{
		db = getConnection(context);

		SparseArray<String>accSarray = new SparseArray<String>();

		String SQLText = " select * from " + DBConsts.Accounts_TABLE;
		Cursor c = db.rawQuery(SQLText, null);

		while(c.moveToNext())
		{
			int i_id = c.getColumnIndex(DBConsts.ACC_ID);
			int i_name = c.getColumnIndex(DBConsts.ACC_Name);

			accSarray.put(c.getInt(i_id), c.getString(i_name));
		}
		return accSarray;
	}

	public static SparseArray<String> getAllAccounts(Context context, AccountType type)
	{
		db = getConnection(context);

		SparseArray<String>accSarray = new SparseArray<String>();

		Integer Acc_Type  = getAccountTypeAsInt(type);

		String SQLText = " select * from "+ DBConsts.Accounts_TABLE +
				" where "  + DBConsts.ACC_TypeCode + " = " + Acc_Type.toString();

		Cursor c = db.rawQuery(SQLText, null);

		while(c.moveToNext())
		{
			int i_id = c.getColumnIndex(DBConsts.ACC_ID);
			int i_name = c.getColumnIndex(DBConsts.ACC_Name);

			accSarray.put(c.getInt(i_id), c.getString(i_name));
		}
		return accSarray;
	}

	// Get accounts by type
	public static ArrayList<Account> getAccounts(Context context, AccountType type)
	{
		return getAccounts(context, type, false);
	}

	public static ArrayList<Account> getAccounts(Context context, AccountType type, boolean ShowAllAccounts)
	{
		db = getConnection(context);

		ArrayList<Account> AccList = new ArrayList<Account>();

		String SQLFilter = "";
		if(!ShowAllAccounts)
			SQLFilter =" where " + DBConsts.ACC_TypeCode + " = "+ getAccountTypeAsInt(type);

		String SQLText = " select * from " + DBConsts.Accounts_TABLE + SQLFilter + ";";
		Cursor c = db.rawQuery(SQLText, null);

		while(c.moveToNext())
		{
			int i_id = c.getColumnIndex(DBConsts.ACC_ID);
			int i_name = c.getColumnIndex(DBConsts.ACC_Name);
			int i_type = c.getColumnIndex(DBConsts.ACC_TypeCode);

			Account acc = new Account(
					c.getInt(i_id),
					c.getString(i_name),
					AppData.getTypeByInt(c.getInt(i_type)));

			AccList.add(acc);
		}
		return AccList;
	}

	//this function is an abomination to programming, and should be deleted
	/*public static int getAccountId (Context context, String name){
		int Id = -1;
		SparseArray<String>accSarray = getAllAccounts(context);
		Id = accSarray.indexOfValue(name); //prob won't work :(
		return Id;
	}*/

	//Get account info by id
	public static Account getAccount(Context context, long id){

		db = getConnection(context);

		String SQLText = " select * from " + DBConsts.Accounts_TABLE +
				" where " + DBConsts.ACC_ID + " = " + String.valueOf(id);
		Cursor c = db.rawQuery(SQLText, null);

		Account account = null;

		//supposed to be only one
		while (c.moveToNext()){
			int i_id = c.getColumnIndex(DBConsts.ACC_ID);
			int i_name = c.getColumnIndex(DBConsts.ACC_Name);
			int i_type = c.getColumnIndex(DBConsts.ACC_TypeCode);
			if(c.getLong(i_id) == id)
				account = new Account(id,c.getString(i_name), AppData.getTypeByInt(c.getInt(i_type)));
		}
		return account;

	}

	//Getting sum of all accounts of type
	public static double getAccountSum (Context context, AccountType type)
	{
		//can be either IncomeMode or OutComeMode
		return getAccountSum(context, type, -1, -1, AppConsts.EntryActivityMode.FILTER, "", "");
	}

	public static double getAccountSum (Context context, AccountType type, long acc_id, long pay_id, AppConsts.EntryActivityMode ActivityMode,
			String StartDate, String EndDate)
	{
		double sum = 0;

		db = getConnection(context);

		//	String SQLFilter = getEntrySQLfilter(ActivityMode, type, acc_id,pay_id);
		String SQLFilter = GetSQLfilter(type, acc_id, pay_id, StartDate, EndDate);
		String SQLText = 
				"select SUM("+ DBConsts.ENT_Sum + ") " + 
						" from " + DBConsts.Entries_TABLE + 
						SQLFilter;

		Cursor c = db.rawQuery(SQLText, null);

		if(c.moveToFirst())
			sum = c.getDouble(0);

		sum = AppUtils.formatTwoDeciamlToDouble(sum);


		return sum;
	}

	public static ArrayList<Entry> GetAllEntries(Context context){
		return getEntries(context, AccountType.EXPENSE, -1, -1,"", "");
	}

	public static ArrayList<Entry> GetEntriesByType(Context context, AccountType type){
		return getEntries(context, type, -1, -1, "", "");
	}

	public static ArrayList<Entry> GetEntriesByAccount(Context context, long acc_id){
		return getEntries(context, AccountType.EXPENSE, acc_id, -1, "", "");
	}

	public static ArrayList<Entry> GetEntriesByPayment(Context context, long pay_id){
		return getEntries(context, AccountType.EXPENSE, -1, pay_id, "", "");
	}

	// Get all entries
	public static ArrayList<Entry> getEntries (Context context, AccountType type, long acc_id, long pay_id, String StartDate, String EndDate){

		db = getConnection(context);

		//		String SQLFilter = getEntrySQLfilter(ActivityMode, type, acc_id, pay_id);
		String SQLFilter = GetSQLfilter(type, acc_id, pay_id, StartDate, EndDate);

		String SQLText = " select ACC_Name,PAY_Name, en.*  from " + DBConsts.Entries_TABLE + " as en" +
				" inner join " + DBConsts.Accounts_TABLE + " on en.ENT_AccId = ACC_ID " +
				" inner join " + DBConsts.Payment_TABLE + " on en.ENT_PayMethodId = PAY_ID " +
				SQLFilter + ";";

		Cursor c = db.rawQuery(SQLText, null);
		ArrayList<Entry>list = generateEntryList(context,c);

		return list;
	}
/*
	private static String getEntrySQLfilter (AppConsts.EntryActivityMode ActivityMode, AccountType type, long acc_id, long pay_id ){
		String SQLFilter = "";

		if (ActivityMode == EntryActivityMode.INCOME_MODE || ActivityMode == EntryActivityMode.OUTCOME_MODE)
		{
			SQLFilter = " where " + DBConsts.ENT_Type + " = " + getAccountTypeAsInt(type);
		}
		else if(ActivityMode == EntryActivityMode.ALL_ENTRIES_FROM_ACCOUNT)
		{
			SQLFilter = " where " + DBConsts.ENT_AccId + " = " + String.valueOf(acc_id);
		}
		else if (ActivityMode == EntryActivityMode.ALL_ENTRIES_FROM_PAYMENT)
		{
			SQLFilter = " where " + DBConsts.ENT_PayMethodId + " = " + String.valueOf(pay_id);
		}
		else if (ActivityMode == EntryActivityMode.ALL_ENTRIES_FILTERED)
		{

		}
		else  //    if(ActivityMode == EntryActivityMode.ALL_ENTRIES)
		{
			//do nothing
		}

		return SQLFilter;
	}
*/
	/**
	 * 
	 * @param type
	 * @param acc_id
	 * @param pay_id
	 * @param StartDate
	 * @param EndDate
	 * @return String GetSQLfilter
	 */
	private static String GetSQLfilter (AccountType type, long acc_id, long pay_id, String StartDate, String EndDate){
		String SQLfilter = " WHERE ";
		// An account has been chosen - income/expense
		if (acc_id > 0){
			SQLfilter += GetAccountFilter(acc_id);
			// A payment has been chosen
			if (pay_id > 0){
				SQLfilter += " AND " + GetPaymentFilter(pay_id);
				//A date has been picked
				if (!StartDate.equals(""))
					SQLfilter += " AND " + GetDateFilter(StartDate, EndDate);
				// else No date was chosen
			}
			else{
				if (!StartDate.equals(""))
					SQLfilter += " AND " + GetDateFilter(StartDate, EndDate);
				// else no date
			}
		}
		else{
			if (pay_id > 0){
				SQLfilter += GetPaymentFilter(pay_id);
				if (type == AccountType.ALL){
					if (!StartDate.equals(""))
						SQLfilter += " AND " + GetDateFilter(StartDate, EndDate);
					// else no date
				}
				else{
					SQLfilter += GetTypeFilter(type);
					if (!StartDate.equals(""))
						SQLfilter += " AND " + GetDateFilter(StartDate, EndDate); 
									
					// else no date
				}
			}
			else{
				if (!StartDate.equals("")){
					if (type == AccountType.ALL)
						SQLfilter += GetDateFilter(StartDate, EndDate);
					else
						SQLfilter += GetTypeFilter(type) + " AND "
									+ GetDateFilter(StartDate, EndDate);
				}
				else
					if (type == AccountType.ALL)
						SQLfilter = "";
					else
						SQLfilter += GetTypeFilter(type);
			}
		}

		return SQLfilter;
	}

	private static String GetTypeFilter(AccountType type){
		String SQLFilter = DBConsts.ENT_Type + " = " + getAccountTypeAsInt(type);
		return SQLFilter;
	}

	private static String GetAccountFilter(long acc_id){
		String SQLFilter = DBConsts.ENT_AccId + " = " + String.valueOf(acc_id); 
		return SQLFilter;
	}

	private static String GetPaymentFilter(long pay_id){
		String SQLFilter = DBConsts.ENT_PayMethodId + " = " + String.valueOf(pay_id);
		return SQLFilter;
	}

	private static String GetDateFilter(String StartDate, String EndDate) {
		String query = "";
		if (!StartDate.equals("")){
			query = " ENT_Date BETWEEN " +
					"'" + StartDate + "'" +
					" AND " + "'" + EndDate + "'";
		}
		return query;
	}

	private static ArrayList<Entry> generateEntryList(Context context, Cursor c) {

		ArrayList<Entry>list = new ArrayList<Entry>();

		while (c.moveToNext()){
			int i_id = c.getColumnIndex(DBConsts.ENT_ID);
			int i_acc = c.getColumnIndex(DBConsts.ENT_AccId);
			int i_sum = c.getColumnIndex(DBConsts.ENT_Sum);
			int i_date = c.getColumnIndex(DBConsts.ENT_Date);	
			int i_payMethod = c.getColumnIndex(DBConsts.ENT_PayMethodId);
			int i_payment = c.getColumnIndex(DBConsts.ENT_Payments);
			int i_notes = c.getColumnIndex(DBConsts.ENT_Notes);
			int i_type = c.getColumnIndex(DBConsts.ENT_Type);
			int i_payCount = c.getColumnIndex(DBConsts.ENT_PaymentCount);
			int i_acc_name = c.getColumnIndex(DBConsts.ACC_Name);
			int i_pay_name = c.getColumnIndex(DBConsts.PAY_Name);

			Entry ent = new Entry();

			ent.setId(c.getLong(i_id));
			ent.setAccId(c.getInt(i_acc));
			ent.setAccountName(c.getString(i_acc_name));
			ent.setAmmount(c.getDouble(i_sum));
			ent.setDate(DateUtils.convertDBDateToLocal(c.getString(i_date)));
			ent.setPayId(c.getInt(i_payMethod));
			ent.setPaymentName(c.getString(i_pay_name));
			ent.setPayments(c.getInt(i_payment));
			ent.setNotes(c.getString(i_notes));
			ent.setType(c.getInt(i_type));
			ent.setPaymentCount(c.getInt(i_payCount));
			list.add(ent);
		}
		return list;
	}

	public static boolean hasAccountGotEntries (Context context, long acc_id){
		db = getConnection(context);
		String SQLText = " select * from " + DBConsts.Entries_TABLE + 
				" where " + DBConsts.ENT_AccId + " = " + String.valueOf(acc_id) +";";
		Cursor c = db.rawQuery(SQLText, null);

		return c.moveToNext();
	}

	public static boolean hasPayMethodGotEntries (Context context, long pay_id){
		db = getConnection(context);
		String SQLText = " select * from " + DBConsts.Entries_TABLE + 
				" where " + DBConsts.ENT_PayMethodId + " = " + String.valueOf(pay_id) +";";
		Cursor c = db.rawQuery(SQLText, null);

		return c.moveToNext();
	}

	public static void deleteEntry (Context context, long entry_id){
		db = getConnection(context);

		db.delete(DBConsts.Entries_TABLE, DBConsts.ENT_ID + " = " + String.valueOf(entry_id), null);

	}

	// method for inserting values into DB
	private static String wrappedValue(String value, boolean isLast)
	{
		if(!isLast)
			return "'" + value + "' ,";
		else
			return "'" + value + "'";
	}

	// Generate new id - integer since not many accounts
	public static int getNewAccountID()
	{
		return (int) DatabaseUtils.queryNumEntries(db, DBConsts.Accounts_TABLE);
	}

	// Generate new id - integer since not many payments
	public static int getNewPaymentID()
	{
		return (int) DatabaseUtils.queryNumEntries(db, DBConsts.Payment_TABLE);
	}

	// Generate new id - Long since many entries expected
	public static Long getNewEntryID()
	{
		return DatabaseUtils.queryNumEntries(db, DBConsts.Entries_TABLE);
	}

	public static Integer getAccountTypeAsInt(AccountType type)
	{
		if(type.equals(AppData.AccountType.INCOME))
			return 1;
		else
			return 0;
	}













}
