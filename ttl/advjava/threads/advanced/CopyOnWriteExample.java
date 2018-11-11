package ttl.advjava.threads.advanced;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CopyOnWriteExample {

	public static void main(String[] args) {
		List<String> first = new ArrayList<>();
		doStuffWithList(first);

		System.out.println("After doing stuff, list is " + first);

		first = new CopyOnWriteArrayList<>();

		doStuffWithList(first);

		System.out.println("After doing stuff, cow list is " + first);
	}

	public static void doStuffWithList(List<String> list) {

		try {
			list.add("one");
			list.add("two");
			list.add("three");

			for (String s : list) {
				list.add("five");
			}
		} catch (Exception ex) {
			System.out.println(ex);
		}

	}

}
