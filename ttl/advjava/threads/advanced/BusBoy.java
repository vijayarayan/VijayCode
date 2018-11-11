package ttl.advjava.threads.advanced;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * Copyright DevelopIntelligence 2007. All rights reserved.
 * <p/>
 * User: developintelligence llc Date: Mar 9, 2010 Time: 4:30:35 PM
 */
public class BusBoy extends Thread {

	private static Random randomGenerator = new Random();

	private CountDownLatch latch;

	BusBoy(CountDownLatch latch) {
		this.latch = latch;
	}

	public void run() {
		try {
			int sleepTime = Math.abs(randomGenerator.nextInt(10));
			Thread.sleep(sleepTime * 1000);
			System.out.println("BusBoy cleaning table " + latch.getCount());
			latch.countDown();

		} catch (InterruptedException ie) {
			System.out.println(ie);
		}
	}
}
