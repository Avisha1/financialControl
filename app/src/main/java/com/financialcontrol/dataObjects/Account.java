package com.financialcontrol.dataObjects;

import com.financialcontrol.data.AppData;

public class Account {
	
	private Long Id;
	private String Name;
	private AppData.AccountType AccountType;
	
	public Account(long id, String name, AppData.AccountType Type){
		Id = id;
		Name = name;
		AccountType = Type;
	}
	
	public Account(String name, AppData.AccountType Type){
		Name = name;
		AccountType = Type;
	}
	
	public long getId()
	{
		return this.Id;
	}
	
	public void setId(long id){
		this.Id = id;
	}

	public String getName(){
		return this.Name;
	}

	public AppData.AccountType getAccountType() {
		return AccountType;
	}
	
	//TBD

}
