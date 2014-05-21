package fi.sokira.metropolialukkari;

import java.text.DateFormat;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import fi.sokira.metropolialukkari.models.Reservation;
import fi.sokira.metropolialukkari.models.Resource;

public class ReservationDetailFragment extends Fragment {
	
	public static final String ARG_RESERVATION = "reservationation";

	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate( 
				R.layout.dialog_reservation_details, container, false);
		
		Bundle args = getArguments();
		
		Reservation reservation = (Reservation)args.getParcelable(ARG_RESERVATION);
		
		if( reservation != null) {
			TextView text;
			DateFormat df = DateFormat.getDateTimeInstance();
			
			text = (TextView) v.findViewById( R.id.subject);
			text.setText( text.getText() + ": " + reservation.getSubject());
			
			text = (TextView) v.findViewById( R.id.start_date);
			text.setText( 
					text.getText() + ": " + df.format(reservation.getStartDate()));
			
			text = (TextView) v.findViewById( R.id.end_date);
			text.setText( 
					text.getText() + ": " + df.format(reservation.getEndDate()));
			
			ListView lv = (ListView) v.findViewById( R.id.list);

			ArrayAdapter<Resource> adapter = 
					new ArrayAdapter<Resource>(
						getActivity(), 
						R.layout.dialog_resource_details, 
						reservation.getResources()) 
			{
				
				public View getView(int position, View convertView, ViewGroup parent) {
					View subView = inflater.inflate( 
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
		}
		
		return v;
	}
}
