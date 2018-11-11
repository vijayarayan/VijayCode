package ttl.advjava.threads.visibility;

public class WordTearing {

	private long val = 0;

	public static void main(String[] args) {
		new WordTearing().go();
	}

	public void go() {
		Thread th1 = new Thread(new Tearer(0xFFFF0000));
		Thread th2 = new Thread(new Tearer(0x0000FFFF));

		th1.start();
		th2.start();

	}

	class Tearer implements Runnable {
		private long myNum;

		public Tearer(long myNum) {
			this.myNum = myNum;
		}

		public void run() {
			while (true) {
				System.out.println("val is " + val);
				val = myNum;
			}
		}
	}
}
