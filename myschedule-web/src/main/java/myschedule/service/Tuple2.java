package myschedule.service;

/**
 * A tuple that hold any two objects.
 * 
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 * @param <A> item 1
 * @param <B> item 2
 */
public class Tuple2<A, B> {
	private A item1;
	private B item2;
	
	public Tuple2(A item1, B item2) {
		this.item1 = item1;
		this.item2 = item2;
	}
	
	public A getItem1() {
		return item1;
	}
	
	public B getItem2() {
		return item2;
	}
}
