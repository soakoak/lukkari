package fi.sokira.lukkari.provider

import android.database.sqlite.SQLiteDatabase

trait SQLiteDelete extends BaseDao {

   val tableName: String
   
   override def delete(writeableDb: SQLiteDatabase, 
         selection: String, selectionArgs: Array[String]): Int = {
      writeableDb.delete(tableName, selection, selectionArgs)
   }
}