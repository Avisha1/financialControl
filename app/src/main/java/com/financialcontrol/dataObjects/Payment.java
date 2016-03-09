package com.financialcontrol.dataObjects;

public class Payment {
	
	private long id;
	private String name;
	
	public Payment(String name){
		this.name = name;
	}
	
	public Payment(long id, String name){
		this.id = id;
		this.name = name;
	}
	
	public long getId(){
		return this.id;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setId(long id){
		this.id = id;
	}
	
	public void setName(String name){
		this.name = name;
	}
}
