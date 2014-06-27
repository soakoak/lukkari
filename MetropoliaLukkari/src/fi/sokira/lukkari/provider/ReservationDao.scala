package fi.sokira.lukkari.provider

import fi.sokira.lukkari.{provider => lprovider}
import lprovider.LukkariContract.Reservation
import Reservation.Columns
import android.content.ContentValues

object ReservationDao extends AbstractDao {

   override val Tag = "ReservationDao"
   override val tableName = Reservation.PATH
   
   override def uniqueSelection(values: ContentValues): (String, Array[String]) = {
      def getValue(column: String) = values.getAsString(column)
      
      val columns = List(Columns.ROOM, Columns.START_DATE, Columns.END_DATE)
      val selection = columns map (_ + " = ?") mkString " AND "
      val selectionArgs = for(s <- columns) yield getValue(s)

      (selection, selectionArgs.toArray)
   }
}