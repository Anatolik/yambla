package com.lohika.yambla;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Our storage content provider
 * Created by IntelliJ IDEA.
 * User: akaverin
 * Date: 1/22/12
 * Time: 12:00 PM
 */
public class StatusProvider extends ContentProvider {
    private static final String TAG = StatusProvider.class.getSimpleName();

    public static final Uri CONTENT_URI = Uri.parse("content://com.lohika.yambla.provider");
    public static final String SINGLE_RECORD_MIME_TYPE = "vnd.android.cursor.item/vnd.lohika.yambla.status";
    public static final String MULTIPLE_RECORDS_MIME_TYPE = "vnd.android.cursor.dir/vnd.lohika.yambla.mstatus";

    private StatusData statusData;

    @Override
    public boolean onCreate() {
        statusData = new StatusData(getContext());

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        long id = getId(uri);
        SQLiteDatabase database = statusData.getDBHelper().getReadableDatabase();
        if (id < 0) {
            return database.query(StatusData.TABLE, projection, selection, selectionArgs, null, null, sortOrder);
        } else {
            return database.query(StatusData.TABLE, projection, StatusData.C_ID + "=" + id, null, null, null, null);
        }
    }

    @Override
    public String getType(Uri uri) {
        return getId(uri) < 0 ?
                MULTIPLE_RECORDS_MIME_TYPE :
                SINGLE_RECORD_MIME_TYPE;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = statusData.insertOrIgnore(values);
        if (id == -1) {
            throw new RuntimeException(
                    String.format("%s: Failed to insert [%s] to [%s] for unknown reason.",
                            TAG, values, uri));
        }
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        long id = getId(uri);
        SQLiteDatabase database = statusData.getDBHelper().getWritableDatabase();
        try {
            if (id < 0) {
                return database.delete(StatusData.TABLE, selection, selectionArgs);
            } else {
                return database.delete(StatusData.TABLE, StatusData.C_ID + "=" + id, null);
            }
        } finally {
            database.close();
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        long id = getId(uri);
        SQLiteDatabase database = statusData.getDBHelper().getWritableDatabase();
        try {
            if (id < 0) {
                return database.update(StatusData.TABLE, values, selection, selectionArgs);
            } else {
                return database.update(StatusData.TABLE, values, StatusData.C_ID + "=" + id, null);
            }
        } finally {
            database.close();
        }
    }

    private long getId(Uri uri) {
        String lastPathSegment = uri.getLastPathSegment();
        if (lastPathSegment == null) {
            return -1;
        }
        try {
            return Long.parseLong(lastPathSegment);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
