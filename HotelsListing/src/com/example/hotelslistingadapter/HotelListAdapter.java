package com.example.hotelslistingadapter;

import java.util.ArrayList;
import java.util.List;

import com.example.hotelslisting.R;
import com.example.model.Hotel;


import android.content.Context;
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
		TextView tvHotelName, tvHotelAddress;
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
			rowView.setTag(holder);
		} else {
			holder =(ViewHolder)rowView.getTag();
		}

		holder.tvHotelName.setText(hotelList.get(position).getHotelName());
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

}
