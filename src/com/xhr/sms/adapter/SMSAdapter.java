package com.xhr.sms.adapter;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xhr.sms.activity.R;
import com.xhr.sms.application.SMSApplication;
import com.xhr.sms.utils.MySMS;

public class SMSAdapter extends BaseAdapter {

	private List<MySMS> mSMSList;
	private Context mContext;
	
	public SMSAdapter(Context context,List<MySMS> list){
		this.mContext=context;
		this.mSMSList=list;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mSMSList.size();
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
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		
		TextView textView;
		
		MySMS mySMS=mSMSList.get(position);
		if(mySMS.getType()=="0"){
			convertView=LayoutInflater.from(mContext).inflate(R.layout.outsms_item,null);
			textView=(TextView)convertView.findViewById(R.id.tv_outsms);
		}else{
			convertView=LayoutInflater.from(mContext).inflate(R.layout.insms_item,null);
			textView=(TextView)convertView.findViewById(R.id.tv_insms);
		}
		
		//显示
		textView.setText(mSMSList.get(position).getBody());
		
		return convertView;
	}

	static class Holder{
		TextView mBody_tv;
	}
}
