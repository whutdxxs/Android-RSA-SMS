package com.xhr.sms.activity;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xhr.sms.utils.LastSMS;
import com.xhr.sms.utils.MyContacts;
import com.xhr.sms.utils.MySMS;

import android.app.ActionBar;
import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.PhoneLookup;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.format.Time;


public class BaseActivity extends Activity{
	
	//短信
	static final String ADDRESS = "address"; 
	static final String THREAD_ID = "thread_id";
	static final int ID_COLUMN_INDEX = 0;  
    static final int SNIPPET_COLUMN_INDEX = 1;  
    static final int MSG_COUNT_COLUMN_INDEX = 2;  
    static final int ADDRESS_COLUMN_INDEX = 3;  
    static final int DATE_COLUMN_INDEX = 4;  
    static final int DISPLAY_NAME_COLUMN_INDEX = 1; 
  //联系人查询的字段信息  
    private static final String[] CONCAT_PROJECTION = new String[]{  
        PhoneLookup._ID,  
        PhoneLookup.DISPLAY_NAME  
    }; 
	static final String[] PROJECTION = new String[]{  
	        //cursor 查询 需要_id  
	        "sms.thread_id AS _id",  
	        "sms.body AS snippet",  
	        "groups.msg_count AS msg_count",  
	        "sms.address AS address",  
	        "sms.date AS date"  
	};	
	
	/**获取库Phon表字段**/  
    private static final String[] PHONES_PROJECTION = new String[] {  
        Phone.DISPLAY_NAME, Phone.NUMBER, Phone.PHOTO_ID,Phone.CONTACT_ID, }; 
    
	/**联系人显示名称**/  
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;  
      
    /**电话号码**/  
    private static final int PHONES_NUMBER_INDEX = 1;  
      
    /**头像ID**/  
    private static final int PHONES_PHOTO_ID_INDEX = 2;  
     
    /**联系人的ID**/  
    private static final int PHONES_CONTACT_ID_INDEX = 3;  
    
	private Cursor mContactsCursor;
	private List<MyContacts> mList;  
	public List<LastSMS> mLastSMSs;
	
