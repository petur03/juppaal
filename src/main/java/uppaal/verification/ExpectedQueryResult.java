package uppaal.verification;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import uppaal.UppaalException;

public class ExpectedQueryResult {
	private double mean;
	private double steps;
	private double val;
	private double min;
	private double max;
	private double variance;
	private double stdDev;
	private List<Integer> buckets;

	public ExpectedQueryResult(String output) throws UppaalException {
		buckets = new ArrayList<>();
		
//		System.out.println(output);
//		output = "-- Formula is satisfied.\n"+
//"(100 runs) E(min) = -1.226\n"+
//"Values in [-2.055,-0.4172] mean=-1.226 steps=0.16381: 4 6 6 16 17 15 17 11 4 4";
		Scanner s = null;
		Scanner s2 = null;
		try {
			String[] out = output.split("\n");

			s = new Scanner(out[1]);
			s.next();
			s.next();
			s.next();
			s.next();
			val = s.nextDouble();

			s2 = new Scanner(out[2]);
			s2.next();
			s2.next();
			String inS = s2.next();
//			System.out.println("Val in: " + inS);
			String[] inSa = inS.substring(1, inS.length()-1).split(",");
			min = new Double(inSa[0]);
			max = new Double(inSa[1]);
//			System.out.println("Val in: [" + min + "," + max + "]");
			
			String tmpS = s2.next();
//			System.out.println(tmpS);
//			System.out.println(tmpS.substring(4, tmpS.length()));
			tmpS = tmpS.substring(5, tmpS.length());
//			System.out.println(tmpS);
			mean = new Double(tmpS);

			tmpS = s2.next();
			tmpS = tmpS.substring(6, tmpS.length()-1);

			steps = new Double(tmpS);
			
			int next;
			int n = 0;
			int total = 0;
			try {
				while (true) {
					next = s2.nextInt();
					double nextI = min + n*steps;
//					System.out.println("nextI: " + nextI);
					variance = variance + Math.pow(((nextI-mean)), 2)*next;
//					System.out.println("variance: " + variance);
					total += next;
					buckets.add(next);
//					System.out.println("Added: " + buckets.get(buckets.size()-1) + " total: " + total);
					n++;
				}
			} catch (NoSuchElementException e) {
//				System.out.println("No more elements");
			}
			variance /= total;
			stdDev = Math.sqrt(variance);
//			System.out.println("var: " + variance + " stdDev: " + stdDev);

		} catch (Throwable e) {
			throw new UppaalException("Error parsing output!", e);
		} finally {
			if (s!=null) s.close();
			if (s2!=null) s2.close();
		}
//		System.out.println("Parsed: mean: " + mean + " val: " + val + " steps: " + steps + " min: " + min + " max: " + max + " buckets: " + buckets.size());
//		System.out.println("Parsed: mean: " + mean + " stdDev: " + stdDev);
	}

	public double getMean() {
		return mean;
	}

	public double getSteps() {
		return steps;
	}

	public double getVal() {
		return val;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public double getVariance() {
		return variance;
	}

	public double getStdDev() {
		return stdDev;
	}

	public List<Integer> getBuckets() {
		return buckets;
	}

}
