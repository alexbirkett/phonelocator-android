package com.birkettenterprise.phonelocator.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class UpdateLogDatabaseContentProvider extends ContentProvider {

	public static final Uri URI = Uri.parse("content://com.birkettenterprise.phonelocator.database.UpdateLogDatabaseContentProvider/");
	
	private UpdateLogDatabase mDatabase;
	
	@Override
	public boolean onCreate() {
		mDatabase = new UpdateLogDatabase(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor cursor = mDatabase.getUpdates();
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		mDatabase.deleteUpdates();
		getContext().getContentResolver().notifyChange(UpdateLogDatabaseContentProvider.URI, null);
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}

}
