package vasco.tests.VirtualCalls;

public class VC2 {
	
    public void method(){ 
    	// do something
    }
    
    public static void callMethod(VC2 cls) {
        cls.method();
        vasco.tests.A.RefToRelation(cls,"vasco.tests.VirtualCalls.VC2");
        vasco.tests.A.RefToRelation(cls,"vasco.tests.VirtualCalls.SubClassVC2");
    }    
    public static void main(String[] args){
        callMethod(new VC2());
        callMethod(new SubClassVC2());
    }
}

class SubClassVC2 extends VC2 {
    public void method() { 
    	// do something
    }
}
