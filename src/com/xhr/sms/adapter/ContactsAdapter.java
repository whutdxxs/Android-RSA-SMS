package com.xhr.sms.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xhr.sms.activity.R;
import com.xhr.sms.utils.MyContacts;

public class ContactsAdapter extends BaseAdapter{

	private List<MyContacts> mListContacts;
	private Context mContext;
	
	public ContactsAdapter(Context context,List<MyContacts> listContacts){
		mContext=context;
		mListContacts=listContacts;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mListContacts.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		Holder holder=null;
		
		if(convertView==null){
			convertView=LayoutInflater.from(mContext).inflate(R.layout.contacts_item, null);
			holder=new Holder();
			holder.mName_tv=(TextView)convertView.findViewById(R.id.name_tv);
			holder.mTelephone_tv=(TextView)convertView.findViewById(R.id.telephone_tv);
			convertView.setTag(holder);
		}else{
			holder=(Holder)convertView.getTag();
		}
		
		//显示
		holder.mName_tv.setText(mListContacts.get(position).getName());
		holder.mTelephone_tv.setText(mListContacts.get(position).getTelephone());
		
		return convertView;
	}

	static class Holder{
		TextView mName_tv;
		TextView mTelephone_tv;
	}
}
