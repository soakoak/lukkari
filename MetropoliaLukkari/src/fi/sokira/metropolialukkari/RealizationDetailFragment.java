package fi.sokira.metropolialukkari;

import java.text.DateFormat;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import fi.sokira.metropolialukkari.models.MpoliaRealization;
import fi.sokira.metropolialukkari.models.MpoliaStudentGroup;

public class RealizationDetailFragment extends Fragment {
	
	public static final String ARG_REALIZATION = "realization";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate( 
				R.layout.dialog_realization_details, container, false);
		
		Bundle args = getArguments();
		
		MpoliaRealization realization = (MpoliaRealization) args.getParcelable(ARG_REALIZATION);
		
		if( realization != null) {
			TextView text;
			StringBuilder strb = new StringBuilder();
			DateFormat df = DateFormat.getDateInstance();
			
			text = (TextView) v.findViewById( R.id.code);
			text.setText( text.getText() + ": " + realization.getCode());
			
			text = (TextView) v.findViewById( R.id.name);
			text.setText( text.getText() + ": " + realization.getName());
		
			text = (TextView) v.findViewById( R.id.student_groups);
			strb = new StringBuilder();
			List<MpoliaStudentGroup> grps = realization.getStudentGroups();
			for( int i = 0; i < grps.size() - 1; i++) {
				strb.append( grps.get(i).getCode());
				strb.append( ", ");
			}
			strb.append( grps.get( grps.size() - 1).getCode());
			text.setText( text.getText() + ": " + strb.toString());
			
			text = (TextView) v.findViewById( R.id.start_date);
			text.setText( 
					text.getText() + ": " + df.format( realization.getStartDate()));
			
			text = (TextView) v.findViewById( R.id.end_date);
			text.setText( 
					text.getText() + ": " + df.format( realization.getEndDate()));
		}
		
		return v;
	}
}
