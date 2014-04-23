package com.xhr.sms.utils;

public class MySMS {
	private String mName;
	private String mBody;
	private String mDate;
	private String mType;
	
	public void setName(String name){
		this.mName=name;
	}
	
	public void setBody(String body){
		this.mBody=body;
	}
	
	public MySMS(String name,String body,String date,String type){
		this.mName=name;
		this.mBody=body;
		this.mDate=date;
		this.mType=type;
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
	
	public String getType(){
		return this.mType;
	}
}
