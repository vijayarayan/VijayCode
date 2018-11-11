package ttl.advjava.threads.visibility;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.IntStream;

public class SimpleVolatileToo {

	private int transferValue;
	private long finalResult;
	private volatile boolean volatileGuard;
	private boolean plainGuard;

	private AtomicBoolean atomicGuard = new AtomicBoolean();
	private static ExecutorService executor = Executors.newFixedThreadPool(3);

	public static void main(String[] args) {

		executor = Executors.newFixedThreadPool(3);

		SimpleVolatileToo sv = new SimpleVolatileToo();
		sv.run();

		executor.shutdown();
	}

	public void run() {

		double val = 0;
		// int reps = 1_000_000;
		int reps = 100_000;
		List<Result> plainResults = new ArrayList<>();
		List<Result> volatileResults = new ArrayList<>();
		List<Result> whileResults = new ArrayList<>();
		List<Result> atomicPlainResults = new ArrayList<>();
		List<Result> atomicWhileResults = new ArrayList<>();
		List<Result> lockResults = new ArrayList<>();
		BiFunction<Result, Result, Result> mushResults = (prodR, consR) -> {
			Result r = new Result(prodR.name, prodR.reps, prodR.prodTime, consR.consTime, consR.numTrue,
					consR.finalResult);
			return r;
		};
		for (int i = 0; i < 10; i++) {

			// init()
			// plainResults.add(goGeneric(reps, new ProducerPlain(reps), new
			// ConsumerPlain(reps), mushResults));
			doRun(reps, plainResults, new ProducerPlain(reps), new ConsumerPlain(reps), mushResults);

			doRun(reps, atomicWhileResults, new ProducerPlainWhile(reps), new ConsumerPlainWhile(reps), mushResults);
			doRun(reps, volatileResults, new ProducerVolatile(reps), new ConsumerVolatile(reps), mushResults);
			doRun(reps, atomicPlainResults, new ProducerPlainAtomic(reps), new ConsumerPlainAtomic(reps), mushResults);
			doRun(reps, lockResults, new ProducerLock(reps), new ConsumerLock(reps), mushResults);
		}

		// System.out.println(plainResults);
		// System.out.println();
		// System.out.println(volatileResults);

		Supplier<Stats> sup = Stats::new;
		BiConsumer<Stats, Result> accum = Stats::accumulate;
		BinaryOperator<Stats> comb = Stats::combine;
		Function<Stats, Stats> fin = Stats::finish;

		Collector<Result, Stats, Stats> collector = Collector.of(sup, accum, comb, fin);

		Stats plainStats = plainResults.stream().collect(Stats::new, Stats::accumulate, Stats::combine);

		Stats plainStatsC = plainResults.stream().collect(collector);

		Stats atomicPlainStatsC = atomicPlainResults.stream()
				.collect(Collector.of(Stats::new, Stats::accumulate, Stats::combine, Stats::finish));

		Stats volStatsC = volatileResults.stream().collect(collector);

		Stats whileStatsC = whileResults.stream().collect(collector);

		Stats atomicWhileC = atomicWhileResults.stream().collect(collector);

		Stats syncC = lockResults.stream().collect(collector);

		System.out.println("Plain: " + plainStats);
		System.out.println("PlainC: " + plainStatsC);
		System.out.println("AtomicPlainC: " + atomicPlainStatsC);
		System.out.println("VolatileC: " + volStatsC);
		System.out.println("WhileC: " + whileStatsC);
		System.out.println("AtomicWhile: " + atomicWhileC);
		System.out.println("SyncWhile: " + syncC);
	}

	private void doRun(int reps, List<Result> list, Callable<Result> producer, Callable<Result> consumer,
			BiFunction<Result, Result, Result> resultMusher) {
		init();
		list.add(goGeneric(reps, new ProducerLock(reps), new ConsumerLock(reps), resultMusher));
	}

	private void init() {
		finalResult = transferValue = 0;
		atomicGuard.set(false);
		volatileGuard = plainGuard = false;
	}

