package fi.sokira.metropolialukkari;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import fi.sokira.metropolialukkari.models.Realization;
import fi.sokira.metropolialukkari.models.StudentGroup;

public class ToteutusListFragment extends ListFragment {

	protected final static String MAP_IMPL_NAME = "name";
	protected final static String MAP_GROUP_ID = "group";
	
	public static final String ARG_RESULT = "result";
	public static final String ARG_RESULT_TYPE = "resType";
	public static final int TYPE_NO_TYPE = 0;
	public static final int TYPE_RESERVATION = 1;
	public static final int TYPE_REALIZATION = 2;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate( R.layout.fragment_impl_list, container, false);
		
		ListAdapter adapter = null;
		
		int resource = R.layout.fragment_impl_list_item;
		int[] to = { R.id.impl_name, R.id.group_id};
		
		Bundle args = getArguments();
		
		switch( args.getInt(ARG_RESULT_TYPE, TYPE_NO_TYPE)) {
		
		case TYPE_REALIZATION:
			ArrayList<Realization> result = 
						args.getParcelableArrayList(ARG_RESULT);
		  	if( result != null) {
				adapter = new RealizationAdapter(
						getActivity(), 
						resource,
						to,
						result);
			}
			break;
			
		case TYPE_RESERVATION:
			//TODO varauksien käsittely
			adapter = getDefaultAdapter( resource, to);
			break;
			
		case TYPE_NO_TYPE:
		default:
			adapter = getDefaultAdapter( resource, to);
			break;
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
	
	private class RealizationAdapter extends ArrayAdapter<Realization> {
		
		private int resource;
		private int[] to;

		public RealizationAdapter(Context context, int resource, int[] to,
				List<Realization> objects) {
			super(context, resource, objects);
			
			this.resource = resource;
			this.to = to;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = 
					(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			View v;
			TextView textV;
			
			if( convertView == null) {
				v = inflater.inflate( resource, parent, false);
			} else {
				v = convertView;
			}
			
			Realization relz = getItem(position);
			
			textV = (TextView) v.findViewById( to[0]);
			textV.setText( relz.getName());
			
			textV = (TextView) v.findViewById( to[1]);
			
			StringBuilder strb = new StringBuilder();
			
			List<StudentGroup> grps = relz.getStudentGroups();
			for( int i = 0; i < grps.size() - 1; i++) {
				strb.append( grps.get(i).getCode());
				strb.append( " ");
			}
			strb.append( grps.get( grps.size() - 1).getCode());
			
			textV.setText( strb.toString());
			
			return v;
		}
	}
}
