package com.xhr.sms.activity;

import com.xhr.sms.application.SMSApplication;
import com.xhr.sms.sqlite.MyKeyDB;
import com.xhr.sms.sqlite.PublicKeyDB;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddKeyActivity extends BaseActivity{
	
	private EditText mPhoneNumber;
	private EditText mKey;
	private Button mBtnAdd;
	private PublicKeyDB mPublicKeyDB;
	private Activity mActivity;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addkey);
		mActivity=this;
		mPhoneNumber=(EditText)findViewById(R.id.add_phonenumber);
		mKey=(EditText)findViewById(R.id.add_key);
		mBtnAdd=(Button)findViewById(R.id.btn_add);
		mPublicKeyDB=((SMSApplication)getApplication()).getPublicKeyDB();
		
		mBtnAdd.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String phoneNumber=mPhoneNumber.getText().toString();
				String key=mKey.getText().toString();
				
				if(!phoneNumber.isEmpty() && !key.isEmpty()){
					ContentValues values=new ContentValues();
					values.put(PublicKeyDB.C_PHONENUMBER, phoneNumber);
					values.put(PublicKeyDB.C_PUBLICKEY, key);
					
					mPublicKeyDB.insertOrUpdate(values);
					
					mActivity.finish();
				}
			}
			
		});
	}
}
