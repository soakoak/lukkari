package fi.sokira.metropolialukkari

import android.content.ContentProviderOperation
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.widget.Toast
import fi.sokira.lukkari.provider.{LukkariContract => Contract}
import fi.sokira.lukkari.provider.LukkariContract
import fi.sokira.metropolialukkari.models.MpoliaRealization
import java.util.ArrayList
import android.content.OperationApplicationException
import android.util.Log
import java.util.{List => JList}
import java.lang.{Boolean => JBoolean}

class MpoliaRealizationAddingTask(context: Context) extends MpoliaRealizationAddingTaskHelp {

   private val TestLukkariName = "testilukkari"
   private val Tag = getClass.getSimpleName
   
   private def resolver = context.getContentResolver
   
   override protected def doInBackground1(params: Array[MpoliaRealization]): JBoolean = {
      import Contract._
      
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
            ContentProviderOperation.newInsert(Realization.CONTENT_URI)
                  .withValues(values)
                  .build() 
         }
      
      def sqInsertOps: Seq[ContentProviderOperation] = {
         val indexRange = 0 until relzInsertOps.length * 2 by 2
         val relzIndexIterator = indexRange.toIterator
         import StudentGroup.Columns
         for(relz <- params; groupCode = relz.getCode) yield {
            ContentProviderOperation.newInsert(StudentGroup.CONTENT_URI)
               .withValueBackReference(Columns.REALIZATION_ID, relzIndexIterator.next)
               .withValue(Columns.CODE, groupCode)
               .build()
         }
      }
      
      def operations = {
         val listOfOperationLists: List[Seq[ContentProviderOperation]] = 
            relzInsertOps :: sqInsertOps :: Nil
         listOfOperationLists flatMap(_.zipWithIndex) sortBy(_._2) map(_._1)
      }
      
      try{
         import collection.JavaConversions.seqAsJavaList
         resolver.applyBatch(LukkariContract.AUTHORITY, new ArrayList( operations))
         JBoolean.TRUE
      } catch {
         case ex: OperationApplicationException =>
            Log.d(Tag, "Applying patch failed")
            JBoolean.FALSE
         case ex: Exception =>
            Log.d(Tag, "Something unexpected happened upon applying patch.")
            JBoolean.FALSE
      }  
   }
   
   override protected def onPostExecute(result: JBoolean) {
      val text = result match {
         case JBoolean.TRUE => "Toteutukset lisätty onnistuneeti."
         case JBoolean.FALSE => "Virhe lisätessätoteutuksia."
      }
      Toast.makeText(context, text, Toast.LENGTH_LONG).show()
   }
}