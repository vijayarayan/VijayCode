package ttl.advjava.threads.advanced;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicDemo {
	private volatile int value = 0;
	private int numTimes = 1000000;

	private AtomicInteger ai = new AtomicInteger(0);

	private Object sync = new Object();

	public static void main(String[] args) throws InterruptedException,
			ExecutionException {
		new AtomicDemo();
	}

	public AtomicDemo() throws InterruptedException, ExecutionException {
		ExecutorService es = Executors.newFixedThreadPool(4);

		Worker w1 = new Worker();
		Worker w2 = new Worker();
		Worker w3 = new Worker();

		long start = System.currentTimeMillis();
		Future<?> f1 = es.submit(w1);
		Future<?> f2 = es.submit(w2);
		Future<?> f3 = es.submit(w3);

		f1.get();
		f2.get();
		f3.get();

		long end = System.currentTimeMillis();

		System.out.println("Final value is " + value + " time = "
				+ (end - start) + " ms");
		System.out.println("Final value is " + ai + " time = " + (end - start)
				+ " ms");

		es.shutdownNow();
		try {
			es.awaitTermination(1, TimeUnit.SECONDS);
		} catch (InterruptedException ie) {
		}

	}

	public class Worker implements Runnable {
		/*
		 * public void run() { for (int i = 0; i < numTimes; i++) { // int v =
		 * value++; int v = ai.getAndIncrement(); } }
		 */

		public void run() {
			for (int i = 0; i < numTimes; i++) {
				synchronized (sync) {
					int v = value++;
				}
			}
		}

	}
}
