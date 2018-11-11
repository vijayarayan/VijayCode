package examples.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ClassPrinter {

	public static void main(String[] args) {
		printClassInfo(String.class);
	}

	public static void printClassInfo(Class<?> clz) {

		Field[] flds = clz.getDeclaredFields();

		System.out.println("\nVariables...");
		for (int k = 0; k < flds.length; k++) {
			String name = flds[k].getName();
			Class<?> type = flds[k].getType();
			int modifiers = flds[k].getModifiers();
			System.out.print(Modifier.toString(modifiers) + " ");
			if (type.isArray())
				System.out.print(type.getComponentType().getName() + "[] ");
			else
				System.out.print(type.getName() + " ");
			System.out.println(name);
		}

		Constructor<?>[] cnsts = clz.getDeclaredConstructors();
		System.out.println("\nConstructors...");
		for (int k = 0; k < cnsts.length; k++) {
			String name = cnsts[k].getName();
			Class<?> params[] = cnsts[k].getParameterTypes();
			int modifiers = cnsts[k].getModifiers();
			String mstr = Modifier.toString(modifiers);
			System.out
					.print(mstr + (mstr.length() > 0 ? " " : "") + name + "(");
			for (int i = 0; params != null && i < params.length; i++) {
				if (i != 0)
					System.out.print(", ");

				Class<?> type = params[i];
				if (type.isArray())
					System.out.print(type.getComponentType().getName() + "[]");
				else
					System.out.print(type.getName());
			}
			System.out.println(")");
		}

		Method[] mms = clz.getDeclaredMethods();
		System.out.println("\nMethods...");
		for (int i = 0; i < mms.length; i++) {
			int md = mms[i].getModifiers();
			Class<?> retType = mms[i].getReturnType();
			System.out.print(Modifier.toString(md) + " ");
			if (retType.isArray())
				System.out.print(retType.getComponentType().getName() + "[] ");
			else
				System.out.print(retType.getName() + " ");
			System.out.print(mms[i].getName());
			Class<?> cx[] = mms[i].getParameterTypes();
			System.out.print("( ");
			if (cx.length > 0) {
				for (int j = 0; j < cx.length; j++) {
					if (cx[j].isArray())
						System.out.print(cx[j].getComponentType().getName()
								+ "[]");
					else
						System.out.print(cx[j].getName());

					if (j < (cx.length - 1))
						System.out.print(", ");
				}
			}
			System.out.print(") ");
			System.out.println("{ }");
		}
	}

}
