package fi.sokira.metropolialukkari;

import java.util.ArrayList;
import java.util.Collection;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.DialogInterface;
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
import fi.sokira.lukkari.provider.LukkariContract;
import fi.sokira.lukkari.provider.LukkariContract.Lukkari;
import fi.sokira.metropolialukkari.HakuFragment.OnSearchListener;
import fi.sokira.metropolialukkari.models.MpoliaRealization;
import fi.sokira.metropolialukkari.models.MpoliaRealizationResult;
import fi.sokira.metropolialukkari.models.MpoliaReservation;
import fi.sokira.metropolialukkari.models.MpoliaReservationResult;
import fi.sokira.metropolialukkari.models.MpoliaResource;
import fi.sokira.metropolialukkari.models.MpoliaResult;
import fi.sokira.metropolialukkari.models.MpoliaResultItem;
import fi.sokira.metropolialukkari.models.MpoliaStudentGroup;

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
		case R.id.see_current :
			Fragment frag = new LukkariDataListFragment();
			Bundle args = new Bundle(1);
			
			args.putString( 
					LukkariDataListFragment.ARG_LUKKARI_NAME, 
					TEST_LUKKARI_NAME);
			frag.setArguments(args);
			
			getFragmentManager()
				.beginTransaction()
				.replace(android.R.id.content, frag)
				.addToBackStack( null)
				.commit();
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onSearchInitiated() {
		Log.d( TAG, "Haku aloitettu");
	}
	
	@Override
	public void onSearchFinished(MpoliaResult<?> result, int resultType) {
		ResultListFragment frag = new ResultListFragment();
		
		Bundle args = new Bundle(2);
		
		switch( resultType) {
		case OnSearchListener.RESULT_REALIZATION :
			args.putParcelableArrayList( 
					ResultListFragment.ARG_RESULT, 
					((MpoliaRealizationResult) result).getRealizations());
			args.putInt( 
					ResultListFragment.ARG_RESULT_TYPE, 
					ResultListFragment.TYPE_REALIZATION);
			break;
			
		case OnSearchListener.RESULT_RESERVATION :
			args.putParcelableArrayList(
					ResultListFragment.ARG_RESULT, 
					((MpoliaReservationResult) result).getReservations());
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
	public void onResultItemSelected(MpoliaResultItem item, int itemType) {
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
	public void onResultItemsAdded(
			final ArrayList<MpoliaResultItem> items, int itemType) {
			
		switch( itemType) {
		
		case ResultListFragment.TYPE_REALIZATION :
			new AlertDialog.Builder( this)
				.setMessage(R.string.dialog_add_related_reservations)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new SqlRealizationAddingTask( true)
							.execute( items.toArray(new MpoliaRealization[items.size()]));
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new SqlRealizationAddingTask()
							.execute( items.toArray(new MpoliaRealization[items.size()]));
					}
				})
				.show();
			break;
			
		case ResultListFragment.TYPE_RESERVATION :
			new SqlReservationAddingTask().execute( 
					items.toArray( new MpoliaReservation[ items.size()]));
			break;
			
		default:
			break;
		}
	}
	
	private abstract class SqlAddingTask<T> 
						extends AsyncTask<T, Void, Boolean> {
		
		private SQLiteDatabase mDatabase = null;
		
		protected void setWritableDatabase( SQLiteDatabase db) {
			mDatabase = db;
		}
		
		protected Cursor getLukkariByName(String lukkariName) {			
			SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
			
			builder.setTables( DbSchema.TBL_LUKKARI);
			builder.appendWhere( 
					DbSchema.COL_NAME + " = '" + lukkariName + "'");
			return builder.query(mDatabase, 
					new String[]{ Lukkari._ID,  Lukkari.NAME}, 
					null,
					null, 
					null, 
					null,
					Lukkari.NAME + " ASC");
		}
		
		protected int getIdColumnValue( Cursor cursor, int idx) {
			cursor.moveToPosition( idx);
			return cursor.getInt( cursor.getColumnIndex( DbSchema.COL_ID));
		}
		
		protected long insertStudentGroup(String groupCode) {
			ContentValues values = new ContentValues();
			values.put( DbSchema.COL_CODE, groupCode);
			
			long groupId;
			try{
				groupId = mDatabase.insertOrThrow( DbSchema.TBL_STUDENT_GROUP, 
					null, values);
			} catch( SQLException e) {
				Log.d(TAG, 
					groupCode + ": there was an existing record.");
				
				SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
				builder.setTables( DbSchema.TBL_STUDENT_GROUP);
				Cursor cursor = builder.query(mDatabase,
					new String[]{ DbSchema.COL_ID }, 
					DbSchema.COL_CODE + " = ?", 
					new String[]{ 
						groupCode
					}, 
					null,
					null, 
					null);
				
				groupId = getIdColumnValue( cursor, 0);
				cursor.close();
			} 
			
			return groupId;
		}
	}

	private class SqlRealizationAddingTask extends SqlAddingTask<MpoliaRealization> {
		
		private final String TAG = SqlRealizationAddingTask.class.getSimpleName();
		
		private boolean mAddRelevantReservations; //TODO tuki
		
		public SqlRealizationAddingTask() {
			this(false);
		}
		
		public SqlRealizationAddingTask(boolean addRelevantReservations) {
			mAddRelevantReservations = addRelevantReservations;
		}
		
		@Override
		protected Boolean doInBackground(MpoliaRealization... params) {
			SQLiteOpenHelper helper = new DatabaseHelper(getApplication());
			SQLiteDatabase db = helper.getWritableDatabase();
			setWritableDatabase(db);
			SQLiteQueryBuilder builder;
			
			ContentValues values = new ContentValues();
			long relzId, groupId, lukId;

			Cursor cursor = getLukkariByName(TEST_LUKKARI_NAME);
			if( cursor.getCount() > 0) {
				cursor.moveToFirst();
				lukId = cursor.getInt( cursor.getColumnIndex( DbSchema.COL_ID));
			} else {
				values.clear();
				values.put( DbSchema.COL_NAME, TEST_LUKKARI_NAME);
				lukId = db.insertOrThrow( 
						DbSchema.TBL_LUKKARI, null, values);
			}
			
			
			Log.d(TAG, "Tulosten määrä " + cursor.getCount());
			Log.d(TAG, "Lukkarin " + TEST_LUKKARI_NAME + " indeksi: " + lukId);
			
			String relzWhere = DbSchema.COL_CODE + " = ?";
			
			db.beginTransaction();
			try {
				for( MpoliaRealization relz : params) {
					builder = new SQLiteQueryBuilder();
					builder.setTables(DbSchema.TBL_REALIZATION);
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
						relzId = getIdColumnValue(cursor, 0);
						db.updateWithOnConflict( 
								DbSchema.TBL_REALIZATION, 
								values, 
								relzWhere, 
								new String[]{ relz.getCode() }, 
								SQLiteDatabase.CONFLICT_FAIL);
					}
					
					for( MpoliaStudentGroup group : relz.getStudentGroups()) {
						groupId = insertStudentGroup(group.getCode());
						
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

			Log.d(TAG, "Toteutusten määrä " + cursor.getCount());
			
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
						"Virhe lisätessätoteutuksia.", 
						Toast.LENGTH_LONG).show();
			}
		}
	}
	
	private class SqlReservationAddingTask extends SqlAddingTask<MpoliaReservation> {
		
		private final String TAG = SqlReservationAddingTask.class.getSimpleName();
		
		@Override
		protected Boolean doInBackground(MpoliaReservation... params) {
			SQLiteOpenHelper helper = new DatabaseHelper(getApplication());
			SQLiteDatabase db = helper.getWritableDatabase();
			setWritableDatabase(db);
			SQLiteQueryBuilder builder = null;
			ContentValues values = new ContentValues();
			
			for( MpoliaReservation reservation : params) {	
				MpoliaResource realization = findResource(
						reservation.getResources(), 
						MpoliaResource.TYPE_REALIZATION);
				
				if( realization.getCode().isEmpty()) {
					Log.d(TAG, "Tyhjä koodi, ei käsitellä.");
					continue;
				}
				
				builder = new SQLiteQueryBuilder();
				builder.setTables( DbSchema.TBL_REALIZATION);
				builder.appendWhere(
						DbSchema.COL_CODE + " = '" + 
						realization.getCode() + "'");
				Cursor cursor = builder.query(db,
						new String[]{ DbSchema.COL_ID, LukkariContract.Realization.CODE }, 
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
							getIdColumnValue( getLukkariByName(TEST_LUKKARI_NAME), 0);
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
							MpoliaResource.TYPE_ROOM)
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
						
						resId = getIdColumnValue( cursor, 0);
					}
					
					ArrayList<MpoliaResource> studentGroups = findAllResources(
							reservation.getResources(), 
							MpoliaResource.TYPE_STUDENT_GROUP);
					
					long groupId;
					for(MpoliaResource group : studentGroups) {
						String groupCode = group.getName();
						
						groupId = insertStudentGroup(groupCode);
						
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

			Log.d(TAG, "Varausten määrä " + cursor.getCount());
			
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
						"Virhe lisätessävarauksia.", 
						Toast.LENGTH_LONG).show();
			}
		}
		
		private MpoliaResource findResource( 
				Collection<MpoliaResource> resources, String resourceType) {
			for( MpoliaResource resource : resources) {
				if( resource.getType().equals( resourceType)) {
					return resource;
				}
			}
			return null;
		}
		
		private ArrayList<MpoliaResource> findAllResources( 
				Collection<MpoliaResource> resources, String resourceType) {
			ArrayList<MpoliaResource> subList = new ArrayList<MpoliaResource>( resources.size() / 2);
		
			for( MpoliaResource resource : resources) {
				if( resource.getType().equals( resourceType)) {
					subList.add( resource);
				}
			}
			
			return subList;
		}
	}
}
