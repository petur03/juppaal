package uppaal.verification;

public class TimeoutThread extends Thread {
	private long timeout;
	private boolean stop = false;
	private Thread mainThread;
	
	public TimeoutThread(long timeout_ms, Thread mainThread) {
		this.timeout = timeout_ms;
		this.mainThread = mainThread;
	}
	
	public void cancel() {
		stop = true;
		this.interrupt();
	}
	
	@Override
	public void run() {
		long start = System.currentTimeMillis();
		long current = System.currentTimeMillis();
		long diff = current - start;
		while (timeout > diff) {
			try {
				Thread.sleep(timeout-diff);
			} catch (InterruptedException e) {
			}
			current = System.currentTimeMillis();
			diff = current - start;
			if (stop)
				return;
		}
		if (stop)
			return;
		mainThread.interrupt();
	}
}
