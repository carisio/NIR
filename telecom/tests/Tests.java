package telecom.tests;

import static java.lang.Math.log10;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static telecom.util.Functions.get2DDistanceKM;
import static telecom.util.Functions.get3DDistanceKM;
import static telecom.util.Functions.getPhiDegree;
import static telecom.util.Functions.toDecimalDegree;

import org.junit.Test;

import telecom.basestation.BaseStation;
import telecom.basestation.DirectivityCat2BS;
import telecom.nir.NIR;
import telecom.propagation.FreeSpace;
import telecom.util.Pair;
import telecom.util.Point2D;
import telecom.util.Point3D;
import telecom.util.SimpleMatrix;

public class Tests {
	@Test
	public void testToDecimalDegree() {
		assertEquals("toDecimalDegree test1", -15.174667, toDecimalDegree(-15, 10, 28.8), 0.00001);
		assertEquals("toDecimalDegree test2", -50.594556, toDecimalDegree(-50, 35, 40.4), 0.00001);
		assertEquals("toDecimalDegree test3", 0.002222, toDecimalDegree(0, 0, 8), 0.00001);
		assertEquals("toDecimalDegree test4", 169.766389, toDecimalDegree(169, 45, 59), 0.00001);
	}
	@Test
	public void test2DDistance() {
		Point3D p1 = new Point3D(toDecimalDegree(-40, 50, 5), toDecimalDegree(-10, 20, 30), 300);
		Point3D p2 = new Point3D(toDecimalDegree(-44, 55, 5), toDecimalDegree(-11, 22, 33), 1.5);
		assertEquals("test2DDistance test1", 461.8, get2DDistanceKM(p1, p2), 0.1); // Tested with http://www.movable-type.co.uk/scripts/latlong.html

		p1 = new Point3D(toDecimalDegree(-40, 50, 5), toDecimalDegree(-10, 20, 30), 300);
		p2 = new Point3D(toDecimalDegree(-40, 50, 5), toDecimalDegree(-10, 20, 33), 1.5);
		assertEquals("test2DDistance test2", 0.07011, get2DDistanceKM(p1, p2), 0.0001); // Tested with http://www.movable-type.co.uk/scripts/latlong.html

		p1 = new Point3D(-18.15760, -47.96688, 1.7); // -18° 9' 27.36" S -47° 58' 0.768 W
		p2 = new Point3D(-18.16456, -47.97139, 18); // -18° 9' 52.416" S -47° 58' 17.004 W
		assertEquals("test2DDistance test3", 0.9088, get2DDistanceKM(p1, p2), 0.0001); // Tested with http://www.movable-type.co.uk/scripts/latlong.html

		p1 = new Point3D(toDecimalDegree(-18, 9, 27.36), toDecimalDegree(-47, 58, 0.768), 1.7);
		p2 = new Point3D(toDecimalDegree(-18, 9, 52.416), toDecimalDegree(-47, 58, 17.004), 18);
		assertEquals("test2DDistance test4", 0.9088, get2DDistanceKM(p1, p2), 0.0001); // Tested with http://www.movable-type.co.uk/scripts/latlong.html
	}
	@Test
	public void test3DDistance() {
		Point3D p1 = new Point3D(toDecimalDegree(-40, 50, 5), toDecimalDegree(-10, 20, 30), 300);
		Point3D p2 = new Point3D(toDecimalDegree(-44, 55, 5), toDecimalDegree(-11, 22, 33), 1.5);
		assertEquals("test3DDistance test1", 461.8001, get3DDistanceKM(p1, p2), 0.1);

		p1 = new Point3D(toDecimalDegree(-40, 50, 5), toDecimalDegree(-10, 20, 30), 300);
		p2 = new Point3D(toDecimalDegree(-40, 50, 5), toDecimalDegree(-10, 20, 33), 1.5);
		assertEquals("test3DDistance test2", 0.306622997, get3DDistanceKM(p1, p2), 0.0001);
	}
	@Test
	public void testGetPhiDegree() {
		Point3D BS = new Point3D(-10, 10, 0);
		Point3D probe = new Point3D(-9, 10, 0);
		assertEquals("testGetPhiDegree pointing to north", 0, getPhiDegree(BS, probe), 0.001);
		
		probe.setLatitude(-11);
		probe.setLongitude(10);
		assertEquals("testGetPhiDegree pointing to south", 180, getPhiDegree(BS, probe), 0.001);

		probe.setLatitude(-10);
		probe.setLongitude(11);
		assertEquals("testGetPhiDegree pointing to east", 90, getPhiDegree(BS, probe), 0.001);
		
		probe.setLatitude(-10);
		probe.setLongitude(-10);
		assertEquals("testGetPhiDegree pointing to west", 270, getPhiDegree(BS, probe), 0.001);
		
		/* Testing accepting a higher because I used google earth to get the results */
		BS = new Point3D(toDecimalDegree(-11,  51,  3.98), toDecimalDegree(-49,  33,  43.63), 0);
		probe = new Point3D(toDecimalDegree(-11,  46,  41.9), toDecimalDegree(-49,  16,  1.92), 0);
		assertEquals("testGetPhiDegree quadrant I", 75.82, getPhiDegree(BS, probe), 0.3);
		
		BS = new Point3D(toDecimalDegree(-11,  51,  3.98), toDecimalDegree(-49,  33,  43.63), 0);
		probe = new Point3D(toDecimalDegree(-11,  52,  51.49), toDecimalDegree(-49,  32,  41.37), 0);
		assertEquals("testGetPhiDegree quadrant II", 150.6, getPhiDegree(BS, probe), 0.3);
		
		BS = new Point3D(toDecimalDegree(-11,  51,  3.98), toDecimalDegree(-49,  33,  43.63), 0);
		probe = new Point3D(toDecimalDegree(-11,  51,  15.14), toDecimalDegree(-49,  34, 26.55), 0);
		assertEquals("testGetPhiDegree quadrant III", 255.2, getPhiDegree(BS, probe), 0.3);
		
		BS = new Point3D(toDecimalDegree(-11,  51,  3.98), toDecimalDegree(-49,  33,  43.63), 0);
		probe = new Point3D(toDecimalDegree(-11,  50,  46.63), toDecimalDegree(-49,  34, 2.97), 0);
		assertEquals("testGetPhiDegree quadrant IV", 312.7, getPhiDegree(BS, probe), 0.3);
	}
	@Test
	public void testFreeSpacePathLoss() {
		FreeSpace fs = new FreeSpace();
		Point3D htx = new Point3D(toDecimalDegree(-15, 45, 30), toDecimalDegree(-47, 51, 02), 30);
		Point3D hrx = new Point3D(toDecimalDegree(-15, 47, 37), toDecimalDegree(-47, 48, 52), 3);

		assertEquals("testFreeSpacePathLoss distance2D htx-hrx", 5.506, get2DDistanceKM(htx, hrx), 0.001);
		assertEquals("testFreeSpacePathLoss distance3D htx-hrx", 5.506, get3DDistanceKM(htx, hrx), 0.001);

		// Move hrx
		double freq = 300;
		int steps = 10;
		double lat_steps = (hrx.getLatitude()-htx.getLatitude())/steps;
		double long_steps = (hrx.getLongitude()-htx.getLongitude())/steps;
		for (int i = 0; i < steps; i++) {
			Point3D probe = new Point3D(hrx.getLatitude()-i*lat_steps, hrx.getLongitude()-i*long_steps, hrx.getHeight());
			double L = fs.getPathLoss(htx, probe, freq);
			double d_3D = 5.506 - i*5.506/10;
			double L_expected = 20*log10(d_3D) + 20*log10(freq) + 32.45;
			assertEquals("testFreeSpacePathLoss getPathLoss", L_expected, L, 0.1);
		}

	}
	@Test
	public void testOneStation() {
		Point2D bsPos = new Point2D(-15.8162942, -47.9173204);
		double[] htx = new double[]{30};
		double[] freq = new double[]{1800};
		double[] tilt_deg = new double[]{3};
		double[] teta_bw_vert_deg = new double[]{8};
		double[] eirp = new double[]{60};
		double[] max_ssl = new double[]{-20};

		
		BaseStation BS1 = new DirectivityCat2BS("Name", bsPos, htx, freq, tilt_deg, teta_bw_vert_deg, eirp, max_ssl);
		FreeSpace fs = new FreeSpace();
		
		NIR nir = new NIR();
		nir.addBaseStation(BS1, fs);
		
		Pair<Double, Double> result = null;
		result = nir.evalEandTERAtProbe(new Point3D(-15.81219225, -47.9130711, 18));
		assertEquals("test E field oneStation. Probe at 18m.", 0.25, result.getFirst(), 0.01);
		assertEquals("test E field oneStation. Probe at 18m.", 0.0018, result.getSecond(), 0.0001);

		result = nir.evalEandTERAtProbe(new Point3D(-15.8172671, -47.9140575, 2));
		assertEquals("test E field oneStation. Probe at 2m.", 0.45, result.getFirst(), 0.01);
		assertEquals("test E field oneStation. Probe at 2m.", 0.0061, result.getSecond(), 0.0001);
	}
	
