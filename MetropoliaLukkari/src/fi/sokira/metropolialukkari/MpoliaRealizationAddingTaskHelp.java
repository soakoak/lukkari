package fi.sokira.metropolialukkari;

import android.os.AsyncTask;
import fi.sokira.metropolialukkari.models.MpoliaRealization;

public abstract class MpoliaRealizationAddingTaskHelp extends AsyncTask<MpoliaRealization, Void, Boolean> {

   @Override
   protected Boolean doInBackground(MpoliaRealization... params) {
      return doInBackground1(params);
   }
   
   protected abstract Boolean doInBackground1(MpoliaRealization[] params);
   
   @Override
   protected void onPostExecute(Boolean result) {
      super.onPostExecute(result);
   }
}
