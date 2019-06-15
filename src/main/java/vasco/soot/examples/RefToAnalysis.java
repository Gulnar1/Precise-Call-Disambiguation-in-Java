package vasco.soot.examples;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.EquivalentValue;
import soot.Local;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.RefType;
import soot.SootMethod;
import soot.Unit;
import soot.IntType;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AnyNewExpr;
import soot.jimple.AssignStmt;
import soot.jimple.DefinitionStmt;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.NewExpr;
import soot.jimple.ReturnStmt;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.internal.JimpleLocal;
import soot.toolkits.scalar.*;
import vasco.BackwardInterProceduralAnalysis;
import vasco.CallSite;
import vasco.Context;
import vasco.InterProceduralAnalysis;
import vasco.ProgramRepresentation;
import vasco.soot.DefaultJimpleRepresentation;
import vasco.ForwardInterProceduralAnalysis;

  public class RefToAnalysis extends InterProceduralAnalysis< SootMethod, Unit, Set<Map<Value, RefType>> > {
  private static final Local RETURN_LOCAL = new JimpleLocal("@return", IntType.v()); 
  //public ContextTransitionGraph CTG;
 
  public RefToAnalysis() {
	  super(false);
	  verbose = false;
  }

public Set<Map<Value, RefType>> normalFlowFunction(
		Context<SootMethod, Unit, Set<Map<Value, RefType>>> context,
		Unit node, Set<Map<Value, RefType>> inValue) {
	
	//System.out.print("Inside NormalFLowFunction : ");
	Set<Map<Value, RefType>> outValue = new HashSet<Map<Value, RefType>>();
	//outValue.addAll(inValue);
	for(Map<Value, RefType> hm : inValue){
	    Map<Value, RefType> mp = new HashMap<Value, RefType>();
	    mp.putAll(hm);
        outValue.add(mp);
	}
	if (node instanceof DefinitionStmt) {
			  DefinitionStmt ds = (DefinitionStmt) node;
		        Value lhs = ds.getLeftOp();
		        Value rhs = ds.getRightOp();
		            if (rhs instanceof NewExpr) {
		            	//System.out.print(" New Expression : ");
		            	RefType rf = ((NewExpr) rhs).getBaseType();
		            	if(outValue.isEmpty()){
		            		if(verbose){
		            			//System.out.print(" outvalue empty!");
		            		}
		            		Map<Value,RefType> hm = new HashMap<Value,RefType>();
		            		hm.put(lhs,rf);
		            		outValue.add(hm);
		            	}
		            	else{
		            		//System.out.print(" outvalue not empty!");
		            		for(Map<Value,RefType> hm : outValue){
		            			hm.put(lhs,rf);
		            		}	
		            	}
		            }
		            else{
		            	//System.out.print(" Assignment Statement!");
		            	for(Map<Value,RefType> hm : outValue){
		        			if(hm.get(rhs) != null){
		        				hm.put(lhs, hm.get(rhs));
		        			}
		        		}	            		
		            }
		} 
	//System.out.println("");
	return outValue;
}

public Set<Map<Value, RefType>> callEntryFlowFunction(
		Context<SootMethod, Unit, Set<Map<Value, RefType>>> context,
		SootMethod targetMethod, Unit unit, Set<Map<Value, RefType>> inValue) {
	//System.out.println("CallEntryFlowFunction");
	Set<Map<Value, RefType>> entryValue = topValue();
	InvokeExpr ie = ((Stmt) unit).getInvokeExpr();
	
	SootClass a = Scene.v().getSootClass("vasco.tests.A");
	SootMethod m = a.getMethod("void RefToRelation(java.lang.Object,java.lang.Object)");
	if(targetMethod == m){
		System.out.println("Ignoring REF-TO-RELATION Function Call");
		Value objref = ie.getArg(0);
		Value classname = ie.getArg(1);
		String s = classname.toString();
		String name = s.substring(1, s.length()-1);
		//System.out.println("String = " + name);
		RefType rf = RefType.v(name);
		//System.out.println("Object Reference = " + objref);
		//System.out.println("Class = " + rf);
		boolean hit = false;
		for(Map<Value,RefType> hm : inValue){
			//System.out.println("Value:" + hm.get(objref));
			if(hm.get(objref) == rf){
				hit = true;
			}
		}
		if(hit)
			System.out.println("		SUCCESS!!!!!!	");
		else
			System.out.println("		FAILURE!!!!!!	");
	}
	
	else{
	for (int i = 0; i < ie.getArgCount(); i++) {
		Value arg = ie.getArg(i);
		Local param = targetMethod.getActiveBody().getParameterLocal(i);
		for(Map<Value,RefType> hm : inValue){
			if(hm.get(arg) != null){
				Map<Value,RefType> temp = new HashMap<Value,RefType>();
				temp.put(param, hm.get(arg));
				entryValue.add(temp);
			}
		}
	}
	if (ie instanceof InstanceInvokeExpr) {
		Value instance = ((InstanceInvokeExpr) ie).getBase();
		Local thisLocal = targetMethod.getActiveBody().getThisLocal();
		for(Map<Value,RefType> hm : inValue){
			if(hm.get(instance) != null){
				Map<Value,RefType> temp = new HashMap<Value,RefType>();
				temp.put(thisLocal, hm.get(instance));
				entryValue.add(temp);
			}
		}
	}
	}
	return entryValue;
}


public Set<Map<Value, RefType>> callExitFlowFunction(
		Context<SootMethod, Unit, Set<Map<Value, RefType>>> context,
		SootMethod targetMethod, Unit unit, Set<Map<Value, RefType>> exitValue) {
	//System.out.println("CallExitFlowFunction");
	Set<Map<Value, RefType>>  afterCallValue = topValue();
	if (unit instanceof AssignStmt) {
		Value lhsOp = ((AssignStmt) unit).getLeftOp();
		for(Map<Value,RefType> hm : exitValue){
			if(hm.get(RETURN_LOCAL) != null){
				Map<Value,RefType> temp = new HashMap<Value,RefType>();
				temp.put(lhsOp, hm.get(RETURN_LOCAL));
				afterCallValue.add(temp);
			}
		}
	}
	return afterCallValue;
}


public Set<Map<Value, RefType>> callLocalFlowFunction(
		Context<SootMethod, Unit, Set<Map<Value, RefType>>> context,
		Unit node, Set<Map<Value, RefType>> inValue) {
	//System.out.println("CallLocalFlowFunction");
	Set<Map<Value, RefType>> afterCallValue = new HashSet<Map<Value, RefType>>();
	afterCallValue.addAll(inValue);
	return afterCallValue;
}

@Override
public Set<Map<Value, RefType>> meet(Set<Map<Value, RefType>> op1,
		Set<Map<Value, RefType>> op2) {
	/*if(verbose){
		System.out.print("Inside MEET! ");
		System.out.print(" Operand1 :" + op1);
		System.out.println("  Operand2 :" + op2);
	}*/
	Set<Map<Value, RefType>> result = new HashSet<Map<Value, RefType>>();
	for(Map<Value, RefType> hm : op1){
	    Map<Value, RefType> mp = new HashMap<Value, RefType>();
	    mp.putAll(hm);
        result.add(mp);
	}
	for(Map<Value, RefType> hm : op2){
	    Map<Value, RefType> mp = new HashMap<Value, RefType>();
	    mp.putAll(hm);
        result.add(mp);
	}
	return result;
}

@Override
public Set<Map<Value, RefType>> boundaryValue(SootMethod entryPoint) {
	Set<Map<Value, RefType>> tmp = new HashSet<Map<Value, RefType>>();
	return tmp;
}

@Override
public Set<Map<Value, RefType>> copy(Set<Map<Value, RefType>> src) {
	Set<Map<Value, RefType>> result = new HashSet<Map<Value, RefType>>();
	for(Map<Value, RefType> hm : src){
	    Map<Value, RefType> mp = new HashMap<Value, RefType>();
	    mp.putAll(hm);
        result.add(mp);
	}
	return result;
}

@Override
public ProgramRepresentation<SootMethod, Unit> programRepresentation() {
	return DefaultJimpleRepresentation.v();
}

@Override
public Set<Map<Value, RefType>> topValue() {
	Set<Map<Value, RefType>> tmp = new HashSet<Map<Value, RefType>>();
	return tmp;
}
@Override
public void doAnalysis() {

	//CTG = new ContextTransitionGraph();
	//int currentState = 0;
	// Initial contexts
	for (SootMethod method : programRepresentation().getEntryPoints()) {
		initContext(method, boundaryValue(method));
	}

	// Perform work-list based analysis
	while (!worklist.isEmpty()) {
		// Get the newest context on the work-list
		Context<SootMethod,Unit,Set<Map<Value, RefType>>> currentContext = worklist.last();
		
		// If this context has no more nodes to analyze, then take it out of the work-list
		if (currentContext.getWorkList().isEmpty()) {
			currentContext.markAnalysed();
			worklist.remove(currentContext);
			continue;
		}


		// Remove the next node to process from the context's work-list
		Unit node = currentContext.getWorkList().pollFirst();

		if (node != null) {
			//System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~NODE : " +  node + "~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			// Compute the IN data flow value (only for non-entry units).
			List<Unit> predecessors = currentContext.getControlFlowGraph().getPredsOf(node);
			if (predecessors.size() != 0) {
				// Initialise to the TOP value
				Set<Map<Value, RefType>> in = topValue();
				// Merge OUT values of all predecessors
				for (Unit pred : predecessors) {
					Set<Map<Value, RefType>> predOut = currentContext.getValueAfter(pred);
					in = meet(in, predOut);
				}					
				// Set the IN value at the node to the result
				currentContext.setValueBefore(node, in);
			}
			
			// Store the value of OUT before the flow function is processed.
			Set<Map<Value, RefType>> prevOut = currentContext.getValueAfter(node);
			
			// Get the value of IN 
			Set<Map<Value, RefType>> in = currentContext.getValueBefore(node);
			
			if (verbose) {
				System.out.println("IN = " + in);
				System.err.println(node);
			}
			
			// Now to compute the OUT value
			Set<Map<Value, RefType>> out = topValue();
			
			// Handle flow functions depending on whether this is a call statement or not
			if (programRepresentation().isCall(node)) {
				
				out = topValue();
				boolean hit = false;
				if (!programRepresentation().resolveTargets(currentContext.getMethod(), node).isEmpty()) {
                
				for (SootMethod targetMethod : programRepresentation().resolveTargets(currentContext.getMethod(), node)) {
					
					SootClass a = Scene.v().getSootClass("vasco.tests.A");
					SootMethod m = a.getMethod("void RefToRelation(java.lang.Object,java.lang.Object)");
					if(targetMethod == m){
						System.out.println();
						System.out.println("	NODE -> " + node);
						out = callEntryFlowFunction(currentContext, targetMethod, node, in);
						break;
					}
					Set<Map<Value, RefType>> entryValue = callEntryFlowFunction(currentContext, targetMethod, node, in);
					
					CallSite<SootMethod,Unit,Set<Map<Value, RefType>>> callSite = new CallSite<SootMethod,Unit,Set<Map<Value, RefType>>>(currentContext, node);
					
					// Check if the called method has a context associated with this entry flow:
					Context<SootMethod,Unit,Set<Map<Value, RefType>>> targetContext = getContext(targetMethod, entryValue);
					// If not, then set 'targetContext' to a new context with the given entry flow.
					if (targetContext == null) {
						targetContext = initContext(targetMethod, entryValue);
						if (verbose) {
							System.out.println("[NEW] X" + currentContext + " -> X" + targetContext + " " + targetMethod + " ");
							System.out.println("ENTRY(X" + targetContext + ") = " + entryValue);
						}

					}

					// Store the transition from the calling context and site to the called context.
					contextTransitions.addTransition(callSite, targetContext);

					// Check if the target context has been analysed (surely not if it is just newly made):
					if (targetContext.isAnalysed()) {
						hit = true;
						Set<Map<Value, RefType>> exitValue = targetContext.getExitValue();
						if (verbose) {
							System.out.println("[HIT] X" + currentContext + " -> X" + targetContext + " " + targetMethod + " ");
								System.out.println("EXIT(X" + targetContext + ") = " + exitValue);
						}
						Set<Map<Value, RefType>> returnedValue = callExitFlowFunction(currentContext, targetMethod, node, exitValue);
						out = meet(out, returnedValue);
					} 
				}

				// If there was at least one hit, continue propagation
				if (hit) {
					Set<Map<Value, RefType>> localValue = callLocalFlowFunction(currentContext, node, in); 
					out = meet(out, localValue);
					}
					else {
						out = callLocalFlowFunction(currentContext, node, in);
					}
				}
				else
				{
					// handle phantom method
					out = callLocalFlowFunction(currentContext, node, in);
				}
			} //end of call node 
			
			else {
				out = normalFlowFunction(currentContext, node, in);
			}
			if (verbose) {
				System.out.println("OUT = " + out);
			}

			// Merge with previous OUT to force monotonicity (harmless if flow functions are monotinic)
			out = meet(out, prevOut);
			// Set the OUT value
			currentContext.setValueAfter(node, out);
			//System.out.println("---------------------------------------");
			// If OUT has changed...
			if (out.equals(prevOut) == false) {
				// Then add successors to the work-list.
				for (Unit successor : currentContext.getControlFlowGraph().getSuccsOf(node)) {
					currentContext.getWorkList().add(successor);
				}
			}
			
			out = null;
			in = null;
			System.gc();
			
			// If the unit is in TAILS, then we have at least one
			// path to the end of the method, so add the NULL unit
			if (currentContext.getControlFlowGraph().getTails().contains(node)) {
				currentContext.getWorkList().add(null);
			}
		} else {
			// NULL unit, which means the end of the method.
			assert (currentContext.getWorkList().isEmpty());

			// Exit value is the merge of the OUTs of the tail nodes.
			Set<Map<Value, RefType>> exitValue = topValue();
			for (Unit tailNode : currentContext.getControlFlowGraph().getTails()) {
				Set<Map<Value, RefType>> tailOut = currentContext.getValueAfter(tailNode);
				exitValue = meet(exitValue, tailOut);
			}
			
			// Set the exit value of the context.
			currentContext.setExitValue(exitValue);
			
			// Mark this context as analysed at least once.
			currentContext.markAnalysed();

			// Add callers to work-list, if any
			Set<CallSite<SootMethod,Unit,Set<Map<Value, RefType>>>> callers =  contextTransitions.getCallers(currentContext);
			if (callers != null) {
				for (CallSite<SootMethod,Unit,Set<Map<Value, RefType>>> callSite : callers) {
					// Extract the calling context and node from the caller site.
					Context<SootMethod,Unit,Set<Map<Value, RefType>>> callingContext = callSite.getCallingContext();
					Unit callNode = callSite.getCallNode();
					// Add the calling unit to the calling context's node work-list.
					callingContext.getWorkList().add(callNode);
					// Ensure that the calling context is on the context work-list.
					worklist.add(callingContext);
				}
			}
			
			// Free memory on-the-fly if not needed
			if (freeResultsOnTheFly) {
				Set<Context<SootMethod,Unit,Set<Map<Value, RefType>>>> reachableContexts = contextTransitions.reachableSet(currentContext, true);
				// If any reachable contexts exist on the work-list, then we cannot free memory
				boolean canFree = true;
				for (Context<SootMethod,Unit,Set<Map<Value, RefType>>> reachableContext : reachableContexts) {
					if (worklist.contains(reachableContext)) {
						canFree = false;
						break;
					}
				}
				// If no reachable contexts on the stack, then free memory associated
				// with this context
				if (canFree) {
					for (Context<SootMethod,Unit,Set<Map<Value, RefType>>> reachableContext : reachableContexts) {
						reachableContext.freeMemory();
					}
				}
			}					
		}
		
	}
	
	// Sanity check
	for (List<Context<SootMethod,Unit,Set<Map<Value, RefType>>>> contextList : contexts.values()) {
		for (Context<SootMethod,Unit,Set<Map<Value, RefType>>> context : contextList) {
			if (context.isAnalysed() == false) {
				System.err.println("*** ATTENTION ***: Only partial analysis of X" + context + 
						" " + context.getMethod());
			}
		}			
	}
}

/**
 * Creates a new value for phantom method
 * 
 * @param method
 * @param entryValue
 * @return
 */
protected Context<SootMethod,Unit,Set<Map<Value, RefType>>> initContextForPhantomMethod(SootMethod method, Set<Map<Value, RefType>> entryValue) {
	Context<SootMethod,Unit,Set<Map<Value, RefType>>> context = new Context<SootMethod,Unit,Set<Map<Value, RefType>>>(method);
	context.setEntryValue(entryValue);
	context.setExitValue(copy(entryValue));
	context.markAnalysed();
	return context;
}

/**
 * Creates a new value context and initialises data flow values for its nodes.
 * 
 * <p>
 * The following steps are performed:
 * <ol>
 * <li>Construct the context.</li>
 * <li>Initialise IN/OUT for all nodes and add them to the work-list</li>
 * <li>Initialise the IN of entry points with a copy of the given entry value.</li>
 * <li>Add this new context to the given method's mapping.</li>
 * <li>Add this context to the global work-list.</li>
 * </ol>
 * </p>
 * 
 * @param method the method whose context to create
 * @param entryValue the data flow value at the entry of this method
 */
protected Context<SootMethod,Unit,Set<Map<Value, RefType>>> initContext(SootMethod method, Set<Map<Value, RefType>> entryValue) {
	// Construct the context
	Context<SootMethod,Unit,Set<Map<Value, RefType>>> context = new Context<SootMethod,Unit,Set<Map<Value, RefType>>>(method, programRepresentation().getControlFlowGraph(method), false);

	// Initialise IN/OUT for all nodes and add them to the work-list
	for (Unit unit : context.getControlFlowGraph()) {
		context.setValueBefore(unit, topValue());
		context.setValueAfter(unit, topValue());
		context.getWorkList().add(unit);
	}

	// Now, initialise the IN of entry points with a copy of the given entry value.
	context.setEntryValue(copy(entryValue));
	for (Unit unit : context.getControlFlowGraph().getHeads()) {
		context.setValueBefore(unit, copy(entryValue));
	}
	context.setExitValue(topValue());

	// Add this new context to the given method's mapping.
	if (!contexts.containsKey(method)) {
		contexts.put(method, new LinkedList<Context<SootMethod,Unit,Set<Map<Value, RefType>>>>());
	}
	contexts.get(method).add(context);
	
	// Add this context to the global work-list
	worklist.add(context);
	
	return context;

}

}
