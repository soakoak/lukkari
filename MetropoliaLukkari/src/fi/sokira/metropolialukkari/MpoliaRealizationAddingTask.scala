package fi.sokira.metropolialukkari

import android.os.AsyncTask
import fi.sokira.metropolialukkari.models.MpoliaRealization
import android.widget.Toast
import android.content.Context
import fi.sokira.lukkari.provider.{LukkariContract => Contract}
import android.net.Uri
import android.content.ContentValues
import android.content.ContentProviderOperation

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
      
      val operations = for(values <- realizationValues) yield { 
         ContentProviderOperation.newInsert(Realization.CONTENT_URI)
                  .withValues(values) }
      false
   }
   
   override protected def onPostExecute(result: Boolean) {
      val text = result match {
         case true => "Toteutukset lisätty onnistuneeti."
         case false => "Virhe lisätessätoteutuksia."
      }
      Toast.makeText(context, text, Toast.LENGTH_LONG).show()
   }
}