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
			SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
			builder.setTables(DbSchema.TBL_REALIZATION);
			
			Cursor cursor = getLukkariByName(db, TEST_LUKKARI_NAME);
			cursor.moveToFirst();
			int lukId = cursor.getInt( cursor.getColumnIndex( DbSchema.COL_ID));
			
			Log.d(TAG, "Tulosten m‰‰r‰: " + cursor.getCount());
			Log.d(TAG, "Lukkarin " + TEST_LUKKARI_NAME + " indeksi: " + lukId);
			
			ContentValues values = new ContentValues();
			long relzId, groupId;
			
			String relzWhere = DbSchema.COL_CODE + " = ?";
			
			db.beginTransaction();
			try {
				for( Realization relz : params) {
					cursor = builder.query(db, 
							new String[]{ DbSchema.COL_ID }, 
							relzWhere, 
							new String[]{ relz.getCode() }, 
							null,
							null, 
							null);
					
					values.clear();
					values.put( DbSchema.COL_CODE, relz.getCode());
					values.put( DbSchema.COL_NAME, relz.getName());
					values.put( DbSchema.COL_START_DATE, relz.getStartDate().getTime());
					values.put( DbSchema.COL_END_DATE, relz.getEndDate().getTime());

					if( cursor.getCount() == 0) {
						relzId = db.insertWithOnConflict( DbSchema.TBL_REALIZATION, 
								null, values, SQLiteDatabase.CONFLICT_IGNORE);
					} else {
						relzId = getId(cursor, 0);
						db.updateWithOnConflict( 
								DbSchema.TBL_REALIZATION, 
								values, 
								relzWhere, 
								new String[]{ relz.getCode() }, 
								SQLiteDatabase.CONFLICT_FAIL);
					}
					
					for( StudentGroup group : relz.getStudentGroups()) {
						values.clear();
						values.put( DbSchema.COL_CODE, group.getCode());
						groupId = db.insertWithOnConflict( DbSchema.TBL_STUDENT_GROUP, 
								null, values, SQLiteDatabase.CONFLICT_IGNORE);
						
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
			
			builder = new SQLiteQueryBuilder();
			
			builder.setTables( DbSchema.TBL_REALIZATION);
			cursor = builder.query(db, 
					new String[]{ DbSchema.COL_ID, DbSchema.COL_CODE}, 
					null,
					null, 
					null, 
					null,
					DbSchema.COL_CODE + " ASC");
			cursor.moveToFirst();

			Log.d(TAG, "Toteutusten m‰‰r‰: " + cursor.getCount());
			
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if( result) {
				Toast.makeText( getApplication(), 
						"Toteutukset lis‰tty onnistuneeti.", 
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText( getApplication(), 
						"Virhe lis‰tess‰ toteutuksia.", 
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
				Resource realization = findResource(
						reservation.getResources(), 
						Resource.TYPE_REALIZATION);
				
				if( realization.getCode().isEmpty()) {
					Log.d(TAG, "Tyhj‰ koodi");
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
					long resId;
					try {
						resId = db.insertOrThrow( 
							DbSchema.TBL_RESERVATION, null, values);
					} catch( SQLException e) {
						Log.d(TAG, "There was an existing record of the reservation.");
						builder = new SQLiteQueryBuilder();
						builder.setTables( DbSchema.TBL_RESERVATION);
						cursor = builder.query(db,
							new String[]{ DbSchema.COL_ID }, 
							DbSchema.COL_ROOM + " = ? "
								+ "AND " + DbSchema.COL_START_DATE + "= ? "
								+ "AND " + DbSchema.COL_END_DATE + " = ?", 
							new String[]{ 
								roomCode, 
								String.valueOf(reservation.getStartDate().getTime()), 
								String.valueOf(reservation.getEndDate().getTime()) 									
							}, 
							null,
							null, 
							null);
						
						resId = getId( cursor, 0);
					}
					
					ArrayList<Resource> studentGroups = findAllResources(
							reservation.getResources(), 
							Resource.TYPE_STUDENT_GROUP);
					
					long groupId;
					for(Resource group : studentGroups) {
						String groupCode = group.getName();
						
						values.clear();
						values.put( DbSchema.COL_CODE, groupCode);
						try{
							groupId = db.insertOrThrow( DbSchema.TBL_STUDENT_GROUP, 
								null, values);
						} catch( SQLException e) {
							Log.d(TAG, 
								groupCode + ": there was an existing record.");
							
							builder = new SQLiteQueryBuilder();
							builder.setTables( DbSchema.TBL_STUDENT_GROUP);
							cursor = builder.query(db,
								new String[]{ DbSchema.COL_ID }, 
								DbSchema.COL_CODE + " = ?", 
								new String[]{ 
									groupCode
								}, 
								null,
								null, 
								null);
							
							groupId = getId( cursor, 0);
						}
						
						values.clear();
						values.put( DbSchema.COL_ID_REALIZATION, relzId);
						values.put( DbSchema.COL_ID_STUDENT_GROUP, groupId);
						db.insertWithOnConflict( 
								DbSchema.TBL_REALIZATION_TO_STUDENT_GROUP, 
								null, values, SQLiteDatabase.CONFLICT_IGNORE);
						
						values.clear();
						values.put( DbSchema.COL_ID_RESERVATION, resId);
						values.put( DbSchema.COL_ID_STUDENT_GROUP, groupId);
						db.insertWithOnConflict( 
								DbSchema.TBL_RESERVATION_TO_STUDENT_GROUP, 
								null, values, SQLiteDatabase.CONFLICT_IGNORE);
					}
					
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

			Log.d(TAG, "Varausten m‰‰r‰: " + cursor.getCount());
			
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if( result) {
				Toast.makeText( getApplication(), 
						"Varaukset lis‰tty onnistuneeti.", 
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText( getApplication(), 
						"Virhe lis‰tess‰ varauksia.", 
						Toast.LENGTH_LONG).show();
			}
		}
		
		private Resource findResource( 
				Collection<Resource> resources, String resourceType) {
			for( Resource resource : resources) {
				if( resource.getType().equals( resourceType)) {
					return resource;
				}
			}
			return null;
		}
		
		private ArrayList<Resource> findAllResources( 
				Collection<Resource> resources, String resourceType) {
			ArrayList<Resource> subList = new ArrayList<Resource>( resources.size() / 2);
		
			for( Resource resource : resources) {
				if( resource.getType().equals( resourceType)) {
					subList.add( resource);
				}
			}
			
			return subList;
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
