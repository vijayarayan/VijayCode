package ttl.advjava.service;

import ttl.advjava.dao.BaseDao;
import ttl.advjava.dao.DaoFactory;
import ttl.advjava.dao.InMemoryStudentDao;
import ttl.advjava.dao.MySqlStudentDao;
import ttl.advjava.domain.Student;

import java.util.List;

public class StudentService {

    //private InMemoryStudentDao dao = new InMemoryStudentDao();
    //private BaseDao dao = InMemoryStudentDao.getStudentDao();
    private BaseDao dao = DaoFactory.createDao();

    public Student addStudent(Student s) {
       return dao.insert(s);
    }

    public List<Student> getAll() {
       return dao.getAllStudents();
    }

    public Student getStudent(int id) {
        return dao.getStudent(id);
    }
}
