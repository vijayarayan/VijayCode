package ttl.advjava.threads;

import javax.xml.transform.sax.SAXSource;
import java.util.concurrent.*;

public class ThreadDemo {

    public static void main(String[] args) {
        new ThreadDemo().go2();
    }

    public void go() {
        int numCores = Runtime.getRuntime().availableProcessors();

        ExecutorService eService = Executors.newFixedThreadPool(numCores);

        Callable w1 = new WorkerCallable();
        Callable w2 = new WorkerCallable();

        Future<Integer> f1 = eService.submit(w1);
        Future<Integer> f2 = eService.submit(w2);

        int finalSum = -1;
        try {
            finalSum = f1.get() + f2.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }


        eService.shutdownNow();

        int numTries = 0;
        while(!eService.isShutdown() && numTries++ < 5) {
            try {
                eService.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Final result is " + finalSum);
    }

    public void go3() {
        int numCores = Runtime.getRuntime().availableProcessors();

        ExecutorService eService = Executors.newFixedThreadPool(numCores);

        IndexHolder ih = new IndexHolder();
        Worker w1 = new Worker(ih);
        BadWorker w2 = new BadWorker();

        Future<?> f1 = eService.submit(w1);
        Future<?> f2 = eService.submit(w2);

        /*
        try {
            f1.get();
            f2.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        */

        /*
        try {
            th1.join();
            th2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */

        w2.stopGoing();

        eService.shutdownNow();

        int numTries = 0;
        while(!eService.isShutdown() && numTries++ < 5) {
            try {
                eService.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int finalSum = w1.getSum() + w2.getSum();
        System.out.println("Final result is " + finalSum);
    }

    public void go2() {
        IndexHolder ih = new IndexHolder();
        Worker w1 = new Worker(ih);
        Thread th1 = new Thread(w1);

        Worker w2 = new Worker(ih);
        Thread th2 = new Thread(w2);

        th1.start();
        th2.start();

        try {
            th1.join();
            th2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int finalSum = w1.getSum() + w2.getSum();
        System.out.println("NextIndex is " + ih.nextIndex);
    }

    public class IndexHolder
    {
        private int nextIndex = 0;
        private Object syncObject = new Object();

        public int getNextIndex() {
            //lot's of code
            synchronized(syncObject) {
                return nextIndex++;
            }
            //lot's of code
        }

        public void strange() {
            synchronized(syncObject) {
                nextIndex--;
            }
        }
    }

    public class Worker implements Runnable {
        private IndexHolder ih;
        private int sum;

        public Worker(IndexHolder ih) {
            this.ih = ih;
        }

        @Override
        public void run() {
            for (int i = 0; i < 1000; i++) {
                int nextIndex = ih.getNextIndex();
                sum += nextIndex;
            }
        }

        public int getSum() {
            return sum;
        }
    }


    public class BadWorker implements Runnable {
        private int sum;

        private volatile boolean keepGoing;

        @Override
        public void run() {
            while(keepGoing) {
                System.out.println("Still here");
            }
        }

        public int getSum() {
            return sum;
        }

        public void stopGoing() {
            keepGoing = false;
        }
    }
    public class WorkerCallable implements Callable<Integer> {

        @Override
        public Integer call () {
            int sum = 0;
            for (int i = 0; i < 1000; i++) {
                sum += 1;
            }

            return sum;
        }
    }
}
