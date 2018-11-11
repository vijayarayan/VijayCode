package ttl.advjava.dao;

import ttl.advjava.domain.Student;

import java.util.List;

public interface BaseDao {
    Student insert(Student s);

    List<Student> getAllStudents();

    Student getStudent(int id);
}
