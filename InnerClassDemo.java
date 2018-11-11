public class InnerClassDemo {

    public static void main(String[] args) {
        InnerClassDemo outer = new InnerClassDemo();
        Inner in = outer.new Inner();

        StaticInner si = new StaticInner();
    }


    public InnerClassDemo() {
        Inner in = new Inner();
    }

    class Inner {

    }

    static class StaticInner {

    }
}
