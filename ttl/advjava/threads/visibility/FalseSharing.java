package ttl.advjava.threads.visibility;

//import jdk.internal.vm.annotation.Contended;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * Demo of FalseSharing and @Contended.  Run with -XX:-RestrictContended
 * 
 * Use either new DataShared() or new DataContended() to try both scenarios
 * @author whynot
 *
 */
public class FalseSharing {

	private static ExecutorService executor;
	private static AtomicInteger counter = new AtomicInteger(0);

	public static void main(String[] args) {
		executor = Executors.newFixedThreadPool(5, (r) -> {
			Thread th = new Thread(r, "Worker " + counter.getAndIncrement());
			return th;
		});

		for (int i = 0; i < 10; i++) {
			long time = go(new DataShared());
			System.out.printf("Shared: %d: Time for n iterations is %d ms%n", i, time);
		}

		for (int i = 0; i < 10; i++) {
			long time = go(new DataContended());
			System.out.printf("Contended: %d: Time for n iterations is %d ms%n", i, time);
		}
		
		executor.shutdown();
	}


	public static long go(Data data) {

		//DataShared data = new DataShared();
		//DataContended data = new DataContended();
		
		//Just for fun, let's do a CompletableFuture
		CompletableFuture<Long> r1 = supplyAsync(() -> {
			Instant start = Instant.now();
			for (int i = 0; i < 10_000_000; i++) {
				data.setX(Math.atan(data.getX()));
				//data.x = (Math.atan(data.x));
			}

			Instant end = Instant.now();
			long ms = start.until(end, ChronoUnit.MILLIS);
			return ms;
		}, executor);

		//And mix it up with a callable
		Callable<Long> r2 = () -> {
			Instant start = Instant.now();
			for (int i = 0; i < 10_000_000; i++) {
				data.setY(Math.atan(data.getY()));
				//data.y = (Math.atan(data.y));
			}

			Instant end = Instant.now();
			long ms = start.until(end, ChronoUnit.MILLIS);
			return ms;
		};

		//Future<Long> f1 = executor.submit(r1);
		Future<Long> f2 = executor.submit(r2);

		long result = -1;
		try {
			//result = f1.get() + f2.get();
			result = r1.get() + f2.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		return result;
	}
}

interface Data {
	public double getX();
	public void setX(double x);
	public double getY();
	public void setY(double y);
}
final class DataContended implements Data{
	public volatile double x = ThreadLocalRandom.current().nextDouble();
	//Requires jdk8
	//@Contended
	public volatile double y = ThreadLocalRandom.current().nextDouble();
	@Override
	public double getX() {
		return x;
	}
	@Override
	public void setX(double x) {
		this.x = x;
	}
	@Override
	public double getY() {
		return y;
	}
	@Override
	public void setY(double y) {
		this.y = y;
		
	}
}

final class DataShared implements Data{
	public volatile double x = ThreadLocalRandom.current().nextDouble();
	public volatile double y = ThreadLocalRandom.current().nextDouble();
	@Override
	public double getX() {
		return x;
	}
	@Override
	public void setX(double x) {
		this.x = x;
	}
	@Override
	public double getY() {
		return y;
	}
	@Override
	public void setY(double y) {
		this.y = y;
		
	}
}
