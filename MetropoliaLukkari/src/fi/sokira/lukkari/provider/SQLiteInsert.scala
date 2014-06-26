package fi.sokira.lukkari.provider

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.database.Cursor
import android.database.SQLException
import fi.sokira.lukkari.provider.CursorUtils.extractFirstId

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
            Log.d(this.Tag, "There was an existing record.")
            val (selection, selectionArgs) = uniqueSelection(values)
            val cursor = query(writeableDb, 
               Array(columnId),
               selection,
               selectionArgs,
               null)
            val id = extractFirstId(cursor)

            val updateSelection = columnId + " = " + id
            update(writeableDb, values, updateSelection, Array())
            
            id
      }
   }
}