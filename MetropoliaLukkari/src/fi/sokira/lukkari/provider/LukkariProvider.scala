package fi.sokira.lukkari.provider

import android.content.ContentProvider
import android.net.Uri
import android.content.ContentValues
import android.database.Cursor
import android.content.UriMatcher
import fi.sokira.lukkari.{provider => lprovider}
import lprovider.LukkariContract._
import android.database.SQLException
import android.util.Log

class LukkariProvider extends ContentProvider {
   import LukkariProvider._
   import CUri._
   
   private[this] val mHelper = new DatabaseHelper(getContext())
   private[this] def writeableDb = mHelper.getWritableDatabase()
   
   override def onCreate(): Boolean = true
   
   override def getType(uri: Uri): String = {
      val uriId = matchUri(uri)
      if( uriId != -1)
         CUri( uriId).toString
      else
         throw new IllegalArgumentException("Unsupported URI: " + uri)
   }

   override def delete(uri: Uri,
         selection: String, selectionArgs: Array[String]) : Int = -1 

   override def insert(uri: Uri, values: ContentValues) : Uri = {
      CUri( matchUri(uri)) match {
         case CUri.StudentGroupList =>
            try {
               writeableDb.insertOrThrow(StudentGroup.PATH, null, values)
            } catch {
               case ex: SQLException =>
                  Log.d("", "There was an existing record.")
//                  query(uri, Array(StudentGroup.CODE))
            } 
            
            null
         case _ => throw new IllegalArgumentException(
                  "Unsupported URI for insertion: " + uri)
      }
   }
   
   override def query(uri: Uri, projection: Array[String],
         selection: String, selectionArgs: Array[String], 
         sortOrder: String): Cursor = null
   
   override def update(uri: Uri, values: ContentValues, selection: String,
         selectionArgs: Array[String]) : Int = -1
}

object LukkariProvider {
   
   object CUri extends Enumeration {
      val LukkariList = Value(Lukkari.CONTENT_TYPE)
      val LukkariId = Value(Lukkari.CONTENT_ITEM_TYPE)
      val RealizationList = Value(Realization.CONTENT_TYPE)
      val RealizationId = Value(Realization.CONTENT_ITEM_TYPE)
      val ReservationList = Value(Reservation.CONTENT_TYPE)
      val ReservationId = Value(Reservation.CONTENT_ITEM_TYPE)
      val StudentGroupList = Value(StudentGroup.CONTENT_TYPE)
      val StudentGroupId = Value(StudentGroup.CONTENT_ITEM_TYPE)
   }
   
   val uriMatcher = {
      import android.content.UriMatcher.NO_MATCH
      val uriMatcher = new UriMatcher(NO_MATCH)
      
      def addUri(path: String, code: Int) =
         uriMatcher.addURI(LukkariContract.AUTHORITY, path, code)
      
      addUri(Lukkari.PATH, CUri.LukkariList.id)
      addUri(Lukkari.PATH + "/#", CUri.LukkariId.id)
      
      addUri(Realization.PATH, CUri.RealizationList.id)
      addUri(Realization.PATH + "/#", CUri.RealizationId.id)

      addUri(Reservation.PATH, CUri.ReservationList.id)
      addUri(Reservation.PATH + "/#", CUri.ReservationId.id)
      
      addUri(StudentGroup.PATH, CUri.StudentGroupList.id)
      addUri(StudentGroup.PATH + "/#", CUri.StudentGroupId.id)
      
      uriMatcher
   }
   
    def matchUri(uri: Uri) = uriMatcher.`match`(uri)
}