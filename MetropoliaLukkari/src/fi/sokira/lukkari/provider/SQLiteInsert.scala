package fi.sokira.lukkari.provider

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.database.Cursor
import android.database.SQLException
import fi.sokira.lukkari.provider.CursorUtils.extractValueLong

trait SQLiteInsert extends BaseDao {

   val Tag: String
   val tableName: String
   val columnId = "_id"

   def uniqueSelection(values: ContentValues): (String, Array[String])
   
   override def insert(writeableDb: SQLiteDatabase, values: ContentValues): Long = {
      try {
         writeableDb.insertOrThrow(tableName, null, values)
      } catch {
         case ex: SQLException =>
            Log.i(this.Tag, "Error while inserting to database, attempting update.")
            val (selection, selectionArgs) = uniqueSelection(values)
            val cursor = query(writeableDb, 
               Array(columnId),
               selection,
               selectionArgs,
               null)
            try {
               if( cursor.getCount() > 0) {
                  val id = extractFirstId(cursor)
      
                  val updateSelection = columnId + " = " + id
                  update(writeableDb, values, updateSelection, Array())
                  
                  id
               } else {
                  Log.e(Tag, "Update failed: no existing record found")
                  -1
               } 
            } finally {
               cursor.close()
            }
      }
   }
   
   def extractFirstId(cursor: Cursor) = 
      extractValueLong(cursor, 0, columnId)
}