/**
 * 
 *  Copyright 2011 Birkett Enterprise Ltd
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * 
 */

package com.birkettenterprise.phonelocator.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

public class UpdateLogDatabase extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "phonelocator_update_log";
	private static final int DATABASE_VERSION = 1;
	
	/**
	 * Update table stores successful and unsuccessful update attempts. Each
	 * update can have 1 or more locations.
	 */
	private static final String UPDATE_TABLE = "updates";
	
	/**
	 * Location table stores information about the location that was updated.
	 * The coordinates are currently not stored due to privacy issues.
	 */
	private static final String LOCATION_TABLE = "locations";
	
	public static final String TIMESTAMP_COLUMN = "timestamp";
	public static final String PROVIDER_COLUMN = "provider";
	public static final String ERROR_COLUMN = "error";
	public static final String ID_COLUMN = "_id";
	
	public static final String DESC = " DESC";
	
	private static final String CREATE_UPDATE_TABLE_QUERY = "CREATE TABLE " + UPDATE_TABLE + " (" + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TIMESTAMP_COLUMN + " INTEGER, " + ERROR_COLUMN + " TEXT );";
	private static final String CREATE_LOCATION_QUERY =   "CREATE TABLE " + LOCATION_TABLE + " (" + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TIMESTAMP_COLUMN + " INTEGER, " + PROVIDER_COLUMN + " TEXT );";
	
	
	public UpdateLogDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_UPDATE_TABLE_QUERY);
		db.execSQL(CREATE_LOCATION_QUERY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	
	void addLocation(Location location) {
		getWritableDatabase();
	}

	public void updateLog(Location location) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(TIMESTAMP_COLUMN, System.currentTimeMillis());
		contentValues.put(ERROR_COLUMN, ""); // no error
		long id = getWritableDatabase().insert(UPDATE_TABLE, null, contentValues);
	
		addLocation(id, location);
	}

	public void updateLog(Exception e) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(TIMESTAMP_COLUMN, System.currentTimeMillis());
		contentValues.put(ERROR_COLUMN, e.getMessage());
		getWritableDatabase().insert(UPDATE_TABLE, null, contentValues);
	}
	
	private void addLocation(long updateId, Location location) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(TIMESTAMP_COLUMN, location.getTime());
		contentValues.put(PROVIDER_COLUMN, location.getProvider());
		getWritableDatabase().insert(LOCATION_TABLE, null, contentValues);
	}
	
	public void close() {
		getWritableDatabase().close();
	}
	
	public Cursor getUpdateTable() {
		return getReadableDatabase().query(UPDATE_TABLE, null, null, null, null, null, ID_COLUMN + DESC, null);
		
	}
 
}
