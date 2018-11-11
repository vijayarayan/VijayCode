package ttl.advjava.threads.visibility;

/**
 * For printing out Assembly: -XX:+UnlockDiagnosticVMOptions -XX:+PrintAssembly
 * Also have to get the appropriate hsdis lib for your system
 * e.g linux-hsdis-amd64.so.  Do a lookup up for set up.
 * @author whynot
 *
 */
public class VanishingLoop {

	public static void main(String[] args) {
		
		for(int i = 0; i < 10; i++) {
			long start = System.currentTimeMillis();
			//run();
			runVanishing();
			long end = System.currentTimeMillis();
			System.out.printf("Iter %d took %d ms%n", i, (end - start));
		}
	}
	
	public static double run() {
		double sum = 0;
		for(int i = 0; i < 1000000; i++) {
			double d = i;
			double x = d * d / i * i;
			sum += x;
		}
		return sum;
	}

	public static void runVanishing() {
		double sum = 0;
		for(int i = 0; i < 1000000; i++) {
			double d = i;
			double x = d * d / i * i;
			sum += x;
		}
	}
}

