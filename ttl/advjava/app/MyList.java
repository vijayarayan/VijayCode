package ttl.advjava.app;

import java.util.function.Function;

public interface MyList {
    public static final int MAX = 100;

    public void add(String s);

    default public void forEach() {
        System.out.println("MyList::forEach");
        Function<String, String> f = s -> s;
    }
}
