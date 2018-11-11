package ttl.advjava.threads.visibility;

import java.util.concurrent.Phaser;

public class VisibilityLoop {

	private int var;
	private boolean ready;
	private Thread th1, th2;
	private volatile boolean keepGoing = true;

	public static void main(String[] args) {
		// measureLoop(1000);

		VisibilityLoop ro = new VisibilityLoop();
		for (int i = 0; i < 2; i++) {
			ro.go();
		}
		
		
		System.out.println("All Done");

	}

	boolean firstTime = true;
	public void go() {

		final int numIterations = 1_000_000;

		var = 0;
		ready = false;
		keepGoing = true;
		Phaser phaser = new Phaser(2) {
			private int numReordered = 0;

			protected boolean onAdvance(int phase, int registeredParties) {

				if (var == 0) {
					numReordered++;
				}
				var = 0;
				ready = false;
				if (phase >= numIterations) {
					keepGoing = false;

					System.out
							.printf("Found %d reorders in %d iterations (%.2f%%), "
									+ "phase %d%n",
									numReordered, numIterations, numReordered
											* 100. / numIterations, phase);

					return true;
				}
				return false;
			}
		};

		Worker1 w1 = new Worker1(phaser);
		Worker2 w2 = new Worker2(phaser);

		th1 = new Thread(w1, "Worker1");
		th2 = new Thread(w2, "Worker2");

		th1.start();
		th2.start();

		try {
			th1.join();
			th2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}


	class Worker1 implements Runnable {

		private Phaser phaser;

		public Worker1(Phaser phaser) {
			this.phaser = phaser;
		}

		public void run() {
			int i = 0;
			int iter = 0;
			while (keepGoing) {

				while(!ready) {
				}
				
				System.out.println("Worker1 var is " + var + ", iter = " + iter++);

				phaser.arriveAndAwaitAdvance();


			}

		}
	}

	class Worker2 implements Runnable {

		private Phaser phaser;

		public Worker2(Phaser barrier) {
			this.phaser = barrier;
		}

		public void run() {
			int i = 0;
			while (keepGoing) {

				var = 10;
				ready = true;

				phaser.arriveAndAwaitAdvance();
			}

		}
	}

}
