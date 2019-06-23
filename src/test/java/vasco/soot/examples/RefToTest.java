package vasco.soot.examples;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import soot.Local;
import soot.PackManager;
import soot.RefType;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootField;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.Value;
import soot.jimple.FieldRef;
import soot.toolkits.scalar.FlowSet;
import vasco.DSG;
import vasco.DataFlowSolution;
public class RefToTest extends SceneTransformer{
	private RefToAnalysis analysis;

	@Override
	protected void internalTransform(String arg0, @SuppressWarnings("rawtypes") Map arg1) {
		analysis = new RefToAnalysis();
		analysis.doAnalysis();
		ContextTransitionGraph CTG = new ContextTransitionGraph();
		int currentState = 0;
		DataFlowSolution<Unit, Set<Map<Value,RefType>>> solution = analysis.getMeetOverValidPathsSolution();
		//System.out.println("--------" + arg0 + "&" + arg1 + "-----------" );
		SootMethod sootMethod = Scene.v().getMainMethod();
		//System.out.println("------------------------------------ANALYSIS METHODS--------------------------------");
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~MAIN METHOD NAME: " +sootMethod + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			for (Unit unit : sootMethod.getActiveBody().getUnits()) {
				System.out.print("-----------------------IN & OUT : ");
				System.out.println(unit + "----------------------------");
				Set<Map<Value,RefType>> in = solution.getValueBefore(unit);
				System.out.println("IN:  " + format(in));
				Set<Map<Value,RefType>> out = solution.getValueAfter(unit);
				System.out.println("OUT: " + format(out));
				currentState = createCTG(in, out, CTG, unit, currentState);
			}
			System.out.println("Total no. of statements processed = " + analysis.statements);
			System.out.println("*****************************CALLEE-CONTEXT TRANSITION  GRAPH*****************************");
			CTG.printCTG();	
			System.out.println("---------------------------------------------------------------------------------------------");
	}
	int createCTG(Set<Map<Value,RefType>> in , Set<Map<Value,RefType>> out, ContextTransitionGraph CTG, Unit node, int currentState){
	//System.out.print("------Node -> " + node + ":");
	if (in.equals(analysis.topValue()) && out.equals(analysis.topValue()) ){
		//System.out.println("	Both Null------------");
		CTG.addEdgeLink(new Integer(0), node, new Integer(0));
	}
	
	else if(out.equals(in)){
		//System.out.println("	Both equal------------");
		for(Map<Value,RefType> hm : in){
			int node_no = CTG.nodes.get(hm);
			CTG.addEdgeLink(node_no, node, node_no);
		}
	}
	
	else{
		//System.out.println("	Both Not Null------------");
		if(in.equals(analysis.topValue())){
			//System.out.println("	IN Null------------");
			for(Map<Value,RefType> hm : out){
				currentState++;
				CTG.nodes.put(hm, currentState);
				CTG.addEdgeLink(new Integer(0), node, currentState);
			}
		}
		
		else{
			//System.out.println("	None Null------------");
			Iterator<Map<Value,RefType>> value = in.iterator(); 
			for(Map<Value,RefType> hm : out){
				Map<Value, RefType> inhm = value.next(); 
				int node_no = CTG.nodes.get(inhm);
				currentState++;
				CTG.nodes.put(hm, currentState);
				CTG.addEdgeLink(node_no, node, currentState);
			}
		}	
	}
	//CTG.printCTG();
	return currentState;
}
	
	private static String format(Set<Map<Value,RefType>> d) {
		if (d == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for( Map<Value,RefType> hm : d){
			sb.append("Refers-to Relation: ");
			for(Value val : hm.keySet()){
				sb.append(" ").append(val).append("->");
				RefType rf = hm.get(val);
				sb.append(" ").append(rf).append(",");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public RefToAnalysis getAnalysis() {
		return analysis;
	}
	
	public static void main(String[] args) {
		String classPath = System.getProperty("java.class.path");
		String mainClass = null;
		/* ------------------- OPTIONS ---------------------- */
		try {
			int i=0;
			while(true){
				if (args[i].equals("-cp")) {
					classPath = args[i+1];
					i += 2;
				} else {
					mainClass = args[i];
					i++;
					break;
				}
			}
			if (i != args.length || mainClass == null)
				throw new Exception();
		} catch (Exception e) {
			System.err.println("Usage: java RefToTest [-cp CLASSPATH] MAIN_CLASS");
			System.exit(1);
		}	
		String[] sootArgs = {
				"-cp", classPath, "-pp", 
				"-w", "-app", 
				"-keep-line-number",
				"-keep-bytecode-offset",
				"-p", "jb", "use-original-names",
				"-p", "cg", "implicit-entry:false",
				"-p", "cg.spark", "enabled",
				"-p", "cg.spark", "simulate-natives",
				"-p", "cg", "safe-forname",
				"-p", "cg", "safe-newinstance",
				"-main-class", mainClass,
				"-f", "none", mainClass 
		};
		RefToTest rft = new RefToTest();
		PackManager.v().getPack("wjtp").add(new Transform("wjtp.lv", rft));
		soot.Main.main(sootArgs);
	}	
	@Test
	public void testRefToAnalysis() {
		// TODO: Compare output with an ideal (expected) output
		RefToTest.main(new String[]{"vasco.tests.Test"});
	}

}