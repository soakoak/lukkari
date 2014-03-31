package fi.sokira.lukkari.provider;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBUtils {
	
	private final static String TAG = "DBUtils";
	
	private DBUtils() {}
	
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

