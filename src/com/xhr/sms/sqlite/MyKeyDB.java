package com.xhr.sms.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class MyKeyDB {

	static final String DB_NAME="MyKey";
	static final int DB_VERSION=1;
	static final String TABLE="key";
	public static final String C_ID=BaseColumns._ID;
	public static final String C_PRIVATEKEY="privatekey";
	public static final String C_PUBLICKEY="publickey";
	static final String[] C_PUBLICKEY_COLUMNS={ C_PUBLICKEY };
	
	private static final String GET_ALL_ORDER_BY=C_PRIVATEKEY + " DESC";
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
					+ C_PRIVATEKEY + " text, " + C_PUBLICKEY + " text)";
			
			db.execSQL(sql);
		}
	
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("drop table if exists " + TABLE);
			
			onCreate(db);
		}
	}
	
	public MyKeyDB(Context context){
		this.dbHelper=new DbHelper(context);
		this.mContext =context;
	}
	
	public void close(){
		this.dbHelper.close();
	}
	
	public void insertOrIgnore(ContentValues values){
		SQLiteDatabase db=this.dbHelper.getWritableDatabase();
		try{
			db.insertWithOnConflict(TABLE, null, values,SQLiteDatabase.CONFLICT_IGNORE);
		}finally{
			db.close();
		}
	}
	
	public String getPrivateKey(){
		SQLiteDatabase  db=this.dbHelper.getWritableDatabase();
		try{
			Cursor cursor=db.query(TABLE, null, null, null, null, null, GET_ALL_ORDER_BY);
			if(cursor.moveToNext()) {
				String str=cursor.getString(1);
				cursor.close();
				return str;
			}
		}
		finally{
			db.close();
		}
		
		return null;
	}
	
	public String getPublicKey(){
		SQLiteDatabase  db=this.dbHelper.getWritableDatabase();
		try{
			Cursor cursor=db.query(TABLE, null, null, null, null, null, GET_ALL_ORDER_BY);
			if(cursor.moveToNext()) {
				String str=cursor.getString(2);
				cursor.close();
				return str;
			}
		}
		finally{
			db.close();
		}
		
		return null;
	}
	
}
