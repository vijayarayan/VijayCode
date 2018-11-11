package ttl.advjava.cc;

public class CustomStatistics {
	public int maxX = Integer.MIN_VALUE;
	public int maxY = Integer.MIN_VALUE;
	public int minX = Integer.MAX_VALUE;
	public int minY = Integer.MAX_VALUE;
	
	public int count = 0;
	
	/**
	 * Add data to this CustomStatics object
	 * @param d
	 */
	public void accumulate(Data d) {
		this.maxX = Math.max(this.maxX,  d.xValue);
		this.maxY = Math.max(this.maxY,  d.yValue);
		
		this.minX = Math.min(this.minX, d.xValue);
		this.minY = Math.min(this.minY, d.yValue);
		
		this.count++;
	}
	
	/**
	 * Combine this CustomStatistics object with c2.
	 * Only done if using a parallel Stream
	 * @param c2
	 */
	public void combine(CustomStatistics c2) {
		this.maxX = Math.max(this.maxX, c2.maxX);
		this.maxY = Math.max(this.minY, c2.maxY);

		this.minX = Math.min(this.minX, c2.minX);
		this.minY = Math.min(this.minY, c2.minY);
		
		this.count += c2.count;
	}

	/**
	 * A final opportunity to massage the result.
	 * Only called with using the 4 arg Collectors call
	 * Collectors.of(supplier, accum, combine, finish)
	 * and, you would have to change the accum, combine etc.
	 * methods to be static, with two arguments.
	 * @param almostFinal
	 * @return
	 */
	public CustomStatistics finisher(CustomStatistics almostFinal) {
		//Might calculate averages and deviations here and add them to the result
		
		return almostFinal;
	}

	@Override
	public String toString() {
		return "CustomStatistics [maxX=" + maxX + ", maxY=" + maxY + ", minX=" + minX + ", minY=" + minY + ", count="
				+ count + "]";
	}
	
}
