package com.xhr.sms.utils;

public class MyContacts {
	private String mName;
	private String mTelephone;
	
	
	public MyContacts(String n,String tele){
		this.mName =n;
		this.mTelephone=tele;
	}
	
	public String getName(){
		return this.mName;
	}
	
	public String getTelephone(){
		return this.mTelephone;
	}
}
