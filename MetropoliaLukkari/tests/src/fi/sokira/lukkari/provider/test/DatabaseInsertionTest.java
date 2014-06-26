package fi.sokira.lukkari.provider.test;

import static fi.sokira.lukkari.provider.DatabaseHelper.clearDatabase;
import static fi.sokira.lukkari.provider.test.TestUtils.assertCursorColumn;

import java.util.Calendar;
import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.test.AndroidTestCase;
import android.test.IsolatedContext;
import fi.sokira.lukkari.provider.DatabaseHelper;
import fi.sokira.lukkari.provider.DbSchema;

public class DatabaseInsertionTest extends AndroidTestCase {
	
	private SQLiteDatabase mDatabase;
	private ContentValues mValues = new ContentValues();
	
	private final static String TEST_LUKKARI_NAME = "testi_lukkari";
	private final static String TEST_REALIZATION_CODE = "T3ST444444N-0123456";
	private final static String TEST_REALIZATION_NAME = "Android ohjelmointi";
	private final static String TEST_RESERVATION_ROOM = "P13N1";
	private final static String TEST_STUDENT_GROUP_CODE = "T3ST1";

	@Override
	protected void setUp() throws Exception {
		IsolatedContext ctx = new IsolatedContext(
				getContext().getContentResolver(), 
				getContext());
		setContext(ctx);
		mDatabase = new DatabaseHelper(ctx).getWritableDatabase();
	}
	
	public void testLukkariInsertion() {
		mValues.put(DbSchema.COL_NAME, TEST_LUKKARI_NAME);
		long insertId = mDatabase.insert( DbSchema.TBL_LUKKARI, null, mValues);
		
		String[] allColumns = new String[]{ 
				DbSchema.COL_ID, 
				DbSchema.COL_NAME};
		
		Cursor cursor = doQuery(DbSchema.TBL_LUKKARI, allColumns);
		cursor.moveToFirst();
		
		int expectedEntries = 1;
		assertEquals( expectedEntries, cursor.getCount());
		
		assertCursorColumn( insertId, cursor, DbSchema.COL_ID);
		assertCursorColumn( TEST_LUKKARI_NAME, cursor, DbSchema.COL_NAME);
	}
	
	public void testLukkariToRealizationInsertion() {
		mValues.put(DbSchema.COL_NAME, TEST_LUKKARI_NAME);
		long lkrId = mDatabase.insert( DbSchema.TBL_LUKKARI, null, mValues);
		mValues.clear();		
		
		mValues.put( DbSchema.COL_CODE, TEST_REALIZATION_CODE);
		mValues.put( DbSchema.COL_NAME, TEST_REALIZATION_NAME);
		long startDate = makeDate(2014, 1, 1).getTime();
		mValues.put( DbSchema.COL_START_DATE, startDate);
		long endDate = makeDate(2014, 5, 31).getTime();
		mValues.put( DbSchema.COL_END_DATE, endDate);
		long rlzId = mDatabase.insert(DbSchema.TBL_REALIZATION, null, mValues);
		mValues.clear();
		
		mValues.put( DbSchema.COL_ID_LUKKARI, lkrId);
		mValues.put( DbSchema.COL_ID_REALIZATION, rlzId);
		long insertId = mDatabase.insert( 
				DbSchema.TBL_LUKKARI_TO_REALIZATION, null, mValues);
		
		String[] allColumns = new String[]{ 
				DbSchema.COL_ID,
				DbSchema.COL_ID_LUKKARI,
				DbSchema.COL_ID_REALIZATION };
		
		
		Cursor cursor = doQuery(
				DbSchema.TBL_LUKKARI_TO_REALIZATION, allColumns);
		cursor.moveToFirst();
		
		int expectedEntries = 1;
		assertEquals( expectedEntries, cursor.getCount());
		
		assertCursorColumn( insertId, cursor, DbSchema.COL_ID);
		assertCursorColumn(lkrId, cursor, DbSchema.COL_ID_LUKKARI);
		assertCursorColumn(rlzId, cursor, DbSchema.COL_ID_REALIZATION);
	}
	
