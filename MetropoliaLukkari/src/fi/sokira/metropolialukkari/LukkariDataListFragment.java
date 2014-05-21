package fi.sokira.metropolialukkari;

import java.text.DateFormat;
import java.util.Date;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import fi.sokira.lukkari.provider.DbSchema;

public class LukkariDataListFragment extends ListFragment {
	
	public static final String ARG_LUKKARI_NAME = "lukkari_name";
	private static final int DATA_LIST_LOADER = 0;
	
	private static final String ARG_PROJECTION = "projection";
	
	private static final String TAG = LukkariDataListFragment.class.getSimpleName();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate( R.layout.vertical_list, container, false);
		
		String[] from = new String[]{ 
				DbSchema.COL_NAME_REALIZATION, 
				DbSchema.COL_ROOM, 
				DbSchema.COL_START_DATE,
				DbSchema.COL_END_DATE };
		int[] to = { 
				R.id.subject, 
				R.id.room,
				R.id.start_time,
				R.id.end_time };

		CursorAdapter adapter = new LukkariDataCursorAdapter(
				getActivity(), 
				R.layout.lukkari_data_list_item, 
				null,
				from, 
				to, 
				0);
		
		String[] projection = new String[ from.length + 1];
		projection[0] = DbSchema.COL_ID;
		System.arraycopy(from, 0, projection, 1, from.length);
		
		Bundle args = getArguments();
		args.putStringArray(ARG_PROJECTION, projection);
		getLoaderManager().initLoader(DATA_LIST_LOADER, args, new DataListCallback(adapter));
		
		setListAdapter(adapter);
		
		return v;
	}
	
	private class DataListCallback implements LoaderManager.LoaderCallbacks<Cursor> {
		
		private CursorAdapter mAdapter;
		
		public DataListCallback(CursorAdapter adapter) {
			mAdapter = adapter;
		}

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			Loader<Cursor> loader = null;
			
			switch( id) {
			case DATA_LIST_LOADER :
				String lukkariName = args.getString(ARG_LUKKARI_NAME);
				String[] projection = args.getStringArray(ARG_PROJECTION);
				loader = new DataListLoader(
						getActivity(), 
						lukkariName,
						projection);
				break;
			}
			
			return loader;
		}
	
		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			switch( loader.getId()) {
			case DATA_LIST_LOADER :
				mAdapter.changeCursor(data);
				break;
			}
			
			Log.d(TAG, "Loader: Loading finished.");
		}
	
		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
			mAdapter.swapCursor( null);
		}

	}
	
	private class LukkariDataCursorAdapter extends CursorAdapter {

		private String[] mOriginalFrom;
		private int[] mFrom;
		private int[] mTo;
		private int mLayout;
		
		private final static int DATE_LONG = 1;
		private final static int DATE_SHORT = 0;
		
		public LukkariDataCursorAdapter(Context context, int layout, Cursor c,
				String[] from, int[] to, int flags) {
			super(context, c, flags);
			mLayout = layout;
			mOriginalFrom = from;
			mTo = to;
			findColumns(c, from);
		}
		
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			for(int i = 0; i < mTo.length; i++) {
				final View v = view.findViewById(mTo[i]);
				if(v != null) {
					switch( mTo[i]) {
					case R.id.start_time :
						setViewDate((TextView) v, cursor, mFrom[i], DATE_LONG);
						break;
					case R.id.end_time :
						setViewDate((TextView) v, cursor, mFrom[i], DATE_SHORT);
						break;
					default :
						setViewText((TextView) v, cursor, mFrom[i]);
						break;
					}
				}
			}
		}
		
		@Override
		public void changeCursor(Cursor cursor) {
			if( mFrom == null) {
				findColumns(cursor, mOriginalFrom);
			}
			super.changeCursor(cursor);
		}
		
	    private void findColumns(Cursor c, String[] from) {
	        if (c != null) {
	            int i;
	            int count = from.length;
	            if (mFrom == null || mFrom.length != count) {
	                mFrom = new int[count];
	            }
	            for (i = 0; i < count; i++) {
	                mFrom[i] = c.getColumnIndexOrThrow(from[i]);
	            }
	        } else {
	            mFrom = null;
	        }
	    }
	    
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = getActivity().getLayoutInflater();
			return inflater.inflate(mLayout, parent, false);
		}
	    
	    private void setViewDate(
	    		TextView textView, Cursor cursor, int columnIndex, int type) {
			DateFormat df;
			
	    	switch( type) {
	    	case DATE_SHORT :
	    		df = DateFormat.getTimeInstance();
	    		break;	
	    	case DATE_LONG :
	    	default:
	    		df = DateFormat.getDateTimeInstance();
	    		break;
	    	}
	    	String text = df.format(new Date(cursor.getLong(columnIndex)));
			textView.setText( text);
	    }

	    private void setViewText(TextView textView, Cursor cursor, int columnIndex) {
	    	String text = cursor.getString(columnIndex);
	    	textView.setText(text);
	    }
	}
}
