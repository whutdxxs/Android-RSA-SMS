package com.xhr.sms.utils;

public class LastSMS {
	private String mName;
	private String mBody;
	private String mDate;
	private String mThreadId;
	private String mPhoneNumber;
	
	public LastSMS(String phone,String name,String body,String date,String id){
		this.mPhoneNumber=phone;
		this.mName = name;
		this.mBody =body;
		this.mDate = date;
		this.mThreadId=id;
	}
	
	public String getPhoneNumber(){
		return this.mPhoneNumber;
	}
	
	public String getName(){
		return this.mName;
	}
	
	public String getBody(){
		return this.mBody;
	}
	
	public String getDate(){
		return this.mDate;
	}
	
	public String getThreadId(){
		return this.mThreadId;
	}
}
