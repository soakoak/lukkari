package fi.sokira.metropolialukkari;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
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
	private ActionMode actionMode = null;
	
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated( savedInstanceState);
		
		final ListView v = getListView();
		v.setChoiceMode( ListView.CHOICE_MODE_SINGLE);
		v.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				if( actionMode == null) {
					Log.d(TAG, "Action mode set");
					actionMode = getActivity().startActionMode( actionModeCallback);
				}
				
				v.setItemChecked( position, true);
				return true;
			}
			
		});
	};

	
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

	private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
		
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}
		
		@Override
		public void onDestroyActionMode(ActionMode mode) {
			ListView lv = getListView();
			int selectPos = lv.getCheckedItemPosition();
			lv.setItemChecked(selectPos, false);
			actionMode = null;
		}
		
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = getActivity().getMenuInflater();
			inflater.inflate( R.menu.result_menu, menu);
			return true;
		}
		
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			
			switch( item.getItemId()) {
			case R.id.more_details :
				final LayoutInflater infl = getActivity().getLayoutInflater();
				ViewGroup v = null;
				TextView text;
				StringBuilder strb;
				DateFormat df;
				int position = getListView().getCheckedItemPosition();
				ResultItem resultItem = ((ResultAdapter) getListAdapter()).getItem( position);
				
				switch( contentType) {
				case TYPE_REALIZATION :
					df = DateFormat.getDateInstance();
					
					Realization relz = (Realization) resultItem;
					
					v = (ViewGroup) infl.inflate( R.layout.dialog_realization_details, null);
					
					text = (TextView) v.findViewById( R.id.code);
					text.setText( text.getText() + ": " + relz.getCode());
					
					text = (TextView) v.findViewById( R.id.name);
					text.setText( text.getText() + ": " + relz.getName());
				
					text = (TextView) v.findViewById( R.id.student_groups);
					strb = new StringBuilder();
					List<StudentGroup> grps = relz.getStudentGroups();
					for( int i = 0; i < grps.size() - 1; i++) {
						strb.append( grps.get(i).getCode());
						strb.append( ", ");
					}
					strb.append( grps.get( grps.size() - 1).getCode());
					text.setText( text.getText() + ": " + strb.toString());
					
					text = (TextView) v.findViewById( R.id.start_date);
					text.setText( 
							text.getText() + ": " + df.format( relz.getStartDate()));
					
					text = (TextView) v.findViewById( R.id.end_date);
					text.setText( 
							text.getText() + ": " + df.format( relz.getEndDate()));
					
					break;
				case TYPE_RESERVATION :
					df = DateFormat.getDateTimeInstance();
					Reservation reserv = (Reservation) resultItem;
					v = (ViewGroup) infl.inflate( R.layout.dialog_reservation_details, null);
					
					text = (TextView) v.findViewById( R.id.subject);
					text.setText( text.getText() + ": " + reserv.getSubject());
					
					text = (TextView) v.findViewById( R.id.start_date);
					text.setText( 
							text.getText() + ": " + df.format(reserv.getStartDate()));
					
					text = (TextView) v.findViewById( R.id.end_date);
					text.setText( 
							text.getText() + ": " + df.format(reserv.getEndDate()));
					
					ListView lv = (ListView) v.findViewById( R.id.list);

					ArrayAdapter<Resource> adapter = 
							new ArrayAdapter<Resource>(
								getActivity(), 
								R.layout.dialog_resource_details, 
								reserv.getResources()) 
					{
						
						public View getView(int position, View convertView, ViewGroup parent) {
							View subView = infl.inflate( 
									R.layout.dialog_resource_details, null);
							
							Resource resource = getItem( position);
							TextView text;
							
							text = (TextView) subView.findViewById( R.id.type);
							text.setText( text.getText() + ": " + resource.getType());
							
							text = (TextView) subView.findViewById( R.id.code);
							text.setText( text.getText() + ": " + resource.getCode());
							
							text = (TextView) subView.findViewById( R.id.name);
							text.setText( text.getText() + ": " + resource.getName());
							
							return subView;
						};
					};
					
					lv.setAdapter(adapter);
					
					break;
				case TYPE_NO_TYPE :
					text = new TextView( getActivity());
					text.setText("No data");
					v = new FrameLayout( getActivity());
					v.addView(text);
					break;
				}
				
				new AlertDialog.Builder( getActivity())
					.setView( v)
					.setPositiveButton("OK", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Log.d(TAG, "More details -dialog dismissed");
						}
					}).show();
				
				break;
			default:
				return false;
			}
			
			mode.finish();
			return true;
		}
	};
}
