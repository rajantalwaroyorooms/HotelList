package com.example.model;

public class Hotel  implements Comparable<Hotel>{
	private String hotelName;
	private String hotelAddress;
	private String lattitude;
	private String longitude;
	private String checkinCounts;

	public String getCheckinCounts() {
		return checkinCounts;
	}

	public void setCheckinCounts(String checkinCounts) {
		this.checkinCounts = checkinCounts;
	}

	public String getLattitude() {
		return lattitude;
	}

	public void setLattitude(String lattitude) {
		this.lattitude = lattitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

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
		return "hotelName :::::::" + hotelName + "hotelAddress::::::::" + hotelAddress
				+ "longitude::::::" + longitude + "lattitude:::::::" + lattitude;
	}

	@Override
	public int compareTo(Hotel another) {
		// TODO Auto-generated method stub
		
		return (Integer.parseInt(another.checkinCounts)) - 
				(Integer.parseInt(this.checkinCounts));
	}
}
