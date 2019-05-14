package vasco.soot.examples;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import soot.EquivalentValue;
import soot.Local;
import soot.SootField;
import soot.RefType;
import soot.SootMethod;
import soot.Unit;
import soot.IntType;
import soot.Value;
import soot.ValueBox;
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
import vasco.Context;
import vasco.ProgramRepresentation;
import vasco.soot.DefaultJimpleRepresentation;
import vasco.ForwardInterProceduralAnalysis;

  public class RefersToAnalysis extends ForwardInterProceduralAnalysis< SootMethod, Unit, HashMap<Value, Set<RefType>> > {
  private static final Local RETURN_LOCAL = new JimpleLocal("@return", IntType.v()); 
  public RefersToAnalysis() {
	  super();
	  verbose = true;
  }
@Override
public HashMap<Value, Set<RefType>> normalFlowFunction(
		Context<SootMethod, Unit, HashMap<Value, Set<RefType>>> context,
		Unit node, HashMap<Value, Set<RefType>> inValue) {
	HashMap<Value, Set<RefType>> outValue = new HashMap<Value, Set<RefType>>();
	outValue.putAll(inValue);
	if (node instanceof DefinitionStmt) {
			  DefinitionStmt ds = (DefinitionStmt) node;
		        Value lhs = ds.getLeftOp();
		        Value rhs = ds.getRightOp();
		            if (rhs instanceof NewExpr) {
		            	RefType rf = ((NewExpr) rhs).getBaseType();
		            	Set<RefType> set = new HashSet<RefType>();
		            	set.add(rf);
		            	outValue.put(lhs, set);
		            }
		            else{
		            	Set<RefType> set = new HashSet<RefType>();
		            	if(outValue.get(rhs) != null){
		            		set = outValue.get(rhs);
		            		outValue.put(lhs, set);
		            	}	            		
		            }
		} 
	return outValue;
}
@Override
public HashMap<Value, Set<RefType>> callEntryFlowFunction(
		Context<SootMethod, Unit, HashMap<Value, Set<RefType>>> context,
		SootMethod targetMethod, Unit unit, HashMap<Value, Set<RefType>> inValue) {
	HashMap<Value, Set<RefType>> entryValue = topValue();
	InvokeExpr ie = ((Stmt) unit).getInvokeExpr();
	for (int i = 0; i < ie.getArgCount(); i++) {
		Value arg = ie.getArg(i);
		Local param = targetMethod.getActiveBody().getParameterLocal(i);
		Set<RefType> val = inValue.get(arg);
		if(val.isEmpty()){
		}
		else{
			entryValue.put(param,val);
		}
	}
	if (ie instanceof InstanceInvokeExpr) {
		Value instance = ((InstanceInvokeExpr) ie).getBase();
		Local thisLocal = targetMethod.getActiveBody().getThisLocal();
		Set<RefType> val = inValue.get(instance);
		if(val.isEmpty())
		;
		else{
			entryValue.put(thisLocal,val);
		}
	}
	return entryValue;
}

@Override
public HashMap<Value, Set<RefType>> callExitFlowFunction(
		Context<SootMethod, Unit, HashMap<Value, Set<RefType>>> context,
		SootMethod targetMethod, Unit unit, HashMap<Value, Set<RefType>> exitValue) {
	HashMap<Value, Set<RefType>>  afterCallValue = topValue();
	if (unit instanceof AssignStmt) {
		Value lhsOp = ((AssignStmt) unit).getLeftOp();
		Set<RefType> val = exitValue.get(RETURN_LOCAL);
		if(val.isEmpty())
		;
		else{
			afterCallValue.put(lhsOp,val);
		}
	}
	return afterCallValue;
}

@Override
public HashMap<Value, Set<RefType>> callLocalFlowFunction(
		Context<SootMethod, Unit, HashMap<Value, Set<RefType>>> context,
		Unit node, HashMap<Value, Set<RefType>> inValue) {
	HashMap<Value, Set<RefType>> afterCallValue = new HashMap<Value, Set<RefType>>();
	afterCallValue.putAll(inValue);
	
	return afterCallValue;
}

@Override
public HashMap<Value, Set<RefType>> meet(HashMap<Value, Set<RefType>> op1,
		HashMap<Value, Set<RefType>> op2) {
	HashMap<Value, Set<RefType>> result = new HashMap<Value, Set<RefType>>();
	result.putAll(op1);
	for (Value v: op2.keySet()){
		Set<RefType> val1 = new HashSet<RefType>();
		if(op1.get(v) != null)
			val1 = op1.get(v);
		
		Set<RefType> val2 = new HashSet<RefType>();
		if(op2.get(v) != null)
			val2 = op2.get(v);
		if(val1.equals(val2)){
			//same refers-to set 
		}
		else{
			val1.addAll(val2);
			result.put(v, val1);
		}
	}
	return result;
}

@Override
public HashMap<Value, Set<RefType>> boundaryValue(SootMethod entryPoint) {
	return new HashMap<Value, Set<RefType>>();
}

@Override
public HashMap<Value, Set<RefType>> copy(HashMap<Value, Set<RefType>> src) {
	HashMap<Value, Set<RefType>> result = new HashMap<Value, Set<RefType>>();
	result.putAll(src);
	return result;
}

@Override
public ProgramRepresentation<SootMethod, Unit> programRepresentation() {
	return DefaultJimpleRepresentation.v();
}

@Override
public HashMap<Value, Set<RefType>> topValue() {
	return new HashMap<Value, Set<RefType>>();
}

}
