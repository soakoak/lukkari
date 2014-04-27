package fi.sokira.metropolialukkari;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import fi.sokira.metropolialukkari.models.Realization;
import fi.sokira.metropolialukkari.models.Reservation;
import fi.sokira.metropolialukkari.models.Resource;
import fi.sokira.metropolialukkari.models.ResultItem;
import fi.sokira.metropolialukkari.models.StudentGroup;

public class ToteutusListFragment extends ListFragment {

	protected final static String MAP_IMPL_NAME = "name";
	protected final static String MAP_GROUP_ID = "group";
	
	public static final String ARG_RESULT = "result";
	public static final String ARG_RESULT_TYPE = "resType";
	public static final int TYPE_NO_TYPE = 0;
	public static final int TYPE_RESERVATION = 1;
	public static final int TYPE_REALIZATION = 2;
	
	private static final String TAG = ToteutusListFragment.class.getSimpleName();
	
	private int contentType = 0; 
	private OnResultItemSelectedListener selectListener = null;

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated( savedInstanceState);
		
		final ListView v = getListView();
		v.setChoiceMode( ListView.CHOICE_MODE_MULTIPLE_MODAL);
		
		v.setMultiChoiceModeListener( new ResultMultiChoiceListener());
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try{ 
			selectListener = (OnResultItemSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(
					activity.toString() + 
					" must implement " + selectListener.getClass().getSimpleName());
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate( R.layout.fragment_impl_list, container, false);
		
		ListAdapter adapter = null;
		
		int resource = R.layout.fragment_impl_list_item;
		int[] to = { R.id.impl_name, R.id.group_id};
		
		Bundle args = getArguments();
		
		contentType = args.getInt(ARG_RESULT_TYPE, TYPE_NO_TYPE);
		List<ResultItem> result = 
				args.getParcelableArrayList(ARG_RESULT);
		
		if( result != null) {
			adapter = new ResultAdapter(
					getActivity(), 
					resource,
					to,
					result);
		} else {
			adapter = getDefaultAdapter( resource, to);
		}

		setListAdapter(adapter);
		
		return v;
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		ResultItem item = (ResultItem) getListView().getItemAtPosition(position);
		l.setItemChecked(position, false);
		selectListener.onResultItemSelected(item, contentType);
	}
	
	private List<Map<String,String>> buildData() {
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		list.add(putData("Käyttöjärjestelmät", "TO10"));
		list.add(putData("Pelitekoälyt", "TO11K"));
		list.add(putData("Android ohjelmointi", "TO10"));
		list.add(putData("Ammatillinen englanti", "T13M"));
		return list;
	}
	
	private Map<String, String> putData(String implName, String implGroupId) {
		Map<String, String> currency = new HashMap<String, String>();
		currency.put(MAP_IMPL_NAME, implName);
		currency.put(MAP_GROUP_ID, implGroupId);
		return currency;
	}
	
	private ListAdapter getDefaultAdapter( int resource, int[] to) {
		List<Map<String,String>> entrys = buildData();
		
		String[] from = { MAP_IMPL_NAME, MAP_GROUP_ID };
		
		return new SimpleAdapter(
					getActivity(), 
					entrys, 
					resource, 
					from, 
					to);
	}
	
	private class ResultAdapter extends ArrayAdapter<ResultItem> {
		
		private int resource;
		private int[] to;

		public ResultAdapter(Context context, int resource, int[] to,
				List<ResultItem> objects) {
			super(context, resource, objects);
			
			this.resource = resource;
			this.to = to;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = 
					(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			View v;
			TextView[] textV = new TextView[2];
			
			if( convertView == null) {
				v = inflater.inflate( resource, parent, false);
			} else {
				v = convertView;
			}
			
			StringBuilder strb = new StringBuilder();
			
			switch( contentType) {
			case TYPE_REALIZATION:
				Realization relz = (Realization) getItem(position);
				
				textV[0] = (TextView) v.findViewById( to[0]);
				textV[1] = (TextView) v.findViewById( to[1]);

				textV[0].setText( relz.getName());
				
				List<StudentGroup> grps = relz.getStudentGroups();
				for( int i = 0; i < grps.size() - 1; i++) {
					strb.append( grps.get(i).getCode());
					strb.append( " ");
				}
				strb.append( grps.get( grps.size() - 1).getCode());
				
				textV[1].setText( strb.toString());

				break;
			case TYPE_RESERVATION :
				Reservation reserv = (Reservation) getItem( position);
				
				textV[0] = (TextView) v.findViewById( to[0]);
				textV[1] = (TextView) v.findViewById( to[1]);
				
				for( Resource resource : reserv.getResources()) {
					if(resource.getType().equals( Resource.TYPE_REALIZATION)) {
						textV[0].setText( resource.getName());
					} else if( resource.getType().equals( Resource.TYPE_STUDENT_GROUP)) {
						strb.append( resource.getName());
						strb.append( " ");
					}
				}
				
				textV[1].setText( strb.toString());
				
				break;
			default:
				v = super.getView(position, convertView, parent);
				break;
			}

			return v;
		}
	}
	
	private class ResultMultiChoiceListener implements AbsListView.MultiChoiceModeListener {
		
		private int itemsChecked = 0;

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.result_menu, menu);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch( item.getItemId()) {
			
			case R.id.add :
				Log.d(TAG, itemsChecked + " items added.");
				
				ListView lv = getListView();
				
				SparseBooleanArray booleanArray = lv.getCheckedItemPositions();
				ArrayList<ResultItem> checkedItems = new ArrayList<ResultItem>( itemsChecked);
				
				for(int i = 0; i < lv.getCount(); i++) {
					if( booleanArray.get(i) == true) {
						checkedItems.add( (ResultItem) lv.getItemAtPosition( i));
					}
				}
				
				selectListener.onResultItemsAdded(checkedItems, contentType);
				mode.finish();
				break;
				
			default :
				return false;
			}
			
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			//actionMode = null; ?
			itemsChecked = 0;
		}

		@Override
		public void onItemCheckedStateChanged(ActionMode mode, int position,
				long id, boolean checked) {
			if( checked) {
				itemsChecked++;
			} else {
				itemsChecked--;
			}
			
			mode.setTitle( itemsChecked + " items selected.");
		}
		
	}
	
	public interface OnResultItemSelectedListener {
		
		public void onResultItemSelected( ResultItem item, int itemType);
		
		public void onResultItemsAdded( ArrayList<ResultItem> items, int itemType);
	}
}
