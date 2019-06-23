package vasco.tests.VirtualCalls;

interface Interface3 {
    void method();
}

public class VC3 {

    public void method(){ }
    
    public static void callOnInterface(Interface3 i){
        i.method();
        vasco.tests.A.RefToRelation(i,"vasco.tests.VirtualCalls.Interface3");
        vasco.tests.A.RefToRelation(i,"vasco.tests.VirtualCalls.ClassImpl3");
    }
    
    public static void main(String[] args){
        callOnInterface(new ClassImpl3());
    }
}
class ClassImpl3 implements Interface3 {
    public void method(){ }
}
