package com.xhr.sms.activity;

import java.util.ArrayList;
import java.util.List;

import com.xhr.sms.adapter.ContactsAdapter;
import com.xhr.sms.adapter.LastSMSAdapter;
import com.xhr.sms.adapter.SMSAdapter;
import com.xhr.sms.application.SMSApplication;
import com.xhr.sms.rsa.MyRSA;
import com.xhr.sms.utils.MySMS;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

	final String SENT_SMS_ACTION = "SENT_SMS_ACTION";  
	private DrawerLayout mDrawerLayout;
	private ListView mContactsList;
	private ListView mSMSList;
	private ActionBarDrawerToggle mDrawerToggle;
	
	private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    
    private SMSAdapter mSMSAdapter;
    private Handler mHandler;
    private List<MySMS> mList;
    
    private Button mBtnSend;
    private EditText mEditText;
    private String mPhoneNumber;
    
    private int mPosition;
    private int mSMSPosition;
    private String mMi;
    private Context mContext;
    
    private BroadcastReceiver sendMessage = new BroadcastReceiver() {  
   	 
        @Override  
        public void onReceive(Context context, Intent intent) {  
            //判断短信是否发送成功  
            switch (getResultCode()) {  
            case Activity.RESULT_OK:  
	            Toast.makeText(context, "短信发送成功", Toast.LENGTH_SHORT).show();  
	            MySMS mySMS=new MySMS("",mMi,"","0");
	            mList.add(mySMS);
	            mSMSAdapter.notifyDataSetChanged();
	            mEditText.setText("");
	            break;  
            default:  
            	Toast.makeText(context, "发送失败", Toast.LENGTH_LONG).show();  
            break;  
            }  
        }  
   }; 
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mList =new ArrayList<MySMS>();
		mPhoneNumber=null;
		mContext=this;
		// 注册广播 发送消息  
	    registerReceiver(sendMessage, new IntentFilter(SENT_SMS_ACTION));  
		startQuery();
		
		mDrawerLayout=(DrawerLayout)findViewById(R.id.drawerlayout);
		mContactsList=(ListView)findViewById(R.id.smss_lv);
		mSMSList=(ListView)findViewById(R.id.sms_lsit);
		mBtnSend=(Button)findViewById(R.id.bt_send_sms);
		mEditText=(EditText)findViewById(R.id.et_smstext);
		
		mTitle = mDrawerTitle = getTitle();
		
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		mDrawerLayout.openDrawer(Gravity.START);
		
		//联系人列表
		//ContactsAdapter adapter=new ContactsAdapter(this,getContacts());
		//mContactsList.setAdapter(adapter);
		
		
		LastSMSAdapter lastSMSAdapter=new LastSMSAdapter(this, mLastSMSs);
		mContactsList.setAdapter(lastSMSAdapter);
		mContactsList.setOnItemClickListener(new DrawerItemClickListener());
		
		//短信列表
		//mList=getSmsInPhone(getPhoneNumber(0));
		mSMSAdapter=new SMSAdapter(MainActivity.this,mList);
		mSMSList.setAdapter(mSMSAdapter);
		
		mSMSList.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view,
					int position, long arg3) {
				// TODO Auto-generated method stub
				mSMSPosition=position;
				String[] items={"解密"};
				new AlertDialog.Builder(MainActivity.this)
				.setTitle("功能")
				.setCancelable(true)
				.setItems(items, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int index) {
						switch(index){
							case 0:
								String mi=mList.get(mSMSPosition).getBody();
								String key=((SMSApplication)getApplication()).getMyKeyDB().getPrivateKey();
								
							try {
								String ming=MyRSA.decryptByPrivateKey(mi, key);
								if(!ming.isEmpty()){
									mList.get(mSMSPosition).setBody(ming);
									mSMSAdapter.notifyDataSetChanged();
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							break;
						}
					}
					
				}).show();
				
				return true;
			}
			
		});
		//button
		mBtnSend.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				String str=mEditText.getText().toString();
				if(mPhoneNumber !=null){
					if(!str.isEmpty() ){
						String key=((SMSApplication)getApplication()).getPublicKeyDB().getPublicKeyByNumber(mPhoneNumber);
						if(key!=null){
							try {
								mMi=MyRSA.encryptByPublicKey(str, key);
								((SMSApplication)getApplication()).sendSMS(mPhoneNumber, mMi);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}else{
							Toast.makeText(mContext, "没有对方的公钥", Toast.LENGTH_LONG).show();;
						}
					}else{
						Toast.makeText(mContext,"短信内容为空", Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(mContext, "收信人为空", Toast.LENGTH_SHORT).show();;
				}
			}
			
		});
		
		// enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        
        mHandler=new Handler(){
        	@Override
        	public void handleMessage(Message msg){
        		super.handleMessage(msg);
        		mSMSAdapter=new SMSAdapter(MainActivity.this,mList);
        		mSMSList.setAdapter(mSMSAdapter);
        		mSMSAdapter.notifyDataSetChanged();
        	}
        };
        
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        
        mDrawerLayout.setDrawerListener(mDrawerToggle);
		
	}
	

	
	/* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	mPosition=position;
        	mList=getSMSs(mLastSMSs.get(position).getThreadId());
        	mPhoneNumber=mLastSMSs.get(position).getPhoneNumber();
        	setTitle(mLastSMSs.get(position).getName());
        	mHandler.sendMessage(new Message());
        }
    }
    

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mContactsList);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
        case R.id.add_publickey:
        	Intent intent=new Intent(this,AddKeyActivity.class);
        	startActivity(intent);
        	break;
        case R.id.my_publickey:
        	Intent i=new Intent(this,KeyActivity.class);
        	startActivity(i);
        	break;
        }
        return super.onOptionsItemSelected(item);
    }
    
	@Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }
	
    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

}
