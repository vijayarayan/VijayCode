package ttl.advjava.app;

import ttl.advjava.domain.Student;
import ttl.advjava.service.StudentService;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class Controller {

    public static void main(String[] args) {
        Controller c = new Controller();
        //c.doSorting();
        c.byName();
    }
    public static void main2(String [] args) {

        Controller c = new Controller();

        c.allStudents();
        System.out.println("*******************************************");

        c.createStudent("Arvind", Student.Status.FULL_TIME);

        c.allStudents();
        System.out.println("*******************************************");
    }

    public void allStudents() {
        StudentService service = new StudentService();

        List<Student> students = service.getAll();

        for(Student s : students) {
            System.out.println(s);
        }
    }

    public void get(int id) {
        StudentService service = new StudentService();

        Student s = service.getStudent(id);

        System.out.println("Student is " + s);
    }

    public void createStudent(String name, Student.Status status) {
        StudentService service = new StudentService();

        Student s = new Student(name, status);

        s = service.addStudent(s);

        System.out.println("Student is " + s);
    }

    public void doSorting() {
        StudentService service = new StudentService();

        List<Student> students = service.getAll();

        Collections.sort(students);

        for(Student s : students) {
            System.out.println(s);
        }
    }

    public void byName() {
        StudentService service = new StudentService();

        List<Student> students = service.getAll();

        //NameComparator nc = new NameComparator();
        Comparator nc = new Comparator<Student>() {
            @Override
            public int compare(Student o1, Student o2) {
                return o1.getName().compareTo(o2.getName());
            }

        };

        Comparator<Student> nclambda = (Student o1, Student o2) ->
             {
                return o1.getName().compareTo(o2.getName());
             };

        Comparator<Student> nclambda2 = (o1, o2) ->
        {
            return o1.getName().compareTo(o2.getName());
        };

        Comparator<Student> nclambda3 = (o1, o2) -> o1.getName().compareTo(o2.getName());

        Comparator<Student> byStatus = (o1, o2) ->
        {
            int i = o1.getStatus().compareTo(o2.getStatus());
            if(i == 0) {
                i = o1.getId() - o2.getId();
            }

            return i;
        };

        Collections.sort(students, byStatus);

        for(Student s : students) {
            System.out.println(s);
        }

        students.forEach(new Consumer<Student>() {
            @Override
            public void accept(Student s) {
                System.out.println(s);
            }
        });

        students.forEach((Student s) -> { System.out.println(s); });

        students.forEach(s -> System.out.println(s));

        students.forEach(s -> prettyPrint(s));

        students.forEach(System.out::println);

        students.forEach(Controller::prettyPrint);
    }

    public static void prettyPrint(Student s) {
        //Lots of code
        System.out.println("Begin:" + s + ": End");
        //Lots of other code
    }

    /*
    class NameComparator implements Comparator<Student>
    {

        @Override
        public int compare(Student o1, Student o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }
    */
}
