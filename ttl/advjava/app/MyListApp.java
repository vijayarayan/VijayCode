package ttl.advjava.app;

public class MyListApp {

    public static void main(String[] args) {
        MyList ml = new MyListImpl();

        ml.add("bb");

        ml.forEach();
    }
}
