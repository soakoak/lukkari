package fi.sokira.lukkari.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	private final static String NAME = DbSchema.DATABASE_NAME;
	private final static int VERSION = 1;
	
	private static final String TAG = "DatabaseHelper";
	
	public DatabaseHelper(Context context) {
		super(context, NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String[] createOperations = {
				DbSchema.CREATE_TABLE_LUKKARI,
				DbSchema.CREATE_TABLE_TOTEUTUS,
				DbSchema.CREATE_TABLE_LUKKARI_TO_TOTEUTUS,
				DbSchema.CREATE_TABLE_VARAUS
			};
		
		for( String op : createOperations) {
			db.execSQL( op);
		}
		
		String[] tables = {
			DbSchema.TBL_LUKKARI,
			DbSchema.TBL_TOTEUTUS,
			DbSchema.TBL_LUKKARI_TO_TOTEUTUS,
			DbSchema.TBL_VARAUS
		};
		
		for( String tbl : tables) {
			DBUtils.printTableInfo(db, tbl);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", all previous data will be destroyed.");

		String[] dropStatements = {
				DbSchema.DROP_TBL_LUKKARI,
				DbSchema.DROP_TBL_TOTEUTUS,
				DbSchema.DROP_TBL_LUKKARI_TO_TOTEUTUS,
				DbSchema.DROP_TBL_VARAUS
		};
		
		db.execSQL( "BEGIN TRANSACTION");
		for( String dropStmt : dropStatements) {
			db.execSQL( dropStmt);
		}
		db.execSQL( "END TRANSACTION");

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
	
}
