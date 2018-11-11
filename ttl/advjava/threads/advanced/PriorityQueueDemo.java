package ttl.advjava.threads.advanced;

import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class PriorityQueueDemo {
	public static void main(String[] args) {

		PriorityQueue<Student> pq = new PriorityQueue<>();

		Student s1 = new Student("Zebra", 20);
		Student s2 = new Student("Ana", 20);
		Student s3 = new Student("Dhario", 20);
		Student s4 = new Student("Yusuf", 20);
		
		pq.add(s1);
		pq.add(s2);
		pq.add(s3);
		pq.add(s4);
		
		while(pq.size() > 0) {
			Student s = pq.poll();
			System.out.println(s);
		}
	}
}

class Student implements Comparable<Student> {
	private int id;
	private static AtomicInteger nextId = new AtomicInteger(0);
	private int age;
	private String name;

	public Student(String name, int age) {
		this.name = name;
		this.age = age;
		this.id = nextId.incrementAndGet();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int compareTo(Student other) {
		return this.name.compareTo(other.name);
	}

	@Override
	public String toString() {
		return "Student [id=" + id + ", nextId=" + nextId + ", age=" + age
				+ ", name=" + name + "]";
	}

}