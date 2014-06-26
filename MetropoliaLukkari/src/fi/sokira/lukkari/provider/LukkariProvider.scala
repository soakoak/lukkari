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
import android.content.ContentUris
import android.database.sqlite.SQLiteQueryBuilder
import LukkariProvider.{CUri, matchUri}

class LukkariProvider extends ContentProvider {
   
   private[this] val Tag = "LukkariProvider"

   private[this] def mHelper = new DatabaseHelper(getContext())
   private[this] def writeableDb = mHelper.getWritableDatabase()
   private[this] def readableDb = mHelper.getReadableDatabase()
   
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
            def insertStudentGroup : Long = {
               StudentGroupDao.insert(writeableDb, values)
            }
            
            getUriForId(insertStudentGroup, uri)
         case _ => throw new IllegalArgumentException(
                  "Unsupported URI for insertion: " + uri)
      }
   }

   override def query(uri: Uri, projection: Array[String],
         selection: String, selectionArgs: Array[String],
         sortOrder: String): Cursor = {
      
      def doQuery: Cursor = {
         
         def doSimpleQuery(dao: AnyRef with SQLiteQuery): Cursor =
            doQueryWithSelection(selection, selectionArgs)(dao)
         
         def doQueryWithSelection(selection: String, 
               selectionArgs: Array[String])(dao: AnyRef with SQLiteQuery): Cursor = 
            dao.query(readableDb, projection, selection, selectionArgs, sortOrder)
         
         import CUri._
         CUri( matchUri(uri)) match {
            
            case RealizationId =>
               val selection = 
                  Realization.Columns.ID + " = " + uri.getLastPathSegment()
               doQueryWithSelection(selection, selectionArgs)(RealizationDao)
            case RealizationList =>
               doSimpleQuery(RealizationDao)
               
            case StudentGroupId =>
               val selection = 
                  StudentGroup.Columns.ID + " = " + uri.getLastPathSegment() 
               doQueryWithSelection(selection, selectionArgs)(StudentGroupDao) 
            case StudentGroupList =>
               doSimpleQuery(StudentGroupDao)
               
            case _ => throw new IllegalArgumentException(
                  "Unsupported URI: " + uri)
         }
      }
      
      val cursor = doQuery
      cursor.setNotificationUri(getContext().getContentResolver(), uri)
      cursor
   }
   
   override def update(uri: Uri, values: ContentValues, selection: String,
         selectionArgs: Array[String]) : Int = -1
         
   def getUriForId(id: Long, uri: Uri): Uri = {
      id match {
         case -1 => throw new SQLException("Problem while inserting uri: " + uri)
         case _ =>
            val itemUri = ContentUris.withAppendedId(uri, id)
            getContext().getContentResolver().notifyChange(itemUri, null)
            itemUri
      }
   }
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