package vasco.tests.VirtualCalls;

public class SimpleCast {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) 
          m(new Target());
        else 
          m(new SimpleCast());
        System.out.println();
    }
    static void m(Object o) {
    	vasco.tests.A.RefToRelation(o,"vasco.tests.VirtualCalls.SimpleCast");
        Target b = (Target) o;
        vasco.tests.A.RefToRelation(b,"vasco.tests.VirtualCalls.SimpleCast");
        b.toString();
        vasco.tests.A.RefToRelation(b,"vasco.tests.VirtualCalls.Target");
    }
    public String toString() { return "Demo"; }
}
class Target {
  public String toString() { return "Target"; }
}
