package fi.sokira.metropolialukkari;

import fi.sokira.lukkari.provider.DatabaseHelper;
import fi.sokira.lukkari.provider.DbSchema;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

// http://www.androiddesignpatterns.com/2012/08/implementing-loaders.html
public class DataListLoader extends AsyncTaskLoader<Cursor> {
	
	private static final String TAG = DataListLoader.class.getSimpleName();
	
	private String mLukkariName;
	private String[] mProjection;
	
	private String mQuery;
	private Cursor mData;

	public DataListLoader(Context context, String lukkariName, String[] projection) {
		super(context);
		this.mLukkariName = lukkariName;
		this.mProjection = projection;
	}

	@Override
	public void deliverResult(Cursor data) {
		if( isReset()) {
			releaseResources( mData);
		} else {
			Cursor oldData = mData;
			mData = data;
			
			if( isStarted()) {
				super.deliverResult(data);
			}
			
			if( oldData != data) {
				releaseResources(oldData);
			}
		}
	}
	
	@Override
	public Cursor loadInBackground() {
		SQLiteDatabase db = 
				new DatabaseHelper(getContext()).getReadableDatabase();

		Cursor cursor = db.rawQuery(mQuery, null);
		Log.d(TAG, "Tuloksia: " + cursor.getCount());
		
		db.close();
		return cursor;
	}
	
	@Override
	protected void onStartLoading() {
		if( mData != null) {
			deliverResult(mData);
		}
		
		if( mQuery == null) {
			mQuery = formQuery();
		}
		
		if( mData == null) {
			forceLoad();
		}
	}
	
	@Override
	protected void onStopLoading() {
		cancelLoad();
	}
	
	@Override
	protected void onReset() {
		onStopLoading();
		
		if( mData != null) {
			releaseResources(mData);
			mData = null;
		}
		
		if( mQuery != null) {
			mQuery = null;
		}
	}
	
	@Override
	public void onCanceled(Cursor data) {
		releaseResources(data);
	}

	private String formQuery() {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		
		builder.setTables(DbSchema.TBL_DATA_LIST);
		builder.appendWhere( DbSchema.COL_NAME_LUKKARI);
		builder.appendWhere(" = ");
		builder.appendWhereEscapeString(mLukkariName);
		
		String query = builder.buildQuery(
				mProjection, 
				null, 
				null, 
				null, 
				null, 
				null);
		
		return query;
	}
	
	private void releaseResources(Cursor data) {
		if( data != null) {
			data.close();
		}
	}
}
