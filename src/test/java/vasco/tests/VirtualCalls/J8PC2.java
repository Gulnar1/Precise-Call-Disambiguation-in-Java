package vasco.tests.VirtualCalls;

public class J8PC2 {
    public void method(){
        // do something
    }
        public static void main(String[] args){ 
        J8PC2 superClass = new SubClass2();
        vasco.tests.A.RefToRelation(superClass,"vasco.tests.VirtualCalls.J8PC2");
        superClass.method();
        vasco.tests.A.RefToRelation(superClass,"vasco.tests.VirtualCalls.SubClass2");
    }
}
interface Interface2 { 
   void method() ;
}
class SubClass2 extends J8PC2 implements Interface2 {
}

