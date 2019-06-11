package vasco.soot.examples;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

  public class RefAnalysis extends ForwardInterProceduralAnalysis< SootMethod, Unit, Set<Map<Value, RefType>> > {
  private static final Local RETURN_LOCAL = new JimpleLocal("@return", IntType.v()); 
  public RefAnalysis() {
	  super();
	  verbose = true;
  }
@Override
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
	System.out.println("");
	return outValue;
}
@Override
public Set<Map<Value, RefType>> callEntryFlowFunction(
		Context<SootMethod, Unit, Set<Map<Value, RefType>>> context,
		SootMethod targetMethod, Unit unit, Set<Map<Value, RefType>> inValue) {
	//System.out.println("CallEntryFlowFunction");
	Set<Map<Value, RefType>> entryValue = topValue();
	InvokeExpr ie = ((Stmt) unit).getInvokeExpr();
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
	return entryValue;
}

@Override
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

@Override
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

}
