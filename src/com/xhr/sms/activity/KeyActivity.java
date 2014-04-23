package com.xhr.sms.activity;

import com.xhr.sms.application.SMSApplication;
import com.xhr.sms.rsa.MyRSA;
import com.xhr.sms.sqlite.MyKeyDB;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class KeyActivity extends BaseActivity{
	
	private Button mBtn;
	private TextView mTextView;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_mypublickey);
		mBtn=(Button)findViewById(R.id.btn_copy);
		mTextView=(TextView)findViewById(R.id.tv_mypublickey);
		
		MyKeyDB myKeyDB=((SMSApplication)getApplication()).getMyKeyDB();
		
		if(myKeyDB!=null){
			String publicKey=myKeyDB.getPublicKey();
			if(publicKey!=null){
				mTextView.setText(publicKey);
			}
		}
		
		mBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				ClipboardManager clip = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
				String str=mTextView.getText().toString();
				clip.setText(str); // 复制
				Toast.makeText(view.getContext(), "复制成功", Toast.LENGTH_SHORT).show();;
			}
			
		});
	}
}
