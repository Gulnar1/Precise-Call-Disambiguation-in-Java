package vasco.tests.VirtualCalls;

public class VC1 {

    public void target(){ }
    
    public static void main(String[] args){
    	
        VC1 cls = new VC1();
        vasco.tests.A.RefToRelation(cls,"vasco.tests.VirtualCalls.VC1");
        vasco.tests.A.RefToRelation(cls,"vasco.tests.VirtualCalls.VC2");
        cls.target();
        
    }
}