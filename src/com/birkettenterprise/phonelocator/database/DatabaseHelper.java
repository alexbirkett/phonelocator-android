package com.birkettenterprise.phonelocator.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "phonelocator";
	private static final int DATABASE_VERSION = 1;
	private static final String CREATE_DATABASE_QUERY = "CREATE TABLE locations (_id INTEGER PRIMARY KEY AUTOINCREMENT, timestamp INTEGER, latitude REAL, longitude REAL, uploaded INTEGER);";
	
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_DATABASE_QUERY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
