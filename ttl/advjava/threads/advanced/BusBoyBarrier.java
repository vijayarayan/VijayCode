package ttl.advjava.threads.advanced;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Copyright DevelopIntelligence 2007. All rights reserved.
 * <p/>
 * User: developintelligence llc Date: Mar 9, 2010 Time: 4:30:35 PM
 */
public class BusBoyBarrier extends Thread {

	private static Random randomGenerator = new Random();

	private CyclicBarrier barrier;
	private int id;

	BusBoyBarrier(CyclicBarrier barrier, int id) {
		this.barrier = barrier;
		this.id = id;
	}

	public void run() {
		try {
			while (true) {
				System.out.printf("BusBoy %d cleaning table %n", id);
				int sleepTime = Math.abs(randomGenerator.nextInt(10));
				Thread.sleep(sleepTime * 1000);
				System.out.printf("BusBoy %d at barrier: %d%n", id, 
						barrier.getNumberWaiting());
				barrier.await();
			}

		} catch (InterruptedException ie) {
			System.out.println(ie);
		} catch (BrokenBarrierException e) {
			System.out.println(e);
		}
	}
}