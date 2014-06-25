package fi.sokira.lukkari.provider

import android.database.Cursor

object CursorUtils {

   def extractFirstId(cursor: Cursor) = 
      extractValueLong(cursor, 0, "_id")

   def extractValueLong( cursor: Cursor, position: Int, 
         columnName: String): Long = {

      cursor.moveToPosition(position)
      val valueIdx = cursor.getColumnIndex(columnName)
      cursor.getLong( valueIdx)
   }
}