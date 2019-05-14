package vasco.tests;

public class Test {
	int n, a, b, c;
	public void set(){
		a = five();
		b = a;
		c = b;
		n = c;
	}
	public static void main(String[] args) {
		Test obj = new Test();
		obj.a = 3;
		obj.b = obj.a * 2;
		//obj.set();
		Test obj1;
		obj1 = obj;
		obj.b = obj.a + obj.n;   
		obj.c = obj.b;
	}
	
	public static int five(){
		int i =5;
		return i;
	}

}