	@Test
	public void testThreeStation() {
		Point2D bsPos = new Point2D(-15.7951642, -47.9278497);
		double[] htx = new double[]{30};
		double[] freq = new double[]{1800};
		double[] tilt_deg = new double[]{3};
		double[] teta_bw_vert_deg = new double[]{8};
		double[] eirp = new double[]{60};
		double[] max_ssl = new double[]{-20};
		
		BaseStation BS1 = new DirectivityCat2BS("BS1", bsPos, htx, freq, tilt_deg, teta_bw_vert_deg, eirp, max_ssl); 
		BaseStation BS2 = new DirectivityCat2BS("BS2", new Point2D(-15.7967418, -47.9532191), htx, freq, tilt_deg, teta_bw_vert_deg, eirp, max_ssl); 
		BaseStation BS3 = new DirectivityCat2BS("BS3", new Point2D(-15.8242286, -47.9414663), htx, freq, tilt_deg, teta_bw_vert_deg, eirp, max_ssl); 
		FreeSpace fs = new FreeSpace();
		
		NIR nir = new NIR();
		nir.addBaseStation(BS1, fs);
		nir.addBaseStation(BS2, fs);
		nir.addBaseStation(BS3, fs);
		
		Pair<Double, Double> result = null;
		result = nir.evalEandTERAtProbe(new Point3D(-15.802712, -47.9299261, 2));
		assertEquals("test E field oneStation. Probe at 2m.", 0.21, result.getFirst(), 0.01);
		assertEquals("test E field oneStation. Probe at 2m.", 0.0013, result.getSecond(), 0.0001);
	}
	
