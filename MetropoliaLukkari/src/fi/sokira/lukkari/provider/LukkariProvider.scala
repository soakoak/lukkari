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
import android.database.sqlite.SQLiteDatabase
import fi.sokira.metropolialukkari.LukkariUtils.{RichContentValues => RichValues} 

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
         
         // TODO
      }
      
      val delCount = doDelete
      if( delCount > 0) {
         getContext.getContentResolver.notifyChange(uri, null)
      }
      return delCount
   }

   override def insert(uri: Uri, values: ContentValues) : Uri = {
      def doInsertWithTransaction: Long = {
         val db = writeableDb
         db.beginTransaction()
         try {
            val insertedId = doInsert(db)
            db.setTransactionSuccessful()
            insertedId
         } catch {
            case e: Exception =>
               Log.d(Tag, "Error while inside transaction")
               throw e
         } finally {
            db.endTransaction()
         }
      }
      
      @throws(classOf[IllegalArgumentException])
      def doInsert(writeableDb: SQLiteDatabase): Long = {
         
         def createLink(id: Long, linkFunction: (Long => Long)) = {
            if(id != -1)
               linkFunction(id)
            else {
               Log.e(Tag, "No fitting id provided when creating link using function: " +
                     linkFunction.toString)
               throw new IllegalArgumentException("Error while creating link")
            }
         }
         
         val dao = matchUriToDao(uri)
         dao match {
            case RealizationDao =>
               import LukkariContract.Realization.Columns.{LUKKARI_ID => ID, 
                  LUKKARI_NAME => NAME}

               val lukkariName = values.extractString(NAME)
               val lukkariId = Option(lukkariName) match {
                  /*
                   * In case there is a name included, we try to insert a new lukkari. 
                   * Otherwise we extract whatever has been included as ID.
                   */
                  case Some(s) if (!s.isEmpty) =>
                     val values = new ContentValues
                     values.put(Lukkari.Columns.NAME, s)
                     LukkariDao.insert(writeableDb, values)
                  case _ => values.extractLong(ID)
               }
               
               val trimmedValues = trimContentValues(dao, values)
               val realizationId = dao.insert(writeableDb, trimmedValues)
               
               def linkToLukkari(lukkariId: Long): Long = {
                  import DbSchema.{COL_ID_LUKKARI, COL_ID_REALIZATION}
                  val values = new ContentValues()
                  values.put(COL_ID_LUKKARI, Long.box(lukkariId))
                  values.put(COL_ID_REALIZATION, Long.box(realizationId))
                  LukkariToRealizationDao.insert(writeableDb, values)
               }
               
               val linkId = createLink(lukkariId, linkToLukkari)
               if( linkId == RichValues.NoLong) {
                  throw new SQLException("Failed to link the realization with lukkari")
               }
               realizationId

            case StudentGroupDao => 
               import StudentGroup.Columns.{REALIZATION_ID => ReaId, 
                  RESERVATION_ID => ResId}
               val realizationId = values.extractLong(ReaId)
               val reservationId = values.extractLong(ResId)
               
               val trimmedValues = trimContentValues(dao, values)
               val groupId = dao.insert(writeableDb, trimmedValues)

               def linkToRealization(realizationId: Long): Long = {
                  import DbSchema.{COL_ID_REALIZATION => ReaId,
                     COL_ID_STUDENT_GROUP => StuId}
                  val values = new ContentValues
                  values.put(ReaId, Long.box(realizationId))
                  values.put(StuId, Long.box(groupId))
                  StudentGroupToRealizationDao.insert(writeableDb, values)
               }
               
               def linkToReservation(reservationId: Long): Long = {
                  import DbSchema.{COL_ID_RESERVATION => ResId,
                     COL_ID_STUDENT_GROUP => StuId}
                  val values = new ContentValues
                  values.put(ResId, Long.box(reservationId))
                  values.put(StuId, Long.box(groupId))
                  StudentGroupToReservationDao.insert(writeableDb, values)
               } 
               
               import RichValues.NoLong
               if( realizationId != NoLong)
                  createLink( realizationId, linkToRealization)
               if( reservationId != NoLong)
                  createLink( reservationId, linkToReservation)
               //TODO testaus
               groupId
            case _ => dao.insert(writeableDb, values)
         }
      }
      
      getUriForId( doInsertWithTransaction, uri)
   }

   override def query(uri: Uri, projection: Array[String],
         selection: String, selectionArgs: Array[String],
         sortOrder: String): Cursor = {
      
      def doQuery: Cursor = {
         val dao = matchUriToDao(uri)
         
         dao match {
            case _ => dao.query(
               readableDb, projection, selection, selectionArgs, sortOrder)
               //TODO studentgroup-kolumnien käyttö haussa
         }
      }
      
      val cursor = doQuery
      cursor.setNotificationUri(getContext().getContentResolver(), uri)
      cursor
   }
   
   override def update(uri: Uri, values: ContentValues, selection: String,
         selectionArgs: Array[String]) : Int = {
      
      def doUpdate: Int =  {
         val dao = matchUriToDao(uri) 
         val trimmedValues = trimContentValues(dao, values)
         dao.update(writeableDb, trimmedValues, selection, selectionArgs)
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
   
   private[this] def trimContentValues(dao: BaseDao, values: ContentValues) =
      dao match {
         case extra: AbstractDao with ExtraColumns =>
            extra.removeExtraColumns(values)
         case _ => values
      }

   
   override def shutdown = 
      mHelper.close()
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
   
   protected object LukkariDao extends AbstractDao {

      override val Tag = "LukkariDao"
      override val tableName = Lukkari.PATH
      import Lukkari.Columns.NAME
      override val uniqueColumns = List(NAME)
   }
   
   object RealizationDao extends AbstractDao with ExtraColumns {

      override val tableName = Realization.PATH 
      override val Tag = "RealizationDao"
      override val queryTableName = DbSchema.VIEW_REALIZATION
      
      import Realization.Columns._
      override val uniqueColumns = List(CODE)    
      override val realColumns = List(ID, CODE, NAME, START_DATE, END_DATE)
   }
   
   object ReservationDao extends AbstractDao {

      override val Tag = "ReservationDao"
      override val tableName = Reservation.PATH
      import Reservation.Columns._
      override val uniqueColumns = 
         List(ROOM, START_DATE, END_DATE)
   }
   
   object StudentGroupDao extends AbstractDao with ExtraColumns {

      override val Tag = "StudentGroupDao"
      override val tableName = StudentGroup.PATH
      
      import StudentGroup.Columns._
      override val uniqueColumns = List(CODE)
      override val realColumns = List(ID, CODE)
   }
   
   protected object LukkariToRealizationDao extends AbstractDao {
      import DbSchema._
      
      override val Tag = "LukkariToRealizationDao"
      override val tableName = TBL_LUKKARI_TO_REALIZATION
      override val uniqueColumns = List(COL_ID_LUKKARI, COL_ID_REALIZATION)
   }
   
   protected object StudentGroupToRealizationDao extends AbstractDao {
      import DbSchema._
      
      override val Tag = "StudentGroupToRealizationDao"
      override val tableName = TBL_REALIZATION_TO_STUDENT_GROUP
      override val uniqueColumns = List(COL_ID_REALIZATION, COL_ID_STUDENT_GROUP)
   }
   
   protected object StudentGroupToReservationDao extends AbstractDao {
      import DbSchema._
      
      override val Tag = "StudentGroupToReservationDao"
      override val tableName = TBL_RESERVATION_TO_STUDENT_GROUP
      override val uniqueColumns = List(COL_ID_RESERVATION, COL_ID_STUDENT_GROUP)
   }
}