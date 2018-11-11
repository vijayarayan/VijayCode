package ttl.advjava.threads.advanced;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.concurrent.Semaphore;

/**
 * SemaphoreOrderBoard represents a basic order board in a restaraunt.
 * 
 * 
 * @author developintelligence llc
 * @version 1.0
 */
public class SemaphoreOrderBoard implements OrderBoard {

	private List<Order> orders;
	private Semaphore fullSem, emptySem;

	/**
	 * Create a new SemaphoreOrderBoard, initializing the shared list.
	 */
	public SemaphoreOrderBoard() {
		orders = Collections.synchronizedList(new ArrayList<Order>());
		fullSem = new Semaphore(5);
		emptySem = new Semaphore(0);
	}

	/**
	 * add an order to the order board
	 * 
	 * @param toBeProcessed
	 */
	public void postOrder(Order toBeProcessed) {
		boolean done = false;
		boolean wasInterrupted = false;
		try {
			fullSem.acquire(); // decrease permits by one
			orders.add(toBeProcessed);
			done = true;
		} catch (InterruptedException e) {
			e.printStackTrace();
			wasInterrupted = true;
		} finally {
			if (done) {
				emptySem.release(); // increase permits by one
			}
			else if(!wasInterrupted){
				fullSem.release();
			}
		}
	}

	public void postOrder2(Order toBeProcessed) {
		boolean done = false;
		try {
			fullSem.acquire(); // decrease permits by one
			orders.add(toBeProcessed);
			emptySem.release(); // increase permits by one
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		catch(Exception e) {
			//We get here because orders.add threw some unexpected exception
			fullSem.release();
		}
	}

	public void postOrder3(Order toBeProcessed) {
		try {
			// decrease permits by one
			fullSem.acquireUninterruptibly();
			orders.add(toBeProcessed);
			emptySem.release(); // increase permits by one
		} catch(Exception ex) {
			//must have been thrown from orders.add(), so 
			//release the fullSem you got
			fullSem.release();
		}
	}

	/**
	 * take an order for the order board to cook.
	 * 
	 * @return
	 */
	public Order cookOrder() {
		Order tmpOrder = null;
		try {
			emptySem.acquire(); // decrease permits by one
			tmpOrder = orders.remove(0);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			fullSem.release(); // increase permits by one
		}

		return tmpOrder;
	}
}
