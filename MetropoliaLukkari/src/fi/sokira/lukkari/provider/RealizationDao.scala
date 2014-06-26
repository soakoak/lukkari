package fi.sokira.lukkari.provider

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.SQLException
import android.util.Log
import fi.sokira.lukkari.{provider => lprovider}
import lprovider.LukkariContract.StudentGroup
import lprovider.LukkariContract.Realization
import lprovider.LukkariContract.Realization.Columns

object RealizationDao extends BaseDao 
      with SQLiteQuery with SQLiteInsert with SQLiteUpdate {

   override val tableName = Realization.PATH   
   override val Tag = "RealizationDao"

   override def uniqueSelection(values: ContentValues): (String, Array[String]) = {
      (Columns.CODE + " = ?", Array( values.getAsString(Columns.CODE)))
   }
}