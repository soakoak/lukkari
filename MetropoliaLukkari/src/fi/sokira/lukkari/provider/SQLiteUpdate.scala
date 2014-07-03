package fi.sokira.lukkari.provider

import android.database.sqlite.SQLiteDatabase
import android.content.ContentValues

trait SQLiteUpdate extends BaseDao {

   val tableName: String
   
   override def update(writeableDb: SQLiteDatabase, values: ContentValues,
         selection: String, selectionArgs: Array[String]): Int = {
      import android.database.sqlite.SQLiteDatabase.CONFLICT_ROLLBACK
      writeableDb.updateWithOnConflict(
            tableName, values, selection, selectionArgs, CONFLICT_ROLLBACK)
   }
}