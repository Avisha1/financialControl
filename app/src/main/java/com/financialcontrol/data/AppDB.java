package com.financialcontrol.data;

import java.util.ArrayList;

import com.financialcontrol.dataObjects.Account;
import com.financialcontrol.dataObjects.Payment;
import com.financialcontrol.utils.DBUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AppDB extends SQLiteOpenHelper{

	private static AppDB DBInstance;

	private static String LOG = AppDB.class.getName();
	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_NAME = "FinacialDB.db";

	public static final String Accounts_TABLE = "Accounts";
	public static final String Entries_TABLE = "Entries";
	public static final String Payment_TABLE = "Payments";

	private static final String createAccounts = 
			"CREATE TABLE IF NOT EXISTS " + Accounts_TABLE + " ("
					+ "ACC_ID INTEGER PRIMARY KEY, "
					+ "ACC_Name TEXT NOT NULL, "
					+ "ACC_TypeCode INTEGER NOT NULL " +");";

	private static final String createEntries = 
			"CREATE TABLE IF NOT EXISTS " + Entries_TABLE + " ("
					+ "ENT_ID INTEGER UNIQUE , "
					+ "ENT_AccId INTEGER, "
					+ "ENT_Sum REAL, "
					+ "ENT_Date TEXT, "
					+ "ENT_Type INTEGER, "
					+ "ENT_Image BLOB, "
					+ "ENT_PayMethodId INTEGER, "
					+ "ENT_Payments INTEGER, "
					+ "ENT_Notes TEXT, "
					+ "ENT_PaymentCount INTEGER, "
					+ "FOREIGN KEY(ENT_AccId) REFERENCES Payments(ACC_ID) ON DELETE CASCADE, " 
					+ "FOREIGN KEY(ENT_PayMethodId) REFERENCES Payments(PAY_ID) ON DELETE CASCADE" +");";

	private static final String createPaymentMethod = 
			"CREATE TABLE IF NOT EXISTS " + Payment_TABLE + " ( " 
					+ "PAY_ID INTEGER PRIMARY KEY, " 
					+ "PAY_Name TEXT" +");";

	public static AppDB getInstance(Context context) {

		// Use the application context, which will ensure that you 
		// don't accidentally leak an Activity's context.
		// See this article for more information: http://bit.ly/6LRzfx
		if (DBInstance == null) {
			DBInstance = new AppDB(context.getApplicationContext());
		}
		return DBInstance;
	}


	private AppDB(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(LOG, "Creating Accounts table");
		db.execSQL(createAccounts);

		Log.d(LOG, "Creating Payments table");
		db.execSQL(createPaymentMethod);

		Log.d(LOG, "Creating Entries table");
		db.execSQL(createEntries);

		Log.d(LOG, "Creating Default Accounts");
		ArrayList<Account>accountsList = DbDefultValues.GetDefultAccounts();
		for (Account account : accountsList)
		{
			ContentValues cv = new ContentValues();
			cv.put(DBConsts.ACC_Name, account.getName());
			cv.put(DBConsts.ACC_TypeCode, DBUtils.getAccountTypeAsInt(account.getAccountType()) );

			db.insert(AppDB.Accounts_TABLE, null, cv);
		}

		Log.d(LOG, "Creating Default Pay Methods");
		ArrayList<Payment>PayList = DbDefultValues.GetPaymentsTypes();
		for (Payment payment : PayList)
		{
			ContentValues cv = new ContentValues();
			cv.put(DBConsts.PAY_Name, payment.getName());

			Long id = db.insert(AppDB.Payment_TABLE, null, cv);
			Log.d("Id", id.toString());
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		// Create upgrade plan
		/*if(oldVersion == 1)
			db.execSQL("ALTER TABLE Entries ADD COLUMN ENT_PayMethodId");*/

		Log.w("DB LOG: ", "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");

		db.execSQL("DROP TABLE IF EXISTS " + Accounts_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + Entries_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + Payment_TABLE);

		onCreate(db);

	}


}