	public Result goGeneric(int reps, Callable<Result> producer, Callable<Result> consumer,
			BiFunction<Result, Result, Result> resultMusher) {
		Future<Result> producerT = executor.submit(producer);
		Future<Result> consumerT = executor.submit(consumer);

		Result producerR = null;
		Result consumerR = null;
		try {
			producerR = producerT.get();
			consumerR = consumerT.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		Result r = resultMusher.apply(producerR, consumerR);
		return r;
	}
	

	/**
	 * T is a Callable AND a Consumer AND a Function.  Going nuts with generics
	 * @param reps
	 * @param producer
	 * @param consumer
	 * @param resultConsumer
	 * @return
	 */
	public <T extends Callable<Result> & Consumer<Integer> & Function<Long, Result>> Result

	goTimed(int reps, T producer, T consumer,
			BiFunction<Result, Result, Result> resultConsumer) {
		
		TimedRunner prod = new TimedRunner(reps, i -> producer.accept(i), t -> producer.apply(t));
		TimedRunner cons = new TimedRunner(reps, i -> consumer.accept(i), t -> consumer.apply(t));

		Future<Result> producerT = executor.submit(prod);
		Future<Result> consumerT = executor.submit(cons);

		Result producerR = null;
		Result consumerR = null;
		try {
			producerR = producerT.get();
			consumerR = consumerT.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		Result r = resultConsumer.apply(producerR, consumerR);
		return r;
	}

	public class TimedRunner implements Callable<Result> {
		private int reps;
		public long time;
		public long numTrue;
		private Consumer<Integer> consumer;
		private Function<Long, Result> resultMapper;

		public TimedRunner(int reps, Consumer<Integer> consumer, Function<Long, Result> resultMapper) {
			this.reps = reps;
			this.consumer = consumer;
			this.resultMapper = resultMapper;
		}

		public Result call() {
			Instant start = Instant.now();
			IntStream.range(0, reps).forEach(i -> {
				consumer.accept(i);
			});
			time = start.until(Instant.now(), ChronoUnit.MILLIS);
			Result r = resultMapper.apply(time);
			return r;
		}
	}

	public class ProducerPlain implements Callable<Result> {
		private int reps;
		public long time;

		public ProducerPlain(int reps) {
			this.reps = reps;
		}

		public Result call() {
			Instant start = Instant.now();
			IntStream.range(0, reps).forEach(i -> {
				if (!plainGuard) {
					transferValue = i;
					plainGuard = true;
				}
			});

			time = start.until(Instant.now(), ChronoUnit.MILLIS);
			Result r = new Result("", reps, time, -1, -1);
			return r;
		}
	}

	public class ConsumerPlain implements Callable<Result> {
		private int reps;
		public long time;
		public long numTrue;

		public ConsumerPlain(int reps) {
			this.reps = reps;
		}

		public Result call() {
			Instant start = Instant.now();
			IntStream.range(0, reps).forEach(i -> {
				if (plainGuard == true) {
					int x = transferValue;
					finalResult += x;
					numTrue++;
					plainGuard = false;
				}
			});
			time = start.until(Instant.now(), ChronoUnit.MILLIS);
			Result r = new Result("", reps, -1, time, numTrue, finalResult);
			return r;
		}
	}

	public class ProducerVolatile implements Callable<Result> {
		private int reps;
		public long time;

		public ProducerVolatile(int reps) {
			this.reps = reps;
		}

		public Result call() {
			Instant start = Instant.now();
			IntStream.range(0, reps).forEach(i -> {
				transferValue = i;
				volatileGuard = true;
			});

			time = start.until(Instant.now(), ChronoUnit.MILLIS);
			Result r = new Result("", reps, time, -1, -1);
			return r;
		}
	}

	public class ConsumerVolatile implements Callable<Result> {
		private int reps;
		public long time;
		public long numTrue;

		public ConsumerVolatile(int reps) {
			this.reps = reps;
		}

		public Result call() {
			Instant start = Instant.now();
			IntStream.range(0, reps).forEach(i -> {
				if (volatileGuard == true) {
					int x = transferValue;
					finalResult += x;
					numTrue++;
					volatileGuard = false;
				}
			});
			time = start.until(Instant.now(), ChronoUnit.MILLIS);
			Result r = new Result("", reps, -1, time, numTrue, finalResult);
			return r;
		}
	}

	public class ProducerPlainWhile implements Callable<Result> {
		private int reps;
		public long time;

		public ProducerPlainWhile(int reps) {
			this.reps = reps;
		}

		private int counter;

		public Result call() {
			Instant start = Instant.now();
			IntStream.range(0, reps).forEach(i -> {
				counter = 0;
				while (volatileGuard) {
					// Thread.yield();
					counter++;
					if (counter > 1000) {
						Thread.yield();
						counter = 0;
					}
				}
				transferValue = i;
				volatileGuard = true;
			});

			time = start.until(Instant.now(), ChronoUnit.MILLIS);
			Result r = new Result("", reps, time, -1, -1);
			return r;
		}
	}

	public class ConsumerPlainWhile implements Callable<Result> {
		private int reps;
		public long time;
		public long numTrue;

		public ConsumerPlainWhile(int reps) {
			this.reps = reps;
		}

		private int counter;

		public Result call() {
			Instant start = Instant.now();
			IntStream.range(0, reps).forEach(i -> {
				counter = 0;
				while (!volatileGuard) {
					// Thread.yield();
					counter++;
					if (counter > 1000) {
						Thread.yield();
						counter = 0;
					}
				}
				int x = transferValue;
				finalResult += x;
				numTrue++;
				volatileGuard = false;
			});
			time = start.until(Instant.now(), ChronoUnit.MILLIS);
			Result r = new Result("", reps, -1, time, numTrue, finalResult);
			return r;
		}
	}

	public class ProducerPlainAtomic implements Callable<Result> {
		private int reps;
		public long time;

		public ProducerPlainAtomic(int reps) {
			this.reps = reps;
		}

		public Result call() {
			Instant start = Instant.now();
			IntStream.range(0, reps).forEach(i -> {
				if (!atomicGuard.get()) {
					transferValue = i;
					atomicGuard.lazySet(true);
				}
			});

			time = start.until(Instant.now(), ChronoUnit.MILLIS);
			Result r = new Result("", reps, time, -1, -1);
			return r;
		}
	}

	public class ConsumerPlainAtomic implements Callable<Result> {
		private int reps;
		public long time;
		public long numTrue;

		public ConsumerPlainAtomic(int reps) {
			this.reps = reps;
		}

		public Result call() {
			Instant start = Instant.now();
			IntStream.range(0, reps).forEach(i -> {
				if (atomicGuard.get()) {
					int x = transferValue;
					finalResult += x;
					numTrue++;
					atomicGuard.lazySet(false);
				}
			});
			time = start.until(Instant.now(), ChronoUnit.MILLIS);
			Result r = new Result("", reps, -1, time, numTrue, finalResult);
			return r;
		}
	}

	public class ProducerAtomicWhile implements Callable<Result> {
		private int reps;
		public long time;

		public ProducerAtomicWhile(int reps) {
			this.reps = reps;
		}

		private int counter;

		public Result call() {
			Instant start = Instant.now();
			IntStream.range(0, reps).forEach(i -> {
				counter = 0;
				while (atomicGuard.get()) {
					counter++;
					if (counter > 5000) {
						Thread.yield();
						counter = 0;
					}
				}
				transferValue = i;
				atomicGuard.lazySet(true);
			});

			time = start.until(Instant.now(), ChronoUnit.MILLIS);
			Result r = new Result("", reps, time, -1, -1);
			return r;
		}
	}

	public class ConsumerAtomicWhile implements Callable<Result> {
		private int reps;
		public long time;
		public long numTrue;

		public ConsumerAtomicWhile(int reps) {
			this.reps = reps;
		}

		private int counter;

		public Result call() {
			Instant start = Instant.now();
			IntStream.range(0, reps).forEach(i -> {
				counter = 0;
				while (!atomicGuard.get()) {
					counter++;
					if (counter > 5000) {
						Thread.yield();
						counter = 0;
					}
				}
				int x = transferValue;
				finalResult += x;
				numTrue++;
				atomicGuard.lazySet(false);
			});
			time = start.until(Instant.now(), ChronoUnit.MILLIS);
			Result r = new Result("", reps, -1, time, numTrue, finalResult);
			return r;
		}
	}

	private Object syncObject = new Object();
	private Object readSync = new Object();
	private Object writeSync = new Object();

	private Lock lock = new ReentrantLock();
	private Condition readReady = lock.newCondition();
	private Condition writeReady = lock.newCondition();

	public class ProducerLock implements Callable<Result> {
		private int reps;
		public long time;

		public ProducerLock(int reps) {
			this.reps = reps;
		}

		public Result call() {
			Instant start = Instant.now();
			IntStream.range(0, reps).forEach(i -> {
				try {
					lock.lock();
					while (plainGuard) {
						try {
							readReady.await();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					transferValue = i;
					plainGuard = (true);
					writeReady.signalAll();
				} finally {
					lock.unlock();
				}
			});

			time = start.until(Instant.now(), ChronoUnit.MILLIS);
			Result r = new Result("", reps, time, -1, -1);
			return r;
		}
	}

	public class ConsumerLock implements Callable<Result> {
		private int reps;
		public long time;
		public long numTrue;

		public ConsumerLock(int reps) {
			this.reps = reps;
		}

		public Result call() {
			Instant start = Instant.now();
			IntStream.range(0, reps).forEach(i -> {
				try {
					lock.lock();
					while (!plainGuard) {
						try {
							writeReady.await();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					int x = transferValue;
					finalResult += x;
					numTrue++;
					plainGuard = false;
					readReady.signalAll();
				} finally {
					lock.unlock();
				}
			});
			time = start.until(Instant.now(), ChronoUnit.MILLIS);
			Result r = new Result("", reps, -1, time, numTrue, finalResult);
			return r;
		}
	}

	public class ProducerSynchronized implements Callable<Result> {
		private int reps;
		public long time;

		public ProducerSynchronized(int reps) {
			this.reps = reps;
		}

		public Result call() {
			Instant start = Instant.now();
			IntStream.range(0, reps).forEach(i -> {
				synchronized (syncObject) {
					while (plainGuard) {
						try {
							synchronized (writeSync) {
								writeSync.wait();
							}
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					transferValue = i;
					plainGuard = (true);
					readSync.notifyAll();
				}
			});

			time = start.until(Instant.now(), ChronoUnit.MILLIS);
			Result r = new Result("", reps, time, -1, -1);
			return r;
		}
	}

	public class ConsumerSynchronized implements Callable<Result> {
		private int reps;
		public long time;
		public long numTrue;

		public ConsumerSynchronized(int reps) {
			this.reps = reps;
		}

		public Result call() {
			Instant start = Instant.now();
			IntStream.range(0, reps).forEach(i -> {
				synchronized (syncObject) {
					while (!plainGuard) {
						try {
							synchronized (readSync) {
								readSync.wait();
							}
							// syncObject.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					int x = transferValue;
					finalResult += x;
					numTrue++;
					plainGuard = false;
					writeSync.notifyAll();
				}
			});
			time = start.until(Instant.now(), ChronoUnit.MILLIS);
			Result r = new Result("", reps, -1, time, numTrue, finalResult);
			return r;
		}
	}

	public static class Result {
		public String name;
		public long reps;
		public long prodTime;
		public long consTime;
		public long numTrue;
		public long finalResult;

		public Result(String name, long reps, long prodTime, long consTime, long numTrue) {
			this(name, reps, prodTime, consTime, numTrue, -1);

		}

		public Result(String name, long reps, long prodTime, long consTime, long numTrue, long finalResult) {
			super();
			this.name = name;
			this.prodTime = prodTime;
			this.consTime = consTime;
			this.numTrue = numTrue;
			this.reps = reps;
			this.finalResult = finalResult;
		}

		public String toString() {
			return String.format("Name: %s, ProdTime %,d, ConsTime %,d, NumTrue: %,d%n", name, prodTime, consTime,
					numTrue);

		}
	}

	public static class Stats {
		public double count;
		public double totalReps;
		public long totalProdTime;
		public long maxProdTime = Integer.MIN_VALUE;
		public long minProdTime = Integer.MAX_VALUE;
		public double avgProdTimePerRep;
		public double avgProdTimeTotal;
		public double prodsPerSecond;

		public long totalConsTime;
		public long maxConsTime = Integer.MIN_VALUE;
		public long minConsTime = Integer.MAX_VALUE;
		public double avgConsTimePerRep;
		public double avgConsTimeTotal;
		public double consPerSecond;

		public long totalNumTrue;
		public double avgNumTrue;

		public long finalResult;

		public static void accumulate(Stats us, Result result) {
			us.count++;
			us.totalReps += result.reps;

			us.maxProdTime = Math.max(us.maxProdTime, result.prodTime);
			us.minProdTime = Math.min(us.minProdTime, result.prodTime);
			us.totalProdTime += result.prodTime;
			// us.avgProdTime = us.totalProdTime / us.totalReps;
			// us.prodsPerSecond = 1000 * us.totalReps / us.totalProdTime;

			us.maxConsTime = Math.max(us.maxConsTime, result.consTime);
			us.minConsTime = Math.min(us.minConsTime, result.consTime);
			us.totalConsTime += result.consTime;
			// us.avgConsTime = us.totalConsTime / us.totalReps;
			// us.consPerSecond = 1000 * us.totalReps / us.totalConsTime;

			us.totalNumTrue += result.numTrue;
			// us.avgNumTrue = us.totalNumTrue / us.totalReps;

			us.finalResult = Math.max(0, result.finalResult);
		}

		public static Stats combine(Stats us, Stats other) {
			us.count += other.count;
			us.totalReps += other.totalReps;

			us.maxProdTime = Math.max(us.maxProdTime, other.maxProdTime);
			us.minProdTime = Math.min(us.minProdTime, other.minProdTime);
			us.totalProdTime += other.totalProdTime;
			// us.avgProdTime = us.totalProdTime / us.count;
			// us.prodsPerSecond = 1000 * us.totalReps / us.totalProdTime;

			us.maxConsTime = Math.max(us.maxConsTime, other.maxConsTime);
			us.minConsTime = Math.min(us.minConsTime, other.minConsTime);
			us.totalConsTime += other.maxConsTime;
			// us.avgConsTime = us.totalConsTime / us.count;
			// us.consPerSecond = 1000 * us.totalReps / us.totalConsTime;

			us.totalNumTrue += other.totalNumTrue;
			// us.avgNumTrue = us.totalNumTrue / us.totalReps;

			return us;
		}

		public static Stats finish(Stats finalResult) {
			// Calculate averages at the end
			finalResult.avgProdTimePerRep = finalResult.totalProdTime / finalResult.count;
			finalResult.avgProdTimeTotal = finalResult.totalProdTime / finalResult.totalReps;

			finalResult.prodsPerSecond = 1000 * finalResult.totalReps / finalResult.totalProdTime;

			finalResult.avgConsTimePerRep = finalResult.totalConsTime / finalResult.count;
			finalResult.avgConsTimeTotal = finalResult.totalConsTime / finalResult.totalReps;
			finalResult.consPerSecond = 1000 * finalResult.totalReps / finalResult.totalConsTime;

			finalResult.avgNumTrue = finalResult.totalNumTrue / finalResult.totalReps;

			return finalResult;
		}

		@Override
		public String toString() {
			String s = String.format(
					"Stats [reps=%,.2f, totalProdTime=%,d, maxProdTime=%,d,  minProdTime=%,d, "
							+ "avgProdTimePerRep=%,f, avgProdTimeTotal=%,f%nprodsPerSecond=%,.2f"
							+ "%ntotalConsTime=%,d, maxConsTime=%,d, minConsTime=%,d "
							+ ", avgConsTimePerRep=%,f, avgConsTimeTotal=%,f%n consPerSecond=%,.2f"
							+ "%n totalNumTrue=%,d, avgNumTrue=%,f" + ", totalReps=%,.2f, finalResult=%,d ]%n",
					count, totalProdTime, maxProdTime, minProdTime, avgProdTimePerRep, avgProdTimeTotal, prodsPerSecond,
					totalConsTime, maxConsTime, minConsTime, avgConsTimePerRep, avgConsTimeTotal, consPerSecond,
					totalNumTrue, avgNumTrue, totalReps, finalResult);

			return s;
			/*
			 * return "Stats [reps=" + count + ", totalProdTime=" + totalProdTime +
			 * ", maxProdTime=" + maxProdTime + " minProdTime=" + minProdTime +
			 * ", avgProdTime=" + avgProdTime + "\n prodsPerSecond=" + prodsPerSecond +
			 * "\n totalConsTime=" + totalConsTime + ", maxConsTime=" + maxConsTime +
			 * ", minConsTime=" + minConsTime + ", avgConsTime=" + avgConsTime +
			 * "\n consPerSecond=" + consPerSecond + "\n totalNumTrue=" + totalNumTrue +
			 * ", avgNumTrue=" + avgNumTrue + ", totalReps=" + totalReps + "]";
			 */
		}
	}
}
