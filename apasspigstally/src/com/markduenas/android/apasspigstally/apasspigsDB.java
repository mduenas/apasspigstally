package com.markduenas.android.apasspigstally;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class apasspigsDB extends SQLiteOpenHelper {
	public class Row extends Object {
	    public long apasspigs_id;
	    public String piggyName;
	    public long piggyScore;
	}
	
    private final static String DB_NAME = "apasspigsDB"; 
    private final static String DB_TABLE_MAIN = "apasspigsplayers";
    private final static int    DB_VERSION = 1;
    
    private static final String CREATE_TABLE_MAIN = "CREATE TABLE IF NOT EXISTS " + DB_TABLE_MAIN
    	+ "(apasspigs_id integer primary key autoincrement, "
        + "piggyName text not null, piggyScore integer null);";
    
	public apasspigsDB(Context ctx) {
		super(ctx, DB_NAME, null, DB_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
        	db.execSQL(CREATE_TABLE_MAIN);
		} catch (SQLException e1) {
			Log.e("(onCreate) Exception on query", e1.toString());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	public long insertContact(Row r) {
        ContentValues values = new ContentValues();
        values.put("piggyName", r.piggyName);
        values.put("piggyScore", r.piggyScore);

        SQLiteDatabase db = null;
        long id = 0;
        try {
        	db = this.getWritableDatabase();
        	id = db.insertOrThrow(CREATE_TABLE_MAIN, null, values);
        } catch (SQLException e) {
        	Log.e("(insertContact) Exception on query", e.toString());
        } finally {
        	db.close();
        }
        return id;
	}
	
	public boolean updateRow(Row r) {
        ContentValues args = new ContentValues();
        args.put("piggyName", r.piggyName);
        args.put("piggyScore", r.piggyScore);

        SQLiteDatabase db = null;
        int rowsAffected = 0;
        try {
        	db = this.getWritableDatabase();
        	rowsAffected = db.update(CREATE_TABLE_MAIN, args, "apasspigs_id=" + r.apasspigs_id, null);
        } catch (Exception e) {
        	Log.e("(updateRow) Error updating quickcontacts", e.toString());
        } finally {
        	db.close();
        }
        if(rowsAffected > 0) 
        	return true;
        else
        	return false;
    }
	
	public int deleteRow(long rowId) {
		SQLiteDatabase db = null;
		int rowsAffected = 0;
        try {
        	db = this.getWritableDatabase();
        	rowsAffected = db.delete(CREATE_TABLE_MAIN, "apasspigs_id=" + rowId, null);
        } catch (Exception e) {
        	Log.e("(deleteRow) Error deleting a contact", e.toString());
        } finally {
        	db.close();
        }
        return rowsAffected;
    }

    public List<Row> fetchAllRows() {
    	SQLiteDatabase db = null;
        ArrayList<Row> ret = new ArrayList<Row>();
        try {
        	db = this.getReadableDatabase();
            Cursor c = db.query(CREATE_TABLE_MAIN, new String[] {
                    "apasspigs_id", "piggyName", "piggyScore"}, null, null, null, null, null);
            int numRows = c.getCount();
            c.moveToFirst();
            for (int i = 0; i < numRows; ++i) {
                Row row = new Row();
                row.apasspigs_id = c.getLong(0);
                row.piggyName = c.getString(1);
                row.piggyScore = c.getLong(2);
                ret.add(row);
                c.moveToNext();
            }
            c.close();
        } catch (SQLException e) {
            Log.e("(fetchAllRows) Exception on query", e.toString());
        } finally {
        	db.close();
        }
        return ret;
    }
    
    public Cursor GetAllRows() {
    	SQLiteDatabase db = this.getReadableDatabase();
        try {
            return db.query(CREATE_TABLE_MAIN, new String[] {
            		"apasspigs_id", "piggyName", "piggyScore"}, null, null, null, null, null);
        } catch (SQLException e) {
            Log.e("(GetAllRows) Exception on query", e.toString());
            return null;
        } finally {
        	db.close();
        }
    }

	public long piggyExists(String piggyName) {
		long retVal = 0;
		Cursor c = null;
		SQLiteDatabase db = this.getReadableDatabase();
		try {
			 c = db.query(CREATE_TABLE_MAIN, new String[] {
	                 "apasspigs_id", "piggyName", "piggyScore"}, "piggyName='" + piggyName + "'", null, null, null, null);
	        int numRows = c.getCount();
	        if(numRows > 0)
	        {
	        	c.moveToFirst();
	        	retVal = c.getLong(0);
	        }
		} finally {
			c.close();
			db.close();
		}
    	return retVal;
	}
}
