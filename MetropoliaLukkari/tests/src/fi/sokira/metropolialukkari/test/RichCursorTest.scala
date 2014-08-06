package fi.sokira.metropolialukkari.test

import junit.framework.TestCase
import junit.framework.Assert
import android.database.Cursor
import android.database.MatrixCursor
import java.lang.{Object => JObject}
import fi.sokira.metropolialukkari.LukkariUtils.RichCursor

class LukkariUtilsTest extends TestCase {

   def testExtractColumnsAsStringList {
      
      val testData = List(Array("example"), Array("example2"))
      val testColumn = "example_column"
      val projection = Array(testColumn)
      val cursor = new MatrixCursor(projection)
      
      for(data <- testData) {
         cursor.addRow(data.asInstanceOf[Array[JObject]])
      }
      
      val expected = testData.flatten.sorted
      val compared = cursor.extractColumnAsStringList(testColumn).sorted
      Assert.assertEquals(expected, compared)
   }
}