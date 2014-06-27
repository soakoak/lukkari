package fi.sokira.lukkari.provider.test;

import static fi.sokira.lukkari.provider.test.TestUtils.assertCursorColumn;
import static fi.sokira.lukkari.provider.test.TestUtils.makeDate;

import java.util.Calendar;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.test.IsolatedContext;
import fi.sokira.lukkari.provider.LukkariContract.Lukkari;
import fi.sokira.lukkari.provider.LukkariContract.Realization;
import fi.sokira.lukkari.provider.LukkariContract.Reservation;
import fi.sokira.lukkari.provider.LukkariContract.StudentGroup;

public class ProviderinsertionTest extends AndroidTestCase {

   private ContentValues mValues = new ContentValues();
   private ContentResolver mResolver;
   
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
      mResolver = ctx.getContentResolver();
   }
   
   public void testLukkariInsertion() {
      mValues.put(Lukkari.Columns.NAME, TEST_LUKKARI_NAME);

      Uri contentUri = Lukkari.CONTENT_URI;
      
      Uri insertUri = mResolver.insert(contentUri, mValues);
      long insertId = ContentUris.parseId(insertUri);
      
      String[] allColumns = new String[]{ 
            Lukkari.Columns.ID, 
            Lukkari.Columns.NAME};
      
      Cursor cursor = mResolver.query(contentUri, allColumns, null, null, null);
      cursor.moveToFirst();
      
      int expectedEntries = 1;
      assertEquals( expectedEntries, cursor.getCount());
      
      assertCursorColumn( insertId, cursor, allColumns[0]);
      assertCursorColumn( TEST_LUKKARI_NAME, cursor, allColumns[1]);
   }
   
   public void testRealizationInsertion() {
      mValues.put( Realization.Columns.CODE, TEST_REALIZATION_CODE);
      mValues.put( Realization.Columns.NAME, TEST_REALIZATION_NAME);
      long startDate = makeDate(2014, 1, 1).getTime();
      mValues.put( Realization.Columns.START_DATE, startDate);
      long endDate = makeDate(2014, 5, 31).getTime();
      mValues.put( Realization.Columns.END_DATE, endDate);
      
      Uri contentUri = Realization.CONTENT_URI;
      
      Uri insertUri = mResolver.insert(contentUri, mValues);
      long insertId = ContentUris.parseId(insertUri);
      
      String[] allColumns = new String[]{ 
            Realization.Columns.ID,
            Realization.Columns.CODE,
            Realization.Columns.NAME,
            Realization.Columns.START_DATE,
            Realization.Columns.END_DATE };
      
      Cursor cursor = mResolver.query(contentUri, allColumns, null, null, null);
      cursor.moveToFirst();
      
      int expectedEntries = 1;
      assertEquals( expectedEntries, cursor.getCount());
      
      assertCursorColumn( insertId, cursor, allColumns[0]);
      assertCursorColumn(TEST_REALIZATION_CODE, cursor, allColumns[1]);
      assertCursorColumn(TEST_REALIZATION_NAME, cursor, allColumns[2]);
      assertCursorColumn(startDate, cursor, allColumns[3]);
      assertCursorColumn(endDate, cursor, allColumns[4]);
   }
   
   public void testReservationInsertion() {
      mValues.put( Realization.Columns.CODE, TEST_REALIZATION_CODE);
      mValues.put( Realization.Columns.NAME, TEST_REALIZATION_NAME);
      mValues.put( Realization.Columns.START_DATE, makeDate(2014, 1, 1).getTime());
      mValues.put( Realization.Columns.END_DATE, makeDate(2014, 5, 31).getTime());
      
      Uri rlzContentUri = Realization.CONTENT_URI;
      Uri rlzUri = mResolver.insert(rlzContentUri, mValues);
      long rlzId = ContentUris.parseId(rlzUri);
      mValues.clear();
      
      mValues.put( Reservation.Columns.REALIZATION_ID, rlzId);
      mValues.put(Reservation.Columns.ROOM, TEST_RESERVATION_ROOM);
      Calendar cal = Calendar.getInstance();
      cal.setTime(makeDate(2014, 3, 12, 12, 30));
      long startDate = cal.getTimeInMillis();
      cal.add( Calendar.HOUR, 2);
      cal.add( Calendar.MINUTE, 30);
      long endDate = cal.getTimeInMillis();
      mValues.put( Reservation.Columns.START_DATE, startDate);
      mValues.put( Reservation.Columns.END_DATE, endDate);
      
      Uri contentUri = Reservation.CONTENT_URI;
      Uri insertUri = mResolver.insert(contentUri, mValues);
      long insertId = ContentUris.parseId(insertUri);
      
      String[] allColumns = new String[]{ 
            Reservation.Columns.ID,
            Reservation.Columns.REALIZATION_ID,
            Reservation.Columns.ROOM, 
            Reservation.Columns.START_DATE,
            Reservation.Columns.END_DATE };

      Cursor cursor = mResolver.query(contentUri, allColumns, null, null, null);
      cursor.moveToFirst();
      
      int expectedEntries = 1;
      assertEquals( expectedEntries, cursor.getCount());
      
      assertCursorColumn( insertId, cursor, allColumns[0]);
      assertCursorColumn( rlzId, cursor, allColumns[1]);
      assertCursorColumn( TEST_RESERVATION_ROOM, cursor, allColumns[2]);
      assertCursorColumn(startDate, cursor, allColumns[3]);
      assertCursorColumn(endDate, cursor, allColumns[4]);
   }
   
   public void testStudentGroupInsertion() {
      mValues.put( StudentGroup.Columns.CODE, TEST_STUDENT_GROUP_CODE);
      
      Uri contentUri = StudentGroup.CONTENT_URI;
      
      Uri insertUri = mResolver.insert(
            contentUri, mValues);
      
      long insertId = ContentUris.parseId(insertUri);
      
      String[] allColumns = new String[]{ 
            StudentGroup.Columns.ID, 
            StudentGroup.Columns.CODE};
      
      Cursor cursor = mResolver.query(
            contentUri, allColumns, null, null, null);
      cursor.moveToFirst();
      
      int expectedEntries = 1;
      assertEquals( expectedEntries, cursor.getCount());
      
      assertCursorColumn( insertId, cursor, allColumns[0]);
      assertCursorColumn( TEST_STUDENT_GROUP_CODE, cursor, allColumns[1]);
   }
}