	private QueryHandler queryHandler;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		mContactsCursor=null;
		mList=new ArrayList<MyContacts>();
		mLastSMSs=new ArrayList<LastSMS>();
		queryHandler = new QueryHandler(getContentResolver());
	}
	
	//取通讯录
	protected List<MyContacts> getContacts(){
		if(mContactsCursor==null){
			//从数据库读取联系人的信息
			
			mContactsCursor=getContentResolver().query(
					Phone.CONTENT_URI, 
					PHONES_PROJECTION, 
					null, 
					null, 
					"sort_key");
			
			if(mContactsCursor!=null){
				while(mContactsCursor.moveToNext()){
					String name = mContactsCursor.getString(PHONES_DISPLAY_NAME_INDEX);  
					String number=mContactsCursor.getString(PHONES_NUMBER_INDEX);
					
					Pattern pattern = Pattern.compile("\\s*|\t|\r|\n");
					Matcher matcher = pattern.matcher(number);
					number=matcher.replaceAll("");
					
					mList.add(new MyContacts(name,number));
				}
			}
			
			mContactsCursor.close();
		}
		return mList;
	}
	
	//查询短信数据  
    protected void startQuery() {  
        /** 
         * 查询会话数据 
         * token Sql id 查询结果的唯一标识  _ID  
         * cookie  用来传递数据  通常VIEW 
         * uri  指定查询数据的地址 
         * projection   相当于SQL查询中 select 中的字段   
         * selection    相当于SQL查询中 where id = ?  
         * selectionArgs ? 
         * orderBy      排序 
         */  
        Uri uri = Uri.parse("content://sms/conversations/");  
        queryHandler.startQuery(0, null, uri, PROJECTION, null, null, " date DESC");  
    }  
      
    //异步查询框架 AsyncQueryHandler  
    private class QueryHandler extends AsyncQueryHandler{  
  
    	private long fristSecondOfToday;  
        public QueryHandler(ContentResolver cr) {  
            super(cr);  
            
          //今天时间00:00:00  
            Time time = new Time();  
            time.setToNow();  
            time.hour = 0;  
            time.minute = 0;  
            time.second = 0;  
            //精准的时间 false       精准的日期  true  
            fristSecondOfToday = time.toMillis(false); 
        }  
  
        @Override  
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {  
            super.onQueryComplete(token, cookie, cursor);             
            //更新数据(adapter 完成数据更新)  
            if(null!=cursor){
            	while(cursor.moveToNext()){
            		LastSMS lastSMS;
            		final String idStr = cursor.getString(ID_COLUMN_INDEX);  
                    String body = cursor.getString(SNIPPET_COLUMN_INDEX);  
                    int msg_count = cursor.getInt(MSG_COUNT_COLUMN_INDEX);  
                    String phonenumber = cursor.getString(ADDRESS_COLUMN_INDEX);  
                    long date = cursor.getLong(DATE_COLUMN_INDEX);
                    
					Pattern pattern = Pattern.compile("\\s*|\t|\r|\n");
					Matcher matcher = pattern.matcher(phonenumber);
					phonenumber=matcher.replaceAll("");
					if(phonenumber.length()>11){
						phonenumber=phonenumber.substring(phonenumber.length()-11, phonenumber.length());
					}
                    //根据电话号码 查询出联系人的信息(名称)  
                    String name = null;  
                    Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phonenumber));  
                    Cursor concatCursor = getContentResolver().query(uri, CONCAT_PROJECTION, null, null, null);  
                    if(concatCursor.moveToFirst()){  
                        //查询到了联系人  
                        name = concatCursor.getString(DISPLAY_NAME_COLUMN_INDEX);  
                    }  
                    
                    concatCursor.close();  
                    
                    //处理短息时间  
                    String dateStr = null;  
                      
                    if((date - fristSecondOfToday) > 0 && (date - fristSecondOfToday) < DateUtils.DAY_IN_MILLIS){  
                        //今天的信息 时间  
                        dateStr = DateFormat.getTimeFormat(getApplicationContext()).format(date);                 
                    } else {  
                        //日期  
                        dateStr = DateFormat.getDateFormat(getApplicationContext()).format(date);  
                    }  
                    
                    if(name!=null){
                    	lastSMS=new LastSMS(phonenumber,name,body,dateStr,idStr);
                    }else{
                    	lastSMS=new LastSMS(phonenumber,phonenumber,body,dateStr,idStr);
                    }
                    
                    mLastSMSs.add(lastSMS);
            	}
            }
            

        }  
          
    }      
    
    public List<MySMS> getSMSs(String thread_id)   
    {   
    	List<MySMS> list=new ArrayList<MySMS>();
    	String[] projection = new String[] {   
			         "_id",   
			         "address",   
			         "person",   
			         "body",   
			         "type",  
			         "date"};   
         
        
       try{   
        Cursor myCursor = managedQuery(Uri.parse("content://sms"),   
          projection, "thread_id=?", new String[]{thread_id} , "date");   
        
        if (myCursor.moveToFirst()) {        
            String name;    
            String phoneNumber;          
            String sms;   
            int type;   
              
            int nameColumn = myCursor.getColumnIndex("person");   
            int phoneColumn = myCursor.getColumnIndex("address");   
            int smsColumn = myCursor.getColumnIndex("body");   
            int typeColum = myCursor.getColumnIndex("type");   
            long date = myCursor.getLong(5);  
            String dateStr = DateFormat.getDateFormat(getApplicationContext()).format(date);  
          
            do {   
                // Get the field values    
                name = myCursor.getString(nameColumn);                
                phoneNumber = myCursor.getString(phoneColumn);   
                sms = myCursor.getString(smsColumn);   
                type = myCursor.getInt(typeColum);   
                  
                if(type == 1){
                	//别人
                	MySMS mySMS=new MySMS(name,sms,dateStr,"1");
                	list.add(mySMS);
                }else{      
                	//自己
                	MySMS mySMS=new MySMS(name,sms,dateStr,"0");
                	list.add(mySMS);
                }  
;   
                if (null==sms)   
                sms="";   
            } while (myCursor.moveToNext());   
        }    
        
       }   
       catch (SQLiteException ex)   
       {   
        ex.printStackTrace();  
       }   
       return list;   
    }
    
    private StringBuilder processResults(Cursor cur) {   
        StringBuilder sb=new StringBuilder();   
        
        
        return sb;   
       
     } 
	
}
