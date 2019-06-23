package vasco.tests.VirtualCalls;

class A {
    public void func(){ }
    public void func1(){ }
}

class B extends A {
    public void func1(){ }
    public void func2(){ }
   public void func3(){ }
}
class VC5 {
    static  void callfunc1(A obj){
        obj.func1();
        vasco.tests.A.RefToRelation(obj,"vasco.tests.VirtualCalls.A");
    }
    public static void main(String[] args){
        A o1=new A();
        o1.func();
        callfunc1(o1);
        A  o2 = new B();
        o2.func();
        callfunc1(o2);
        B o3 = new B();
        o3.func1();
        o3.func3();
        vasco.tests.A.RefToRelation(o3,"vasco.tests.VirtualCalls.B");
        o1=o3;
        vasco.tests.A.RefToRelation(o1,"vasco.tests.VirtualCalls.B");
        o1.func1();
        }
}


