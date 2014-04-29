package fi.sokira.metropolialukkari;

import java.util.ArrayList;
import java.util.Collection;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import fi.sokira.lukkari.provider.DatabaseHelper;
import fi.sokira.lukkari.provider.DbSchema;
import fi.sokira.lukkari.provider.LukkariContract.Lukkari;
import fi.sokira.metropolialukkari.HakuFragment.OnSearchListener;
import fi.sokira.metropolialukkari.models.Realization;
import fi.sokira.metropolialukkari.models.RealizationResult;
import fi.sokira.metropolialukkari.models.Reservation;
import fi.sokira.metropolialukkari.models.ReservationResult;
import fi.sokira.metropolialukkari.models.Resource;
import fi.sokira.metropolialukkari.models.Result;
import fi.sokira.metropolialukkari.models.ResultItem;
import fi.sokira.metropolialukkari.models.StudentGroup;

public class LukkariActivity extends Activity
		implements
			HakuFragment.OnSearchListener,
			ResultListFragment.OnResultItemSelectedListener {
	
	private final static String TAG = "LukkariActivity";
	
	private final static String TEST_LUKKARI_NAME = "testilukkari";

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
				values.put(DbSchema.COL_NAME, TEST_LUKKARI_NAME);
				long lkrId = db.insert( DbSchema.TBL_LUKKARI, null, values);
				Log.d(TAG, "Lukkari row id: " + lkrId);
				
				values.clear();
				values.put(DbSchema.COL_NAME, "Pelitekoälyt");
				values.put(DbSchema.COL_END_DATE, System.currentTimeMillis());
				long totId = db.insert( DbSchema.TBL_REALIZATION, null, values);
				Log.d( TAG, "Toteutus row id: " + totId);
				
				values.clear();
				values.put(DbSchema.COL_ID_LUKKARI, lkrId);
				values.put(DbSchema.COL_ID_REALIZATION, totId);
				db.insert( DbSchema.TBL_LUKKARI_TO_REALIZATION, null, values);
				
				values.clear();
				values.put(DbSchema.COL_ID_REALIZATION, totId);
				values.put(DbSchema.COL_ROOM, "U204");
				values.put(DbSchema.COL_END_DATE, System.currentTimeMillis());
				long varId = db.insert(DbSchema.TBL_RESERVATION, null, values);
				
				values.clear();
				values.put(DbSchema.COL_CODE, "TO11K");
				long ryhmaId = db.insert( DbSchema.TBL_STUDENT_GROUP, null, values);
				
				values.clear();
				values.put( DbSchema.COL_ID_REALIZATION, totId);
				values.put( DbSchema.COL_ID_STUDENT_GROUP, ryhmaId);
				db.insert(DbSchema.TBL_REALIZATION_TO_STUDENT_GROUP, null, values);

				values.clear();
				values.put( DbSchema.COL_ID_RESERVATION, varId);
				values.put( DbSchema.COL_ID_STUDENT_GROUP, ryhmaId);
				db.insert(DbSchema.TBL_RESERVATION_TO_STUDENT_GROUP, null, values);
				
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
					DbSchema.TBL_REALIZATION,
					DbSchema.TBL_LUKKARI_TO_REALIZATION,
					DbSchema.TBL_RESERVATION,
					DbSchema.TBL_STUDENT_GROUP,
					DbSchema.TBL_REALIZATION_TO_STUDENT_GROUP,
					DbSchema.TBL_RESERVATION_TO_STUDENT_GROUP
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
		ResultListFragment frag = new ResultListFragment();
		
		Bundle args = new Bundle(2);
		
		switch( resultType) {
		case OnSearchListener.RESULT_REALIZATION :
			args.putParcelableArrayList( 
					ResultListFragment.ARG_RESULT, 
					((RealizationResult) result).getRealizations());
			args.putInt( 
					ResultListFragment.ARG_RESULT_TYPE, 
					ResultListFragment.TYPE_REALIZATION);
			break;
			
		case OnSearchListener.RESULT_RESERVATION :
			args.putParcelableArrayList(
					ResultListFragment.ARG_RESULT, 
					((ReservationResult) result).getReservations());
			args.putInt(
					ResultListFragment.ARG_RESULT_TYPE, 
					ResultListFragment.TYPE_RESERVATION);
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

	@Override
	public void onResultItemSelected(ResultItem item, int itemType) {
		Fragment frag = null;
		Bundle args = new Bundle(1);
		
		switch( itemType) {
		
		case ResultListFragment.TYPE_REALIZATION :
			frag = new RealizationDetailFragment();
			args.putParcelable( 
					RealizationDetailFragment.ARG_REALIZATION, item);
			break;
			
		case ResultListFragment.TYPE_RESERVATION :
			frag = new ReservationDetailFragment();
			args.putParcelable( 
					ReservationDetailFragment.ARG_RESERVATION, item);
			break;
			
		default:
			break;
		}
		
		if( frag != null) {
			frag.setArguments(args);
			
			getFragmentManager()
				.beginTransaction()
				.replace(android.R.id.content, frag)
				.addToBackStack( null)
				.commit();
		}
	}
	
	@Override
	public void onResultItemsAdded(ArrayList<ResultItem> items, int itemType) {
			
		switch( itemType) {
		
		case ResultListFragment.TYPE_REALIZATION :
			new SqlRealizationAddingTask().execute( 
					items.toArray( new Realization[ items.size()]));
			break;
			
		case ResultListFragment.TYPE_RESERVATION :
			new SqlReservationAddingTask().execute( 
					items.toArray( new Reservation[ items.size()]));
			break;
			
		default:
			break;
		}
	}

	private class SqlRealizationAddingTask extends AsyncTask<Realization, Void, Boolean> {
		
		private final String TAG = SqlRealizationAddingTask.class.getSimpleName();
		
		@Override
		protected Boolean doInBackground(Realization... params) {
			SQLiteOpenHelper helper = new DatabaseHelper(getApplication());
			SQLiteDatabase db = helper.getWritableDatabase();
			
			Cursor cursor = getLukkariByName(db, TEST_LUKKARI_NAME);
			cursor.moveToFirst();
			int lukId = cursor.getInt( cursor.getColumnIndex( DbSchema.COL_ID));
			
			Log.d(TAG, "Tulosten määrä: " + cursor.getCount());
			Log.d(TAG, "Lukkarin " + TEST_LUKKARI_NAME + " indeksi: " + lukId);
			
			ContentValues values = new ContentValues();
			long relzId, groupId;
			
			db.beginTransaction();
			try {
				for( Realization relz : params) {
					values.clear();
					values.put( DbSchema.COL_CODE, relz.getCode());
					values.put( DbSchema.COL_NAME, relz.getName());
					values.put( DbSchema.COL_START_DATE, relz.getStartDate().getTime());
					values.put( DbSchema.COL_END_DATE, relz.getEndDate().getTime());
					relzId = db.insertWithOnConflict( DbSchema.TBL_REALIZATION, 
							null, values, SQLiteDatabase.CONFLICT_REPLACE);
					
					for( StudentGroup group : relz.getStudentGroups()) {
						values.clear();
						values.put( DbSchema.COL_CODE, group.getCode());
						groupId = db.insertWithOnConflict( DbSchema.TBL_STUDENT_GROUP, 
								null, values, SQLiteDatabase.CONFLICT_REPLACE);
						
						values.clear();
						values.put( DbSchema.COL_ID_REALIZATION, relzId);
						values.put( DbSchema.COL_ID_STUDENT_GROUP, groupId);
						db.insertWithOnConflict( 
								DbSchema.TBL_REALIZATION_TO_STUDENT_GROUP, 
								null, values, SQLiteDatabase.CONFLICT_IGNORE);
					}
					
					values.clear();
					values.put( DbSchema.COL_ID_LUKKARI, lukId);
					values.put( DbSchema.COL_ID_REALIZATION, relzId);
					db.insertWithOnConflict( 
							DbSchema.TBL_LUKKARI_TO_REALIZATION, 
							null, values, SQLiteDatabase.CONFLICT_IGNORE);
				}
				
				db.setTransactionSuccessful();			
			} catch (SQLException e) {
				Log.d(TAG, "Error while inserting to db");
				return false;
			} finally {
				db.endTransaction();
			}
			
			SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
			
			builder.setTables( DbSchema.TBL_REALIZATION);
			cursor = builder.query(db, 
					new String[]{ DbSchema.COL_ID, DbSchema.COL_CODE}, 
					null,
					null, 
					null, 
					null,
					DbSchema.COL_CODE + " ASC");
			cursor.moveToFirst();

			Log.d(TAG, "Toteutusten määrä: " + cursor.getCount());
			
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if( result) {
				Toast.makeText( getApplication(), 
						"Toteutukset lisätty onnistuneeti.", 
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText( getApplication(), 
						"Virhe lisätessä toteutuksia.", 
						Toast.LENGTH_LONG).show();
			}
		}
	}
	
	private class SqlReservationAddingTask extends AsyncTask<Reservation, Void, Boolean> {
		
		private final String TAG = SqlReservationAddingTask.class.getSimpleName();
		
		@Override
		protected Boolean doInBackground(Reservation... params) {
			SQLiteOpenHelper helper = new DatabaseHelper(getApplication());
			SQLiteDatabase db = helper.getWritableDatabase();
			SQLiteQueryBuilder builder = null;
			ContentValues values = new ContentValues();
			
			for( Reservation reservation : params) {	
				Resource realization = 
						findResource( reservation.getResources(), Resource.TYPE_REALIZATION);
				if( realization.getCode().isEmpty()) {
					Log.d(TAG, "Tyhjä koodi");
					continue;
				}
				
				builder = new SQLiteQueryBuilder();
				builder.setTables( DbSchema.TBL_REALIZATION);
				builder.appendWhere(
						DbSchema.COL_CODE + " = '" + 
						realization.getCode() + "'");
				Cursor cursor = builder.query(db, 
						new String[]{ DbSchema.COL_ID, DbSchema.COL_CODE }, 
						null,
						null, 
						null, 
						null,
						DbSchema.COL_CODE + " ASC");
				
				long relzId;
				
				if( cursor.getCount() > 0) {
					cursor.moveToFirst();
					relzId = cursor.getInt( cursor.getColumnIndex( DbSchema.COL_ID));
				} else {
					db.beginTransaction();
					try {
						values.clear();
						values.put( DbSchema.COL_CODE, realization.getCode());
						values.put( DbSchema.COL_NAME, realization.getName());
						relzId = db.insertWithOnConflict( DbSchema.TBL_REALIZATION, 
								null, values, SQLiteDatabase.CONFLICT_IGNORE);
						
						int lukId = 
							getId( getLukkariByName(db, TEST_LUKKARI_NAME), 0);
						values.clear();
						values.put( DbSchema.COL_ID_LUKKARI, lukId);
						values.put( DbSchema.COL_ID_REALIZATION, relzId);
						db.insertWithOnConflict( 
								DbSchema.TBL_LUKKARI_TO_REALIZATION, 
								null, values, SQLiteDatabase.CONFLICT_IGNORE);
						
						db.setTransactionSuccessful();
					} catch (SQLException e) {
						Log.d(TAG, "Error while inserting to db");
						return false;
					} finally {
						db.endTransaction();
					}
				}
				
				db.beginTransaction();
				try {
					String roomCode = findResource( 
							reservation.getResources(), 
							Resource.TYPE_ROOM)
						.getCode();
					values.clear();
					values.put( DbSchema.COL_ID_REALIZATION, relzId);
					values.put( DbSchema.COL_ROOM, roomCode);
					values.put( DbSchema.COL_START_DATE, 
							reservation.getStartDate().getTime());
					values.put( DbSchema.COL_END_DATE, 
							reservation.getEndDate().getTime());
					long resId = db.insertWithOnConflict( 
							DbSchema.TBL_RESERVATION, 
							null, values, SQLiteDatabase.CONFLICT_REPLACE);
					Log.d(TAG, "Lisätyn varauksen rivi tietokannassa: " + resId);
					
					db.setTransactionSuccessful();
				} catch (SQLException e) {
					Log.d(TAG, "Error while inserting to db");
					return false;
				} finally {
					db.endTransaction();
				}
			}
			
			builder = new SQLiteQueryBuilder();
			builder.setTables( DbSchema.TBL_RESERVATION);
			Cursor cursor = builder.query(db, 
					new String[]{ DbSchema.COL_ID }, 
					null,
					null, 
					null, 
					null,
					null);
			cursor.moveToFirst();

			Log.d(TAG, "Varausten määrä: " + cursor.getCount());
			
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if( result) {
				Toast.makeText( getApplication(), 
						"Varaukset lisätty onnistuneeti.", 
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText( getApplication(), 
						"Virhe lisätessä varauksia.", 
						Toast.LENGTH_LONG).show();
			}
		}
		
		private Resource findResource( Collection<Resource> resources, String resourceType) {
			for( Resource resource : resources) {
				if( resource.getType().equals( resourceType)) {
					return resource;
				}
			}
			return null;
		}
	}
	
	protected Cursor getLukkariByName( SQLiteDatabase db, String lukkariName) {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		
		builder.setTables( DbSchema.TBL_LUKKARI);
		builder.appendWhere( 
				DbSchema.COL_NAME + " = '" + lukkariName + "'");
		return builder.query(db, 
				new String[]{ Lukkari._ID,  Lukkari.LUKKARI_NAME}, 
				null,
				null, 
				null, 
				null,
				Lukkari.LUKKARI_NAME + " ASC");
	}
	
	protected int getId( Cursor cursor, int idx) {
		cursor.moveToPosition( idx);
		return cursor.getInt( cursor.getColumnIndex( DbSchema.COL_ID));
	}
}
