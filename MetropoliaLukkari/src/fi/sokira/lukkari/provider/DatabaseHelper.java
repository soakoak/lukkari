package fi.sokira.lukkari.provider;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	private final static String NAME = DbSchema.DATABASE_NAME;
	private final static int VERSION = 1;
	
	private static final String TAG = "DatabaseHelper";
	
	private final static String[] TABLES = {
			DbSchema.TBL_LUKKARI,
			DbSchema.TBL_REALIZATION,
			DbSchema.TBL_LUKKARI_TO_REALIZATION,
			DbSchema.TBL_RESERVATION,
			DbSchema.TBL_STUDENT_GROUP,
			DbSchema.TBL_REALIZATION_TO_STUDENT_GROUP,
			DbSchema.TBL_RESERVATION_TO_STUDENT_GROUP
		};
	
	public DatabaseHelper(Context context) {
		super(context, NAME, null, VERSION);
	}
	
	protected static SQLiteQueryBuilder appendWhere(
			SQLiteQueryBuilder builder, String target, String in) {
		builder.appendWhereEscapeString(target);
		builder.appendWhere(" = ");
		builder.appendWhereEscapeString(in);
		
		return builder;
	}
	
	public static Cursor doQuery(SQLiteDatabase db, 
			String inTables, String[] projection) {
		return doQuery(db, inTables, projection, null);
	}
	
	public static Cursor doQuery(SQLiteDatabase db, 
			String inTables, String[] projection, String sortOrder) {
		
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables( inTables);
		return builder.query(
				db, 
				projection, 
				null,
				null, 
				null, 
				null,
				sortOrder);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String[] createOperations = {
				DbSchema.CREATE_TABLE_LUKKARI,
				DbSchema.CREATE_TABLE_REALIZATION,
				DbSchema.CREATE_TABLE_LUKKARI_TO_REALIZATION,
				DbSchema.CREATE_TABLE_RESERVATION,
				DbSchema.CREATE_TABLE_STUDENT_GROUP,
				DbSchema.CREATE_TABLE_REALIZATION_TO_STUDENT_GROUP,
				DbSchema.CREATE_TABLE_RESERVATION_TO_STUDENT_GROUP
			};
		
		for( String op : createOperations) {
			db.execSQL( op);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", all previous data will be destroyed.");

		clearDatabase(db);
		
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
	
	public static void clearDatabase(SQLiteDatabase db) {
		int i = 0;
		String select = "SELECT COUNT(*) FROM ";
		String dropStatement = "";
		db.beginTransaction();
		try {
			for(i = TABLES.length - 1; i >= 0; i--) {
				Cursor c = db.rawQuery(select + TABLES[i], new String[]{});
				c.moveToFirst();
				if( c.getInt(0) > 0); 
					dropStatement = "DROP TABLE IF EXISTS " + TABLES[i];
					db.execSQL( dropStatement);
			}
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.d(TAG, "Error while dropping tables. "
					+ "Sentence error came in was: " + dropStatement);
		} finally {
			db.endTransaction();
		}
	}
}
