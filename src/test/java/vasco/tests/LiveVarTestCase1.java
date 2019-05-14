package vasco.tests;

public class LiveVarTestCase1 {
	static int n;
	public static void main(String[] args) {
		int a,b,c;
		a = 3;
		n = 5;
		b = a * 2;
		a = five();
		c = b + n;   
		b = c;
	}
	
	public static int five(){
		n =2;
		n = n+3;
		return n;
	}

}
