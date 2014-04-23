package com.xhr.sms.adapter;

import java.util.List;

import com.xhr.sms.activity.R;
import com.xhr.sms.utils.MySMS;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

public class DetailsAdapter implements ListAdapter{

	private List<MySMS> mSMSs;
	private Context mContext;
	
	public DetailsAdapter(Context context,List<MySMS> smss){
		this.mContext=context;
		this.mSMSs=smss;
	}
	
	@Override
	public int getCount() {
		return mSMSs.size();
	}

	@Override
	public Object getItem(int position) {
		return mSMSs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int type) {
		return type;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup ViewGroup) {
		TextView textView;
		
		MySMS mySMS=mSMSs.get(position);
		if(mySMS.getType()=="0"){
			convertView=LayoutInflater.from(mContext).inflate(R.layout.outsms_item,null);
			textView=(TextView)convertView.findViewById(R.id.tv_outsms);
		}else{
			convertView=LayoutInflater.from(mContext).inflate(R.layout.insms_item,null);
			textView=(TextView)convertView.findViewById(R.id.tv_insms);
		}
		
		textView.setText(mySMS.getBody());
		
		return convertView;
	}

	@Override
	public int getViewTypeCount() {
		return mSMSs.size();
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver arg0) {
		
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver arg0) {
		
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int arg0) {
		return false;
	}

}
