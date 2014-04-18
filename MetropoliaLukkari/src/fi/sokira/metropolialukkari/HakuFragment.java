package fi.sokira.metropolialukkari;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fi.sokira.metropolialukkari.models.Realization;
import fi.sokira.metropolialukkari.models.RealizationResult;

public class HakuFragment extends Fragment implements OnClickListener {
	
	private EditText groupInput = null;
	
	private OnSearchListener listener = null;
	
	private final static String TAG = "HakuFragment";
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			listener = (OnSearchListener) activity;
		} catch ( ClassCastException e) {
			throw new ClassCastException(
				activity.toString() + 
				" must implement " + listener.getClass().getSimpleName());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_haku, container, false);
		
		groupInput = (EditText) v.findViewById( R.id.input_group);
		
		Button searchBtn = (Button) v.findViewById( R.id.search_button);
		searchBtn.setOnClickListener( this);
		
		return v;
	}
	
	@Override
	public void onClick(View v) {
		switch( v.getId()) {
		case R.id.search_button :
			Toast.makeText(getActivity(), "Haetaan data from internets", Toast.LENGTH_SHORT).show();
			
			ConnectivityManager connMgr = 
				(ConnectivityManager) getActivity().getSystemService( Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
			
			if( netInfo != null && netInfo.isConnected()) {
//				new LukkariWebLoadTask().execute( "https://opendata.metropolia.fi/r1/reservation/search");
				Bundle args = new Bundle(16);
				args.putStringArray( 
						LukkariWebLoadTask.VALUE_STUDENT_GROUPS, 
						new String[]{ 
							groupInput.getText().toString()
						});
				
				Log.d(TAG, groupInput.getText().toString());
			
				new LukkariWebLoadTask().execute( args);
				Toast.makeText(getActivity(), "We have data.", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getActivity(), "Aaaaaand it's gone.", Toast.LENGTH_LONG).show();
			}
			
			listener.onSearchInitiated();
			groupInput.setText( "");
			break;
		default :
			break;
		}
	}
	
	public interface OnSearchListener {
		
		public void onSearchInitiated();
	}
	
	private class LukkariWebLoadTask extends AsyncTask<Bundle, Void, String> {
		
		public static final String VALUE_NAME = "name";
		public static final String VALUE_START_DATE = "startDate";
		public static final String VALUE_END_DATE = "endDate";
		public static final String VALUE_STUDENT_GROUPS = "studentGroups";

		private static final String TAG = "LukkariWebLoadTask";
		
		private static final String reservationServiceUrl = "https://opendata.metropolia.fi/r1/reservation";
		private static final String realizationServiceUrl = "https://opendata.metropolia.fi/r1/realization";
		
		public LukkariWebLoadTask() {
		}
		
		@Override
		protected String doInBackground(Bundle... params) {

			String apikey = Secrets.METROPOLIA_API_KEY;
			String contentType = "application/json";
			String charset = HTTP.UTF_8;
			
			DefaultHttpClient client = new DefaultHttpClient();
			
			HttpParams clientParams = client.getParams();
			HttpConnectionParams.setConnectionTimeout( clientParams, 30000);
			HttpConnectionParams.setSoTimeout(clientParams, 30000);			
			
			HttpPost postMethod = new HttpPost( realizationServiceUrl + "/search?apiKey=" + apikey);
			
			Bundle values = params[0];
			JSONObject query = new JSONObject();
			
			try {
				if( values.containsKey( VALUE_NAME)) {
					query.put( VALUE_NAME, values.getString( VALUE_NAME));
				}
				
				if( values.containsKey( VALUE_START_DATE)) {
					query.put(VALUE_START_DATE, values.getString( VALUE_START_DATE));
				}
				
				if( values.containsKey( VALUE_END_DATE)) {
					query.put( VALUE_END_DATE, values.getString( VALUE_END_DATE));
				}
				
				if( values.containsKey( VALUE_STUDENT_GROUPS)) {
					Log.d(TAG, "We have student groups");
					query.put( VALUE_STUDENT_GROUPS, asJsonArray( values.getStringArray( VALUE_STUDENT_GROUPS)));
				}
				
				Log.d(TAG, "Written JSON: " + query.toString());
			} catch (JSONException e) {
				Log.e(TAG, "Error while writing JSON");
				e.printStackTrace();
			}
			
			try {
				StringEntity entity = new StringEntity( query.toString());
				entity.setContentType(contentType);
				entity.setContentEncoding( charset);
				postMethod.setEntity( entity);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			HttpResponse response = null;
			try {
				response = client.execute( postMethod);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
			
			String resultStr = "noResult";
			
			if( response != null) {
				try {
					resultStr = EntityUtils.toString( response.getEntity(), charset);
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
			
			return resultStr;
		}
		
		@Override
		protected void onPostExecute(String result) {
			Gson gson = new GsonBuilder()
				.setDateFormat("yyyy-MM-dd'T'HH:mm")
				.disableHtmlEscaping()
				.create();

			Log.d(TAG, "Result string length: " + result.length());
			RealizationResult realzResult = gson.fromJson(result, RealizationResult.class);
			
			Log.d(TAG, "Number of results: " + realzResult.getRealizations().size());
			for( Realization realz : realzResult.getRealizations())
			{
			Log.d(TAG, "Realization name: " + realz.getName());
			}
		}
		
		JSONArray asJsonArray( String[] strings) {
			JSONArray vals = new JSONArray();
			
			for (String str : strings) {
				vals.put( str);
			}
			
			return vals;
		}
	}
}
