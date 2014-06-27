package fi.sokira.lukkari.provider.test;

import static junit.framework.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import android.database.Cursor;

public class TestUtils {

   public static void assertCursorColumn(
         String expected, Cursor cursor, String columnName) {
      
      int idx = cursor.getColumnIndex( columnName);   
      assertEquals( expected, cursor.getString( idx));
   }
   
   public static void assertCursorColumn(
         long expected, Cursor cursor, String columnName) {
      
      int idx = cursor.getColumnIndex( columnName);   
      assertEquals( expected, cursor.getLong( idx));
   }
   
   public static Date makeDate( int year, int month, int day) {
      return makeDate( year, month, day, 0, 0);
   }
   
   public static Date makeDate( int year, int month, int day, int hour, int minute) {
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(0);
      cal.set(year, month, day, hour, minute);
      return cal.getTime();
   }
}
