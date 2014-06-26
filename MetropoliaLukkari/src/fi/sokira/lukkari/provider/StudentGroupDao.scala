package fi.sokira.lukkari.provider

import android.database.sqlite.SQLiteDatabase
import android.content.ContentValues
import android.util.Log
import android.database.SQLException
import fi.sokira.lukkari.{provider => lprovider}
import lprovider.LukkariContract.StudentGroup
import lprovider.LukkariContract.StudentGroup.Columns
import android.database.Cursor

object StudentGroupDao extends BaseDao 
      with SQLiteQuery with SQLiteInsert with SQLiteUpdate {

   override val Tag = "StudentGroupDao"
   override val tableName = StudentGroup.PATH
   
   override def uniqueSelection(values: ContentValues): (String, Array[String]) = {
      (Columns.CODE + " = ?", Array(values.getAsString(Columns.CODE)))
   }
}