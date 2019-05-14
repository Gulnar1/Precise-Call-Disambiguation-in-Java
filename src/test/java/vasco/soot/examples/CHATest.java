package vasco.soot.examples;

import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.G;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootMethod;
import soot.Transform;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.CallGraphBuilder;
import soot.jimple.toolkits.callgraph.Targets;
import soot.options.CHAOptions;
import soot.util.queue.QueueReader;
import soot.MethodOrMethodContext;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.jimple.toolkits.pointer.DumbPointerAnalysis;

/** Builds an invoke graph using Class Hierarchy Analysis. */
public class CHATest extends SceneTransformer {
  private static final Logger logger = LoggerFactory.getLogger(CHATest.class);

  //public CHATest(Singletons.Global g) {
  //}

  public static CHATransformer v() {
    return G.v().soot_jimple_toolkits_callgraph_CHATransformer();
  }

  protected void internalTransform(String phaseName, Map<String, String> opts) {
    CHAOptions options = new CHAOptions(opts);
    CallGraphBuilder cg = options.apponly() ? new CallGraphBuilder() : new CallGraphBuilder(DumbPointerAnalysis.v());
    //CallGraphBuilder cg = new CallGraphBuilder(RefersToAnalysis.v());
    cg.build();
    System.out.println("---------------------------CFG---------------------------");
    SootMethod src = Scene.v().getMainClass().getMethodByName("main");
    CallGraph cg1 = Scene.v().getCallGraph();
    Iterator<MethodOrMethodContext> targets = new Targets(cg1.edgesOutOf(src));
    while (targets.hasNext()) {
        SootMethod tgt = (SootMethod)targets.next();
        System.out.println("\t" + src + " call " + tgt);
    }
    if (options.verbose()) {
    	System.out.println("------" + "Number of reachable methods: " + Scene.v().getReachableMethods().size());
        
    }
    else{
    	System.out.println("-----" + "No. of reachable methods: " + Scene.v().getReachableMethods().size());
    	ReachableMethods rm = cg.reachables();
    	//System.out.println("-------------------" + "No. of reachables: " + rm.size());
    	QueueReader<MethodOrMethodContext> all = rm.listener();
    	int i =0;
    	while(all.hasNext()){
    		i++;
    		MethodOrMethodContext m = all.next();
    		System.out.println("\tMethod " + i + ": " + m);
    	}
    }
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
			System.err.println("Usage: java CHATest [-cp CLASSPATH] MAIN_CLASS");
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
		CHATest cha = new CHATest();
		PackManager.v().getPack("wjtp").add(new Transform("wjtp.lv", cha));
		soot.Main.main(sootArgs);
	}
   public void testCHA() {
		// TODO: Compare output with an ideal (expected) output
		CHATest.main(new String[]{"vasco.tests.Test"});
	}
}
