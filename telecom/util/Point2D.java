package telecom.util;

public class Point2D {
	private double latitude = 0;
	private double longitude = 0;
	
	public Point2D() {
		setLatitude(0);
		setLongitude(0);
	}
	public Point2D(double latitude, double longitude) {
		setLatitude(latitude);
		setLongitude(longitude);
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	
}
