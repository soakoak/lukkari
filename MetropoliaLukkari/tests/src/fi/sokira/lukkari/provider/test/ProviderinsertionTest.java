package fi.sokira.lukkari.provider.test;

import static fi.sokira.lukkari.provider.DatabaseHelper.clearDatabase;
import static fi.sokira.lukkari.provider.test.TestUtils.assertCursorColumn;
import static fi.sokira.lukkari.provider.test.TestUtils.makeDate;

import java.util.Calendar;
import java.util.Date;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.test.IsolatedContext;
import fi.sokira.lukkari.provider.DatabaseHelper;
import fi.sokira.lukkari.provider.LukkariContract.Lukkari;
import fi.sokira.lukkari.provider.LukkariContract.Realization;
import fi.sokira.lukkari.provider.LukkariContract.Reservation;
import fi.sokira.lukkari.provider.LukkariContract.StudentGroup;

public class ProviderinsertionTest extends AndroidTestCase {

   private ContentResolver mResolver;
   
   private final static String TEST_LUKKARI_NAME = "testi_lukkari";
   private final static String TEST_REALIZATION_CODE = "T3ST444444N-0123456";
   private final static String TEST_REALIZATION_NAME = "Android ohjelmointi";
   private final static String TEST_RESERVATION_ROOM = "P13N1";
   private final static String TEST_STUDENT_GROUP_CODE = "T3ST1";
   
   private final static Uri URI_LUKKARI = Lukkari.CONTENT_URI;
   private final static Uri URI_REALIZATION = Realization.CONTENT_URI;
   private final static Uri URI_RESERVATION = Reservation.CONTENT_URI;
   private final static Uri URI_STUDENT_GRP = StudentGroup.CONTENT_URI;
   
   @Override
   protected void setUp() throws Exception {
      IsolatedContext ctx = new IsolatedContext(
            getContext().getContentResolver(), 
            getContext());
      setContext(ctx);
      mResolver = ctx.getContentResolver();
   }
   
   public void testLukkariInsertion() {
      long insertId = insertLukkari(TEST_LUKKARI_NAME);
      
      String[] allColumns = new String[]{ 
            Lukkari.Columns.ID, 
            Lukkari.Columns.NAME};
      
      Cursor cursor = query(URI_LUKKARI, allColumns);
      cursor.moveToFirst();
      
      int expectedEntries = 1;
      assertEquals( expectedEntries, cursor.getCount());
      
      assertCursorColumn( insertId, cursor, allColumns[0]);
      assertCursorColumn( TEST_LUKKARI_NAME, cursor, allColumns[1]);
   }
   
   private long insertLukkari(String lukkariName) {
      ContentValues values = new ContentValues();
      values.put(Lukkari.Columns.NAME, lukkariName);
      
      Uri insertUri = mResolver.insert(URI_LUKKARI, values);
      long insertId = ContentUris.parseId(insertUri);
      
      return insertId;
   }
   
   public void testRealizationInsertion() {
      ContentValues values = new ContentValues();
      values.put( Realization.Columns.CODE, TEST_REALIZATION_CODE);
      values.put( Realization.Columns.NAME, TEST_REALIZATION_NAME);
      long startDate = makeDate(2014, 1, 1).getTime();
      values.put( Realization.Columns.START_DATE, startDate);
      long endDate = makeDate(2014, 5, 31).getTime();
      values.put( Realization.Columns.END_DATE, endDate);
      
      Uri insertUri = null;
      try {
         insertUri = insertRealization(values);
         fail("Missing IllegalArgumentException");
      } catch (IllegalArgumentException e) {
      }
      
      long lukkariId = insertLukkari(TEST_LUKKARI_NAME);
      
      values.put( Realization.Columns.LUKKARI_ID, lukkariId);
      insertUri = insertRealization(values);
      long insertId = ContentUris.parseId(insertUri);
      
      String[] allColumns = new String[]{
            Realization.Columns.ID,
            Realization.Columns.CODE,
            Realization.Columns.NAME,
            Realization.Columns.START_DATE,
            Realization.Columns.END_DATE,
            Realization.Columns.LUKKARI_ID};
      
      Cursor cursor = query(URI_REALIZATION, allColumns);
      cursor.moveToFirst();
      
      int expectedEntries = 1;
      assertEquals( expectedEntries, cursor.getCount());
      
      assertCursorColumn( insertId, cursor, allColumns[0]);
      assertCursorColumn(TEST_REALIZATION_CODE, cursor, allColumns[1]);
      assertCursorColumn(TEST_REALIZATION_NAME, cursor, allColumns[2]);
      assertCursorColumn(startDate, cursor, allColumns[3]);
      assertCursorColumn(endDate, cursor, allColumns[4]);
      assertCursorColumn(lukkariId, cursor, allColumns[5]);
      
      //TODO haku lukkarin nimellä
   }
   
   private Uri insertRealization(long lukkariId, String code, String name, Date startDate, Date endDate) {
      ContentValues values = new ContentValues();

      values.put( Realization.Columns.LUKKARI_ID, lukkariId);
      values.put( Realization.Columns.CODE, TEST_REALIZATION_CODE);
      values.put( Realization.Columns.NAME, TEST_REALIZATION_NAME);
      values.put( Realization.Columns.START_DATE, startDate.getTime());
      values.put( Realization.Columns.END_DATE, endDate.getTime());
      
      return insertRealization(values);
   }
   
