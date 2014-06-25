package fi.sokira.lukkari.provider

import android.database.sqlite.SQLiteDatabase
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder

trait SQLiteQuery {

   val tableName: String
   
   def query(readableDb: SQLiteDatabase, projection: Array[String],
         selection: String, selectionArgs: Array[String], 
         sortOrder: String): Cursor = {
      val builder = new SQLiteQueryBuilder
      builder.setTables(tableName)
      builder.query(
            readableDb,
            projection, 
            selection, 
            selectionArgs, 
            null, 
            null, 
            sortOrder);
   }
}