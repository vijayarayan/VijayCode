package ttl.advjava.threads.visibility;

public class SimpleClass {
	
	private int intVar;
	
	public double doStuff(double arg1) {
		int j = (int)arg1;
		j = j * j;
		
		return j;
	}

}
