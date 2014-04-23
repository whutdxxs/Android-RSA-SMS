package com.xhr.sms.activity;

import com.xhr.sms.adapter.ContactsAdapter;

import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;

public class ContactsActivity extends BaseActivity{
	
	private ListView mContactsListView;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);
		
		mContactsListView=(ListView)findViewById(R.id.contacts_lv);
		ContactsAdapter adapter=new ContactsAdapter(this,getContacts());
		mContactsListView.setAdapter(adapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contacts_main, menu);
		return true;
	}
}
