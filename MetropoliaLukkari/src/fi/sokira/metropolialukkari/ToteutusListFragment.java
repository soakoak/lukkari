package fi.sokira.metropolialukkari;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

public class ToteutusListFragment extends ListFragment {

	protected final static String MAP_IMPL_NAME = "name";
	protected final static String MAP_GROUP_ID = "group";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate( R.layout.fragment_impl_list, container, false);
		
		List<Map<String,String>> entrys = buildData();
		
		String[] from = { MAP_IMPL_NAME, MAP_GROUP_ID };
		int[] to = { R.id.impl_name, R.id.group_id};
		
		ListAdapter adapter = new SimpleAdapter(
				getActivity(), 
				entrys, 
				R.layout.fragment_impl_list_item, 
				from, 
				to);
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
}
