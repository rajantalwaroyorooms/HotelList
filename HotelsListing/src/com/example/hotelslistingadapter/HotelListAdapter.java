package com.example.hotelslistingadapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.hotelslisting.R;
import com.example.model.Hotel;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HotelListAdapter extends BaseAdapter {
	private ArrayList<Hotel> hotelList ;
	private Context _context;
	
	
	public void setHotelList(ArrayList<Hotel> list) {
		hotelList = list;
	}
	private static class ViewHolder{
		TextView tvHotelName, tvHotelAddress,tvHotelCheckins;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
				if(hotelList != null){
				return hotelList.size();
				} else {
					return 0;
				}
	}

	public HotelListAdapter(Context context, List<Hotel> hotelList) {
		//super(context, R.layout.hotel_list_row, hotelList);
		this.hotelList = (ArrayList<Hotel>)hotelList;
		_context = context;
		// TODO Auto-generated constructor stub
	}
	
	
	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//Log.d("navya15","navya15 inside getView item hotelList::;"+ hotelList.get(position));
		View rowView = convertView;
		ViewHolder holder;
		// TODO Auto-generated method stub
		if(convertView == null) {
			LayoutInflater inflator = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflator.inflate(R.layout.hotel_list_row, parent, false);
			holder = new ViewHolder();
			holder.tvHotelName = (TextView) rowView.findViewById(R.id.tv_hotel_name);
			holder.tvHotelAddress = (TextView) rowView.findViewById(R.id.tv_hotel_address);
			holder.tvHotelCheckins = (TextView) rowView.findViewById(R.id.tv_hotel_checkins);
			rowView.setTag(holder);
		} else {
			holder =(ViewHolder)rowView.getTag();
		}
       /* 
		new FourSquareAsyncTask(hotelList.get(position).getLattitude(), 
				                hotelList.get(position).getLongitude(), 
				                hotelList.get(position).getHotelName(),
				                holder.tvHotelCheckins).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, hotelList.get(position));*/
		holder.tvHotelName.setText(hotelList.get(position).getHotelName());
		holder.tvHotelCheckins.setText(hotelList.get(position).getCheckinCounts());
	    holder.tvHotelAddress.setText(hotelList.get(position).getHotelAddress());
		return rowView;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return hotelList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	
	public class FourSquareAsyncTask extends AsyncTask<Hotel, Void, String> {
       private String FOUR_SQUARE_BASE_URL = "https://api.foursquare.com/v2/venues/search?ll=";
       private String CLIENT_ID="BEUDWXV0VQBWQBEFBJGAN53QM5C5INFMR1C5F2JWC3VJU4WX";
       private String CLIENT_SECRET = "10VUMJAFUILBAKD3IQRTNEM1QDHJCCNYX5GSLG2H2P5OW0MS";
       private String TARGET_COMPANY = "foursquare";
       
       private String lat, longitude, name;
       private TextView tvCheckoutCount;
       
       public FourSquareAsyncTask(String lat, String longitude, String name, TextView tvCheckoutCount) {
    	   this.lat = lat;
    	   this.longitude = longitude;
    	   this.name = name;
    	   this.tvCheckoutCount = tvCheckoutCount;
       }
       @Override
    	protected void onPreExecute() {
    		super.onPreExecute();
    	}
       
		@Override
		protected String doInBackground(Hotel... params) {
			StringBuilder sb = new StringBuilder();
			try {
			URL url = new URL(makeFourSquareUrl());
			URLConnection urlConnection = url.openConnection();
			BufferedReader bufferReader = new BufferedReader(
					new InputStreamReader(urlConnection.getInputStream()));
			String line;
			while ((line = bufferReader.readLine()) != null) {
				sb.append(line + "\n");
			}
			bufferReader.close();
			//Log.e("navya51","navya51 foursquare response ::::::"+ sb.toString());
			return sb.toString();
		} catch (Exception e) {
			//Log.e("navya21","navya21 exceptione::::::::"+ e);
           return null;
		}
		}

		
		@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				try {
					Log.e("navya15","navya15 inside on postexcecute result :::::::" + result);
				JSONObject json = new JSONObject(result);
				JSONObject reponseJson= json.getJSONObject("response");
				Log.e("navya21","navya21 responsjson::::" + reponseJson);
			    foursquareJsonParse(reponseJson.getJSONArray("venues"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.e("navya15","navya15 exception e ::::::::" + e);
					//tvCheckoutCount.setText("0");
					e.printStackTrace();
				}
				// parse the json  and set the result in a text view
			}
		
		
		/*https://api.foursquare.com/v2/venues/search?ll=28.4824165,77.0891655&
	     client_id=BEUDWXV0VQBWQBEFBJGAN53QM5C5INFMR1C5F2JWC3VJU4WX
	     &client_secret=10VUMJAFUILBAKD3IQRTNEM1QDHJCCNYX5GSLG2H2P5OW0MS&v=20140806
	     &m=foursquare&query=The%20Leela%20Ambience%20Gurgaon%20Hotel%20&%20Residences&limit=1
		*/
		private String makeFourSquareUrl(){
			StringBuilder sb = new StringBuilder();
			sb.append(FOUR_SQUARE_BASE_URL);
			sb.append(lat+","+ longitude);
			sb.append("&client_id=");
			sb.append(CLIENT_ID);
			sb.append("&client_secret=");
			sb.append(CLIENT_SECRET);
			sb.append("&v=20140806");
			sb.append("&m=foursquare&query=");
			sb.append(name.replaceAll("\\s+","%20"));
			sb.append("&limit=1");
			Log.e("navya51","navya51 foursquare url ::::::"+ sb.toString());
			//return "https://api.foursquare.com/v2/venues/search?ll=28.4824165,77.0891655&client_id=BEUDWXV0VQBWQBEFBJGAN53QM5C5INFMR1C5F2JWC3VJU4WX&client_secret=10VUMJAFUILBAKD3IQRTNEM1QDHJCCNYX5GSLG2H2P5OW0MS&v=20140806&m=foursquare&query=The%20Leela%20Ambience%20Gurgaon%20Hotel%20&%20Residences&limit=1";
			return sb.toString();
		}
		
		
		
		private void foursquareJsonParse(JSONArray jsonArray) throws JSONException{
			JSONObject fjsonObject = jsonArray.getJSONObject(0);
			//Log.e("navya151","navya151 insidepostExecute");
			JSONObject fjosnArray = fjsonObject.getJSONObject("stats");
			tvCheckoutCount.setText( fjosnArray.getString("checkinsCount"));
			Log.e("navya151","navya151 checkins::::" + fjosnArray.getString("checkinsCount"));
			Log.e("navya151","navya151 fjsonObjectname ::::" + fjsonObject.getString("name"));
			
		}
	}

}
