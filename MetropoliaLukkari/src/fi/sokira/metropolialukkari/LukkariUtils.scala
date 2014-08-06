package fi.sokira.metropolialukkari

import android.content.ContentValues
import android.database.Cursor

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
   
   implicit class RichCursor(cursor: Cursor) {
      
      def getIndex(columnName: String) =
         cursor.getColumnIndex(columnName)
      
      def getLong(columnName: String) = {
         val idx = getIndex(columnName)
         cursor.getLong(idx)
      }
      
      def getString(columnName: String) = {
         val idx = getIndex(columnName)
         cursor.getString(idx)
      }
      
      def extractColumnAsStringList(columnName: String): List[String] = {
         def doExtraction(cursor: Cursor): List[String] = {
            cursor.moveToNext match {
               case true => Nil
               case false => cursor.getString(columnName) :: doExtraction(cursor)
            }
         }
         
         doExtraction(cursor)
      }
   }
}