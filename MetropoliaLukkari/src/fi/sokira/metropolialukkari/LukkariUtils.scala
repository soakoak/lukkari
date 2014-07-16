package fi.sokira.metropolialukkari

import android.content.ContentValues

object LukkariUtils {

   implicit class RichContentValues(values: ContentValues) {
      import RichContentValues._
      
      def extractLong(key: String): Long = {
         Option(values.getAsLong(key)) match {
            case Some(value) =>
               value
            case None =>
               NoLong
         }
      }
   }
   
   object RichContentValues {
      def NoLong = -1
   }
}