package ttl.advjava.threads.basic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * OrderBoard represents a basic
 * order board in a restaraunt.
 *
 *
 * @author developintelligence llc
 * @version 1.0
 */
public class OrderBoard {

  private List<Order> orders;

  /**
   * Create a new SemaphoreOrderBoard, initializing
   * the shared list.
   */
  public OrderBoard() {
    orders = new ArrayList<Order>();
  }

  /**
   * add an order to the order board
   * @param toBeProcessed
   */
  public void postOrder(Order toBeProcessed) {
    synchronized(orders) {
      while(orders.size() == 5) {
        try {
          orders.wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }

      orders.add(toBeProcessed);
      
      orders.notifyAll();
    }
  }

  /**
   * take an order for the order board to
   * cook.
   *
   * @return
   */
  public Order cookOrder() {
    Order tmpOrder = null;

    synchronized(orders) {
      while(orders.isEmpty()) {
        try {
          orders.wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      
      tmpOrder = orders.remove(0);
      orders.notifyAll();
    }

    return tmpOrder;
  }
}


class MyClass
{
	volatile boolean b;
	AtomicInteger i = new AtomicInteger(0);
	
	long l;
	
	public void foo() {
		int j = i.getAndIncrement();
		
		b = false;
		
	}
	
	
	
}