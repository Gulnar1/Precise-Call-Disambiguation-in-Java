package vasco.soot.examples;
import java.util.Map;
import java.util.List;

import org.junit.Test;

import soot.Local;
import soot.PackManager;
import soot.SceneTransformer;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.toolkits.scalar.FlowSet;
import vasco.DataFlowSolution;

public class LiveVarTest extends SceneTransformer{

	private LiveVariableAnalysis analysis;

	@Override
	protected void internalTransform(String arg0, @SuppressWarnings("rawtypes") Map arg1) {
		analysis = new LiveVariableAnalysis();
		analysis.doAnalysis();
		DataFlowSolution<Unit,FlowSet<Local>> solution = analysis.getMeetOverValidPathsSolution();
		System.out.println("--------" + arg0 + "&" + arg1 + "-----------" );
		System.out.println("---------------************************----------------------");
		for (SootMethod sootMethod : analysis.getMethods()) {
			System.out.println(sootMethod);
			for (Unit unit : sootMethod.getActiveBody().getUnits()) {
				System.out.println("---------------IN & OUT---------------------------");
				System.out.println(unit);
				System.out.println("IN:  " + format(solution.getValueBefore(unit)));
				System.out.println("OUT: " + format(solution.getValueAfter(unit)));
			}
			System.out.println("----------------------------------------------------------------");
		}		
	}
	
	private static String format(FlowSet<Local> value) {
		if (value == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		List<Local> list = value.toList();
		for(Local entry : list ){
			sb.append(" ").append(entry).append(", ");
		}
		return sb.toString();
	}
	public LiveVariableAnalysis getAnalysis() {
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
			System.err.println("Usage: java LiveVarTest [-cp CLASSPATH] MAIN_CLASS");
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
		LiveVarTest lv = new LiveVarTest();
		PackManager.v().getPack("wjtp").add(new Transform("wjtp.lv", lv));
		soot.Main.main(sootArgs);
	}
	
	@Test
	public void testLiveVarAnalysis() {
		// TODO: Compare output with an ideal (expected) output
		LiveVarTest.main(new String[]{"vasco.tests.LiveVarTestCase"});
	}

}
