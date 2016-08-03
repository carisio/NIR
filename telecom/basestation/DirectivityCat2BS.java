package telecom.basestation;

import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static telecom.util.Functions.between;
import static telecom.util.Functions.deg2rad;
import static telecom.util.Functions.watt2dB;

import java.util.ArrayList;

import telecom.util.Functions;
import telecom.util.Parser;
import telecom.util.Point2D;
import telecom.util.Point3D;

/**
 * This class represents a Base Station with a "Directivity category 2" antenna type, as
 * described in section IV.2.2 of Rec. ITU-T K.52 [1]. The main lobe is modeled as described
 * in section IV.2.2. According to [1], the side lobe can be described only by a constant
 * envelope or by a dipole factor. The side lobe was modulated by a short dipole
 * (cosine square) - see equation (5a) of [2].
 * 
 * Note: Antenna is considered omnidirectional in the horizontal plane.
 * 
 * [1] Recommendation ITU-T K.52 - Guidance on complying with limits for human
 * exposure to electromagnetic fields
 * [2] Estimating the location of maximum exposure to electromagnetic fields 
 * associated with a radiocommunication station
 * D.O.I http://dx.doi.org/10.1590/S2179-10742013000100012 
 * 
 * @author Leandro
 *
 */
public class DirectivityCat2BS extends BaseStation {
	private double[] tilt_degree;
	private double[] theta_bw_vertical_degree;
	private double[] theta_bw_vertical_rad;
	private double[] eirp_max_dbm;
	private double[] envelope_db;

	public DirectivityCat2BS() {
		
	}
	public DirectivityCat2BS(String name, Point2D position, 
			double[] height, double[] frequency_mhz, 
			double[] tilt_degree, double[] theta_bw_vertical_degree, 
			double[] eirp_max_dbm, double[] max_envelope_side_lobe_db) {
		super(name, position, height, frequency_mhz);
		setEirpMaxdBm(eirp_max_dbm);
		setTiltDegree(tilt_degree);
		setMaxSideLobeEnvelopedB(max_envelope_side_lobe_db);
		setThetaBwVerticalDegree(theta_bw_vertical_degree);
	}
	public DirectivityCat2BS(Point2D position, 
			double[] height, double[] frequency_mhz, 
			double[] tilt_degree, double[] theta_bw_vertical_degree, 
			double[] eirp_max_dbm, double[] max_envelope_side_lobe_db) {
		this("", position, height, frequency_mhz, tilt_degree, theta_bw_vertical_degree, eirp_max_dbm, max_envelope_side_lobe_db);
	}
	public double[] getEIRPdBm(Point3D probe) {
		int NRadioSources = getNRadioSources();
		double eirp[] = new double[NRadioSources];
		for (int i = 0; i < NRadioSources; i++) {
			Point3D radioSource = new Point3D(
					getLatitude(), 
					getLongitude(), 
					getHeight()[i]);
			double theta_vertical_rad = 
					Functions.getThetaRad(radioSource, probe);
			double F = getFdB(theta_vertical_rad, 
					deg2rad(tilt_degree[i]), 
					theta_bw_vertical_rad[i], 
					envelope_db[i]);
			eirp[i] = eirp_max_dbm[i] + F;
		}
		return eirp;
	}
	
	private double getFdB(double theta_rad, double tilt_rad, 
			double theta_bw_vertical_rad, double envelope_db) {
		double firstNull = 2.257*theta_bw_vertical_rad/2;
		double firstNullMin = tilt_rad - firstNull;
		double firstNullMax = tilt_rad + firstNull;
		boolean mainBeam = between(firstNullMin, 
				theta_rad, firstNullMax);

		if (between(tilt_rad - 0.00175, 
				theta_rad, tilt_rad + 0.00175)) {
			return 0;
		}

		if (mainBeam) {
			double c = 1.392/sin(theta_bw_vertical_rad/2);
			double aux = c*sin(theta_rad-tilt_rad);
			double FLinear = pow(sin(aux)/aux, 2);

			double FdB = watt2dB(FLinear);
			if (FdB < envelope_db)
				FdB = envelope_db;
			return FdB;
		} else {
			return envelope_db;
		}
	}
	
	@Override
	public String doToString(String separator) {
		String result = Parser.codeDoubleArray(getTiltDegree()) + separator +
				Parser.codeDoubleArray(getThetaBwVerticalDegree()) + separator + 
				Parser.codeDoubleArray(getEirpMaxdBm()) + separator +
				Parser.codeDoubleArray(getMaxSideLobeEnvelopedB()) + separator;
		return result;
	}
	@Override
	public void doFromString(ArrayList<String> strings) {
		setTiltDegree(Parser.uncodeDoubleArray(strings.remove(0)));
		setThetaBwVerticalDegree(Parser.uncodeDoubleArray(strings.remove(0)));
		setEirpMaxdBm(Parser.uncodeDoubleArray(strings.remove(0)));
		setMaxSideLobeEnvelopedB(Parser.uncodeDoubleArray(strings.remove(0)));
	}
	
	public void setMaxSideLobeEnvelopedB(double[] envelope) {
		envelope_db = envelope;
	}
	public double[] getMaxSideLobeEnvelopedB() {
		if (envelope_db == null)
			envelope_db = new double[0];
		return envelope_db;
	}
	public double[] getEirpMaxdBm() {
		if (eirp_max_dbm == null)
			eirp_max_dbm = new double[0];
		return eirp_max_dbm;
	}
	public void setEirpMaxdBm(double[] eirpMaxdbm) {
		eirp_max_dbm = new double[eirpMaxdbm.length];
		for (int i = 0; i < eirp_max_dbm.length; i++)
			eirp_max_dbm[i] = eirpMaxdbm[i];
	}
	public void setThetaBwVerticalDegree(double[] thetaDeg) {
		theta_bw_vertical_degree = thetaDeg;
		theta_bw_vertical_rad = deg2rad(theta_bw_vertical_degree);
	}
	public double[] getThetaBwVerticalDegree() {
		if (theta_bw_vertical_degree == null)
			theta_bw_vertical_degree = new double[0];

		return theta_bw_vertical_degree;
	}
	public double[] getTiltDegree() {
		if (tilt_degree == null)
			tilt_degree = new double[0];

		return tilt_degree;
	}
	public void setTiltDegree(double[] tiltDegree) {
		tilt_degree = tiltDegree;
	}
}
