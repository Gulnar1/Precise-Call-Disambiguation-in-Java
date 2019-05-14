package vasco.tests;

public class Test1 extends Test{
	int n, a, b, c;
	public void set1(){
		a = five();
		b = a;
		c = b;
		n = c;
	}
	public static void main(String[] args) {
		Test ob;
		Test obj = new Test();
		//obj.set();
		Test1 obj1 =  new Test1();
		//obj1.set();
		int a=5;
		if(a>0)
			ob = obj;
		else
			ob = obj1;
		ob.set();
	}
	
	public static int five(){
		int i =5;
		return i;
	}
}
