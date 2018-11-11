package ttl.advjava.threads.advanced;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Phaser;

/**
 * Copyright DevelopIntelligence 2007. All rights reserved.
 * <p/>
 * User: developintelligence llc Date: Mar 9, 2010 Time: 4:39:16 PM
 */
public class PhaserSmokeBreak {

	public static void main(String[] args) {
        Phaser phaser = new Phaser(5) {
            protected boolean onAdvance(int phase, int registeredParties) {
				System.out.println("All Bus Boys at the barrier, and they are off again");
            	System.out.println(">>Phaser.onAdvance, phase = " + phase + 
            			", registered = " + registeredParties);
            	boolean done = phase > 2 || registeredParties == 0;
            	if(done) {
            		System.out.println("Going bye bye at phase " + phase + " with registeredParties " + registeredParties);
            	}
            	
            	return done;
            }
          };

		for (int i = 0; i < 5; i++) {
			new BusBoyPhaser(phaser, i).start();
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