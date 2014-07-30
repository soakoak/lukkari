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

class MpoliaRealizationAddingTask(context: Context) extends AsyncTask[MpoliaRealization, Void, Boolean] {

   private val TestLukkariName = "testilukkari"
   private val Tag = getClass.getSimpleName
   
   private def resolver = context.getContentResolver
   
   override protected def doInBackground(params: MpoliaRealization*): Boolean = {
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
      
      def sqInsertOps = {
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
         val listOfOperationLists = relzInsertOps :: sqInsertOps :: Nil
         listOfOperationLists flatMap(_.zipWithIndex) sortBy(_._2) map(_._1)
      }
      
      try{
         import collection.JavaConversions.seqAsJavaList
         resolver.applyBatch(LukkariContract.AUTHORITY, new ArrayList( operations))
      } catch {
         case ex: OperationApplicationException =>
            Log.d(Tag, "Applying patch failed")
            false
      }
      
      true
   }
   
   override protected def onPostExecute(result: Boolean) {
      val text = result match {
         case true => "Toteutukset lisätty onnistuneeti."
         case false => "Virhe lisätessätoteutuksia."
      }
      Toast.makeText(context, text, Toast.LENGTH_LONG).show()
   }
}