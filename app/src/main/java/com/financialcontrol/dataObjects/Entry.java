package com.financialcontrol.dataObjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import com.financialcontrol.utils.DateUtils;
import com.financialcontrol.utils.DateUtils.FormatString;


public class Entry implements Comparable<Entry> {

	private long id;
	private int Acc_Id;
	private String account;
	private double ammount;
	private String date;
	private long Pay_Id;
	private String payMethod;
	private int payments;
	private String paymentDisplay;
	private String notes;
	private int type;
	private int paymentCount;

	public Entry(){}

	public Entry (double sum, String date, int payments,String notes ){
		this.ammount = sum;
		this.date = date;
		this.payments = payments;
		this.notes = notes;
	}
	public Entry (double sum, String date,int payments )
	{
		this(sum, date, payments, "");
	}

	public void setId (long id){
		this.id = id;
	}
	public long getId (){
		return this.id;
	}
	
	public void setAccId (int id){
		this.Acc_Id = id;
	}
	public int getAccId (){
		return this.Acc_Id;
	}
	
	public void setAccountName(String account){
		this.account = account;
	}
	
	public String getAccountName(){
		return this.account;
	}
	
	public void setAmmount (double sum){
		this.ammount = sum;
	}
	public double getAmmount(){
		return this.ammount;
	}

	public void setDate(String date){
		this.date = date;
	}
	public String getDate(){
		return this.date;
	}
	
	public void setPayId (long Pay_Id){
		this.Pay_Id = Pay_Id;
	}
	
	public long getPayId(){
		return this.Pay_Id;
	}
	
	public void setPaymentName(String payMethod){
		this.payMethod = payMethod;
	}
	
	public String getPaymentName(){
		return this.payMethod;
	}

	public void setPayments (int payments){
		this.payments = payments;
	}
	public int getPayments(){
		return payments;
	}

	public String getPaymentDisplay() {
		if (payments > 1)
			paymentDisplay = paymentCount + "/" + payments;
		else
			paymentDisplay = String.valueOf(payments);
		return paymentDisplay;
	}

	public void setPaymentDisplay(String paymentDisplay) {
		this.paymentDisplay = paymentDisplay;
	}

	public void setNotes (String notes){
		this.notes = notes;
	}
	public String getNotes(){
		return this.notes;
	}
	
	public void setType (int type){
		this.type = type;
	}
	public int getType (){
		return this.type;
	}

	public int getPaymentCount() {
		return paymentCount;
	}

	public void setPaymentCount(int paymentCount) {
		this.paymentCount = paymentCount;
	}

	@Override
	public int compareTo(Entry ent) {
		String dateStr = ent.getDate();
		Date entDate = DateUtils.convertStringToFormattedDate(dateStr, FormatString.LOCAL_FORMAT);
		Date thisDate = DateUtils.convertStringToFormattedDate(this.date, FormatString.LOCAL_FORMAT);
		int compare = thisDate.compareTo(entDate); 
		return compare;
	}
	
	public static void sortEntryList (ArrayList<Entry>list){
		Collections.sort(list);
	}
	



}
