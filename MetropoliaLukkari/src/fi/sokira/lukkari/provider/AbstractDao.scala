package fi.sokira.lukkari.provider

import android.content.ContentValues
import fi.sokira.metropolialukkari.LukkariUtils.{RichContentValues => RichValues}
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import android.database.Cursor
import android.util.Log
import android.database.SQLException

abstract class AbstractDao extends BaseDao {

   val Tag: String
   
   val tableName: String
   /*
    * Optional values. Override in case the DAO uses table of some other name
    * for the respectable operations.
    */
   def queryTableName = tableName
   def insertTableName = tableName
   
   def uniqueColumns: List[String]
   val columnId = "_id"
   
   def uniqueSelection(values: ContentValues) = {
      def getValue(column: String) = values.extractString(column)
   
      val selection = uniqueColumns map (_ + " = ?") mkString " AND "
      val selectionArgs = for(s <- uniqueColumns) yield getValue(s)

      (selection, selectionArgs.toArray)
   }
   
   override def query(readableDb: SQLiteDatabase, projection: Array[String],
         selection: String, selectionArgs: Array[String], 
         sortOrder: String): Cursor = {
      val builder = new SQLiteQueryBuilder
      builder.setTables(queryTableName)
      builder.query(
            readableDb,
            projection, 
            selection, 
            selectionArgs, 
            null, 
            null, 
            sortOrder);
   }
   
   override def insert(writeableDb: SQLiteDatabase, values: ContentValues): Long = {
      try {
         writeableDb.insertOrThrow(insertTableName, null, values)
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
         case ex: Exception => 
            Log.w(this.Tag, "Got something else than SQLException; aborting insert")
            throw ex
      }
   }
   
   override def update(writeableDb: SQLiteDatabase, values: ContentValues,
         selection: String, selectionArgs: Array[String]): Int = {
      import android.database.sqlite.SQLiteDatabase.CONFLICT_ROLLBACK
      writeableDb.updateWithOnConflict(
            tableName, values, selection, selectionArgs, CONFLICT_ROLLBACK)
   }
   
   override def delete(writeableDb: SQLiteDatabase, 
         selection: String, selectionArgs: Array[String]): Int = {
      writeableDb.delete(tableName, selection, selectionArgs)
   }
   
   def extractFirstId(cursor: Cursor) = {
      import fi.sokira.lukkari.provider.CursorUtils.extractValueLong
      extractValueLong(cursor, 0, columnId)
      //TODO tee RichCursor
   }
}