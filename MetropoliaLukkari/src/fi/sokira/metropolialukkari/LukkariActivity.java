package fi.sokira.metropolialukkari;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import fi.sokira.lukkari.provider.DatabaseHelper;
import fi.sokira.lukkari.provider.DbSchema;
import fi.sokira.metropolialukkari.HakuFragment.OnSearchListener;
import fi.sokira.metropolialukkari.models.RealizationResult;
import fi.sokira.metropolialukkari.models.ReservationResult;
import fi.sokira.metropolialukkari.models.Result;

public class LukkariActivity extends Activity
		implements
			HakuFragment.OnSearchListener {
	
	private final static String TAG = "LukkariActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lukkari);
		
		if( savedInstanceState == null) {
			getFragmentManager()
			.beginTransaction()
			.add(android.R.id.content, 
					new HakuFragment())
			.commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.lukkari, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch( item.getItemId()) {
		case R.id.test :
			SQLiteOpenHelper helper = new DatabaseHelper( this);
			helper.onUpgrade(helper.getWritableDatabase(), 1, 2);
			SQLiteDatabase db = helper.getWritableDatabase();
			
			db.beginTransaction();
			
			try {
				ContentValues values = new ContentValues();
				values.put(DbSchema.COL_NAME, "testilukkari");
				long lkrId = db.insert( DbSchema.TBL_LUKKARI, null, values);
				Log.d(TAG, "Lukkari row id: " + lkrId);
				
				values.clear();
				values.put(DbSchema.COL_STUDENT_GROUPS, "TO11K");
				values.put(DbSchema.COL_NAME, "Pelitekoälyt");
				values.put(DbSchema.COL_END_DATE, System.currentTimeMillis());
				long totId = db.insert( DbSchema.TBL_TOTEUTUS, null, values);
				Log.d( TAG, "Toteutus row id: " + totId);
				
				values.clear();
				values.put(DbSchema.COL_ID_LUKKARI, lkrId);
				values.put(DbSchema.COL_ID_TOTEUTUS, totId);
				db.insert( DbSchema.TBL_LUKKARI_TO_TOTEUTUS, null, values);
				
				values.clear();
				values.put(DbSchema.COL_ID_TOTEUTUS, totId);
				values.put(DbSchema.COL_TILA, "U204");
				values.put(DbSchema.COL_END_DATE, System.currentTimeMillis());
				db.insert(DbSchema.TBL_VARAUS, null, values);
				
				db.setTransactionSuccessful();
			} catch (SQLException e) {
				Log.d(TAG, "Error while inserting to db");
			} finally {
				db.endTransaction();
			}
			
			Cursor c;
			String select = "SELECT COUNT(*) FROM ";
			String[] tables = {
				DbSchema.TBL_LUKKARI,
				DbSchema.TBL_TOTEUTUS,
				DbSchema.TBL_LUKKARI_TO_TOTEUTUS,
				DbSchema.TBL_VARAUS
			};
			for(int i = tables.length - 1; i >= 0; i--) {
				c = db.rawQuery(select + tables[i], new String[]{});
				c.moveToFirst();
				if( c.getInt(0) > 0) {
					Log.d(TAG, "Table " + tables[i] + " seems to work.");
				} else {
					Log.d(TAG, "Error while reading table " + tables[i] + ".");
				}
			}
			db.close();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSearchInitiated() {
		Log.d( TAG, "Haku aloitettu");
	}
	
	@Override
	public void onSearchFinished(Result result, int resultType) {
		ToteutusListFragment frag = new ToteutusListFragment();
		
		Bundle args = new Bundle(2);
		
		switch( resultType) {
		case OnSearchListener.RESULT_REALIZATION :
			args.putParcelableArrayList( 
					ToteutusListFragment.ARG_RESULT, 
					((RealizationResult) result).getRealizations());
			args.putInt( 
					ToteutusListFragment.ARG_RESULT_TYPE, 
					ToteutusListFragment.TYPE_REALIZATION);
			break;
			
		case OnSearchListener.RESULT_RESERVATION :
			args.putParcelableArrayList(
					ToteutusListFragment.ARG_RESULT, 
					((ReservationResult) result).getReservations());
			args.putInt(
					ToteutusListFragment.ARG_RESULT_TYPE, 
					ToteutusListFragment.TYPE_RESERVATION);
			break;
		default:
			break;
		}
		
		frag.setArguments( args);
		getFragmentManager()
			.beginTransaction()
			.replace(android.R.id.content, frag)
			.addToBackStack( null)
			.commit();
	}
}
