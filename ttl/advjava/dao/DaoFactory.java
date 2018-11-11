package ttl.advjava.dao;

public class DaoFactory {

    public static BaseDao createDao() {
        //private InMemoryStudentDao dao = new InMemoryStudentDao();
        BaseDao dao = InMemoryStudentDao.getStudentDao();
        //BaseDao dao = MySqlStudentDao.getStudentDao();
        return dao;
    }
}
