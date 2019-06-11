package vasco.soot.examples;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import soot.Local;
import soot.RefType;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import vasco.Context;
import vasco.DSG;
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
import vasco.ProgramRepresentation;
import vasco.soot.DefaultJimpleRepresentation;
import vasco.ForwardInterProceduralAnalysis;

public class RefToAnalysis extends ForwardInterProceduralAnalysis< SootMethod, Unit, Set<HashMap<Value,RefType>> > {
	
	private static final Local RETURN_LOCAL = new JimpleLocal("@return", IntType.v()); 
	  public RefToAnalysis() {
		  super();
		  verbose = true;
	  }
	@Override
	public Set<HashMap<Value,RefType>> normalFlowFunction(Context<SootMethod, Unit, Set<HashMap<Value,RefType>>> context,
			Unit node, Set<HashMap<Value,RefType>> inValue) {
		Set<HashMap<Value,RefType>> outValue = new HashSet<HashMap<Value,RefType>>();
		for(HashMap<Value,RefType> hm : inValue)
			outValue.add(hm);
		if (node instanceof DefinitionStmt) {
			  DefinitionStmt ds = (DefinitionStmt) node;
		        Value lhs = ds.getLeftOp();
		        Value rhs = ds.getRightOp();
		            if (rhs instanceof NewExpr) {
		            	RefType rf = ((NewExpr) rhs).getBaseType();
		            	//outValue.set(lhs, rf);
		            	if(outValue.isEmpty()){
		       
		            		HashMap<Value,RefType> hm = new HashMap<Value,RefType>();
		            		hm.put(lhs,rf);
		            		outValue.add(hm);
		            	}
		            	else{
		            		for(HashMap<Value,RefType> hm : outValue){
		            			hm.put(lhs,rf);
		            		}	
		            	}
		            }
		            else{
		            		//outValue.set(lhs, rhs);
		            	for(HashMap<Value,RefType> hm : outValue){
		        			if(hm.get(rhs) != null){
		        				hm.put(lhs, hm.get(rhs));
		        			}
		        		}	
		            }
		} 
		return outValue;
	}
	
	@Override
	public Set<HashMap<Value,RefType>> callEntryFlowFunction(Context<SootMethod, Unit, Set<HashMap<Value,RefType>>> context,
			SootMethod targetMethod, Unit node, Set<HashMap<Value,RefType>> inValue) {
		Set<HashMap<Value,RefType>> entryValue = topValue();
		InvokeExpr ie = ((Stmt) node).getInvokeExpr();
		for (int i = 0; i < ie.getArgCount(); i++) {
			Value arg = ie.getArg(i);
			Local param = targetMethod.getActiveBody().getParameterLocal(i);
			//entryValue.set(param,arg);
			for(HashMap<Value,RefType> hm : inValue){
				if(hm.get(arg) != null){
					HashMap<Value,RefType> temp = new HashMap<Value,RefType>();
					temp.put(param, hm.get(arg));
					entryValue.add(temp);
				}
			}
		}
		if (ie instanceof InstanceInvokeExpr) {
			Value instance = ((InstanceInvokeExpr) ie).getBase();
			Local thisLocal = targetMethod.getActiveBody().getThisLocal();
			//entryValue.set(thisLocal, instance);
			for(HashMap<Value,RefType> hm : inValue){
				if(hm.get(instance) != null){
					HashMap<Value,RefType> temp = new HashMap<Value,RefType>();
					temp.put(thisLocal, hm.get(instance));
					//entryValue.add(temp);
				}
			}
		}
		return entryValue;
	}

	@Override
	public Set<HashMap<Value,RefType>> callExitFlowFunction(Context<SootMethod, Unit, Set<HashMap<Value,RefType>>> context,
			SootMethod targetMethod, Unit node, Set<HashMap<Value,RefType>> exitValue) {
		Set<HashMap<Value,RefType>> afterCallValue = topValue();
		if (node instanceof AssignStmt) {
			Value lhsOp = ((AssignStmt) node).getLeftOp();
			//Set<RefType> val = exitValue.get(RETURN_LOCAL);
		    //afterCallValue.set(lhsOp,RETURN_LOCAL);
			for(HashMap<Value,RefType> hm : exitValue){
				if(hm.get(RETURN_LOCAL) != null){
					HashMap<Value,RefType> temp = new HashMap<Value,RefType>();
					temp.put(lhsOp, hm.get(RETURN_LOCAL));
					afterCallValue.add(temp);
				}
			}
		}
		return afterCallValue;
	}

	@Override
	public Set<HashMap<Value,RefType>> callLocalFlowFunction(Context<SootMethod, Unit, Set<HashMap<Value,RefType>>> context,
			Unit node, Set<HashMap<Value,RefType>> inValue) {
		Set<HashMap<Value,RefType>> afterCallValue = new HashSet<HashMap<Value,RefType>>();
		afterCallValue.addAll(inValue);
		return afterCallValue;
	}

	@Override
	public Set<HashMap<Value,RefType>> boundaryValue(SootMethod entryPoint) {
		return new HashSet<HashMap<Value,RefType>>();
	}

	@Override
	public Set<HashMap<Value,RefType>> copy(Set<HashMap<Value,RefType>> src) {
		Set<HashMap<Value,RefType>> temp = new HashSet<HashMap<Value,RefType>>();
		temp.addAll(src);
		return temp;
	}

	@Override
	public Set<HashMap<Value,RefType>> meet(Set<HashMap<Value,RefType>> op1, Set<HashMap<Value,RefType>> op2) {
		Set<HashMap<Value,RefType>> res = new HashSet<HashMap<Value,RefType>>();
		for(HashMap<Value,RefType> hm1 : op1)
			res.add(hm1);
		for(HashMap<Value,RefType> hm2 : op2)
			res.add(hm2);
		return res;
	}

	@Override
	public ProgramRepresentation<SootMethod, Unit> programRepresentation() {
		return DefaultJimpleRepresentation.v();
	}

	@Override
	public Set<HashMap<Value,RefType>> topValue() {
		return new HashSet<HashMap<Value,RefType>>();
	}

}
