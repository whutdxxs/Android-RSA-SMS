package com.xhr.sms.application;

import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.HashMap;

import com.xhr.sms.rsa.MyRSA;
import com.xhr.sms.sqlite.MyKeyDB;
import com.xhr.sms.sqlite.PublicKeyDB;

import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.telephony.SmsManager;
import android.widget.Toast;

public class SMSApplication extends Application{

	final String SENT_SMS_ACTION = "SENT_SMS_ACTION";  
    final String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";
    
    private MyKeyDB mMyKeyDB;
    private PublicKeyDB mPublicKeyDB;
    
    private BroadcastReceiver receiver = new BroadcastReceiver() {  
       	 
            @Override  
            public void onReceive(Context context, Intent intent) {  
                //表示对方成功收到短信  
                Toast.makeText(context, "对方接收成功",Toast.LENGTH_LONG).show();  
            }  
    };     
        
	@Override
	public void onCreate(){
		super.onCreate();
		
		// 注册广播 发送消息  
	    registerReceiver(receiver, new IntentFilter(DELIVERED_SMS_ACTION));
	    
	    mMyKeyDB=new MyKeyDB(getApplicationContext());
	    mPublicKeyDB =new PublicKeyDB(getApplicationContext());
	    
	    //如果数据库里没有存数据，就生成公钥和私钥并存入数据库
	    if(mMyKeyDB.getPrivateKey()==null){
	    	HashMap<String, Object> map;
			try {
				map = MyRSA.getKeys();
				//生成公钥和私钥  
				RSAPublicKey publicKey = (RSAPublicKey) map.get("public");  
		        RSAPrivateKey privateKey = (RSAPrivateKey) map.get("private");
		        
		        //模  
		        String modulus = publicKey.getModulus().toString();  
		        //公钥指数  
		        String public_exponent = publicKey.getPublicExponent().toString();  
		        //私钥指数  
		        String private_exponent = privateKey.getPrivateExponent().toString();  
		        
		        ContentValues values=new ContentValues();
		        
		        values.clear();
		        values.put(MyKeyDB.C_ID, 0);
		        values.put(MyKeyDB.C_PRIVATEKEY, modulus+"@"+private_exponent);
		        values.put(MyKeyDB.C_PUBLICKEY, modulus+"@"+public_exponent);
		        
		        mMyKeyDB.insertOrIgnore(values);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}  
	        
	    }
	}
	
	@Override
	public void onTerminate(){
		super.onTerminate();
	}
	
	public MyKeyDB getMyKeyDB(){
		return this.mMyKeyDB;
	}
	
	public PublicKeyDB getPublicKeyDB(){
		return this.mPublicKeyDB;
	}
	
	public void sendSMS(String phoneNumber,String message){
		SmsManager sms = SmsManager.getDefault();  
		 
	    // create the sentIntent parameter  
	    Intent sentIntent = new Intent(SENT_SMS_ACTION);  
	    PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, sentIntent,  
	        0);  
	 
	    // create the deilverIntent parameter  
	    Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);  
	    PendingIntent deliverPI = PendingIntent.getBroadcast(this, 0,  
	        deliverIntent, 0);  
	 
	    //如果短信内容超过70个字符 将这条短信拆成多条短信发送出去  
	    if (message.length() > 70) {  
	    	SmsManager smsMng = SmsManager.getDefault();
	        ArrayList<String> sendArray = smsMng.divideMessage(message);
	        ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
	        sentIntents.add(sentPI);
	        ArrayList<PendingIntent> deliverIntents = new ArrayList<PendingIntent>();
	        deliverIntents.add(deliverPI);
	        smsMng.sendMultipartTextMessage(phoneNumber, null, sendArray, sentIntents, deliverIntents);
	        
	        // 将发送的短信插入短信库  
            ContentValues values = new ContentValues();  
            // 发送时间  
            values.put("date", System.currentTimeMillis());  
            // 阅读状态：0为未读 1为已读  
            values.put("read", 0);  
            // 类型：1为接收 2为发送  
            values.put("type", 2);  
            // 接收者号码  
            values.put("address", phoneNumber);  
            // 短信内容  
            values.put("body", message);  
            // 插入短信库  
            getContentResolver().insert(Uri.parse("content://sms"), values); 
	    } else {  
	        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliverPI);  
	    }   
	}
}
