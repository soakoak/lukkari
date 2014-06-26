package fi.sokira.lukkari.provider.test;

import static junit.framework.Assert.assertEquals;

import android.database.Cursor;

public class TestUtils {

   protected static void assertCursorColumn(
         String expected, Cursor cursor, String columnName) {
      
      int idx = cursor.getColumnIndex( columnName);   
      assertEquals( expected, cursor.getString( idx));
   }
   
   protected static void assertCursorColumn(
         long expected, Cursor cursor, String columnName) {
      
      int idx = cursor.getColumnIndex( columnName);   
      assertEquals( expected, cursor.getLong( idx));
   }
}
