package com.anonymous.solar.android;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.anonymous.solar.shared.SolarResult;

/**
 * Database class for storing our items in.
 * 
 * @author 07627505 Darran Kartaschew
 * @version 1.0
 */
public class DeviceLocalStorage {

	public static final String TABLE_RESULTS = "results";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_DATETIME = "timestamp";
	public static final String COLUMN_RESULTS = "result";

	private static final String DATABASE_NAME = "results.db";
	private static final int DATABASE_VERSION = 1;

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { DeviceLocalStorage.COLUMN_ID, DeviceLocalStorage.COLUMN_NAME,
			DeviceLocalStorage.COLUMN_DATETIME, DeviceLocalStorage.COLUMN_RESULTS };

	private MainActivity parent;

	public DeviceLocalStorage(Context context, MainActivity parent) {
		dbHelper = new MySQLiteHelper(context);
		this.parent = parent;
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	/**
	 * General Database Helper Class needed for creating the source if not
	 * existing yet.
	 */
	public class MySQLiteHelper extends SQLiteOpenHelper {

		// Database creation sql statement
		private static final String DATABASE_CREATE = "create table " + TABLE_RESULTS + "(" + COLUMN_ID
				+ " integer primary key autoincrement, " + COLUMN_DATETIME + " text not null, " + COLUMN_NAME
				+ " text not null, " + COLUMN_RESULTS + " text not null);";

		public MySQLiteHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase database) {
			database.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(MySQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion
					+ ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULTS);
			onCreate(db);
		}
	}

	/**
	 * Store a result set into the database
	 * 
	 * @param result
	 *            The result set to store
	 * @return The database key
	 */
	public long createResult(SolarResult result) {
		ContentValues values = new ContentValues();
		values.put(DeviceLocalStorage.COLUMN_NAME, result.getSolarSetup().getSetupName());
		values.put(DeviceLocalStorage.COLUMN_DATETIME, result.getDateTime().toString());

		// Marshall the results to a String;
		Serializer serializer = new Persister();
		File xmlFile = new File(parent.getFilesDir().getPath() + "/Result.xml");

		// Serialize the Person

		try {
			serializer.write(result, xmlFile);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		// read our file to a string...
		String text = null;
		text = readFile(parent.getFilesDir().getPath() + "/Result.xml");

		values.put(DeviceLocalStorage.COLUMN_RESULTS, text);

		long insertId = database.insert(DeviceLocalStorage.TABLE_RESULTS, null, values);
		return insertId;
	}

	/**
	 * Read a file from internal storage.
	 * @param string
	 * @return
	 */
	private String readFile(String pathname) {
		File file = new File(pathname);
		StringBuilder fileContents = new StringBuilder((int) file.length());
		Scanner scanner;
		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		String lineSeparator = System.getProperty("line.separator");

		try {
			while (scanner.hasNextLine()) {
				fileContents.append(scanner.nextLine() + lineSeparator);
			}
			return fileContents.toString();
		} finally {
			scanner.close();
		}
	}

	/**
	 * Remove a result dataset from the database.
	 * 
	 * @param result
	 *            The item to remove
	 */
	public void deleteResult(SolarResult result) {
		long id = result.getId();
		System.out.println("Comment deleted with id: " + id);
		database.delete(DeviceLocalStorage.TABLE_RESULTS, DeviceLocalStorage.COLUMN_ID + " = " + id, null);
	}

	/**
	 * Get all results from the database.
	 * 
	 * @return
	 */
	public List<SolarResult> getAllResults() {
		List<SolarResult> results = new ArrayList<SolarResult>();

		Cursor cursor = database.query(DeviceLocalStorage.TABLE_RESULTS, allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			SolarResult result = cursorToResult(cursor);
			if (result != null){
				result.setId(cursor.getLong(0));
				results.add(result);
			}
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return results;
	}

	/**
	 * Convert a cursor record into an Object
	 * @param cursor
	 * @return
	 */
	private SolarResult cursorToResult(Cursor cursor) {
		SolarResult result = new SolarResult();
		Serializer serial = new Persister();

		// Marshall into an object cursor.getText(3));
		String text = cursor.getString(3);
		try {
			result = serial.read(SolarResult.class, text);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return result;
	}
}