	@Test
	public void testOneStationWith4Frequencies() {
		Point2D bsPos = new Point2D(-15.8162942, -47.9173204);
		double[] htx = new double[]{30, 40, 50, 60};
		double[] freq = new double[]{1800, 900, 2100, 3000};
		double[] tilt_deg = new double[]{3, 4, 5, 6};
		double[] teta_bw_vert_deg = new double[]{8, 8.5, 9, 10};
		double[] eirp = new double[]{60, 61, 62, 63};
		double[] max_ssl = new double[]{-20, -19, -21, -18};
		
		BaseStation BS1 = new DirectivityCat2BS("BS", bsPos, htx, freq, tilt_deg, teta_bw_vert_deg, eirp, max_ssl);
		FreeSpace fs = new FreeSpace();
		
		NIR nir = new NIR();
		nir.addBaseStation(BS1, fs);
		
		Pair<Double, Double> result = null;
		result = nir.evalEandTERAtProbe(new Point3D(-15.8134122, -47.9159914, 18));
		assertEquals("test E field oneStationW4Freq. Probe at 18m.", 1.18, result.getFirst(), 0.01);
		assertEquals("test E field oneStationW4Freq. Probe at 18m.", 0.0477, result.getSecond(), 0.0001);

		result = nir.evalEandTERAtProbe(new Point3D(-15.8129903, -47.9145752, 2));
		assertEquals("test E field oneStationW4Freq. Probe at 2m.", 0.88, result.getFirst(), 0.01);
		assertEquals("test E field oneStationW4Freq. Probe at 2m.", 0.0263, result.getSecond(), 0.0001);
	}
	