	public void testRealizationInsertion() {
		mValues.put( DbSchema.COL_CODE, TEST_REALIZATION_CODE);
		mValues.put( DbSchema.COL_NAME, TEST_REALIZATION_NAME);
		long startDate = makeDate(2014, 1, 1).getTime();
		mValues.put( DbSchema.COL_START_DATE, startDate);
		long endDate = makeDate(2014, 5, 31).getTime();
		mValues.put( DbSchema.COL_END_DATE, endDate);
		
		long insertId = mDatabase.insert(DbSchema.TBL_REALIZATION, null, mValues);
		
		String[] allColumns = new String[]{ 
				DbSchema.COL_ID,
				DbSchema.COL_CODE,
				DbSchema.COL_NAME,
				DbSchema.COL_START_DATE,
				DbSchema.COL_END_DATE };
		
		Cursor cursor = doQuery(DbSchema.TBL_REALIZATION, allColumns);
		cursor.moveToFirst();
		
		int expectedEntries = 1;
		assertEquals( expectedEntries, cursor.getCount());
		
		assertCursorColumn( insertId, cursor, DbSchema.COL_ID);
		assertCursorColumn(TEST_REALIZATION_CODE, cursor, DbSchema.COL_CODE);
		assertCursorColumn(TEST_REALIZATION_NAME, cursor, DbSchema.COL_NAME);
		assertCursorColumn(startDate, cursor, DbSchema.COL_START_DATE);
		assertCursorColumn(endDate, cursor, DbSchema.COL_END_DATE);
	}
	
	public void testRealizationToStudentGroupInsertion() {
		mValues.put( DbSchema.COL_CODE, TEST_REALIZATION_CODE);
		mValues.put( DbSchema.COL_NAME, TEST_REALIZATION_NAME);
		mValues.put( DbSchema.COL_START_DATE, makeDate(2014, 1, 1).getTime());
		mValues.put( DbSchema.COL_END_DATE, makeDate(2014, 5, 31).getTime());
		long rlzId = mDatabase.insert(DbSchema.TBL_REALIZATION, null, mValues);
		mValues.clear();
		
		mValues.put( DbSchema.COL_CODE, TEST_STUDENT_GROUP_CODE);
		long studentGrpId = mDatabase.insert( 
				DbSchema.TBL_STUDENT_GROUP, null, mValues);
		mValues.clear();
		
		mValues.put( DbSchema.COL_ID_REALIZATION, rlzId);
		mValues.put( DbSchema.COL_ID_STUDENT_GROUP, studentGrpId);
		long insertId = mDatabase.insert(
				DbSchema.TBL_REALIZATION_TO_STUDENT_GROUP, null, mValues);
		
		String[] allColumns = new String[]{ 
				DbSchema.COL_ID,
				DbSchema.COL_ID_REALIZATION,
				DbSchema.COL_ID_STUDENT_GROUP };
		
		
		Cursor cursor = doQuery(
				DbSchema.TBL_REALIZATION_TO_STUDENT_GROUP, allColumns);
		cursor.moveToFirst();
		
		int expectedEntries = 1;
		assertEquals( expectedEntries, cursor.getCount());
		
		assertCursorColumn( insertId, cursor, DbSchema.COL_ID);
		assertCursorColumn(rlzId, cursor, DbSchema.COL_ID_REALIZATION);
		assertCursorColumn(studentGrpId, cursor, DbSchema.COL_ID_STUDENT_GROUP);
	}
	
	public void testReservationInsertion() {
		mValues.put( DbSchema.COL_CODE, TEST_REALIZATION_CODE);
		mValues.put( DbSchema.COL_NAME, TEST_REALIZATION_NAME);
		mValues.put( DbSchema.COL_START_DATE, makeDate(2014, 1, 1).getTime());
		mValues.put( DbSchema.COL_END_DATE, makeDate(2014, 5, 31).getTime());
		long rlzId = mDatabase.insert(DbSchema.TBL_REALIZATION, null, mValues);
		mValues.clear();
		
		mValues.put( DbSchema.COL_ID_REALIZATION, rlzId);
		mValues.put(DbSchema.COL_ROOM, TEST_RESERVATION_ROOM);
		Calendar cal = Calendar.getInstance();
		cal.setTime(makeDate(2014, 3, 12, 12, 30));
		long startDate = cal.getTimeInMillis();
		cal.add( Calendar.HOUR, 2);
		cal.add( Calendar.MINUTE, 30);
		long endDate = cal.getTimeInMillis();
		mValues.put( DbSchema.COL_START_DATE, startDate);
		mValues.put( DbSchema.COL_END_DATE, endDate);
		long insertId = mDatabase.insert( 
				DbSchema.TBL_RESERVATION, null, mValues);
		
		String[] allColumns = new String[]{ 
				DbSchema.COL_ID,
				DbSchema.COL_ID_REALIZATION,
				DbSchema.COL_ROOM, 
				DbSchema.COL_START_DATE,
				DbSchema.COL_END_DATE };

		Cursor cursor = doQuery(
				DbSchema.TBL_RESERVATION, allColumns);
		cursor.moveToFirst();
		
		int expectedEntries = 1;
		assertEquals( expectedEntries, cursor.getCount());
		
		assertCursorColumn( insertId, cursor, DbSchema.COL_ID);
		assertCursorColumn( rlzId, cursor, DbSchema.COL_ID_REALIZATION);
		assertCursorColumn( TEST_RESERVATION_ROOM, cursor, DbSchema.COL_ROOM);
		assertCursorColumn(startDate, cursor, DbSchema.COL_START_DATE);
		assertCursorColumn(endDate, cursor, DbSchema.COL_END_DATE);
	}
	
