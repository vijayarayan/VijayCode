package ttl.advjava.cc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CustStatsApp {

	public static void main(String[] args) throws IOException {
		//flatMapper();
        //foo();
		foo2();
	}

	public static void foo2() {
		AtomicInteger supplier = new AtomicInteger(0);
		AtomicInteger accum = new AtomicInteger(0);
		AtomicInteger comb = new AtomicInteger(0);

		List<Data> data = new ArrayList<>();
		for(int i = 0; i < 1000000; i++) {
			Data d = new Data(ThreadLocalRandom.current().nextInt(1000),
					ThreadLocalRandom.current().nextInt(1000));
			data.add(d);
		}
		CustomStatistics result = data
				.stream()
                .parallel()
				.collect(() -> {
							supplier.getAndIncrement();
							return new CustomStatistics();
						},
						(cs, d) -> {
							accum.getAndIncrement();
							cs.accumulate(d);
						},
						(cs1, cs2) -> {
							comb.getAndIncrement();
							cs1.combine(cs2);
						});

		System.out.println(supplier.get() + ", " + accum.get() + ", " + comb.get());
		System.out.println("result is " + result);

	}
	public static void foo() {
		AtomicInteger supplier = new AtomicInteger(0);
		AtomicInteger accum = new AtomicInteger(0);
		AtomicInteger comb = new AtomicInteger(0);

		List<Data> data = Arrays.asList(
				new Data(0,10),
				new Data(4,20),
				new Data(6,80),
				new Data(8,80),
				new Data(9,80),
				new Data(6,80),
				new Data(5,80),
				new Data(2,10)
		);

		/*
		CustomStatistics cs1 = data.stream()
				.collect(() -> new CustomStatistics(),
						(acc, d) -> acc.accumulate(d),
						(acc1, acc2) -> acc1.combine(acc2));

		CustomStatistics cs = new CustomStatistics();
		for(Data d : data) {
			if(d.xValue > cs.maxX) {
				cs.maxX = d.xValue;
			}
		}
		*/

		CustomStatistics result = data
				.stream()
				.parallel()
				.collect(() -> {
							supplier.getAndIncrement();
							return new CustomStatistics();
						},
						(c, d) -> {
							accum.getAndIncrement();
							//System.out.println("accum: " + accum.getAndIncrement() + ", " + Thread.currentThread().getName());
							c.accumulate(d);
						},
						(c1, c2) -> {
							comb.getAndIncrement();
							//System.out.println("comb: " + comb.getAndIncrement() + ", " + Thread.currentThread().getName());
							c1.combine(c2);
						});

		//Simpler way
        /*
		result = data.stream()
				.parallel()
				.collect(CustomStatistics::new,
						CustomStatistics::accumulate,
						CustomStatistics::combine);

		*/
		
		System.out.println("result = " + result);
		System.out.println(supplier.get() + ", " + accum.get() + ", " + comb.get());

	}

	public void intStreams(List<Integer> someList) {
		int sum = IntStream.range(0, 10).parallel().reduce(0, (cs, i) -> cs + i);

		Optional<Integer> optResult = someList.stream().parallel().reduce((cs, i) -> cs + i);

		optResult.ifPresent((it) -> System.out.println(it));
	}

	public static void flatMapper() {
		String [][] sarr = { {"one", "two", "three" },
				{"four", "five", "six" }
		};

		/*
		for(int i =0; i < sarr.length; i++) {
			for(int j = 0; j < sarr[i].length; j++) {
				System.out.println(sarr[i][j]);
			}
		}

		List<String> ssa = Arrays.stream(sarr)
				.peek(sa -> System.out.println("Peek1 with " + sa))
				.flatMap(sa -> Arrays.stream(sa))
				.peek(str -> System.out.println("Peek 2 with " + str))
				.collect(Collectors.toList());
		*/

		List<Stream<String>> x = Arrays.stream(sarr)
				.peek(sa -> System.out.println("Peek1 with " + sa))
				.map(sa -> Arrays.stream(sa))
				.peek(str -> System.out.println("Peek 2 with " + str))
				.collect(Collectors.toList());


	}
}
