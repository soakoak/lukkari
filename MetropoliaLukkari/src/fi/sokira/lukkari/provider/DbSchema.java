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
	final static String VIEW_RESERVATION = "reservation_view";
	final static String VIEW_REALIZATION = "realization_view";
	final static String VIEW_STUDENT_GROUP = "student_group_view";
	final static String VIEW_STUDENT_GROUP_HELP1 = "student_group_help_view1";
	final static String VIEW_STUDENT_GROUP_HELP2 = "student_group_help_view2";

	final static String COL_ID = BaseColumns._ID;
	final static String COL_NAME = "name";
	final static String COL_NAME_LUKKARI = "l_name";
	final static String COL_NAME_REALIZATION = "rz_name";
	final static String COL_CODE = "code";
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

	final static String CREATE_VIEW_RESERVATION = 
		"CREATE VIEW IF NOT EXISTS " + VIEW_RESERVATION + " AS "
			+ "SELECT"
			+ " rv." + COL_ID
			+ ", l." + COL_NAME + " AS " + COL_NAME_LUKKARI 
			+ ", rz." + COL_NAME + " AS " + COL_NAME_REALIZATION
			+ ", rv." + COL_ROOM
			+ ", rv." + COL_START_DATE 
			+ ", rv." + COL_END_DATE
				+ " FROM " + TBL_LUKKARI + " AS l"
			+ " LEFT OUTER JOIN "
				+ TBL_LUKKARI_TO_REALIZATION + " AS lr"
				+ " ON l." + COL_ID + " = lr." + COL_ID_LUKKARI
			+ " LEFT OUTER JOIN "
				+ TBL_REALIZATION + " AS rz"
				+ " ON lr." + COL_ID_REALIZATION + " = rz." + COL_ID
			+ " INNER JOIN "
				+ TBL_RESERVATION + " AS rv"
				+ " ON rz." + COL_ID + " = rv." + COL_ID_REALIZATION;
	
	final static String CREATE_VIEW_REALIZATION =
      "CREATE VIEW IF NOT EXISTS " + VIEW_REALIZATION + " AS "
         + "SELECT"
         + " l." + COL_ID + " AS " + COL_ID_LUKKARI
         + ", l." + COL_NAME + " AS " + COL_NAME_LUKKARI
         + ", rz." + COL_ID
         + ", rz." + COL_CODE
      	+ ", rz." + COL_NAME
      	+ ", rz." + COL_START_DATE
      	+ ", rz." + COL_END_DATE
         + " FROM " + TBL_LUKKARI + " AS l"
      + " INNER JOIN " + TBL_LUKKARI_TO_REALIZATION + " AS lr"
         + " ON l." + COL_ID + " = lr." + COL_ID_LUKKARI
      + " INNER JOIN " + TBL_REALIZATION + " AS rz"
         + " ON lr." + COL_ID_REALIZATION + " = rz." + COL_ID;

	final static String CREATE_VIEW_STUDENT_GROUP_HELP1 =
	      "CREATE VIEW IF NOT EXISTS " + VIEW_STUDENT_GROUP_HELP1 + " AS "
	         + "SELECT"
            + " sg." + COL_ID
            + ", sg." + COL_CODE
            + ", rz." + COL_ID + " AS " + COL_ID_REALIZATION
            + ", rz." + COL_NAME
            + " FROM " + TBL_STUDENT_GROUP + " AS sg"
         + " LEFT OUTER JOIN " + TBL_REALIZATION_TO_STUDENT_GROUP + " AS rz2sg"
            + " ON sg." + COL_ID + " = rz2sg." + COL_ID_STUDENT_GROUP
         + " LEFT OUTER JOIN " + TBL_REALIZATION + " AS rz"
            + " ON rz2sg." + COL_ID_REALIZATION + " = rz." + COL_ID;
            
	  final static String CREATE_VIEW_STUDENT_GROUP_HELP2 =
	         "CREATE VIEW IF NOT EXISTS " + VIEW_STUDENT_GROUP_HELP2 + " AS "
	            + "SELECT"
	            + " sg." + COL_ID
	            + ", sg." + COL_CODE
	            + ", rs." + COL_ID + " AS " + COL_ID_RESERVATION
	            + ", rs." + COL_ROOM
	            + ", rs." + COL_ID_REALIZATION
	            + " FROM " + TBL_STUDENT_GROUP + " AS sg"
	         + " LEFT OUTER JOIN " + TBL_RESERVATION_TO_STUDENT_GROUP + " AS rs2sg"
	            + " ON sg." + COL_ID + " = rs2sg." + COL_ID_STUDENT_GROUP
	         + " LEFT OUTER JOIN " + TBL_RESERVATION + " AS rs"
	            + " ON rs2sg." + COL_ID_RESERVATION + " = rs." + COL_ID;
	
	
	final static String CREATE_VIEW_STUDENT_GROUP = 
	      "CREATE VIEW IF NOT EXISTS " + VIEW_STUDENT_GROUP + " AS "
	         + "SELECT"
            + COL_ID
            + ", " + COL_CODE
            + ", " + COL_ID_REALIZATION
            + ", " + COL_ID_RESERVATION
            + " FROM " + VIEW_STUDENT_GROUP_HELP1
         + " NATURAL LEFT OUTER JOIN " + VIEW_STUDENT_GROUP_HELP2;
}
