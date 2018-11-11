package ttl.advjava.threads.advanced;

import java.util.Random;
import java.util.concurrent.Exchanger;

public class ExchangerDemo {
	Exchanger<DataBuffer> exchanger = new Exchanger<DataBuffer>();
	DataBuffer initialEmptyBuffer = new DataBuffer();
	DataBuffer initialFullBuffer = new DataBuffer();

	class FillingLoop implements Runnable {
		public void run() {
			DataBuffer currentBuffer = initialEmptyBuffer;
			try {
				while (currentBuffer != null) {
					currentBuffer.fillBuffer();
					System.out.println("Filler filled new Buffer " + currentBuffer);
					
					currentBuffer = exchanger.exchange(currentBuffer);
					
					System.out.println("Filler exchanged buffer");
					try {
						Thread.sleep(2000);
					}
					catch(InterruptedException e) {}
				}
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}

	class EmptyingLoop implements Runnable {
		public void run() {
			DataBuffer currentBuffer = initialFullBuffer;
			try {
				while (currentBuffer != null) {
					int[] iarr = currentBuffer.getContents();
					System.out.println("Reader currently has " + currentBuffer);
					
					currentBuffer = exchanger.exchange(currentBuffer);
					
					System.out.println("exchaned for new buffer");
					try {
						Thread.sleep(2000);
					}
					catch(InterruptedException e) {}
				}
			} catch (InterruptedException ex) {
			}
		}
	}

	void start() {
		new Thread(new FillingLoop()).start();
		new Thread(new EmptyingLoop()).start();
	}

	public static void main(String[] args) {
		new ExchangerDemo().start();
	}
}

class DataBuffer {
	int[] buffer = new int[10];
	Random random = new Random();

	public void fillBuffer() {
		// "Read" data from somewhere
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = random.nextInt(2000);
		}
	}

	public int[] getContents() {
		return buffer;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buffer.length; i++) {
			sb.append(buffer[i] + ", ");
		}
		return sb.toString();

	}
}