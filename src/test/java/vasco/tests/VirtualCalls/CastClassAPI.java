package vasco.tests.VirtualCalls;

class CastClassAPI{ 
    public static void main(String[] args) throws Exception {
        if (args.length == 0) 
          m(new Target1());
        else 
          m(new CastClassAPI());
    }
    static void m(Object o) {
      vasco.tests.A.RefToRelation(o,"vasco.tests.VirtualCalls.CastClassAPI");
      if (o instanceof Target1)
        o.toString();
      vasco.tests.A.RefToRelation(o,"vasco.tests.VirtualCalls.Target1");
    }
    public String toString() { return "Demo"; }
}
class Target1 {
  public String toString() { return "Target"; }
}


