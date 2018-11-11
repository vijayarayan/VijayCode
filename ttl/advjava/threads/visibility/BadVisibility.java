package ttl.advjava.threads.visibility;

public class BadVisibility {

	public static void main(String [] args) {
		
		BadVisibility bv = new BadVisibility();
		bv.start();
		
	}
	
	public void start() {
		Thread th1 = new Worker1();
		Thread th2 = new Worker2();
		
		th2.start();
		th1.start();
	}

	private boolean ready;
	private int var;

	public class Worker1 extends Thread
	{
		public void run() {
			while(!ready) {

			}
			System.out.println("Var is " + var);
		}
	}
	
	public class Worker2 extends Thread
	{
		public void run() {
			var = 10;
			ready = true;
		}
	}
}
