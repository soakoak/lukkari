package fi.sokira.lukkari.provider;

import android.provider.BaseColumns;


public interface DbSchema {
	
	final static String DATABASE_NAME = "lukkari.db";

	final static String TBL_LUKKARI = "lukkari";
	final static String TBL_LUKKARI_TO_TOTEUTUS = "lukkari_to_toteutus";
	final static String TBL_TOTEUTUS = "toteutus";
	final static String TBL_VARAUS = "varaus";

	final static String COL_ID = BaseColumns._ID;
	final static String COL_NIMI = "nimi";
	final static String COL_TUNNUS = "tunnus";
	final static String COL_RYHMATUNNUS = "ryhmatunnus";
	final static String COL_TILA = "tila";
	final static String COL_ALOITUS_PVM = "aloitus_pvm";
	final static String COL_LOPPU_PVM = "loppu_pvm";
	final static String COL_ID_LUKKARI = "id_lukkari";
	final static String COL_ID_TOTEUTUS = "id_toteutus";
	

	final static String CREATE_TABLE_LUKKARI = "CREATE TABLE " + TBL_LUKKARI + 
			"(" + 
					COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					COL_NIMI + " TEXT" + 
			")";
	
	final static String CREATE_TABLE_TOTEUTUS = "CREATE TABLE " + TBL_TOTEUTUS + 
			"(" + 
					COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					COL_TUNNUS + " TEXT, " + 
					COL_NIMI + " TEXT, " +
					COL_RYHMATUNNUS + " TEXT, " +
					COL_ALOITUS_PVM + " INTEGER, " +
					COL_LOPPU_PVM + " INTEGER" +
			")";
	
	final static String CREATE_TABLE_LUKKARI_TO_TOTEUTUS = "CREATE TABLE " + TBL_LUKKARI_TO_TOTEUTUS + 
			"(" + 
					COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					COL_ID_LUKKARI + " INTEGER NOT NULL REFERENCES " + TBL_LUKKARI + "(" + COL_ID + ") ON DELETE CASCADE ON UPDATE CASCADE, " +
					COL_ID_TOTEUTUS + " INTEGER NOT NULL REFERENCES " + TBL_TOTEUTUS + "(" + COL_ID + ") ON DELETE CASCADE ON UPDATE CASCADE" +
			")";
	
	final static String CREATE_TABLE_VARAUS = "CREATE TABLE " + TBL_VARAUS + 
			"(" + 
					COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					COL_ID_TOTEUTUS + " INTEGER NOT NULL REFERENCES " + TBL_TOTEUTUS + "(" + COL_ID + ") ON DELETE CASCADE ON UPDATE CASCADE, " +
					COL_TILA + " TEXT, " +
					COL_ALOITUS_PVM + " INTEGER, " +
					COL_LOPPU_PVM + " INTEGER" +
			")";
	
	final static String DROP_TBL_LUKKARI = "DROP TABLE IF EXISTS " + TBL_LUKKARI;
	final static String DROP_TBL_TOTEUTUS = "DROP TABLE IF EXISTS " + TBL_TOTEUTUS;
	final static String DROP_TBL_LUKKARI_TO_TOTEUTUS = "DROP TABLE IF EXISTS " + TBL_LUKKARI_TO_TOTEUTUS;
	final static String DROP_TBL_VARAUS = "DROP TABLE IF EXISTS " + TBL_VARAUS;

//	final static String DEFAULT_TBL_CURRENCIES_SORT_ORDER = COL_NAME
//			+ " ASC";
//
//	final static String[] FIELDS = { COL_ID, COL_NAME, COL_RATE,
//			COL_DATE };
//
}
