package ttl.advjava.domain;

import java.util.Objects;

public class Student implements Comparable<Student>, Runnable{

    public void run() {}

    public enum Status
    {
        FULL_TIME,
        PART_TIME,
        HIBERNATING
    }

    private int id;
    private String name;
    private Status status;

    public Student() {

    }

    public Student(String name, Status status) {
        this.name = name;
        this.status = status;
    }

    private Student(int id, String name, Status status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    /*
    public void setId(int id) {
        this.id = id;
    }
    */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return id == student.id &&
                Objects.equals(name, student.name) &&
                status == student.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, status);
    }

    public static class StudentBuilder
    {
        private int id;
        private String name;
        private Status status;

        public StudentBuilder id(int id) {
            this.id = id;
            return this;
        }

        public StudentBuilder name(String name) {
            this.name = name;
            return this;
        }

        public StudentBuilder status(Status status) {
            this.status = status;
            return this;
        }

        public Student build() {
            return new Student(id, name, status);
        }
    }

    @Override
    public int compareTo(Student other) {
        return id - other.id;
    }
}
