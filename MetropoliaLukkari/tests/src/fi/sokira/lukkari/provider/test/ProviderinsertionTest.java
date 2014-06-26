package fi.sokira.lukkari.provider.test;

import static fi.sokira.lukkari.provider.test.TestUtils.assertCursorColumn;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.test.IsolatedContext;
import fi.sokira.lukkari.provider.DbSchema;
import fi.sokira.lukkari.provider.LukkariContract;

public class ProviderinsertionTest extends AndroidTestCase {

   private ContentValues mValues = new ContentValues();
   
//   private final static String TEST_LUKKARI_NAME = "testi_lukkari";
//   private final static String TEST_REALIZATION_CODE = "T3ST444444N-0123456";
//   private final static String TEST_REALIZATION_NAME = "Android ohjelmointi";
//   private final static String TEST_RESERVATION_ROOM = "P13N1";
   private final static String TEST_STUDENT_GROUP_CODE = "T3ST1";
   
   @Override
   protected void setUp() throws Exception {
      IsolatedContext ctx = new IsolatedContext(
            getContext().getContentResolver(), 
            getContext());
      setContext(ctx);
   }
   
   public void testStudentGroupInsertion() {
      mValues.put( DbSchema.COL_CODE, TEST_STUDENT_GROUP_CODE);
      
      ContentResolver resolver = getContext().getContentResolver();
      Uri contentUri = LukkariContract.StudentGroup.CONTENT_URI;
      
      Uri insertUri = resolver.insert(
            contentUri, mValues);
      
      long insertId = ContentUris.parseId(insertUri);
      
      String[] allColumns = new String[]{ 
            DbSchema.COL_ID, 
            DbSchema.COL_CODE};
      
      Cursor cursor = resolver.query(
            contentUri, allColumns, null, null, null);
      cursor.moveToFirst();
      
      int expectedEntries = 1;
      assertEquals( expectedEntries, cursor.getCount());
      
      assertCursorColumn( insertId, cursor, DbSchema.COL_ID);
      assertCursorColumn( TEST_STUDENT_GROUP_CODE, cursor, DbSchema.COL_CODE);
   }
}
