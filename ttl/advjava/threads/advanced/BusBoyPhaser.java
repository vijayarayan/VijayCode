package ttl.advjava.threads.advanced;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Phaser;

/**
 * Copyright DevelopIntelligence 2007. All rights reserved.
 * <p/>
 * User: developintelligence llc Date: Mar 9, 2010 Time: 4:30:35 PM
 */
public class BusBoyPhaser extends Thread {

	private static Random randomGenerator = new Random();

	private Phaser phaser;
	private int id;

	BusBoyPhaser(Phaser barrier, int id) {
		this.phaser = barrier;
		this.id = id;
	}

	public void run() {
		try {
			do {
				System.out.printf("BusBoy %d cleaning table %n", id);
				int sleepTime = Math.abs(randomGenerator.nextInt(2000));
				Thread.sleep(sleepTime);
				System.out.printf("BusBoy %d at barrier: %d waiting%n", id, 
						phaser.getArrivedParties());
				
				phaser.arriveAndAwaitAdvance();
				
			}while(!phaser.isTerminated());

		} catch (InterruptedException ie) {
			System.out.println(ie);
		}
	}
}