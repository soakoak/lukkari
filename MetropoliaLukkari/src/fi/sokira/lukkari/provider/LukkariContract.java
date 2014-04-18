package fi.sokira.lukkari.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class LukkariContract {

	public static final String AUTHORITY = "fi.sokira.lukkari.provider";
	public static final Uri CONTENT_URI = Uri.parse( "content://" + AUTHORITY);
	
	public static final class Realization implements BaseColumns {
		
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				LukkariContract.CONTENT_URI, DbSchema.TBL_TOTEUTUS);
		
		private static final String MIME_END = "/vnd.fi.sokira.lukkari."
				+ DbSchema.TBL_TOTEUTUS;
		public static final String CONTENT_TYPE = 
				ContentResolver.CURSOR_DIR_BASE_TYPE
				+ MIME_END;
		
		public static final String CONTENT_ITEM_TYPE = 
				ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ MIME_END;
		
		public static final String CODE = DbSchema.COL_CODE;
		
		public static final String NAME = DbSchema.COL_NAME;
		
		public static final String STUDENT_GROUPS = DbSchema.COL_STUDENT_GROUPS;
		
		public static final String START_DATE = DbSchema.COL_START_DATE;
		
		public static final String END_DATE = DbSchema.COL_END_DATE;
		
		public static final String[] PROJECTION_ALL = {
				_ID, CODE, NAME, STUDENT_GROUPS, START_DATE, END_DATE
		};
		
		public static final String SORT_ORDER_DEFAULT = NAME + "ASC";
	}
}
