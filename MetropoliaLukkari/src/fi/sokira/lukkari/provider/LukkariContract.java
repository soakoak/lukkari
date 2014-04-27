package fi.sokira.lukkari.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class LukkariContract {

	public static final String AUTHORITY = "fi.sokira.lukkari.provider";
	public static final Uri AUTHORITY_URI = Uri.parse( "content://" + AUTHORITY);
	
	private static final String SOKIRA_BASE_TYPE = "/vnd.fi.sokira.lukkari.";
	
	protected interface DateColumns {
		public static final String START_DATE = DbSchema.COL_START_DATE;
		
		public static final String END_DATE = DbSchema.COL_END_DATE;
	}
	
	public static final class Lukkari implements BaseColumns, LukkariColumns {
		
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, DbSchema.TBL_LUKKARI);
		
		private static final String MIME_END = SOKIRA_BASE_TYPE + DbSchema.TBL_LUKKARI;
		
		public static final String CONTENT_TYPE = 
				ContentResolver.CURSOR_DIR_BASE_TYPE
				+ MIME_END;
		
		public static final String CONTENT_ITEM_TYPE = 
				ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ MIME_END;
	}
	
	protected interface LukkariColumns {
		public static final String LUKKARI_NAME = DbSchema.COL_NAME;
	}

	
	public static final class Realization implements BaseColumns, 
		RealizationColumns, DateColumns {
		
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				LukkariContract.AUTHORITY_URI, DbSchema.TBL_REALIZATION);
		
		private static final String MIME_END = SOKIRA_BASE_TYPE
				+ DbSchema.TBL_REALIZATION;
		
		public static final String CONTENT_TYPE = 
				ContentResolver.CURSOR_DIR_BASE_TYPE
				+ MIME_END;
		
		public static final String CONTENT_ITEM_TYPE = 
				ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ MIME_END;
		
		public static final String[] PROJECTION_ALL = {
				_ID, REALIZATION_CODE, REALIZATION_NAME, START_DATE, END_DATE
		};
		
		public static final String SORT_ORDER_DEFAULT = REALIZATION_NAME + "ASC";
	}
	
	protected interface RealizationColumns {
		public static final String REALIZATION_CODE = DbSchema.COL_CODE;
		
		public static final String REALIZATION_NAME = DbSchema.COL_NAME;
	}
	
	public final static class Reservation implements BaseColumns, 
		ReservationColumns, DateColumns {
		
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, DbSchema.TBL_RESERVATION);
		
		private static final String MIME_END = SOKIRA_BASE_TYPE + DbSchema.TBL_RESERVATION;
		
		public static final String CONTENT_TYPE = 
				ContentResolver.CURSOR_DIR_BASE_TYPE
				+ MIME_END;
		
		public static final String CONTENT_ITEM_TYPE = 
				ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ MIME_END;
	}

	protected interface ReservationColumns {
		public static final String REALIZATION_ID = DbSchema.COL_ID_REALIZATION;
		
		public static final String RESERVATION_ROOM = DbSchema.COL_ROOM;
	}
	
	public final static class StudentGroup implements BaseColumns, StudentGroupColumns {
		
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, DbSchema.TBL_STUDENT_GROUP);
		
		private static final String MIME_END = SOKIRA_BASE_TYPE + DbSchema.TBL_STUDENT_GROUP;
		
		public static final String CONTENT_TYPE = 
				ContentResolver.CURSOR_DIR_BASE_TYPE
				+ MIME_END;
		
		public static final String CONTENT_ITEM_TYPE = 
				ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ MIME_END;
	}
	
	protected interface StudentGroupColumns {
		public static final String GROUP_CODE = DbSchema.COL_CODE;
	}
}
