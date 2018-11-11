package ttl.advjava.app;

import ttl.advjava.domain.Student;
import ttl.advjava.service.StudentService;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.toList;

public class ControllerStreams {

    private String str = "abc";
    public static void main(String[] args) {
        ControllerStreams c = new ControllerStreams();
        //c.doSorting();

        c.customListCollectorLarge();
    }

    public void allStudentsMap() {
        StudentService service = new StudentService();

        List<Student> students = service.getAll();

        Map<Student.Status, Long> result = students.stream()
                //.collect(Collectors.groupingBy(s -> s.getId()));
                //.collect(Collectors.groupingBy(s -> s.getId()));
                .collect(Collectors.groupingBy(Student::getStatus, Collectors.counting()));

        result.forEach( (key, value) -> System.out.println(key + ":" + value));
        //result.forEach( System.out::println):

    }


    public Map<Integer, Student> makeMap(List<Student> input) {
        Map<Integer, Student> result = new HashMap<>();
        for(Student s : input) {
            result.put(s.getId(), s);
        }

        return result;
    }

    public void customListCollectorLarge(){
        StudentService service = new StudentService();

        AtomicInteger supplier = new AtomicInteger(0);
        AtomicInteger accum = new AtomicInteger(0);
        AtomicInteger comb = new AtomicInteger(0);

        List<Double> students = new ArrayList<>();
        for(double i = 0; i < 1_000_000; i++) {
            students.add(i);
        }

        long start = System.currentTimeMillis();
        List<Double> s = students.stream()
                .collect(() -> {
                    supplier.getAndIncrement();
                    //System.out.println("supplier: " + Thread.currentThread().getName());
                    return new ArrayList<Double>();
                }, (l1, st) -> {
                    accum.getAndIncrement();
                    //System.out.println("accum: " + Thread.currentThread().getName());
                    l1.add(Math.sin(st) * Math.cos(st));
                }, (l1, l2) -> {
                    comb.getAndIncrement();
                    //System.out.println("comb: " + Thread.currentThread().getName());
                    l1.addAll(l2);
                });

        long end = System.currentTimeMillis();
        System.out.println("Serial time: " + (end - start) + ", " + supplier.get() + ", " + accum.get() + ", " + comb.get());

        start = System.currentTimeMillis();
        List<Double> s2 = students.stream()
                .parallel()
                .collect(() -> {
                    supplier.getAndIncrement();
                    //System.out.println("supplier: " + Thread.currentThread().getName());
                    return new ArrayList<Double>();
                }, (l1, st) -> {
                    accum.getAndIncrement();
                    //System.out.println("accum: " + Thread.currentThread().getName());
                    l1.add(Math.sin(st) * Math.cos(st));
                }, (l1, l2) -> {
                    comb.getAndIncrement();
                    //System.out.println("comb: " + Thread.currentThread().getName());
                    l1.addAll(l2);
                });

        end = System.currentTimeMillis();
        System.out.println("Parrallel time: " + (end - start) + ", " + supplier.get() + ", " + accum.get() + ", " + comb.get());
    }

    public void customListCollector(){
        StudentService service = new StudentService();

        AtomicInteger supplier = new AtomicInteger(0);
        AtomicInteger accum = new AtomicInteger(0);
        AtomicInteger comb = new AtomicInteger(0);

        List<Student> students = service.getAll();


        List<Student> s = students.stream().parallel()
                .collect(Collector.of(() -> {
                    supplier.getAndIncrement();
                    System.out.println("supplier: " + Thread.currentThread().getName());
                    return new ArrayList<Student>();
                }, (l1, st) -> {
                    accum.getAndIncrement();
                    System.out.println("accum: " + Thread.currentThread().getName());
                    l1.add(st);
                }, (l1, l2) -> {
                    comb.getAndIncrement();
                    System.out.println("comb: " + Thread.currentThread().getName());
                    l1.addAll(l2);
                    return l1;
                }));

        System.out.println(supplier.get() + ", " + accum.get() + ", " + comb.get());
    }
    public void customList(){
        StudentService service = new StudentService();

        AtomicInteger supplier = new AtomicInteger(0);
        AtomicInteger accum = new AtomicInteger(0);
        AtomicInteger comb = new AtomicInteger(0);

        List<Student> students = service.getAll();


        List<Student> s = students.stream().parallel()
                .collect(() -> {
                    supplier.getAndIncrement();
                    System.out.println("supplier: " + Thread.currentThread().getName());
                    return new ArrayList<Student>();
                }, (l1, st) -> {
                    accum.getAndIncrement();
                    System.out.println("accum: " + Thread.currentThread().getName());
                    l1.add(st);
                }, (l1, l2) -> {
                    comb.getAndIncrement();
                    System.out.println("comb: " + Thread.currentThread().getName());
                    l1.addAll(l2);
                });

        System.out.println(supplier.get() + ", " + accum.get() + ", " + comb.get());
    }

    public void allStudents() {
        StudentService service = new StudentService();

        List<Student> students = service.getAll();

        //List<String> withM = students.stream()
        Long l = students.stream()
        //Stream<String> ss = students.stream()
                .filter((s) -> s.getName().startsWith("M"))
                .map(s -> s.getName())
                //.collect(toList());
                .collect(counting());

    }

    interface MyFunction {
        public void foo() throws IOException;

    }

    public Function<String, String> someMethod(int i, String fileName) {
        int y = 34;
        String ssss = "boo";

        Function<String, String> f = (s) -> {

            str = "xyz";
            System.out.println("i is " + i);
            System.out.println("str is " + str);
            return s;
        };

        return f;
    }

    public <T, R extends Comparable > Comparator<T> makeComparator(Function<T, R> extractor) {

        Comparator<T> result = (s1, s2) -> extractor.apply(s1).compareTo(extractor.apply(s2));

        return result;
    }



    /*
    public Student.Status getStatusHard(Student s) {
        //Do lots of stuff
        Student.Status result = s.getStatus();

        return result;
    }
    */

    public <T, R> List<R> getAPiece(List<T> input, Function<T, R> extractor) {
        List<R> result = new ArrayList<>();
        for(T s : input) {
            result.add(extractor.apply(s));
        }

        return result;
    }

    class MyFunc implements Function<Student, Student.Status> {

        @Override
        public Student.Status apply(Student student) {
            return student.getStatus();
        }
    }

    class NameExtractor implements Extractor<Student, String>
    {
        @Override
        public String extract(Student s) {
            return s.getName();
        }
    }

    interface Extractor<T, R> {
        public R extract(T s);
    }


    public <T, R> List<R> getAPiece2(List<T> input, Extractor<T, R> extractor) {
        List<R> result = new ArrayList<>();
        for(T s : input) {
           result.add(extractor.extract(s));
        }

        return result;
    }

    public <T> List<T> extractor(List<T> input, Predicate<T> checker) {
        List<T> result = new ArrayList<>();
        for(T s : input) {
            if(checker.test(s)) {
                result.add(s);
            }
        }

        return result;
    }



    interface Checker<T> {
        public boolean check(T s);
    }
}
