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
import LukkariProvider._

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
         selection: String, selectionArgs: Array[String]) : Int = {
      def doDelete: Int =  {
         val dao = matchUriToDao(uri)
         dao.delete(writeableDb, selection, selectionArgs)
      }
      
      val delCount = doDelete
      if( delCount > 0) {
         getContext.getContentResolver.notifyChange(uri, null)
      }
      return delCount
   }

   @throws(classOf[IllegalArgumentException])
   override def insert(uri: Uri, values: ContentValues) : Uri = {
      def doInsert: Long = {
         val dao = matchUriToDao(uri)
         
         if( dao == RealizationDao) {
            val db = writeableDb
            
            db.beginTransaction()
            try {
               import LukkariContract.Lukkari.Columns.ID
               val lukkariId = values.getAsLong(ID)
               if(lukkariId == null) {
                  Log.d(Tag, "No lukkari id provided when inserting realization")
                  throw new IllegalArgumentException(
                        "Lukkari id needed when inserting realization")
               }
               
               values.remove(ID)
               val realizationId = dao.insert(db, values)
               
               import DbSchema.{COL_ID_LUKKARI, COL_ID_REALIZATION}
               val l2rValues = new ContentValues()
               l2rValues.put(COL_ID_LUKKARI, lukkariId)
               l2rValues.put(COL_ID_REALIZATION, Long.box(realizationId))
               LukkariToRealizationDao.insert(db, l2rValues)
               db.setTransactionSuccessful()
               
               realizationId
            } catch {
               case e: Exception =>
                  Log.d(Tag, "Error while inside transaction")
                  throw e
            } finally {
               db.endTransaction()
            }
         } else
            dao.insert(writeableDb, values)
      }
      
      getUriForId( doInsert, uri)
   }

   override def query(uri: Uri, projection: Array[String],
         selection: String, selectionArgs: Array[String],
         sortOrder: String): Cursor = {
      
      def doQuery: Cursor = {  
         val dao = matchUriToDao(uri)
         dao.query(readableDb, projection, selection, selectionArgs, sortOrder)
      }
      
      val cursor = doQuery
      cursor.setNotificationUri(getContext().getContentResolver(), uri)
      cursor
   }
   
   override def update(uri: Uri, values: ContentValues, selection: String,
         selectionArgs: Array[String]) : Int = {
      
      def doUpdate: Int =  {
         val dao = matchUriToDao(uri)
         dao.update(writeableDb, values, selection, selectionArgs)
      }
      
      val updateCount = doUpdate
      if( updateCount > 0) {
         getContext.getContentResolver.notifyChange(uri, null)
      }
      return updateCount
   }
         
   private[this] def getUriForId(id: Long, uri: Uri): Uri = {
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
   
   protected def matchUri(uri: Uri) = uriMatcher.`match`(uri)
   
   protected def matchUriToDao(uri: Uri): BaseDao = 
      CUri( matchUri(uri)) match {
         case CUri.LukkariList => LukkariDao
         case CUri.RealizationList => RealizationDao
         case CUri.ReservationList => ReservationDao
         case CUri.StudentGroupList => StudentGroupDao  
         case _ => throw new IllegalArgumentException(
                        "Unsupported URI: " + uri)
      }
   
   protected object LukkariToRealizationDao extends AbstractDao {
      import DbSchema._
      
      override val Tag = "LukkariToRealizationDao"
      override val tableName = TBL_LUKKARI_TO_REALIZATION
      
      override def uniqueSelection(values: ContentValues) = {
         def getValue(column: String) = values.getAsString(column)
      
         val columns = List(COL_ID_LUKKARI, COL_ID_REALIZATION)
         val selection = columns map (_ + " = ?") mkString " AND "
         val selectionArgs = for(s <- columns) yield getValue(s)
   
         (selection, selectionArgs.toArray)
      }
   }
}