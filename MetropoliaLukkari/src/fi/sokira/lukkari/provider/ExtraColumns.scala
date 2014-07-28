package fi.sokira.lukkari.provider

import android.content.ContentValues
import fi.sokira.metropolialukkari.LukkariUtils.{RichContentValues => RichValues}
import android.util.Log

trait ExtraColumns {

   def realColumns: List[String]
   
   def removeExtraColumns(values: ContentValues) = {
      val realColumnValues = new ContentValues
      for(s <- realColumns; value = values.extractString(s) if !value.isEmpty)
         realColumnValues.put(s, value.toString)
      
      realColumnValues
   }
}