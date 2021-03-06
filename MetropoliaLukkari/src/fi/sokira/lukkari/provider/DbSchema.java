package fi.sokira.lukkari.provider;

import android.provider.BaseColumns;


public interface DbSchema {
	
	final static String DATABASE_NAME = "lukkari.db";

	final static String TBL_LUKKARI = "lukkari";
	final static String TBL_LUKKARI_TO_REALIZATION = "lukkari_to_realization";
	final static String TBL_REALIZATION = "realization";
	final static String TBL_RESERVATION = "reservation";
	final static String TBL_STUDENT_GROUP = "student_group";
	final static String TBL_REALIZATION_TO_STUDENT_GROUP 
							= "realization_to_student_group";
	final static String TBL_RESERVATION_TO_STUDENT_GROUP 
							= "reservation_to_student_group";
	final static String TBL_DATA_LIST = "data_list";

	final static String COL_ID = BaseColumns._ID;
	final static String COL_NAME = "name"; //Nimi
	final static String COL_NAME_LUKKARI = "l_name";
	final static String COL_NAME_REALIZATION = "rz_name";
	final static String COL_CODE = "code"; //Tunnus
	final static String COL_ROOM = "room";
	final static String COL_START_DATE = "startDate";
	final static String COL_END_DATE = "endDate";
	final static String COL_ID_LUKKARI = "id_lukkari";
	final static String COL_ID_REALIZATION = "id_realization";
	final static String COL_ID_RESERVATION = "id_reservation";
	final static String COL_ID_STUDENT_GROUP = "id_student_group";
	

	final static String CREATE_TABLE_LUKKARI = "CREATE TABLE " + TBL_LUKKARI + 
			"(" + 
				COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + 
				", " + COL_NAME + " TEXT" + 
				", " + "CONSTRAINT unq UNIQUE (" + COL_NAME + ")" +
			")";
	
	final static String CREATE_TABLE_REALIZATION = "CREATE TABLE " + TBL_REALIZATION + 
			"(" + 
				COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + 
				", " + COL_CODE + " TEXT" + 
				", " + COL_NAME + " TEXT" +
				", " + COL_START_DATE + " INTEGER" +
				", " + COL_END_DATE + " INTEGER" +
				", " + "CONSTRAINT unq UNIQUE (" + COL_CODE + ")" +
			")";
	
	final static String CREATE_TABLE_LUKKARI_TO_REALIZATION = "CREATE TABLE " + TBL_LUKKARI_TO_REALIZATION + 
			"(" + 
				COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + 
				", " + COL_ID_LUKKARI + " INTEGER NOT NULL REFERENCES " + TBL_LUKKARI + "(" + COL_ID + ") ON DELETE CASCADE ON UPDATE CASCADE" +
				", " + COL_ID_REALIZATION + " INTEGER NOT NULL REFERENCES " + TBL_REALIZATION + "(" + COL_ID + ") ON DELETE CASCADE ON UPDATE CASCADE" +
				", " + "CONSTRAINT unq UNIQUE (" + COL_ID_LUKKARI + ", " + COL_ID_REALIZATION + ")" +
			")";
	
	final static String CREATE_TABLE_RESERVATION = "CREATE TABLE " + TBL_RESERVATION + 
			"(" + 
				COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + 
				", " + COL_ID_REALIZATION + " INTEGER NOT NULL REFERENCES " + TBL_REALIZATION + "(" + COL_ID + ") ON DELETE CASCADE ON UPDATE CASCADE" +
				", " + COL_ROOM + " TEXT" +
				", " + COL_START_DATE + " INTEGER" +
				", " + COL_END_DATE + " INTEGER" +
				", " + "CONSTRAINT unq UNIQUE (" + COL_ROOM + ", " + COL_START_DATE + ", " + COL_END_DATE + ")" +
			")";
	
	final static String CREATE_TABLE_STUDENT_GROUP = "CREATE TABLE " + TBL_STUDENT_GROUP +
			"(" + 
				COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + 
				", " + COL_CODE + " TEXT" + 
				", " + "CONSTRAINT unq UNIQUE (" + COL_CODE + ")" + 
			")";
	
	final static String CREATE_TABLE_REALIZATION_TO_STUDENT_GROUP 
						= "CREATE TABLE " + TBL_REALIZATION_TO_STUDENT_GROUP + 
			"(" + 
				COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + 
				", " + COL_ID_REALIZATION + " INTEGER NOT NULL REFERENCES " + TBL_REALIZATION + "(" + COL_ID + ") ON DELETE CASCADE ON UPDATE CASCADE" +
				", " + COL_ID_STUDENT_GROUP + " INTEGER NOT NULL REFERENCES " + TBL_STUDENT_GROUP + "(" + COL_ID + ") ON DELETE CASCADE ON UPDATE CASCADE" +
				", " + "CONSTRAINT unq UNIQUE (" + COL_ID_REALIZATION + ", " + COL_ID_STUDENT_GROUP + ")" +
			")";
	
	final static String CREATE_TABLE_RESERVATION_TO_STUDENT_GROUP 
						= "CREATE TABLE " + TBL_RESERVATION_TO_STUDENT_GROUP + 
			"(" + 
				COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + 
				", " + COL_ID_RESERVATION + " INTEGER NOT NULL REFERENCES " + TBL_RESERVATION + "(" + COL_ID + ") ON DELETE CASCADE ON UPDATE CASCADE" +
				", " + COL_ID_STUDENT_GROUP + " INTEGER NOT NULL REFERENCES " + TBL_STUDENT_GROUP + "(" + COL_ID + ") ON DELETE CASCADE ON UPDATE CASCADE" +
				", " + "CONSTRAINT unq UNIQUE (" + COL_ID_RESERVATION + ", " + COL_ID_STUDENT_GROUP + ")" +
			")";

	final static String CREATE_VIEW_DATA_LIST = 
		"CREATE VIEW " + TBL_DATA_LIST + " AS "
			+ "SELECT "
			+ "rv." + COL_ID
			+ ", l." + COL_NAME + " AS " + COL_NAME_LUKKARI 
			+ ", rz." + COL_NAME + " AS " + COL_NAME_REALIZATION
			+ ", rv." + COL_ROOM
			+ ", rv." + COL_START_DATE 
			+ ", rv." + COL_END_DATE
				+ " FROM lukkari AS l"
			+ " LEFT OUTER JOIN "
				+ TBL_LUKKARI_TO_REALIZATION + " AS lr"
				+ " ON l." + COL_ID + " = lr." + COL_ID_LUKKARI
			+ " LEFT OUTER JOIN "
				+ TBL_REALIZATION + " AS rz"
				+ " ON lr." + COL_ID_REALIZATION + " = rz." + COL_ID
			+ " INNER JOIN "
				+ TBL_RESERVATION + " AS rv"
				+ " ON rz." + COL_ID + " = rv." + COL_ID_REALIZATION;

}
