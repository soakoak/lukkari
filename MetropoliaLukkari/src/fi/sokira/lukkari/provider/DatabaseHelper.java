package fi.sokira.lukkari.provider;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	private final static String NAME = DbSchema.DATABASE_NAME;
	private final static int VERSION = 1;
	
	private static final String TAG = "DatabaseHelper";
	
	private final static String[] TABLES = {
			DbSchema.TBL_LUKKARI,
			DbSchema.TBL_REALIZATION,
			DbSchema.TBL_LUKKARI_TO_REALIZATION,
			DbSchema.TBL_RESERVATION
		};
	
	public DatabaseHelper(Context context) {
		super(context, NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String[] createOperations = {
				DbSchema.CREATE_TABLE_LUKKARI,
				DbSchema.CREATE_TABLE_REALIZATION,
				DbSchema.CREATE_TABLE_LUKKARI_TO_REALIZATION,
				DbSchema.CREATE_TABLE_RESERVATION
			};
		
		for( String op : createOperations) {
			db.execSQL( op);
		}
		
		for( String tbl : TABLES) {
			printTableInfo(db, tbl);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", all previous data will be destroyed.");

		String[] dropStatements = {
				DbSchema.DROP_TBL_LUKKARI,
				DbSchema.DROP_TBL_REALIZATION,
				DbSchema.DROP_TBL_LUKKARI_TO_REALIZATION,
				DbSchema.DROP_TBL_RESERVATION
		};
		
		int i = 0;
		String select = "SELECT COUNT(*) FROM ";
		db.beginTransaction();
		try {
			for(i = TABLES.length - 1; i >= 0; i--) {
				Cursor c = db.rawQuery(select + TABLES[i], new String[]{});
				c.moveToFirst();
				Log.d(TAG, "Table " + TABLES[i] + " entry count: " + c.getInt(0));
				if( c.getInt(0) > 0); 
					db.execSQL( dropStatements[i]);
			}
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.d(TAG, "Error while dropping tables. "
					+ "Sentence error came in was: " + dropStatements[i]);
		} finally {
			db.endTransaction();
		}
		
		// TODO http://stackoverflow.com/a/3505944

		onCreate(db);
	}

	/* 
	 * http://stackoverflow.com/a/3266882/2039863
	 * Foreign keyt ovat oletuksena pois p‰‰lt‰.
	 */
	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if( !db.isReadOnly()) {
			db.execSQL( "PRAGMA foreign_keys=ON;");
		}
	}
	
	public static List<String> getColumns(SQLiteDatabase db, String tableName) {
		List<String> cols = new ArrayList<String>();
		
		//TODO toteutus
		
		return cols;
	}
	
	public static void printTableInfo(SQLiteDatabase db, String tableName) {
		Cursor c = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
		
		Log.d(TAG, "Table " + tableName + " column count: " + c.getCount());
		StringBuilder s = new StringBuilder();
		
		while(c.moveToNext()) {
			Log.d(TAG, "Column " + c.getPosition() + " info:");
			
			for(int i = 0; i < c.getColumnCount(); i++) {
				s.append(c.getString(i));
				s.append(" ");
			}
			
			Log.d(TAG, s.toString());
			s.delete(0, s.length());
		}
	}
	
}
