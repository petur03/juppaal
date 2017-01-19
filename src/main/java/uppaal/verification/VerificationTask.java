package uppaal.verification;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uppaal.NTA;
import uppaal.UppaalException;

public class VerificationTask {
	private static final Logger logger = LoggerFactory.getLogger(VerificationTask.class);
	
	private NTA nta;
	private String verifyta;
	private String query;
	private long timeout;

	private String stdout;
	private String stderr;
	private List<String> results;

	public VerificationTask(NTA nta, String queryFilename, String verifytaFilename, long timeout) {
		this.nta = nta;
		query = queryFilename;
		verifyta = verifytaFilename;
		this.timeout = timeout;
	}

	public int executeQuery() throws UppaalException, TimeoutException {
		File tmpfile;
		File outtmpfile;
		File errtmpfile;
		try {
			tmpfile = File.createTempFile("juppaal", ".xml");
			outtmpfile = File.createTempFile("juppaal-output", ".txt");
			errtmpfile = File.createTempFile("juppaal-error", ".txt");
//			logger.debug("tmpFile: {}", tmpfile.getAbsolutePath());
//			logger.debug("outtmpfile: {}", outtmpfile.getAbsolutePath());
//			logger.debug("errtmpfile: {}", errtmpfile.getAbsolutePath());

			nta.writeXML(new PrintStream(tmpfile));
		} catch (IOException e) {
			throw new UppaalException("File error when saving tmp Uppaal file!", e);
		}
		
		//int factor = 5; // Quick
		int factor = 2; // Realistic
		
		// Run uppaal
		String [] args = new String[]{verifyta,
				"--learning-method", Integer.toString(3),
				"--good-runs", Integer.toString(50/factor),
				"--total-runs", Integer.toString(100/factor),
				"--runs-pr-state", Integer.toString(25/factor),
				"--eval-runs", Integer.toString(25/factor),
				"--max-iterations", Integer.toString(200/factor),
				"--reset-no-better", Integer.toString(10/factor),
				"--max-reset-learning", Integer.toString(6/factor),
				"--filter", Integer.toString(2),
				"--seed", Long.toString(System.currentTimeMillis()),
				"--epsilon", Double.toString(0.01),
				tmpfile.getAbsolutePath(),
				query
		};
		
		StringBuilder sb = new StringBuilder();
		for (String s : args) {
			sb.append(s).append(" ");
		}
//		logger.debug(sb.toString());
		
		ProcessBuilder pb = new ProcessBuilder(args);
		
		pb.redirectOutput(outtmpfile);
		pb.redirectError(errtmpfile);
		Process p;
		try {
			p = pb.start();
		} catch (IOException e) {
			throw new UppaalException("Error starting verifyta!", e);
		}

		TimeoutThread timeoutThread = new TimeoutThread(timeout, Thread.currentThread());
		timeoutThread.start();
		
		int ret;
		try {
			ret = p.waitFor();
		} catch (InterruptedException e) {
			throw new TimeoutException("Uppaal timeout!");
		}
		timeoutThread.cancel();
		
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(outtmpfile.getAbsolutePath()));
			stdout = new String(encoded, StandardCharsets.UTF_8);
			
			results = new ArrayList<>();
			String[] tmpres = stdout.split("\\033\\[2K");
			if (tmpres.length >= 2) {
				for (int i = 1; i < tmpres.length; i++) {
					String s = tmpres[i].trim();
					if (!s.startsWith("Verifying formula ")) {
						results.add(s);
					}
				}
			}

			encoded = Files.readAllBytes(Paths.get(errtmpfile.getAbsolutePath()));
			stderr = new String(encoded, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new UppaalException("Error reading output from verifyta!", e);
		}
		
		if (ret != 0) {
			throw new UppaalException("Uppaal returned error value: " + ret + ". See getStderr() for details.");
		}
		
		return ret;
	}
	
	public List<String> getResults() {
		return new ArrayList<>(results);
	}

	public String getStdout() {
		return stdout;
	}

	public String getStderr() {
		return stderr;
	}
}
