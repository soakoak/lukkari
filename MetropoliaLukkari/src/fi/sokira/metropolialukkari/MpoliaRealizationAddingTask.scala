package fi.sokira.metropolialukkari

import java.lang.{Boolean => JBoolean}
import scala.collection.JavaConversions._
import android.content.{ContentProviderOperation => CPOperation}
import android.content.ContentValues
import android.content.Context
import android.content.OperationApplicationException
import android.net.Uri
import android.util.Log
import android.widget.Toast
import fi.sokira.lukkari.provider.{LukkariContract => LContract}
import fi.sokira.metropolialukkari.models.MpoliaRealization
import java.util.ArrayList

class MpoliaRealizationAddingTask protected[metropolialukkari] (
      val context: Context,
      val toastsEnabled: Boolean
) extends MpoliaRealizationAddingTaskHelp {

   private val TestLukkariName = "testilukkari"
   private val Tag = getClass.getSimpleName
   
   def this(context: Context) = this(context, true)
   
   private def resolver = context.getContentResolver
   
   override protected def doInBackground1(params: Array[MpoliaRealization]): JBoolean = {
      import LContract._
      
      def realizationSeqAsContentValueSeq(realizations: Seq[MpoliaRealization]):
            Seq[ContentValues] = {
         import Realization.Columns._
         for(relz <- realizations) yield {
            val realizationValues = new ContentValues
            realizationValues.put(CODE, relz.getCode.toString)
            realizationValues.put(NAME, relz.getName)
            realizationValues.put(START_DATE, Long.box(relz.getStartDate.getTime))
            realizationValues.put(END_DATE, Long.box(relz.getEndDate.getTime))
            realizationValues
         }
      }
      
      val realizationValues = realizationSeqAsContentValueSeq(params)
      for(values <- realizationValues) {
         values.put(Realization.Columns.LUKKARI_NAME, TestLukkariName)
      }
      
      def relzInsertOps = 
         for(values <- realizationValues) yield { 
            CPOperation.newInsert(Realization.CONTENT_URI)
                  .withValues(values)
                  .build() 
         }
      
      def studentGroupInOps: Seq[CPOperation] = {
         val indexRange = 0 until relzInsertOps.length * 2 by 2
         val relzIndexIterator = indexRange.toIterator
         import StudentGroup.Columns
         for{relz <- params
            backReference = relzIndexIterator.next
            studentGroup <- relz.getStudentGroups } yield {
            CPOperation.newInsert(StudentGroup.CONTENT_URI)
               .withValueBackReference(Columns.REALIZATION_ID, backReference)
               .withValue(Columns.CODE, studentGroup.getCode)
               .build()
         }
      }
      
      def operations = {
         val listOfOperationLists: List[Seq[CPOperation]] = 
            relzInsertOps :: studentGroupInOps :: Nil
         listOfOperationLists flatMap(_.zipWithIndex) sortBy(_._2) map(_._1)
      }
      
      try{
         resolver.applyBatch(LContract.AUTHORITY, new ArrayList( operations))
         JBoolean.TRUE
      } catch {
         case ex: OperationApplicationException =>
            Log.d(Tag, "Applying patch failed")
            JBoolean.FALSE
         case ex: Exception =>
            Log.d(Tag, "Something unexpected happened upon applying patch.")
            Log.d(Tag, ex.getStackTraceString)
            JBoolean.FALSE
      }  
   }
   
   override protected def onPostExecute(result: JBoolean) {
      val text = result match {
         case JBoolean.TRUE => "Toteutukset lisätty onnistuneeti."
         case JBoolean.FALSE => "Virhe lisätessä toteutuksia."
      }

      if( toastsEnabled) {
         Toast.makeText(context, "", Toast.LENGTH_LONG).show()
      }
      
      Log.d(Tag, text)
   }
}