package fi.sokira.lukkari.provider

import fi.sokira.lukkari.{provider => lprovider}
import lprovider.LukkariContract.Lukkari
import Lukkari.Columns
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.database.SQLException

object LukkariDao extends AbstractDao {

   override val Tag = "LukkariDao"
   override val tableName = Lukkari.PATH
   
   override def uniqueSelection(values: ContentValues): (String, Array[String]) = {
      (Columns.NAME + " = ?", Array(values.getAsString(Columns.NAME)))
   }
   
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
            Log.d(Tag, "Cursor size: " + cursor.getCount)
            val id = extractFirstId(cursor)

            val updateSelection = columnId + " = " + id
            update(writeableDb, values, updateSelection, Array())
            
            id
      }
   }
}