	@Test
	public void testOneStationWith4FrequenciesAndOneWithOneFrequency() {
		Point2D bsPos = new Point2D(-15.8162942, -47.9173204);
		double[] htx = new double[]{30, 40, 50, 60};
		double[] freq = new double[]{1800, 900, 2100, 3000};
		double[] tilt_deg = new double[]{3, 4, 5, 6};
		double[] teta_bw_vert_deg = new double[]{8, 8.5, 9, 10};
		double[] eirp = new double[]{60, 61, 62, 63};
		double[] max_ssl = new double[]{-20, -19, -21, -18};

		
		BaseStation BS1 = new DirectivityCat2BS("BS", bsPos, htx, freq, tilt_deg, teta_bw_vert_deg, eirp, max_ssl);
		BaseStation BS2 = new DirectivityCat2BS("BS", new Point2D(-15.8145742, -47.9132320), new double[]{23.5}, new double[]{876.2}, new double[]{0}, new double[]{9}, new double[]{58.12}, new double[]{-15});
		FreeSpace fs = new FreeSpace();
		
		NIR nir = new NIR();
		nir.addBaseStation(BS1, fs);
		nir.addBaseStation(BS2, fs);
		
		Pair<Double, Double> result = null;
		result = nir.evalEandTERAtProbe(new Point3D(-15.8143542, -47.9151263, 18));
		assertEquals("test E field 2BS. Probe at 18m.", 1.45, result.getFirst(), 0.01);
		assertEquals("test E field 2BS. Probe at 18m.", 0.0832, result.getSecond(), 0.0001);

		result = nir.evalEandTERAtProbe(new Point3D(-15.8142413, -47.9152323, 2));
		assertEquals("test E field 2BS. Probe at 2m.", 1.14, result.getFirst(), 0.01);
		assertEquals("test E field 2BS. Probe at 2m.", 0.0485, result.getSecond(), 0.0001);
	}
	
