package ttl.advjava.app;

import ttl.advjava.domain.Student;
import ttl.advjava.service.StudentService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ControllerFunc {

    private String str = "abc";
    public static void main(String[] args) {
        ControllerFunc c = new ControllerFunc();
        //c.doSorting();

        c.allStudents();
    }

    public void allStudents() {
        StudentService service = new StudentService();

        List<Student> students = service.getAll();

        List<Student> withM = extractor(students, (s) ->  s.getName().startsWith("M"));

        //withM.forEach(System.out::println);

        //List<String> names = getAPiece(students, (s) -> s.getName());
        List<String> names = getAPiece(students, Student::getName);
        names.forEach(System.out::println);

        List<String> ls = null;
        //List<Student.Status> statuses = getAPiece(students, (s) -> getStatusHard(s));
        List<Student.Status> statuses = getAPiece(students, Student::getStatus);

        //Extractor<Student, Student.Status> ex = (s) -> s.getStatus();
        //List<Student.Status> statuses = getAPiece2(students, ex);

        statuses.forEach(System.out::println);

        students.sort((s1, s2) -> s1.getName().compareTo(s2.getName()));

        students.sort((s1, s2) -> s1.getStatus().compareTo(s2.getStatus()));

        Comparator<Student> cs = makeComparator(Student::getName);

        //students.sort(cs);
        students.sort(Comparator.comparing(Student::getName).thenComparing(Student::getId));

        Function<String, String> foo = someMethod(10, "f1");
        foo.apply("boo");

        str = "bye";
        Function<String, String> foo2 = someMethod(234, "f1");
        foo2.apply("boo");
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
