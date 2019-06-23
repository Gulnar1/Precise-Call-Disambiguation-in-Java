package vasco.tests.VirtualCalls;

interface InterfaceJ8PC1 {
    void func();
}
interface InterfaceJ8PC2 extends InterfaceJ8PC1{
    void method();
}
class AJ8PC implements InterfaceJ8PC1 {
   public void  func() { }
}
class BJ8PC implements InterfaceJ8PC2 {
   public void  func() { }
   public void method() { }
}
class CJ8PC extends AJ8PC {
   public InterfaceJ8PC1 f;
   public void func() { }
}
class J8PC4 {
   public static void main(String[] args){ 
               InterfaceJ8PC1[] types = { new AJ8PC(), new BJ8PC(), new CJ8PC() };
       InterfaceJ8PC1 i1;
           for(int p=0; p<=2; p++){
               i1 = types[p];
               i1.func(); 
               vasco.tests.A.RefToRelation(i1,"vasco.tests.VirtualCalls.AJ8PC");
           }
           InterfaceJ8PC2 i2 = new BJ8PC();
           i2.method();
           vasco.tests.A.RefToRelation(i2,"vasco.tests.VirtualCalls.BJ8PC");
           int a = 4;
           if(a%2 == 0)
               i1 = new AJ8PC();
           else 
               i1 = new BJ8PC();
           vasco.tests.A.RefToRelation(i1,"vasco.tests.VirtualCalls.AJ8PC");
           CJ8PC obj = new CJ8PC();
           obj.f = i1;
           obj.f.func();
           vasco.tests.A.RefToRelation(obj.f,"vasco.tests.VirtualCalls.AJ8PC");
       } }

