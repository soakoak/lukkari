package fi.sokira.lukkari.provider;

import android.content.ContentResolver;
import android.net.Uri;

public final class LukkariContract {

   private LukkariContract() {}
   
	public static final String AUTHORITY = "fi.sokira.lukkari.provider";
	public static final Uri AUTHORITY_URI = Uri.parse( "content://" + AUTHORITY);
	
	private static final String SOKIRA_BASE_TYPE = "/vnd.fi.sokira.lukkari.";
	
	public static final class Lukkari {
	   
	   protected static final String PATH = DbSchema.TBL_LUKKARI;
	   
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, PATH);
		
		private static final String MIME_END = SOKIRA_BASE_TYPE + PATH;
		
		public static final String CONTENT_TYPE = 
				ContentResolver.CURSOR_DIR_BASE_TYPE
				+ MIME_END;
		
		public static final String CONTENT_ITEM_TYPE = 
				ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ MIME_END;
		
		public static final class Columns {
		   public static final String ID = DbSchema.COL_ID;
		   public static final String NAME = DbSchema.COL_NAME;
		}
	}

	public static final class Realization {
		
	   protected static final String PATH = DbSchema.TBL_REALIZATION;
	   
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				LukkariContract.AUTHORITY_URI, PATH);
		
		private static final String MIME_END = SOKIRA_BASE_TYPE
				+ PATH;
		
		public static final String CONTENT_TYPE = 
				ContentResolver.CURSOR_DIR_BASE_TYPE
				+ MIME_END;
		
		public static final String CONTENT_ITEM_TYPE = 
				ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ MIME_END;
		
		public static final String SORT_BY_NAME = Columns.NAME + "ASC";
		
		public final static class Columns {
	      public static final String ID = DbSchema.COL_ID;
         public static final String CODE = DbSchema.COL_CODE;
         public static final String NAME = DbSchema.COL_NAME;
         public static final String START_DATE = DbSchema.COL_START_DATE;
         public static final String END_DATE = DbSchema.COL_END_DATE;
		}
	}
	
	public final static class Reservation {
		
	   protected static final String PATH = DbSchema.TBL_RESERVATION;
	   
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, PATH);
		
		private static final String MIME_END = SOKIRA_BASE_TYPE + PATH;
		
		public static final String CONTENT_TYPE = 
				ContentResolver.CURSOR_DIR_BASE_TYPE
				+ MIME_END;
		
		public static final String CONTENT_ITEM_TYPE = 
				ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ MIME_END;
		
		public static final class Columns {
	      public static final String ID = DbSchema.COL_ID;
		   public static final String REALIZATION_ID = DbSchema.COL_ID_REALIZATION;
	      public static final String ROOM = DbSchema.COL_ROOM;
	      public static final String START_DATE = DbSchema.COL_START_DATE;
	      public static final String END_DATE = DbSchema.COL_END_DATE;
		}
	}
	
	public final static class StudentGroup {
		
	   protected static final String PATH = DbSchema.TBL_STUDENT_GROUP;
	   
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				AUTHORITY_URI, PATH);
		
		private static final String MIME_END = SOKIRA_BASE_TYPE + PATH;
		
		public static final String CONTENT_TYPE = 
				ContentResolver.CURSOR_DIR_BASE_TYPE
				+ MIME_END;
		
		public static final String CONTENT_ITEM_TYPE = 
				ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ MIME_END;

		public final static class Columns {
		   public static final String CODE = DbSchema.COL_CODE;
		   public static final String ID = DbSchema.COL_ID;
		}

	}
}
