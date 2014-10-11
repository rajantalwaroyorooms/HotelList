package com.example.hotelslisting;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hotelslistingadapter.HotelListAdapter;
import com.example.model.Hotel;

public class HotelMainActivity extends Activity {
	private static final String API_KEY = "AIzaSyAIUn8Mj7hxSFn197OHKhOjs_PKl7nsXZ4";
	private static final String PLACES_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json?";
	private static final String QUERY_TEXT = "hotelsinGurgaon";
	private static final String PAGE_TOKEN = "pagetoken=";
	private TextView tvShowJson;
	private List<Hotel> hotelList = new ArrayList<Hotel>();
	private HotelListAdapter hotelAdapter;
	private ListView hotelListView;
	private String pageToken;
	private boolean loadMore = false;
    private boolean isLoadingData = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hotel_activity_main);
		hotelListView = (ListView) findViewById(R.id.lv_hotel_list);
		hotelAdapter = new HotelListAdapter(this, new ArrayList<Hotel>());
		hotelListView.setAdapter(hotelAdapter);
		implementPaginationCalls();
	}
	
	// google places return data of size 20 in one call
	private void implementPaginationCalls() {
		hotelListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			
				loadMore = (firstVisibleItem + visibleItemCount) >= totalItemCount;
				if(loadMore && !isLoadingData && totalItemCount != 0) {
					Log.d("navya21","navya21 making the api call");
					isLoadingData = true;
					loadMore = false;
					Log.d("navya21","navya21 pagetokenUrl::::" + makepageTokenUrl());
					new HttpAsyncTask().execute(makepageTokenUrl());
				}
				
			}
		});
	}
	
	public void fetchTheList(View view) {
		new HttpAsyncTask().execute(makeUrl());
	}
	
	/*https://maps.googleapis.com/maps/api/place/textsearch/json?pagetoken=*/	
	private String makepageTokenUrl() {
		StringBuilder pageTokenUrl = new StringBuilder(PLACES_SEARCH_URL);
		pageTokenUrl.append(PAGE_TOKEN);
		pageTokenUrl.append(pageToken);
		pageTokenUrl.append("&key=");
		pageTokenUrl.append(API_KEY);
		return pageTokenUrl.toString();
	}
	
	/*https://maps.googleapis.com/maps/api/place/textsearch/json?query=restaurants+in+Gurgaon&key=AIzaSyAIUn8Mj7hxSFn197OHKhOjs_PKl7nsXZ4
*/	private String makeUrl(){
		StringBuilder urlString = new StringBuilder(PLACES_SEARCH_URL);
		urlString.append("query=");
		urlString.append(QUERY_TEXT);
		urlString.append("&key=");
		urlString.append(API_KEY);
		return urlString.toString();
	}


private String makeCall(String theurl)  {
		try {
			StringBuilder sb = new StringBuilder();
			URL url = new URL(theurl);
			URLConnection urlConnection = url.openConnection();
			BufferedReader bufferReader = new BufferedReader(
					new InputStreamReader(urlConnection.getInputStream()));
			String line;
			while ((line = bufferReader.readLine()) != null) {
				sb.append(line + "\n");
			}
			bufferReader.close();
			return sb.toString();
		} catch (Exception e) {
			Log.e("navya21","navya21 exceptione::::::::"+ e);
           return null;
		}
}


 private Hotel parseJson(JSONObject jsonObject) throws JSONException  {
	 Hotel hotel = new Hotel();
	 String hotelName = (String) jsonObject.get("name");
	 String hotelAddress = (String) jsonObject.get("formatted_address");
	 hotel.setHotelAddress(hotelAddress);
	 hotel.setHotelName(hotelName);
	 return hotel;
 }

class HttpAsyncTask extends AsyncTask<String, Void, String> {
	
	private ProgressDialog dialog;
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		dialog = ProgressDialog.show(HotelMainActivity.this, "Please wait", "Loading....", false, true);
		dialog.show();
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... arg0) {
			return makeCall(arg0[0]);
		}
	
	
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		dialog.dismiss();
		isLoadingData = false;
		super.onPostExecute(result);
		try {
			JSONObject jsonObject = new JSONObject(result);
			pageToken = (String) jsonObject.get("next_page_token");
			Log.d("navya21","navya21 next_page_token::::" + jsonObject.get("next_page_token"));
			 JSONArray jsonArray = jsonObject.getJSONArray("results");
			 hotelAdapter.setHotelList(retrieveHotelList(jsonArray));
			 hotelAdapter.notifyDataSetChanged();
		} catch (JSONException e) {	
			// TODO Auto-generated catch block
			Log.e("JsonException","json  e :::::" + e);
			e.printStackTrace();
		}
		
	}
	
	
	private ArrayList<Hotel> retrieveHotelList(JSONArray jsonArray) throws JSONException{
		for(int i = 0; i < jsonArray.length(); i++) {
			 hotelList.add(parseJson((JSONObject)jsonArray.get(i)));
		}
		Log.d("navya21","navya21 retrievehotelListsize :::::::" + hotelList.size());
		return (ArrayList<Hotel>) hotelList;
	}
	
	
	
}

}
