package com.xhr.sms.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.widget.Toast;

public class PublicKeyDB {

	static final String DB_NAME="PublicKey";
	static final int DB_VERSION=1;
	static final String TABLE="key";
	static final String C_ID=BaseColumns._ID;
	public static final String C_PHONENUMBER="phonenumber";
	public static final String C_PUBLICKEY="publickey";
	static final String[] C_PUBLICKEY_COLUMNS={ C_PUBLICKEY };
	static final String[] C_UPDATE={ C_ID };
	
	private static final String GET_ALL_ORDER_BY=C_PHONENUMBER + " DESC";
	private final DbHelper dbHelper;
	private Context mContext;
	
	class DbHelper extends SQLiteOpenHelper{
		public DbHelper(Context context){
			super(context,DB_NAME,null,DB_VERSION);
		}
	
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			String sql="create table if not exists " + TABLE+ " ("+ C_ID + " int primary key, "
					+ C_PHONENUMBER + " text, " + C_PUBLICKEY + " text)";
			
			db.execSQL(sql);
		}
	
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("drop table if exists " + TABLE);
			
			onCreate(db);
		}
	}
	
	public PublicKeyDB(Context context){
		this.dbHelper=new DbHelper(context);
		this.mContext =context;
	}
	
	public void close(){
		this.dbHelper.close();
	}
	
	public void insertOrUpdate(ContentValues values){
		SQLiteDatabase db=this.dbHelper.getWritableDatabase();
		try{
			String number=(String) values.get(C_PHONENUMBER);
			Cursor cursor=db.query(TABLE, C_UPDATE, C_PHONENUMBER + "=" +number, null, null, null, null);
			if(cursor.moveToNext()){
				String[] ids={number};
				db.update(TABLE, values, C_PHONENUMBER+"=?", ids);
				Toast.makeText(mContext, "更新成功", Toast.LENGTH_SHORT).show();
			}else{
				db.insertWithOnConflict(TABLE, null, values,SQLiteDatabase.CONFLICT_IGNORE);
				Toast.makeText(mContext, "添加成功", Toast.LENGTH_SHORT).show();
			}
		}finally{
			db.close();
		}
	}
	
	public Cursor getDatas(){
		SQLiteDatabase  db=this.dbHelper.getWritableDatabase();
		return db.query(TABLE, null, null, null, null, null, GET_ALL_ORDER_BY);
	}
	
	public String getPublicKeyByNumber(String number){
		SQLiteDatabase db=this.dbHelper.getReadableDatabase();
		
		try{
			Cursor cursor=db.query(TABLE, C_PUBLICKEY_COLUMNS, C_PHONENUMBER + "=" +number, null, null, null, null);
			
			try{
				return cursor.moveToNext() ? cursor.getString(0): null;
			}finally{
				cursor.close();
			}
		}finally{
			db.close();
		}
	}
	
}
