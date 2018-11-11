package ttl.advjava.threads.advanced;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Synchronization {

	
	public synchronized void foo(String name) {
		System.out.println(name + " has the lock");
		try {
			Thread.sleep(100000);
		}
		catch(InterruptedException e) {
			Thread.interrupted();
		}
	}

	class S1 implements Callable<Void> {
		private String name;

		public S1(String name) {
			this.name = name;
		}

		public Void call() {
			foo(name);
			return null;
		}
	}
	
	public static void main(String[] args) {
		Synchronization s = new Synchronization();
		s.doit();
	}
	
	public void doit() {
		List<S1> tasks = new ArrayList<>();
		tasks.add(new S1("Worker 1"));
		tasks.add(new S1("Worker 2"));
		tasks.add(new S1("Worker 3"));
		
		ExecutorService es = Executors.newFixedThreadPool(5);
		
		try {
			es.invokeAll(tasks);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		es.shutdown();
	}
}
