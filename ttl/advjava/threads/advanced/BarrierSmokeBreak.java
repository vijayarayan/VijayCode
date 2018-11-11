package ttl.advjava.threads.advanced;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * Copyright DevelopIntelligence 2007. All rights reserved.
 * <p/>
 * User: developintelligence llc Date: Mar 9, 2010 Time: 4:39:16 PM
 */
public class BarrierSmokeBreak {

	public static void main(String[] args) {
		CyclicBarrier barrier = new CyclicBarrier(1, new Runnable() {
			public void run() {
				System.out.println("All Bus Boys at the barrier, and they are off again");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		for (int i = 0; i < 1; i++) {
			new BusBoyBarrier(barrier, i).start();
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * public static void main(String[] args) {
	 * 
	 * CyclicBarrier barrier = new CyclicBarrier(5, new Runnable() { public void
	 * run() { System.out.println("BusBoy Smoke Break"); } });
	 * 
	 * 
	 * for (int i = 0; i < 5; i++) { new BusBoyBarrier(barrier).start(); }
	 * 
	 * }
	 */
}