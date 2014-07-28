package fi.sokira.lukkari.provider

import android.database.sqlite.SQLiteDatabase
import android.content.ContentValues

trait ContentValueTrim extends AbstractDao with ExtraColumns {

   override def insert(writeableDb: SQLiteDatabase, values: ContentValues) = {
      val trimmedValues = this.removeExtraColumns(values)
      super.insert(writeableDb, trimmedValues)
   }
   
   override def update(writeableDb: SQLiteDatabase, values: ContentValues,
         selection: String, selectionArgs: Array[String]) = {
      val trimmedValues = this.removeExtraColumns(values)
      super.update(writeableDb, trimmedValues, selection, selectionArgs)
   }
}