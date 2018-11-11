package ttl.advjava.dao;

import ttl.advjava.domain.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryStudentDao implements BaseDao {

    private static List<Student> students = new ArrayList<>();

    //private static int nextId;
    private static AtomicInteger nextId = new AtomicInteger(0);

    private static BaseDao dao = new InMemoryStudentDao();

    public static BaseDao getStudentDao() {
        return dao;
    }

    private InMemoryStudentDao() {

        Student s = new Student("Manoj", Student.Status.FULL_TIME);
        insert(s);

        s = new Student("Sammy", Student.Status.FULL_TIME);
        insert(s);

        s = new Student("Charlene", Student.Status.PART_TIME);
        insert(s);

        s = new Student("Joe", Student.Status.HIBERNATING);
        insert(s);

        s = new Student("Roberta", Student.Status.HIBERNATING);
        insert(s);

        s = new Student("Ana", Student.Status.HIBERNATING);
        insert(s);
    }

    @Override
    public Student insert(Student s) {

        s = new Student.StudentBuilder()
                .id(nextId.getAndIncrement())
                .name(s.getName())
                .status(s.getStatus())
                .build();

        students.add(s);
        return s;
    }

    @Override
    public List<Student> getAllStudents() {
        return students;
    }

    @Override
    public Student getStudent(int id) {
        for(Student s : students) {
            if(s.getId() == id) {
                return s;
            }
        }

       return null;
    }
}