	@Test
	public void testPaper() {
		Point2D pos_BS1 = new Point2D(-15.828, -47.844);
		double[] htx_BS1 = new double[]{30};
		double[] freq_BS1 = new double[]{1800};
		double[] tilt_deg_BS1 = new double[]{3};
		double[] teta_bw_vert_deg_BS1 = new double[]{8};
		double[] eirp_BS1 = new double[]{60};
		double[] max_ssl_BS1 = new double[]{-20};
		
		Point2D pos_BS2 = new Point2D(-15.828, -47.847);
		double[] htx_BS2 = new double[]{25};
		double[] freq_BS2 = new double[]{1500};
		double[] tilt_deg_BS2 = new double[]{2};
		double[] teta_bw_vert_deg_BS2 = new double[]{7.5};
		double[] eirp_BS2 = new double[]{63};
		double[] max_ssl_BS2 = new double[]{-10};
		
		Point2D pos_BS3 = new Point2D(-15.828, -47.843);
		double[] htx_BS3 = new double[]{20};
		double[] freq_BS3 = new double[]{1600};
		double[] tilt_deg_BS3 = new double[]{3};
		double[] teta_bw_vert_deg_BS3 = new double[]{7};
		double[] eirp_BS3 = new double[]{57};
		double[] max_ssl_BS3 = new double[]{-10};
		
		Point2D pos_BS4 = new Point2D(-15.83, -47.84);
		double[] htx_BS4 = new double[]{20};
		double[] freq_BS4 = new double[]{1600};
		double[] tilt_deg_BS4 = new double[]{3};
		double[] teta_bw_vert_deg_BS4 = new double[]{7};
		double[] eirp_BS4 = new double[]{57};
		double[] max_ssl_BS4 = new double[]{-10};
		
		Point2D pos_BS56 = new Point2D(-15.8275, -47.843);
		double[] htx_BS56 = new double[]{45, 25};
		double[] freq_BS56 = new double[]{2100, 1800};
		double[] tilt_deg_BS56 = new double[]{3, 10};
		double[] teta_bw_vert_deg_BS56 = new double[]{7, 8};
		double[] eirp_BS56 = new double[]{58, 58};
		double[] max_ssl_BS56 = new double[]{-15, -15};
		
		Point2D pos_BS7 = new Point2D(-15.827, -47.844);
		double[] htx_BS7 = new double[]{30};
		double[] freq_BS7 = new double[]{1900};
		double[] tilt_deg_BS7 = new double[]{0};
		double[] teta_bw_vert_deg_BS7 = new double[]{6.5};
		double[] eirp_BS7 = new double[]{62};
		double[] max_ssl_BS7 = new double[]{-10};
		
		Point2D pos_BS8 = new Point2D(-15.82, -47.844);
		double[] htx_BS8 = new double[]{20};
		double[] freq_BS8 = new double[]{1900};
		double[] tilt_deg_BS8 = new double[]{0};
		double[] teta_bw_vert_deg_BS8 = new double[]{6.5};
		double[] eirp_BS8 = new double[]{55};
		double[] max_ssl_BS8 = new double[]{-10};
		
		BaseStation bs1 = new DirectivityCat2BS("BS 1", pos_BS1, htx_BS1, freq_BS1, tilt_deg_BS1, teta_bw_vert_deg_BS1, eirp_BS1, max_ssl_BS1);
		BaseStation bs2 = new DirectivityCat2BS("BS 2", pos_BS2, htx_BS2, freq_BS2, tilt_deg_BS2, teta_bw_vert_deg_BS2, eirp_BS2, max_ssl_BS2);
		BaseStation bs3 = new DirectivityCat2BS("BS 3", pos_BS3, htx_BS3, freq_BS3, tilt_deg_BS3, teta_bw_vert_deg_BS3, eirp_BS3, max_ssl_BS3);
		BaseStation bs4 = new DirectivityCat2BS("BS 4", pos_BS4, htx_BS4, freq_BS4, tilt_deg_BS4, teta_bw_vert_deg_BS4, eirp_BS4, max_ssl_BS4);
		BaseStation bs56 = new DirectivityCat2BS("BS 5/6", pos_BS56, htx_BS56, freq_BS56, tilt_deg_BS56, teta_bw_vert_deg_BS56, eirp_BS56, max_ssl_BS56);
		BaseStation bs7 = new DirectivityCat2BS("BS 7", pos_BS7, htx_BS7, freq_BS7, tilt_deg_BS7, teta_bw_vert_deg_BS7, eirp_BS7, max_ssl_BS7);
		BaseStation bs8 = new DirectivityCat2BS("BS 8", pos_BS8, htx_BS8, freq_BS8, tilt_deg_BS8, teta_bw_vert_deg_BS8, eirp_BS8, max_ssl_BS8);

		FreeSpace fs = new FreeSpace();
		
		NIR nir = new NIR();
		nir.addBaseStation(bs1, fs);
		nir.addBaseStation(bs2, fs);
		nir.addBaseStation(bs3, fs);
		nir.addBaseStation(bs4, fs);
		nir.addBaseStation(bs56, fs);
		nir.addBaseStation(bs7, fs);
		nir.addBaseStation(bs8, fs);

		Pair<Double, Double> result = null;
		result = nir.evalEandTERAtProbe(new Point3D(-15.8278845, -47.8436878, 1.5));
		assertEquals("E_TOTAL", 1.4050, result.getFirst(), 0.0001);
		assertEquals("TER", 0.06078, result.getSecond(), 0.0001);

	}
	
