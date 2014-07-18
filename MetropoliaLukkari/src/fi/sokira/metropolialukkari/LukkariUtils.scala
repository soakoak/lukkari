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
      
      def extractString(key: String): String = {
         Option(values.getAsString(key)) match {
            case Some(value) =>
               value
            case None =>
               NoString
         }
      }
   }
   
   object RichContentValues {
      def NoLong = -1
      def NoString = ""
   }
}