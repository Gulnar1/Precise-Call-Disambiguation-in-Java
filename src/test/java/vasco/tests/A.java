package vasco.tests;

public class A {
	
	public void m(){
		System.out.println("Inside A");
	}
	
	public static void main(String[] args) {
		
		int i =2;
		A a = new A();
		B b = new B();
		C c = new C();
		if(i>0)
			a = b;
		else
			a = c;
		i=0;
		a.m();
		//b.m();
		//c.m();*/
	}
}

class B extends A{
	/*public void m(){
		System.out.println("Inside B");
	}*/
}

class C extends A{
	/*public void m(){
		System.out.println("Inside C");
	}:*/
}
