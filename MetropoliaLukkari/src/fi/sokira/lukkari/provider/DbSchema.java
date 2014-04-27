package fi.sokira.lukkari.provider;

import android.provider.BaseColumns;


public interface DbSchema {
	
	final static String DATABASE_NAME = "lukkari.db";

	final static String TBL_LUKKARI = "lukkari";
	final static String TBL_LUKKARI_TO_REALIZATION = "lukkari_to_realization";
	final static String TBL_REALIZATION = "realization";
	final static String TBL_RESERVATION = "reservation";
	final static String TBL_STUDENT_GROUP = "student_group";

	final static String COL_ID = BaseColumns._ID;
	final static String COL_NAME = "name"; //Nimi
	final static String COL_CODE = "code"; //Tunnus
	//TODO muuta studentGroups omaksi tableksi
//	final static String COL_STUDENT_GROUPS = "studentGroups";
	final static String COL_ROOM = "room";
	final static String COL_START_DATE = "startDate";
	final static String COL_END_DATE = "endDate";
	final static String COL_ID_LUKKARI = "id_lukkari";
	final static String COL_ID_REALIZATION = "id_realization";
	

	final static String CREATE_TABLE_LUKKARI = "CREATE TABLE " + TBL_LUKKARI + 
			"(" + 
					COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					COL_NAME + " TEXT" + 
			")";
	
	final static String CREATE_TABLE_REALIZATION = "CREATE TABLE " + TBL_REALIZATION + 
			"(" + 
					COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					COL_CODE + " TEXT, " + 
					COL_NAME + " TEXT, " +
					COL_START_DATE + " INTEGER, " +
					COL_END_DATE + " INTEGER" +
			")";
	
	final static String CREATE_TABLE_LUKKARI_TO_REALIZATION = "CREATE TABLE " + TBL_LUKKARI_TO_REALIZATION + 
			"(" + 
					COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					COL_ID_LUKKARI + " INTEGER NOT NULL REFERENCES " + TBL_LUKKARI + "(" + COL_ID + ") ON DELETE CASCADE ON UPDATE CASCADE, " +
					COL_ID_REALIZATION + " INTEGER NOT NULL REFERENCES " + TBL_REALIZATION + "(" + COL_ID + ") ON DELETE CASCADE ON UPDATE CASCADE" +
			")";
	
	final static String CREATE_TABLE_RESERVATION = "CREATE TABLE " + TBL_RESERVATION + 
			"(" + 
					COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					COL_ID_REALIZATION + " INTEGER NOT NULL REFERENCES " + TBL_REALIZATION + "(" + COL_ID + ") ON DELETE CASCADE ON UPDATE CASCADE, " +
					COL_ROOM + " TEXT, " +
					COL_START_DATE + " INTEGER, " +
					COL_END_DATE + " INTEGER" +
			")";
	
	final static String DROP_TBL_LUKKARI = "DROP TABLE IF EXISTS " + TBL_LUKKARI;
	final static String DROP_TBL_REALIZATION = "DROP TABLE IF EXISTS " + TBL_REALIZATION;
	final static String DROP_TBL_LUKKARI_TO_REALIZATION = "DROP TABLE IF EXISTS " + TBL_LUKKARI_TO_REALIZATION;
	final static String DROP_TBL_RESERVATION = "DROP TABLE IF EXISTS " + TBL_RESERVATION;

//	final static String DEFAULT_TBL_CURRENCIES_SORT_ORDER = COL_NAME
//			+ " ASC";
//
//	final static String[] FIELDS = { COL_ID, COL_NAME, COL_RATE,
//			COL_DATE };
//
}
