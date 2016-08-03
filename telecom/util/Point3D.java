package telecom.util;

public class Point3D {
	private double latitude = 0;
	private double longitude = 0;
	private double height = 0;
	
	public Point3D() {
		setLatitude(0);
		setLongitude(0);
		setHeight(0);
	}
	public Point3D(double latitude, double longitude, double height) {
		setLatitude(latitude);
		setLongitude(longitude);
		setHeight(height);
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
	
	public double getHeight() {
		return height;
	}
	
	public void setHeight(double height) {
		this.height = height;
	}
	
}