	@Test
	public void testOnLOS() {
		Point2D pos_BS1 = new Point2D(-15.8278845, -47.83434);
		double[] htx_BS1 = new double[]{30};
		double[] freq_BS1 = new double[]{3000};
		double[] tilt_deg_BS1 = new double[]{0};
		double[] teta_bw_vert_deg_BS1 = new double[]{7};
		double[] eirp_BS1 = new double[]{60};
		double[] max_ssl_BS1 = new double[]{-20};
		
		BaseStation bs1 = new DirectivityCat2BS("BS 1", pos_BS1, htx_BS1, freq_BS1, tilt_deg_BS1, teta_bw_vert_deg_BS1, eirp_BS1, max_ssl_BS1);
		
		FreeSpace fs = new FreeSpace();
		
		NIR nir = new NIR();
		nir.addBaseStation(bs1, fs);
		
		Pair<Double, Double> result = null;
		result = nir.evalEandTERAtProbe(new Point3D(-15.8278845, -47.8436878, 30));
		assertEquals("E_TOTAL", 0.17323, result.getFirst(), 0.0001);
		assertEquals("TER", 0.00080, result.getSecond(), 0.0001);

	}
	@Test
	public void testConvertToAndFromString() throws Exception {
		Point2D bsPos = new Point2D(-15.8162942, -47.9173204);
		double[] htx = new double[]{30, 40, 50, 60};
		double[] freq = new double[]{1800, 900, 2100, 3000};
		double[] tilt_deg = new double[]{3, 4, 5, 6};
		double[] teta_bw_vert_deg = new double[]{8, 8.5, 9, 10};
		double[] eirp = new double[]{60, 61, 62, 63};
		double[] max_ssl = new double[]{-20, -19, -21, -18};

		BaseStation bs1 = new DirectivityCat2BS("BS", bsPos, htx, freq, tilt_deg, teta_bw_vert_deg, eirp, max_ssl);
		String bsStr1 = bs1.toString();
		
		bs1.setName("A name with space and other symbos <>&amps");
		String bsStr2 = bs1.toString();

		assertNotEquals("testConverToAndFromString toString() differents after name changed", bsStr1, bsStr2);
		
		BaseStation bs2 = BaseStation.fromString(bsStr1);
		bsStr2 = bs2.toString();
		assertEquals("testConverToAndFromString restore the name using fromString method", bsStr1, bsStr2);
	}
	@Test
	public void testSimpleMatrix() {
		SimpleMatrix matrix = new SimpleMatrix();
		assertEquals("testSimpleMatrix - size = 0, test 1", 0, matrix.getSize().getFirst(), 0);
		assertEquals("testSimpleMatrix - size = 0, test 2", 0, matrix.getSize().getSecond(), 0);

		matrix.initializeMatrix(10, 20);
		assertEquals("testSimpleMatrix - size = 10, test 3", 10, matrix.getSize().getFirst(), 0);
		assertEquals("testSimpleMatrix - size = 20, test 4", 20, matrix.getSize().getSecond(), 0);
		assertEquals("testSimpleMatrix - Default element, test 5", 0, matrix.getElement(0, 0), 0);
		assertEquals("testSimpleMatrix - Default element, test 6", 0, matrix.getElement(8, 5), 0);
		assertEquals("testSimpleMatrix - Default element, test 7", 0, matrix.getElement(9, 19), 0);

		matrix.setElement(8, 5, 281.9);
		assertEquals("testSimpleMatrix - Default element, test 8", 0, matrix.getElement(0, 0), 0);
		assertEquals("testSimpleMatrix - Changed element, test 9", 281.9, matrix.getElement(8, 5), 0);
		assertEquals("testSimpleMatrix - Default element, test 10", 0, matrix.getElement(9, 19), 0);
	}
}