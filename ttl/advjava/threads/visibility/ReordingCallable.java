package ttl.advjava.threads.visibility;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ReordingCallable {

	private int X; 
	private int Y;
	private int a;
	private int b;

	private static int totalReordered = 0;

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		// measureLoop(1000);

		ReordingCallable ro = new ReordingCallable();
		int numRounds = 100;
		for (int i = 0; i < numRounds; i++) {
			ro.go(i);
		}
		System.out.printf("TotalReordered: %d in %d iterations. All Done%n", totalReordered, numRounds);

	}

	public void go(int round) throws InterruptedException, ExecutionException {
		ExecutorService es = Executors.newFixedThreadPool(2);

		Worker1 w1 = new Worker1();
		Worker2 w2 = new Worker2();

		final int numIterations = 100_000;

		int numReordered = 0;
		for(int i = 0; i < numIterations; i++) {
			X = Y = a = b = 0;
			Future<Integer> f2 = es.submit(w2);
			Future<Integer> f1 = es.submit(w1);

			a = f1.get();
			b = f2.get();
			if (a == 0 && b == 0) {
				numReordered++;
			}
		}
		System.out.printf("%d: Found %d reorders in %d iterations (%.2f%%), %n",
				round, numReordered, numIterations, numReordered * 100. / numIterations);
		
		totalReordered += numReordered;
		
		es.shutdown();
	}

	class Worker1 implements Callable<Integer> {


		public Worker1() {
		}

		public Integer call() {
			X = 1;
			a = Y;

			return a;

		}
	}

	class Worker2 implements Callable<Integer> {

		public Worker2() {
		}

		public Integer call() {
			Y = 1;
			b = X;

			return b;

		}
	}

}
