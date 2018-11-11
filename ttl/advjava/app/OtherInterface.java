package ttl.advjava.app;

public interface OtherInterface {
    default public void forEach() {
        System.out.println("OtherInterface:foreach");
    }
}
