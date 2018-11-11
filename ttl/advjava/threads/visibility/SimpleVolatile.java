package ttl.advjava.threads.visibility;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.IntStream;

public class SimpleVolatile {

	private int transferValue;
	private long finalResult;
	private volatile boolean volatileGuard;
	private boolean plainGuard;

	private AtomicBoolean atomicGuard = new AtomicBoolean();

	public static void main(String[] args) {

		SimpleVolatile sv = new SimpleVolatile();

		double val = 0;
		// int reps = 1_000_000;
		int reps = 100_000;
		List<Result> plainResults = new ArrayList<>();
		List<Result> volatileResults = new ArrayList<>();
		List<Result> whileResults = new ArrayList<>();
		List<Result> atomicPlainResults = new ArrayList<>();
		List<Result> atomicWhileResults = new ArrayList<>();
		List<Result> lockResults = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			// plainResults.add(sv.goPlain(reps));
			//// atomicWhileResults.add(sv.goAtomicWhile(reps));
			// volatileResults.add(sv.goVolatile(reps));
			// whileResults.add(sv.goWhile(reps));
			// atomicPlainResults.add(sv.goAtomicPlain(reps));
			lockResults.add(sv.goLock(reps));
		}

		// System.out.println(plainResults);
		// System.out.println();
		// System.out.println(volatileResults);

		Supplier<Stats> sup = Stats::new;
		BiConsumer<Stats, Result> accum = Stats::accumulate;
		BinaryOperator<Stats> comb = Stats::combine;
		Function<Stats, Stats> fin = Stats::finish;

		Collector<Result, Stats, Stats> collector = Collector.of(sup, accum, comb, fin);

		Stats plainStatsC = plainResults.stream().collect(collector);

		Stats plainStats = plainResults.stream().collect(Stats::new, Stats::accumulate, Stats::combine);

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

