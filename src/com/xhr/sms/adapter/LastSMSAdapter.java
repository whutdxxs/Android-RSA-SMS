package com.xhr.sms.adapter;

import java.util.List;

import com.xhr.sms.utils.LastSMS;
import com.xhr.sms.activity.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LastSMSAdapter extends BaseAdapter{

	private List<LastSMS> mLastSMS;
	private Context mContext;
	public LastSMSAdapter(Context context,List<LastSMS> last){
		this.mContext=context;
		this.mLastSMS=last;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mLastSMS.size();
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
			convertView=LayoutInflater.from(mContext).inflate(R.layout.lastsms_item,null);
			holder=new Holder();
			holder.name =(TextView)convertView.findViewById(R.id.lastsms_name);
			holder.date =(TextView)convertView.findViewById(R.id.lastsms_date);
			holder.body =(TextView)convertView.findViewById(R.id.lastsms_body);
			convertView.setTag(holder);
		}else{
			holder=(Holder)convertView.getTag();
			
		}
		
		holder.name.setText(mLastSMS.get(position).getName());
		holder.date.setText(mLastSMS.get(position).getDate());
		holder.body.setText(mLastSMS.get(position).getBody());
		
		return convertView;
	}
	
	static class Holder{
		TextView name;
		TextView date;
		TextView body;
	}

}
