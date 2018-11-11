package ttl.advjava.threads.advanced;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The application that starts the waiter
 * and the cook.
 *
 * @author developintelligence llc
 * @version 1.0
 */
public class TheBurgerJointExecutor {

  public static void main(String[] args) throws InterruptedException {
	  int numWaiters = 5;
	  ExecutorService service = Executors.newFixedThreadPool(numWaiters);
    OrderBoard orders = new BlockingQueueOrderBoard();
    //OrderBoard orders = new LockOrderBoard();
    //OrderBoard orders = new SemaphoreOrderBoard();

    service.execute(new Cook(orders));
    for(int i = 0; i < numWaiters; i++) {
    	service.execute(new Waiter(orders));
    }
    /*
    Runnable cook = new Cook(orders);
    Runnable waiter1 = new Waiter(orders);
    Thread producer = new Thread(waiter1);
    Thread consumer = new Thread(cook);
    consumer.start();
    producer.start();
    */
  }
}
