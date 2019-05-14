package vasco.tests;
public class LiveVarTestCase2 {
	int n, a, b, c;
	public void set(){
		a = 5;
		b = a;
		c = b;
		n = c;
	}
	public static void main(String[] args) {
		LiveVarTestCase2 obj = new LiveVarTestCase2();
		obj.a = 3;
		obj.b = obj.a * 2;
		obj.set();
		obj.c = obj.a + obj.n;   
		obj.n = obj.c;
	}
	
	public static int five(){
		int i =5;
		return i;
	}
}
