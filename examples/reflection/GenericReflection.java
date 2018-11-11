package examples.reflection;

import java.lang.reflect.Type;

public class GenericReflection {

	public static void main(String[] args) throws Exception{
		String cName = "examples.reflection.MyClass";
		
		Class<?> klass = Class.forName(cName);
		
		Type [] genInts = klass.getGenericInterfaces();
		System.out.println("Generic Interfaces");
		for(Type type : genInts) {
			System.out.println(type);
		}
		
		Type [] ints = klass.getInterfaces();
		System.out.println("Interfaces");
		for(Type type : ints) {
			System.out.println(type);
		}
		
	}
}
