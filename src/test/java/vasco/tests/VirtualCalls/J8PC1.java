package vasco.tests.VirtualCalls;

class J8PC1 {
    public void method(){
        // do something
    }
    public static void main(String[] args){ 
        Interface1 i = new SubClass1();
        vasco.tests.A.RefToRelation(i,"vasco.tests.VirtualCalls.Interface1");
        i.method();
        vasco.tests.A.RefToRelation(i,"vasco.tests.VirtualCalls.SubClass1");
    }
}
interface Interface1 { 
     void method() ;
}
class SubClass1 extends J8PC1 implements Interface1 { 
	
}