	public void testReservationToStudentGroupInsertion() {
		mValues.put( DbSchema.COL_CODE, TEST_REALIZATION_CODE);
		mValues.put( DbSchema.COL_NAME, TEST_REALIZATION_NAME);
		mValues.put( DbSchema.COL_START_DATE, makeDate(2014, 1, 1).getTime());
		mValues.put( DbSchema.COL_END_DATE, makeDate(2014, 5, 31).getTime());
		long rlzId = mDatabase.insert(DbSchema.TBL_REALIZATION, null, mValues);
		mValues.clear();
		
		mValues.put( DbSchema.COL_ID_REALIZATION, rlzId);
		mValues.put(DbSchema.COL_ROOM, TEST_RESERVATION_ROOM);
		Calendar cal = Calendar.getInstance();
		cal.setTime(makeDate(2014, 3, 12, 12, 30));
		mValues.put( DbSchema.COL_START_DATE, cal.getTimeInMillis());
		cal.add( Calendar.HOUR, 2);
		cal.add( Calendar.MINUTE, 30);
		mValues.put( DbSchema.COL_END_DATE, cal.getTimeInMillis());
		long reservId = mDatabase.insert( 
				DbSchema.TBL_RESERVATION, null, mValues);
		mValues.clear();
		
		mValues.put( DbSchema.COL_CODE, TEST_STUDENT_GROUP_CODE);
		long studentGrpId = mDatabase.insert( 
				DbSchema.TBL_STUDENT_GROUP, null, mValues);
		mValues.clear();
		
		mValues.put( DbSchema.COL_ID_RESERVATION, reservId);
		mValues.put( DbSchema.COL_ID_STUDENT_GROUP, studentGrpId);
		long insertId = mDatabase.insert(
				DbSchema.TBL_RESERVATION_TO_STUDENT_GROUP, null, mValues);
		
		String[] allColumns = new String[]{ 
				DbSchema.COL_ID,
				DbSchema.COL_ID_RESERVATION,
				DbSchema.COL_ID_STUDENT_GROUP };
		
		
		Cursor cursor = doQuery(
				DbSchema.TBL_RESERVATION_TO_STUDENT_GROUP, allColumns);
		cursor.moveToFirst();
		
		int expectedEntries = 1;
		assertEquals( expectedEntries, cursor.getCount());
		
		assertCursorColumn( insertId, cursor, DbSchema.COL_ID);
		assertCursorColumn(reservId, cursor, DbSchema.COL_ID_RESERVATION);
		assertCursorColumn(studentGrpId, cursor, DbSchema.COL_ID_STUDENT_GROUP);
	}
	
   public void testStudentGroupInsertion() {
      mValues.put( DbSchema.COL_CODE, TEST_STUDENT_GROUP_CODE);
      long insertId = mDatabase.insert( 
            DbSchema.TBL_STUDENT_GROUP, null, mValues);
      
      String[] allColumns = new String[]{ 
            DbSchema.COL_ID, 
            DbSchema.COL_CODE};
      
      Cursor cursor = doQuery(DbSchema.TBL_STUDENT_GROUP, allColumns);
      cursor.moveToFirst();
      
      int expectedEntries = 1;
      assertEquals( expectedEntries, cursor.getCount());
      
      assertCursorColumn( insertId, cursor, DbSchema.COL_ID);
      assertCursorColumn( TEST_STUDENT_GROUP_CODE, cursor, DbSchema.COL_CODE);
   }
	
	private Date makeDate( int year, int month, int day) {
		return makeDate( year, month, day, 0, 0);
	}
	
	private Date makeDate( int year, int month, int day, int hour, int minute) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(0);
		cal.set(year, month, day, hour, minute);
		return cal.getTime();
	}
	

	
	private Cursor doQuery(String inTables, String[] projection) {
		return doQuery(inTables, projection, null);
	}
	
	private Cursor doQuery( 
			String inTables, String[] projection, String sortOrder) {
		
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables( inTables);
		return builder.query(
				mDatabase, 
				projection, 
				null,
				null, 
				null, 
				null,
				sortOrder);
	}
	
	@Override
	protected void tearDown() throws Exception {
		clearDatabase(mDatabase);
		new DatabaseHelper(getContext()).onCreate(mDatabase);
		mDatabase.close();
		super.tearDown();
	}
}
