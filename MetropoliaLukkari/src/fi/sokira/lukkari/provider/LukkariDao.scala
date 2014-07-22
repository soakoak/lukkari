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
}