	public Result goPlain(int reps) {
		List<Result> result = new ArrayList<>();
		CountDownLatch gate = new CountDownLatch(1);

		ProducerPlain producer = new ProducerPlain(reps, gate);
		ConsumerPlain consumer = new ConsumerPlain(reps, gate);
		Thread producerT = new Thread(producer, "Producer");
		Thread consumerT = new Thread(consumer, "Consumer");

		producerT.start();
		consumerT.start();

		gate.countDown();
		try {
			producerT.join();
			consumerT.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// System.out.printf("Plain Reps %,d, ProdTime %,d, ConsTime %,d, NumTrue:
		// %,d%n", x, producer.time, consumer.time, consumer.numTrue);
		Result r = new Result("Plain", reps, producer.time, consumer.time, consumer.numTrue);
		return r;

	}

	public Result goWhile(int reps) {
		List<Result> result = new ArrayList<>();
		CountDownLatch gate = new CountDownLatch(1);

		ProducerPlainWhile producer = new ProducerPlainWhile(reps, gate);
		ConsumerPlainWhile consumer = new ConsumerPlainWhile(reps, gate);
		Thread producerT = new Thread(producer, "Producer");
		Thread consumerT = new Thread(consumer, "Consumer");

		producerT.start();
		consumerT.start();

		gate.countDown();
		try {
			producerT.join();
			consumerT.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// System.out.printf("Plain Reps %,d, ProdTime %,d, ConsTime %,d, NumTrue:
		// %,d%n", x, producer.time, consumer.time, consumer.numTrue);
		Result r = new Result("Plain", reps, producer.time, consumer.time, consumer.numTrue);
		return r;

	}

	public Result goVolatile(int reps) {
		List<Result> result = new ArrayList<>();
		CountDownLatch gate = new CountDownLatch(1);
		ProducerVolatile producer = new ProducerVolatile(reps, gate);
		ConsumerVolatile consumer = new ConsumerVolatile(reps, gate);
		Thread producerT = new Thread(producer, "Producer");
		Thread consumerT = new Thread(consumer, "Consumer");

		producerT.start();
		consumerT.start();

		gate.countDown();
		try {
			producerT.join();
			consumerT.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// System.out.printf("Volatile Reps %,d, ProdTime %,d, ConsTime %,d, NumTrue:
		// %,d%n", x, producer.time, consumer.time, consumer.numTrue);
		Result r = new Result("Volatile", reps, producer.time, consumer.time, consumer.numTrue);
		return r;

	}

	public Result goAtomicPlain(int reps) {
		List<Result> result = new ArrayList<>();
		CountDownLatch gate = new CountDownLatch(1);
		ProducerPlainAtomic producer = new ProducerPlainAtomic(reps, gate);
		ConsumerPlainAtomic consumer = new ConsumerPlainAtomic(reps, gate);
		Thread producerT = new Thread(producer, "Producer");
		Thread consumerT = new Thread(consumer, "Consumer");

		producerT.start();
		consumerT.start();

		gate.countDown();
		try {
			producerT.join();
			consumerT.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// System.out.printf("Volatile Reps %,d, ProdTime %,d, ConsTime %,d, NumTrue:
		// %,d%n", x, producer.time, consumer.time, consumer.numTrue);
		Result r = new Result("AtomicPlain", reps, producer.time, consumer.time, consumer.numTrue);
		return r;
	}

	public Result goAtomicWhile(int reps) {
		List<Result> result = new ArrayList<>();
		CountDownLatch gate = new CountDownLatch(1);
		ProducerAtomicWhile producer = new ProducerAtomicWhile(reps, gate);
		ConsumerAtomicWhile consumer = new ConsumerAtomicWhile(reps, gate);
		Thread producerT = new Thread(producer, "Producer");
		Thread consumerT = new Thread(consumer, "Consumer");

		producerT.start();
		consumerT.start();

		gate.countDown();
		try {
			producerT.join();
			consumerT.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// System.out.printf("Volatile Reps %,d, ProdTime %,d, ConsTime %,d, NumTrue:
		// %,d%n", x, producer.time, consumer.time, consumer.numTrue);
		Result r = new Result("AtomicWhile", reps, producer.time, consumer.time, consumer.numTrue);
		return r;
	}

	public Result goLock(int reps) {
		List<Result> result = new ArrayList<>();
		CountDownLatch gate = new CountDownLatch(1);
		ProducerLock producer = new ProducerLock(reps, gate);
		ConsumerLock consumer = new ConsumerLock(reps, gate);
		Thread producerT = new Thread(producer, "Producer");
		Thread consumerT = new Thread(consumer, "Consumer");

		producerT.start();
		consumerT.start();

		gate.countDown();
		try {
			producerT.join();
			consumerT.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// System.out.printf("Volatile Reps %,d, ProdTime %,d, ConsTime %,d, NumTrue:
		// %,d%n", x, producer.time, consumer.time, consumer.numTrue);
		Result r = new Result("Synchronized", reps, producer.time, consumer.time, consumer.numTrue);
		return r;
	}

	public class ProducerPlain implements Runnable {
		private int reps;
		public long time;
		private CountDownLatch gate;

		public ProducerPlain(int reps, CountDownLatch gate) {
			this.reps = reps;
			this.gate = gate;
		}

		public void run() {
			/*
			 * try { gate.await(); } catch (InterruptedException e) { // TODO Auto-generated
			 * catch block e.printStackTrace(); }
			 */
			Instant start = Instant.now();
			IntStream.range(0, reps).forEach(i -> {
				if (!plainGuard) {
					transferValue = i * i;
					plainGuard = true;
				}
			});

			time = start.until(Instant.now(), ChronoUnit.MILLIS);
		}
	}

	public class ConsumerPlain implements Runnable {
		private int reps;
		public long time;
		public long numTrue;
		private CountDownLatch gate;

		public ConsumerPlain(int reps, CountDownLatch gate) {
			this.reps = reps;
			this.gate = gate;
		}

		public void run() {
			/*
			 * try { gate.await(); } catch (InterruptedException e) { // TODO Auto-generated
			 * catch block e.printStackTrace(); }
			 */
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
		}
	}

	public class ProducerVolatile implements Runnable {
		private int reps;
		public long time;
		private CountDownLatch gate;

		public ProducerVolatile(int reps, CountDownLatch gate) {
			this.reps = reps;
			this.gate = gate;
		}

		public void run() {
			try {
				gate.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Instant start = Instant.now();
			IntStream.range(0, reps).forEach(i -> {
				transferValue = i * i;
				volatileGuard = true;
			});

			time = start.until(Instant.now(), ChronoUnit.MILLIS);
		}
	}

	public class ConsumerVolatile implements Runnable {
		private int reps;
		public long time;
		public long numTrue;
		private CountDownLatch gate;

		public ConsumerVolatile(int reps, CountDownLatch gate) {
			this.reps = reps;
			this.gate = gate;
		}

		public void run() {
			try {
				gate.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		}
	}

	public class ProducerPlainWhile implements Runnable {
		private int reps;
		public long time;
		private CountDownLatch gate;

		public ProducerPlainWhile(int reps, CountDownLatch gate) {
			this.reps = reps;
			this.gate = gate;
		}

		public void run() {
			try {
				gate.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Instant start = Instant.now();
			IntStream.range(0, reps).forEach(i -> {
				while (volatileGuard) {
					// Thread.yield();
				}
				transferValue = i * i;
				volatileGuard = true;
			});

			time = start.until(Instant.now(), ChronoUnit.MILLIS);
		}
	}

	public class ConsumerPlainWhile implements Runnable {
		private int reps;
		public long time;
		public long numTrue;
		private CountDownLatch gate;

		public ConsumerPlainWhile(int reps, CountDownLatch gate) {
			this.reps = reps;
			this.gate = gate;
		}

		public void run() {
			try {
				gate.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Instant start = Instant.now();
			IntStream.range(0, reps).forEach(i -> {
				while (!volatileGuard) {
					// Thread.yield();
				}
				int x = transferValue;
				finalResult += x;
				numTrue++;
				volatileGuard = false;
			});
			time = start.until(Instant.now(), ChronoUnit.MILLIS);
		}
	}

	public class ProducerPlainAtomic implements Runnable {
		private int reps;
		public long time;
		private CountDownLatch gate;

		public ProducerPlainAtomic(int reps, CountDownLatch gate) {
			this.reps = reps;
			this.gate = gate;
		}

		public void run() {
			Instant start = Instant.now();
			IntStream.range(0, reps).forEach(i -> {
				if (!atomicGuard.get()) {
					transferValue = i * i;
					atomicGuard.lazySet(true);
				}
			});

			time = start.until(Instant.now(), ChronoUnit.MILLIS);
		}
	}

	public class ConsumerPlainAtomic implements Runnable {
		private int reps;
		public long time;
		public long numTrue;
		private CountDownLatch gate;

		public ConsumerPlainAtomic(int reps, CountDownLatch gate) {
			this.reps = reps;
			this.gate = gate;
		}

		public void run() {
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
		}
	}

	public class ProducerAtomicWhile implements Runnable {
		private int reps;
		public long time;
		private CountDownLatch gate;

		public ProducerAtomicWhile(int reps, CountDownLatch gate) {
			this.reps = reps;
			this.gate = gate;
		}

		public void run() {
			Instant start = Instant.now();
			IntStream.range(0, reps).forEach(i -> {
				while (atomicGuard.get()) {
				}
				transferValue = i * i;
				atomicGuard.set(true);
			});

			time = start.until(Instant.now(), ChronoUnit.MILLIS);
		}
	}

	public class ConsumerAtomicWhile implements Runnable {
		private int reps;
		public long time;
		public long numTrue;
		private CountDownLatch gate;

		public ConsumerAtomicWhile(int reps, CountDownLatch gate) {
			this.reps = reps;
			this.gate = gate;
		}

		public void run() {
			Instant start = Instant.now();
			IntStream.range(0, reps).forEach(i -> {
				while (!atomicGuard.get()) {
				}
				int x = transferValue;
				finalResult += x;
				numTrue++;
				atomicGuard.set(false);
			});
			time = start.until(Instant.now(), ChronoUnit.MILLIS);
		}
	}

	private Object syncObject = new Object();
	private Object readSync = new Object();
	private Object writeSync = new Object();

	private Lock lock = new ReentrantLock();
	private Condition readReady = lock.newCondition();
	private Condition writeReady = lock.newCondition();

	public class ProducerLock implements Runnable {
		private int reps;
		public long time;
		private CountDownLatch gate;

		public ProducerLock(int reps, CountDownLatch gate) {
			this.reps = reps;
			this.gate = gate;
		}

		public void run() {
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
					transferValue = i * i;
					plainGuard = (true);
					writeReady.signalAll();
				} finally {
					lock.unlock();
				}
			});

			time = start.until(Instant.now(), ChronoUnit.MILLIS);
		}
	}

	public class ConsumerLock implements Runnable {
		private int reps;
		public long time;
		public long numTrue;
		private CountDownLatch gate;

		public ConsumerLock(int reps, CountDownLatch gate) {
			this.reps = reps;
			this.gate = gate;
		}

		public void run() {
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
		}
	}

	public class ProducerSynchronized implements Runnable {
		private int reps;
		public long time;
		private CountDownLatch gate;

		public ProducerSynchronized(int reps, CountDownLatch gate) {
			this.reps = reps;
			this.gate = gate;
		}

		public void run() {
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
					transferValue = i * i;
					plainGuard = (true);
					readSync.notifyAll();
				}
			});

			time = start.until(Instant.now(), ChronoUnit.MILLIS);
		}
	}

	public class ConsumerSynchronized implements Runnable {
		private int reps;
		public long time;
		public long numTrue;
		private CountDownLatch gate;

		public ConsumerSynchronized(int reps, CountDownLatch gate) {
			this.reps = reps;
			this.gate = gate;
		}

		public void run() {
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
		}
	}

	public void accumulate(Stats stats, Result result) {
		stats.count++;
		stats.totalReps += result.reps;

		stats.maxProdTime = Math.max(stats.maxProdTime, result.prodTime);
		stats.minProdTime = Math.min(stats.minProdTime, result.prodTime);
		stats.totalProdTime += result.prodTime;
		stats.avgProdTimePerRep = stats.totalProdTime / stats.totalReps;
		stats.prodsPerSecond = 1000 * stats.totalReps / stats.totalProdTime;

		stats.maxConsTime = Math.max(stats.maxConsTime, result.consTime);
		stats.minConsTime = Math.min(stats.minConsTime, result.consTime);
		stats.totalConsTime += result.consTime;
		stats.avgConsTimePerRep = stats.totalConsTime / stats.totalReps;
		stats.consPerSecond = 1000 * stats.totalReps / stats.totalConsTime;

		stats.totalNumTrue += result.numTrue;
		stats.avgNumTrue = stats.totalNumTrue / stats.totalReps;
	}

	public void combine(Stats us, Stats other) {
		us.count += other.count;
		us.totalReps += other.totalReps;

		us.maxProdTime = Math.max(us.maxProdTime, other.maxProdTime);
		us.minProdTime = Math.min(us.minProdTime, other.minProdTime);
		us.totalProdTime += other.totalProdTime;
		us.avgProdTimePerRep = us.totalProdTime / us.count;
		us.prodsPerSecond = 1000 * us.totalReps / us.totalProdTime;

		us.maxConsTime = Math.max(us.maxConsTime, other.maxConsTime);
		us.minConsTime = Math.min(us.minConsTime, other.minConsTime);
		us.totalConsTime += other.maxConsTime;
		us.avgConsTimePerRep = us.totalConsTime / us.count;
		us.consPerSecond = 1000 * us.totalReps / us.totalConsTime;

		us.totalNumTrue += other.totalNumTrue;
		us.avgNumTrue = us.totalNumTrue / us.totalReps;
	}

	public Stats finish(Stats finalResult) {
		// Calculate averages at the end
		finalResult.avgProdTimePerRep = finalResult.totalProdTime / finalResult.totalReps;
		finalResult.prodsPerSecond = 1000 * finalResult.totalReps / finalResult.totalProdTime;

		finalResult.avgConsTimePerRep = finalResult.totalConsTime / finalResult.totalReps;
		finalResult.consPerSecond = 1000 * finalResult.totalReps / finalResult.totalConsTime;

		finalResult.avgNumTrue = finalResult.totalNumTrue / finalResult.totalReps;

		return finalResult;
	}

	public static class Result {
		public String name;
		public long reps;
		public long prodTime;
		public long consTime;
		public long numTrue;

		public Result(String name, long reps, long prodTime, long consTime, long numTrue) {
			super();
			this.name = name;
			this.prodTime = prodTime;
			this.consTime = consTime;
			this.numTrue = numTrue;
			this.reps = reps;
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
							+ "%n totalNumTrue=%,d, avgNumTrue=%,f" + ", totalReps=%,.2f ]%n",
					count, totalProdTime, maxProdTime, minProdTime, avgProdTimePerRep, avgProdTimeTotal, prodsPerSecond,
					totalConsTime, maxConsTime, minConsTime, avgConsTimePerRep, avgConsTimeTotal, consPerSecond,
					totalNumTrue, avgNumTrue, totalReps);

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
