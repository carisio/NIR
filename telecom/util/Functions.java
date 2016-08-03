package telecom.util;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.acos;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.log10;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static telecom.util.Constants.DEGREE_TO_RAD;
import static telecom.util.Constants.RAD_TO_DEGREE;

public class Functions {
	public static boolean between(double min, double val, double max) {
		return val >= min && val <= max;
	}
	public static double dB2Watt(double db) {
		return pow(10, db/10);
	}
	public static double[] dB2Watt(double[] db) {
		double watt[] = new double[db.length];
		for (int i = 0; i < db.length; i++)
			watt[i] = pow(10, db[i]/10);
		return watt;
	}
	public static double watt2dB(double watt) {
		return 10*log10(watt);
	}

	public static double[] watt2dB(double[] watt) {
		double db[] = new double[watt.length];
		for (int i = 0; i < watt.length; i++)
			db[i] = 10*log10(watt[i]);
		return db;
	}
	public static double power2electricfield(double potenciaWatt, double freqMHz) {
		double lambda = 3e8/(freqMHz*1e6);
		double aff = (lambda*lambda)/(4*PI);
		return sqrt(377*potenciaWatt/aff);
	}
	public static double deg2rad(double deg) {
		return deg * DEGREE_TO_RAD;
	}
	public static double[] deg2rad(double[] deg) {
		double rad[] = new double[deg.length];
		for (int i = 0; i < deg.length; i++)
			rad[i] = deg[i] * DEGREE_TO_RAD;
		return rad;
	}
	public static double rad2deg(double rad) {
		return rad * RAD_TO_DEGREE;
	}
	public static double[] rad2deg(double[] rad) {
		double deg[] = new double[rad.length];
		for (int i = 0; i < rad.length; i++)
			deg[i] = rad[i] * RAD_TO_DEGREE;
		return deg;
	}
	public static double getICNIRPLimits(double f) {
		if (f < 10) return 83;
		if (f < 400) return 28;
		if (f < 2000) return 1.375 * pow(f, 0.5);
		if (f < 300000) return 61;
		return 61;
	}

	public static double get2DDistanceKM(Point3D p1, Point3D p2) {
		double lat1 = p1.getLatitude();
		double long1 = p1.getLongitude();

		double lat2 = p2.getLatitude();
		double long2 = p2.getLongitude();
		return 6371*acos(cos((90-lat2)*DEGREE_TO_RAD)*cos((90-lat1)*DEGREE_TO_RAD)+sin((90-lat2)*DEGREE_TO_RAD)*sin((90-lat1)*DEGREE_TO_RAD)*cos((long1-long2)*DEGREE_TO_RAD));
	}

	public static double get3DDistanceKM(Point3D p1, Point3D p2) {
		double d_2d = get2DDistanceKM(p1, p2);
		double height = abs(p1.getHeight() - p2.getHeight());
		return sqrt(d_2d*d_2d + pow((height)/1000,2));
	}

	public static double toDecimalDegree(double degree, double minutes, double seconds) {
		int posNeg = (degree < 0) ? -1 : 1;
		return posNeg*(abs(degree) + minutes/60 + seconds/3600);
	}
	
	/**
	 * Returns the elevation angle (theta) between the point bs and the point probe. Theta is the angle
	 * between the horizon and the line of sign between the points bs and probe. If htx is the height 
	 * of the point bs and hrx is the height of the point probe, the elevation angle is given by:
	 * 
	 * 
	 * teta_degree = atan2(htx-hrx, d_2d) * 180 / PI
	 * 
	 * 		^
	 * 		|
	 * 		|
	 * 	htx	x\----------------------> Horizon
	 * 		| \  
	 * 		|  \
	 * 		|   \   -----> Line of sign between bs and probe
	 * 		|    \
	 * 	hrx	|     \x---> Probe
	 * 		0------|------------------> x
	 *           d_2d
	 * 
	 * @return
	 */
	public static double getThetaDegree(Point3D bs, Point3D probe) {
		double htx = bs.getHeight();
		double hrx = probe.getHeight();
		double d_2d = get2DDistanceKM(bs, probe);
		double teta_degree = atan2(htx-hrx, d_2d*1000) * RAD_TO_DEGREE;
		
		return teta_degree;
	}
	public static double getThetaRad(Point3D bs, Point3D probe) {
		double htx = bs.getHeight();
		double hrx = probe.getHeight();
		double d_2d = get2DDistanceKM(bs, probe);
		double teta_degree = atan2(htx-hrx, d_2d*1000);
		
		return teta_degree;
	}
	/**
	 * Returns the azimuth angle between the points bs and probe. The reference (0 degree) is the north 
	 * and the angle increases clockwise until reachs the line connecting the points bs and probe (see figure).
	 * The angle alfa is given by:
	 * 
	 * alfa_degree = atan2(probe's longitude - bs' longitude, probe's latitude - bs' latitude) * 180/PI 
	 * phi_degree = 90 - alfa_degree
	 * 
	 * Note: The arguments of atan2 should be given in length units. So, the difference must be converted to distance
	 * before computing the operations
	 *  
	 * Note2: Because of the translation of alpha to get phi, the result of the operation in quadrant IV can be negative.
	 * The result should be converted to positive [if (phi_degree < 0) then (phi_degree = 360 + phi_degree)] before
	 * return. 
	 * 
	 * 		North (latitude)
	 * 			^
	 * 			|       o
	 * 			|      /
	 * 			|_phi /
	 * 			|  \ /
	 * 			|   /
	 * 			|  /
	 * 			| /_  alfa
	 * 			|/   \
	 * 	--------X-------------------------------> East (longitude)
	 * 			|
	 * 			|
	 * Note: The point bs is the orign, represented with an 'X'. The point probe is represented with an 'o'.
	 */
	public static double getPhiDegree(Point3D bs, Point3D probe) {
		Point3D probeSameLat = new Point3D(bs.getLatitude(), probe.getLongitude(), 0);
		Point3D probeSameLong = new Point3D(probe.getLatitude(), bs.getLongitude(), 0);
		
		double longDistance = get2DDistanceKM(bs, probeSameLat);
		double latDistance = get2DDistanceKM(bs, probeSameLong);
		
		if (probe.getLongitude() < bs.getLongitude())
			longDistance *= -1;
		if (probe.getLatitude() < bs.getLatitude())
			latDistance *= -1;
		
		// atan2(y, x)
		double phi_degree = 90.0 - atan2(latDistance, longDistance) * RAD_TO_DEGREE;
		if (phi_degree < 0)
			phi_degree = 360 + phi_degree;
		
		return phi_degree;
	}
}
