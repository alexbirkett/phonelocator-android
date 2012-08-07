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
	private static final int DATABASE_VERSION = 4;
	
	private static int UPDATE_TIMESTAMP_COLUMN_INDEX = 1;
	
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
	
	public static final String UPDATE_TIMESTAMP_COLUMN = "update_timestamp";
	public static final String LOCATION_TIMESTAMP_COLUMN = "location_timestamp";
	
	public static final String PROVIDER_COLUMN = "provider";
	public static final String ERROR_TYPE_COLUMN = "error_type";
	public static final String ERROR_MESSAGE_COLUMN = "error_message";
	public static final String ID_COLUMN = "_id";
	public static final String UPDATE_ID_COLUMN = "update_id";
	
	
	public static final String DESC = " DESC";
	
	private static final String CREATE_UPDATE_TABLE_QUERY = "CREATE TABLE " + UPDATE_TABLE + " (" + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " + UPDATE_TIMESTAMP_COLUMN + " INTEGER, " + ERROR_MESSAGE_COLUMN + " TEXT, " + ERROR_TYPE_COLUMN + " TEXT );";	private static final String CREATE_LOCATION_QUERY =   "CREATE TABLE " + LOCATION_TABLE + " (" + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " + UPDATE_ID_COLUMN + " INTEGER, " + LOCATION_TIMESTAMP_COLUMN + " INTEGER, " + PROVIDER_COLUMN + " TEXT );";
	
	private static final String ADD_UPDATE_ID_COLUMN_INDEX_QUERY = "CREATE INDEX "+ UPDATE_ID_COLUMN + "_INDEX ON " + LOCATION_TABLE + "("+ UPDATE_ID_COLUMN + ");";
	
	private static String SELECT_UPDATES_QUERY = "SELECT "  + 
						UPDATE_TABLE + "." + ID_COLUMN + " , " +
						UPDATE_TIMESTAMP_COLUMN + " , " + 
						ERROR_TYPE_COLUMN + " , " + 
						ERROR_MESSAGE_COLUMN + " , " + 
						LOCATION_TIMESTAMP_COLUMN + " , " + 
						PROVIDER_COLUMN +
	  " FROM "  + UPDATE_TABLE +
	  " LEFT OUTER JOIN " + LOCATION_TABLE + " ON " + UPDATE_TABLE + "." + ID_COLUMN + " = " + LOCATION_TABLE + "." + UPDATE_ID_COLUMN +
	  " ORDER BY " + UPDATE_TABLE + "." + ID_COLUMN + DESC;

	
	public UpdateLogDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createDb(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		dropTables(db);
		createDb(db);
	}
	
	private void createDb(SQLiteDatabase db) {
		db.execSQL(CREATE_UPDATE_TABLE_QUERY);
		db.execSQL(CREATE_LOCATION_QUERY);
		db.execSQL(ADD_UPDATE_ID_COLUMN_INDEX_QUERY);
	}
	
	private void dropTables(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + UPDATE_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + LOCATION_TABLE);
	}

	public void updateLog(Location location) {
		long id = updateLog(null, null);
		addLocation(id, location);
	}

	public void updateLog(Exception exception) {
		updateLog(exception.toString(), exception.getMessage());
	}
	
	public long updateLog(String errorType, String errorMessage) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(UPDATE_TIMESTAMP_COLUMN, System.currentTimeMillis());
		contentValues.put(ERROR_TYPE_COLUMN, errorType);
		contentValues.put(ERROR_MESSAGE_COLUMN, errorMessage);
		return getWritableDatabase().insert(UPDATE_TABLE, null, contentValues);
	}
	private void addLocation(long updateId, Location location) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(LOCATION_TIMESTAMP_COLUMN, location.getTime());
		contentValues.put(PROVIDER_COLUMN, location.getProvider());
		contentValues.put(UPDATE_ID_COLUMN, updateId);
		getWritableDatabase().insert(LOCATION_TABLE, null, contentValues);
	}
	
	public void close() {
		getWritableDatabase().close();
	}
	
	public Cursor getUpdates() {
		return getReadableDatabase().rawQuery(SELECT_UPDATES_QUERY, null);
	}
	
	public void deleteUpdates() {
		SQLiteDatabase database = getWritableDatabase();
		database.delete(LOCATION_TABLE, null, null);
		database.delete(UPDATE_TABLE, null, null);
	}
	
	public long getLastUpdateTimestamp() {
		long lastUpdateTimestamp = 0;
		Cursor cursor = getUpdates();
		if (cursor.moveToFirst()) {
			lastUpdateTimestamp = cursor.getLong(UPDATE_TIMESTAMP_COLUMN_INDEX);
		}
		cursor.close();
		return lastUpdateTimestamp;
	}
 
}
