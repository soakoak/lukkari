package fi.sokira.metropolialukkari;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
				new LukkariWebLoadTask().execute( "https://opendata.metropolia.fi/r1/reservation/search");
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
	
	private class LukkariWebLoadTask extends AsyncTask<String, Void, String> {

		private static final String TAG = "LukkariWebLoadTask";
		
		@Override
		protected String doInBackground(String... urls) {
			
			StringBuilder strb = new StringBuilder();
			
			URL url = null;
			HttpsURLConnection conn = null;
			int statusCode = 0;
			
			try {
				url = new URL(urls[0]);
			} catch ( MalformedURLException me) {
			     Log.e(TAG, "URL could not be parsed. URL : " + urls[0] + ".");
			     me.printStackTrace();
			}
			
			try {
				conn = (HttpsURLConnection) url.openConnection();
				conn.setReadTimeout( 10000);
				conn.setConnectTimeout( 15000);
				conn.setRequestMethod( "POST");
				
				String base64auth = Secrets.METROPOLIA_API_KEY;
				conn.setRequestProperty("Authorization", "Basic " + base64auth);
				conn.setRequestProperty("Content-Type", "application/json");
				conn.setRequestProperty("Accept", "application/json");

				conn.setDoInput( true);
				conn.setDoOutput( true);
				
				JSONObject query = null;
				
				try {
					query = new JSONObject();
					
					query.put("startDate", "2013-11-22T09:00");
					
					JSONArray room = new JSONArray();
					room.put("U203");
					
					query.put("room", room);
					
					Log.d(TAG, "Written JSON: " + query.toString());
				} catch (JSONException e) {
					Log.e(TAG, "Error while writing JSON");
					e.printStackTrace();
				}
				
				OutputStreamWriter wr = new OutputStreamWriter( conn.getOutputStream());
				wr.write( query.toString());
				wr.flush();
				
				try {
					statusCode = conn.getResponseCode();
				} catch (IOException e) {
					statusCode = conn.getResponseCode();
					Log.d( TAG, "The response is: " + statusCode);
				}
				
//				conn.connect();
				
				InputStream in = new BufferedInputStream( conn.getInputStream());
				
				try {
					statusCode = conn.getResponseCode();
				} catch (IOException e) {
					statusCode = conn.getResponseCode();
					Log.d( TAG, "The response is: " + statusCode);
				}
				
				Reader reader = null;
				reader = new InputStreamReader( in, "UTF-8");
				
				int len = 512;
				char[] buffer = new char[len];
				int numChars;
				numChars = reader.read( buffer);
				
				while( numChars != -1) {
					strb.append( buffer, 0, numChars);
					numChars = reader.read( buffer);
					Log.d( TAG, "Builder length: " + strb.length());
				}
				
				Log.d(TAG, "Reading ended.");
			
			} catch( IOException e) {
				Log.e(TAG, "Unable to retrieve the web page. URL may be invalid.");
				e.printStackTrace();
			} finally {
				conn.disconnect();
			}
			
			return strb.toString();
		}
		
		@Override
		protected void onPostExecute(String result) {
			Log.d(TAG, "Result: " + result);
		}
	}
}
