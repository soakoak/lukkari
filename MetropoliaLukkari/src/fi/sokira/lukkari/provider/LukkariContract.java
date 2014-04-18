package fi.sokira.lukkari.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class LukkariContract {

	public static final String AUTHORITY = "fi.sokira.lukkari.provider";
	public static final Uri CONTENT_URI = Uri.parse( "content://" + AUTHORITY);
	
	public static final class Toteutus implements BaseColumns {
		
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
		
		public static final String TUNNUS = DbSchema.COL_TUNNUS;
		
		public static final String NIMI = DbSchema.COL_NIMI;
		
		public static final String RYHMATUNNUS = DbSchema.COL_RYHMATUNNUS;
		
		public static final String ALOITUS_PVM = DbSchema.COL_ALOITUS_PVM;
		
		public static final String LOPPU_PVM = DbSchema.COL_LOPPU_PVM;
		
		public static final String[] PROJECTION_ALL = {
				_ID, TUNNUS, NIMI, RYHMATUNNUS, ALOITUS_PVM, LOPPU_PVM
		};
		
		public static final String SORT_ORDER_DEFAULT = NIMI + "ASC";
	}
}
