package ttl.advjava.threads.advanced;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class DelayQueueDemo {

	public static void main(String[] args) throws InterruptedException {
		DelayQueue<Snoozer> dqueue = new DelayQueue<>();
		Snoozer snoozer = new Snoozer("1 seconds", 1000, TimeUnit.MILLISECONDS);
		dqueue.add(snoozer);

		snoozer = new Snoozer("5 seconds", 5000, TimeUnit.MILLISECONDS);
		dqueue.add(snoozer);

		snoozer = new Snoozer("3 seconds", 3000, TimeUnit.MILLISECONDS);
		dqueue.add(snoozer);

		
		
		snoozer = dqueue.take();
		System.out.println(snoozer);
		
		snoozer = dqueue.take();
		System.out.println(snoozer);

		snoozer = dqueue.take();
		System.out.println(snoozer);
	}
}

class Snoozer implements Delayed
{

	private long createTime; 
	private String name;
	
	public Snoozer(String name, long delay, TimeUnit unit) {
		this.name = name;
		this.createTime = System.currentTimeMillis() + unit.convert(delay, TimeUnit.MILLISECONDS);
	}


	@Override
	public int compareTo(Delayed other) {
		return this.createTime < ((Snoozer)other).createTime ? -1 :
			this.createTime > ((Snoozer)other).createTime ? 1 : 0;
	}

	@Override
	public long getDelay(TimeUnit unit) {
		long now = System.currentTimeMillis();
		long diff = now - createTime;

		return unit.convert(diff, TimeUnit.MILLISECONDS);
	}


	@Override
	public String toString() {
		return "Snoozer [createTime=" + createTime + ", name=" + name + "]";
	}


}