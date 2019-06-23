package vasco.tests.VirtualCalls;

interface Interface4 {
    void method();
}
public class VC4 implements Interface4 {
	    public static Interface4[] types = new Interface4[]{new VC4(), new ClassImpl4()};
	    public void method(){ }
	       public static void main(String[] args){
	    	for(int i=0; i<2; i++){
	         Interface4 in = types[0];
	         in.method();
	         vasco.tests.A.RefToRelation(in,"vasco.tests.VirtualCalls.VC4");
	         vasco.tests.A.RefToRelation(in,"vasco.tests.VirtualCalls.ClassImpl4");
	    	}
	    }
	}
	class ClassImpl4 implements Interface4 {
	    public void method(){ }
	}
