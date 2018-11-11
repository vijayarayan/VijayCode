package ttl.advjava.app;

import java.util.ArrayList;
import java.util.List;

public class GenericsDemo {

    public static void main(String[] args) {
        new GenericsDemo().go();
    }

    public void go() {
        List<Number> lNum = new ArrayList<>();
        lNum.add(10.2);
        lNum.add(25);
        lNum.add(13.2);
        lNum.add(15.2);
        lNum.add(10);

        double result = sum(lNum);

        List<Integer> lInt = new ArrayList<>();
        lInt.add(10);
        lInt.add(20);
        lInt.add(30);
        lInt.add(40);

        List l = lInt;
        l.add("abc");

        for(Object it : lInt) {
            System.out.println(it);
        }
        /*
        double result2 = sum(lInt);

        Integer [] iarr = {0, 4, 5, 28 };

        addToList(lInt, iarr);

        addToList(lNum, iarr);

        System.out.println(lInt);
        */
    }

    public <T> void addToList(List<? super T> input, T [] arr) {
        for(T it : arr) {
            input.add(it);
        }
    }

    public double sum(List<? extends Number> input) {
        double sum = 0;
        for(Number n : input) {
            sum += n.doubleValue();
        }

        return sum;
    }

}
