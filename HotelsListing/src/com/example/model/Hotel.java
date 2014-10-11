package com.example.model;

public class Hotel {
	private String hotelName;
	private String hotelAddress;

	public String getHotelName() {
		return hotelName;
	}

	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}

	public String getHotelAddress() {
		return hotelAddress;
	}

	public void setHotelAddress(String hotelAddress) {
		this.hotelAddress = hotelAddress;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "hotelName :::::::" + hotelName + "hotelAddress::::::::" + hotelAddress;
	}
}
