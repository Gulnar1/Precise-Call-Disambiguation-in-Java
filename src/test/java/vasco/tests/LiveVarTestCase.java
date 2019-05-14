package vasco.tests;

public class LiveVarTestCase {
	static int a, b, c, d, num;
	static class A {
		void foo() { 
			c = 5;
			num = d;
		}
		void bar() { 
			a = 2;
			num = d;
		}		
	}
	public static void main(String[] args) {
		b = 1;
		A a1 = new A();
		a1.foo();
		
		A a2 = new A();
		a2.bar();
		num = a;

	}

}
