package uppaal.verification;

import java.util.Scanner;

import uppaal.UppaalException;

public class ProbabilityQueryResult {
	private double min;
	private double max;

	public ProbabilityQueryResult(String output) throws UppaalException {
//		System.out.println(output);

		Scanner s = null;
		try {
			String[] out = output.split("\n");

			s = new Scanner(out[1]);
			s.next();
			s.next();
			s.next();
			s.next();
			s.next();
			String inS = s.next();
//			System.out.println("inS:  " + inS);
			String[] inSa = inS.substring(1, inS.length()-1).split(",");
//			System.out.println("inSa: [" + inSa[0] + "," + inSa[1] + "]");
			min = new Double(inSa[0]);
			max = new Double(inSa[1]);


		} catch (Throwable e) {
			throw new UppaalException("Error parsing output!", e);
		} finally {
			if (s!=null) s.close();
		}
		
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}
}
