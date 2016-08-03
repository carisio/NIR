package telecom.nir;

/**
 * Based on ITU-R K.100
 */
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static telecom.util.Functions.dB2Watt;
import static telecom.util.Functions.getICNIRPLimits;
import static telecom.util.Functions.power2electricfield;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Vector;

import telecom.basestation.BaseStation;
import telecom.propagation.PropagationModel;
import telecom.util.Pair;
import telecom.util.Point2D;
import telecom.util.Point3D;
import telecom.util.SimpleMatrix;

public class NIR {
	private Vector<Pair<BaseStation, PropagationModel>> baseStations;
	
	public NIR() {
		baseStations = new Vector<Pair<BaseStation, PropagationModel>>();
	}
	public void clearBaseStations() {
		baseStations.clear();
	}
	public void addBaseStation(BaseStation bs, PropagationModel pm) {
		baseStations.add(new Pair<BaseStation, PropagationModel>(bs, pm));
	}
	
	public Pair<Double, Double> evalEandTERAtProbe(Point3D probe) {
		double E_field_total = 0;
		double TER = 0;
		
		for (Pair<BaseStation, PropagationModel> pairBsPm : baseStations) {
			BaseStation bs = pairBsPm.getFirst();
			PropagationModel pm = pairBsPm.getSecond();

			int NRadioSources = bs.getNRadioSources();
			double[] freq_mhz = bs.getFrequencyMHz();
			double[] eirpToProbe_dBm = bs.getEIRPdBm(probe);
			for (int i = 0; i < NRadioSources; i++) {
				Point3D txPos = new Point3D(bs.getLatitude(), 
						bs.getLongitude(), bs.getHeight()[i]);
				double rxIsotropicPower = eirpToProbe_dBm[i] - 30 
						- pm.getPathLoss(txPos, probe, freq_mhz[i]);

				double E_field_bs_i = power2electricfield(dB2Watt(rxIsotropicPower), freq_mhz[i]);
				double ER_bs_i = pow((E_field_bs_i/getICNIRPLimits(freq_mhz[i])), 2);

				E_field_total += pow(E_field_bs_i, 2);
				TER += ER_bs_i;
			}
		}
		E_field_total = pow(E_field_total, 0.5);
		TER *= 100;

		return new Pair<Double, Double>(E_field_total, TER);
	}
	
	public Pair<SimpleMatrix, SimpleMatrix> evalEandTERAtPlane(Point2D coord1, Point2D coord2, double height, int nStepsLongitude) {
		double latitudeMin = min(coord1.getLatitude(), coord2.getLatitude());
		double longitudeMin = min(coord1.getLongitude(), coord2.getLongitude());
		double latitudeMax = max(coord1.getLatitude(), coord2.getLatitude());
		double longitudeMax = max(coord1.getLongitude(), coord2.getLongitude());

		int nStepsLatitude = (int) (((latitudeMax-latitudeMin)/(longitudeMax-longitudeMin))*nStepsLongitude);
		if (nStepsLatitude < 1)
			nStepsLatitude = 1;
		double stepLong = (longitudeMax-longitudeMin)/nStepsLongitude;
		double stepLat = (latitudeMax-latitudeMin)/nStepsLatitude;

		SimpleMatrix resultE = new SimpleMatrix(nStepsLatitude, nStepsLongitude);
		SimpleMatrix resultTER = new SimpleMatrix(nStepsLatitude, nStepsLongitude);

		Point3D probe = new Point3D(0, 0, height);
		int i = 0;
		for (double latProbe = latitudeMax - 0.5*stepLat; i < nStepsLatitude; latProbe -= stepLat, i++) {
			int j = 0;
			for (double longProbe = longitudeMin + 0.5*stepLong; j < nStepsLongitude; longProbe += stepLong, j++) {
					probe.setLatitude(latProbe);
					probe.setLongitude(longProbe);

					Pair<Double, Double> EandTERAtProbe = evalEandTERAtProbe(probe);
					resultE.setElement(i, j, EandTERAtProbe.getFirst());
					resultTER.setElement(i, j, EandTERAtProbe.getSecond());
			}
		}
		return new Pair<SimpleMatrix, SimpleMatrix>(resultE, resultTER);
	}
	
	public void saveEandTerAtFile(Point2D coord1, Point2D coord2, double height, int nStepsLongitude, String eFileName, String TERFileName) throws FileNotFoundException {
		PrintWriter eFile = new PrintWriter(eFileName);
		PrintWriter terFile = new PrintWriter(TERFileName);

		Pair<SimpleMatrix, SimpleMatrix> E_TER = evalEandTERAtPlane(coord1, coord2, height, nStepsLongitude);
		SimpleMatrix E = E_TER.getFirst();
		SimpleMatrix TER = E_TER.getSecond();
		int N_lat = E.getSize().getFirst();
		int N_long = E.getSize().getSecond();

		for (int i = 0; i < N_lat; i++) {
			for (int j = 0; j < N_long; j++) {
				eFile.print(E.getElement(i, j) + "\t");
				terFile.print(TER.getElement(i, j) + "\t");
			}
			eFile.print("\n");
			terFile.print("\n");
		}
		eFile.close();
		terFile.close();
	}
}
