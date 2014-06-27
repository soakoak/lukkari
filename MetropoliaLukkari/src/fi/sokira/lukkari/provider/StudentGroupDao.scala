package fi.sokira.lukkari.provider

import android.content.ContentValues
import fi.sokira.lukkari.{provider => lprovider}
import lprovider.LukkariContract.StudentGroup
import lprovider.LukkariContract.StudentGroup.Columns

object StudentGroupDao extends BaseDao
      with SQLiteQuery with SQLiteInsert with SQLiteUpdate with SQLiteDelete {

   override val Tag = "StudentGroupDao"
   override val tableName = StudentGroup.PATH
   
   override def uniqueSelection(values: ContentValues): (String, Array[String]) = {
      (Columns.CODE + " = ?", Array(values.getAsString(Columns.CODE)))
   }
}