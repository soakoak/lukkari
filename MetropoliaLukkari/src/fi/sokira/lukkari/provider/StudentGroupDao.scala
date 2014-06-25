package fi.sokira.lukkari.provider

import android.database.sqlite.SQLiteDatabase
import android.content.ContentValues
import android.util.Log
import android.database.SQLException
import fi.sokira.lukkari.{provider => lprovider}
import lprovider.LukkariContract.StudentGroup
import android.database.Cursor

object StudentGroupDao extends SQLiteQuery {

   private[this] val Tag = "StudentGroupDao"
      
   override val tableName = StudentGroup.PATH
   
   def insert(writeableDb: SQLiteDatabase, values: ContentValues): Long = {
      try {
         writeableDb.insertOrThrow(StudentGroup.PATH, null, values)
      } catch {
         case ex: SQLException =>
            Log.d(this.Tag, "There was an existing record.")
            import StudentGroup.Columns
            val cursor = query(writeableDb, 
               Array(Columns.ID),
               Columns.CODE + " = ?",
               Array(values.getAsString(Columns.CODE)),
               null)
            import lprovider.CursorUtils.extractFirstId 
            extractFirstId(cursor)
      }
   }
   
//   override def query(readableDb: SQLiteDatabase, projection: Array[String],
//         selection: String, selectionArgs: Array[String], 
//         sortOrder: String): Cursor = {
//      super.query(readableDb, projection, selection, selectionArgs, sortOrder)
//   }
}