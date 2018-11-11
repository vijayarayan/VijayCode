package ttl.advjava.dao;

import ttl.advjava.domain.Student;

import java.util.ArrayList;
import java.util.List;

public class MySqlStudentDao implements BaseDao {

    private static List<Student> students = new ArrayList<>();

    private static int nextId;

    private static BaseDao dao = new MySqlStudentDao();

    public static BaseDao getStudentDao() {
        return dao;
    }

    private MySqlStudentDao() {

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
        System.out.println("Sql Dao");
        s = new Student.StudentBuilder()
                .id(nextId++)
                .name(s.getName())
                .status(s.getStatus())
                .build();

        students.add(s);
        return s;
    }

    @Override
    public List<Student> getAllStudents() {
        System.out.println("Sql Dao");
        return students;
    }

    @Override
    public Student getStudent(int id) {
        System.out.println("Sql Dao");
        for(Student s : students) {
            if(s.getId() == id) {
                return s;
            }
        }

       return null;
    }
}
