package vasco.tests.VirtualCalls;

class J8PC3 {
    public void compute(){ /* do something*/ }
    public static void main(String[] args){ 
        Class cls = new Class();
        cls.method();
        vasco.tests.A.RefToRelation(cls,"vasco.tests.VirtualCalls.J8PC3");
        cls.compute();
        vasco.tests.A.RefToRelation(cls,"vasco.tests.VirtualCalls.Class");
    }
}
class Class extends J8PC3 implements DirectInterface, InterfacePC1, InterfacePC2 {
	public void method(){
        // do something
    }
}
interface InterfacePC1 {
    void compute();
    void method();
}
interface InterfacePC2 {
    void compute();
    void method();
}
interface DirectInterface extends Interface1, Interface2 { 
    void method();
}