   private Uri insertRealization(ContentValues values) {
      Uri contentUri = Realization.CONTENT_URI;
      return mResolver.insert(contentUri, values);
   }
   
   public void testReservationInsertion() {
      long lukkariId = insertLukkari(TEST_LUKKARI_NAME);
      long realizationId = ContentUris.parseId(
            insertRealization(lukkariId, 
               TEST_REALIZATION_CODE, TEST_REALIZATION_NAME,
               makeDate(2014, 1, 1), makeDate(2014, 5, 31)));
      
      Calendar cal = Calendar.getInstance();
      cal.setTime(makeDate(2014, 3, 12, 12, 30));
      Date startDate = cal.getTime();
      cal.add( Calendar.HOUR, 2);
      cal.add( Calendar.MINUTE, 30);
      Date endDate = cal.getTime();
      
      Uri insertUri = insertReservation(realizationId, 
            TEST_RESERVATION_ROOM, startDate, endDate);
      long insertId = ContentUris.parseId(insertUri);
      
      String[] allColumns = new String[]{ 
            Reservation.Columns.ID,
            Reservation.Columns.REALIZATION_ID,
            Reservation.Columns.ROOM, 
            Reservation.Columns.START_DATE,
            Reservation.Columns.END_DATE };

      Cursor cursor = query(URI_RESERVATION, allColumns);
      cursor.moveToFirst();
      
      int expectedEntries = 1;
      assertEquals( expectedEntries, cursor.getCount());
      
      assertCursorColumn( insertId, cursor, allColumns[0]);
      assertCursorColumn( realizationId, cursor, allColumns[1]);
      assertCursorColumn( TEST_RESERVATION_ROOM, cursor, allColumns[2]);
      assertCursorColumn(startDate.getTime(), cursor, allColumns[3]);
      assertCursorColumn(endDate.getTime(), cursor, allColumns[4]);
   }
   
   private Uri insertReservation(long realizationId, String room, 
         Date startDate, Date endDate) {
      
      ContentValues values = new ContentValues();
      values.put( Reservation.Columns.REALIZATION_ID, realizationId);
      values.put( Reservation.Columns.ROOM, room);
      values.put( Reservation.Columns.START_DATE, startDate.getTime());
      values.put( Reservation.Columns.END_DATE, endDate.getTime());

      return mResolver.insert(URI_RESERVATION, values);
   }
   
   public void testStudentGroupInsertion() {
      long lukkariId = insertLukkari(TEST_LUKKARI_NAME);
      long realizationId = ContentUris.parseId(
            insertRealization(lukkariId, 
               TEST_REALIZATION_CODE, TEST_REALIZATION_NAME,
               makeDate(2014, 1, 1), makeDate(2014, 5, 31)));
      
      Calendar cal = Calendar.getInstance();
      cal.setTime(makeDate(2014, 3, 12, 12, 30));
      Date startDate = cal.getTime();
      cal.add( Calendar.HOUR, 2);
      cal.add( Calendar.MINUTE, 30);
      Date endDate = cal.getTime();
      long reservationId = ContentUris.parseId(insertReservation(realizationId, 
               TEST_RESERVATION_ROOM, startDate, endDate));
      
      Uri insertUri = insertStudentGroup(
            realizationId, 
            reservationId, 
            TEST_STUDENT_GROUP_CODE);
      
      long insertId = ContentUris.parseId(insertUri);
      
      String[] allColumns = new String[]{ 
            StudentGroup.Columns.ID, 
            StudentGroup.Columns.CODE};
      
      Cursor cursor = query(URI_STUDENT_GRP, allColumns);
      cursor.moveToFirst();
      
      int expectedEntries = 1;
      assertEquals( expectedEntries, cursor.getCount());
      
      assertCursorColumn( insertId, cursor, allColumns[0]);
      assertCursorColumn( TEST_STUDENT_GROUP_CODE, cursor, allColumns[1]);
      
      //TODO linkitys toteutukseen ja varaukseen
   }
   
   @SuppressWarnings("unused")
   private Uri insertStudentGroup(String code) {
      return insertStudentGroup(null, null, code);
   }
   
   private Uri insertStudentGroup(Long realizationId, Long reservationId, String code) {
      ContentValues values = new ContentValues();
      values.put(StudentGroup.Columns.REALIZATION_ID, realizationId);
      values.put(StudentGroup.Columns.RESERVATION_ID, reservationId);
      values.put(StudentGroup.Columns.CODE, code);
      return mResolver.insert(URI_STUDENT_GRP, values);
   }
   
   @Override
   protected void tearDown() throws Exception {
      Context ctx = getContext();
      SQLiteDatabase mDatabase = new DatabaseHelper(ctx).getWritableDatabase();
      clearDatabase(mDatabase);
      new DatabaseHelper(ctx).onCreate(mDatabase);
      mDatabase.close();
      super.tearDown();
   }
   
   private Cursor query(Uri contentUri, String[] projection) {
      return mResolver.query(contentUri, projection, null, null, null);
   }
}
