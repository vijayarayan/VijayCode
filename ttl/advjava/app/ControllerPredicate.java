package ttl.advjava.app;

import ttl.advjava.domain.Student;
import ttl.advjava.service.StudentService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ControllerPredicate {

    public static void main(String[] args) {
        ControllerPredicate c = new ControllerPredicate();
        //c.doSorting();

        c.allStudents();
    }

    public void allStudents() {
        StudentService service = new StudentService();

        List<Student> students = service.getAll();

        List<Student> withM = startingWithM(students, "M");

        NameChecker nc = new NameChecker("M");
        withM = extractor(students, nc);

        withM = extractor(students, (s) ->  s.getName().startsWith("M"));

        withM.forEach(System.out::println);
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

    public <T> List<T> extractor3(List<T> input, Checker<T> checker) {
        List<T> result = new ArrayList<>();
        for(T s : input) {
            if(checker.check(s)) {
                result.add(s);
            }
        }

        return result;
    }

    public List<Student> extractorOld(List<Student> input, Checker<Student> checker) {
        List<Student> result = new ArrayList<>();
        for(Student s : input) {
            if(checker.check(s)) {
                result.add(s);
            }
        }

        return result;
    }

    public List<Student> startingWithM(List<Student> input, String prefix) {
        List<Student> result = new ArrayList<>();
        for(Student s : input) {
            if(s.getName().startsWith(prefix)) {
                result.add(s);
            }
        }

        return result;
    }


    public List<Student> withStatus(List<Student> input, Student.Status status) {
        List<Student> result = new ArrayList<>();
        for(Student s : input) {
            if(s.getStatus() == status) {
                result.add(s);
            }
        }

        return result;
    }



    //public class NameChecker implements Checker<Student>
    public class NameChecker implements Predicate<Student>
    {
        private String prefix;
        public NameChecker(String prefix) {
            this.prefix = prefix;
        }
        @Override
        public boolean test(Student s) {
            return s.getName().startsWith(prefix);
        }
    }

    public class StatusChecker implements Checker<Student>
    {
        private Student.Status prefix;
        public StatusChecker(Student.Status prefix) {
            this.prefix = prefix;
        }
        @Override
        public boolean check(Student s) {
            return s.getStatus() == prefix;
        }
    }

    interface Checker<T> {
        public boolean check(T s);
    }




























}
