package vasco.soot.examples;

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
public class LiveVariableAnalysis extends BackwardInterProceduralAnalysis<SootMethod, Unit, Set<Value>>{
	private static final Local RETURN_LOCAL = new JimpleLocal("@return", IntType.v());
	
	public LiveVariableAnalysis() {
		super();
		verbose = true;
	}
	@Override
	public Set<Value> normalFlowFunction(
			Context<SootMethod, Unit, Set<Value>> context, Unit node,
			Set<Value> outValue) {
		Set<Value>  inValue = new HashSet<Value>();
		inValue.addAll(outValue);
		if (node instanceof AssignStmt) {
			 for (ValueBox def: node.getDefBoxes()) {
				Value value = def.getValue();
				if (value instanceof Local){
					inValue.remove(value);
				}
				if (value instanceof FieldRef){
					if(inValue.contains(value))
						inValue.clear();
					FieldRef fieldRef = (FieldRef) value;
					inValue.remove(new EquivalentValue(fieldRef));
				}
			 }
			 for (ValueBox use: node.getUseBoxes()) {
					Value value = use.getValue();
					if (value instanceof Local)
						inValue.add(value);
					if (value instanceof FieldRef)
					{
						FieldRef ffr = (FieldRef) value;
		                inValue.add(new EquivalentValue(ffr));
					}
				 }
			  /*if (node instanceof DefinitionStmt) {
			        DefinitionStmt ds = (DefinitionStmt) node;
			        Value lhs = ds.getLeftOp();
			        Value rhs = ds.getRightOp();
			            if (rhs instanceof NewExpr) {
			                inValue.clear();
			            } 
			    }*/
			} 
		else if (node instanceof ReturnStmt) {
				Value value = ((ReturnStmt) node).getOp();
				//inValue.add(rhsOp);
				if(value instanceof FieldRef){
					FieldRef rhsOp = (FieldRef) value;
					SootField l = ((FieldRef) rhsOp).getField();
					RefType r = l.getDeclaringClass().getType();
					inValue.add(new EquivalentValue(rhsOp));
				}
	            inValue.remove(RETURN_LOCAL);
			}
		return inValue;
	}

	@Override
	public Set<Value> callEntryFlowFunction(
			Context<SootMethod, Unit, Set<Value>> context,
			SootMethod targetMethod, Unit unit, Set<Value> entryValue) {
		Set<Value> exitValue = topValue();
		InvokeExpr ie = ((Stmt) unit).getInvokeExpr();
		for (int i = 0; i < ie.getArgCount(); i++) {
			Value arg = ie.getArg(i);
			Local param = targetMethod.getActiveBody().getParameterLocal(i);
			if(entryValue.contains(arg)){
				//SootField l = (SootField)param;
				//RefType r = l.getDeclaringClass().getType();
				exitValue.add(param);
			}
		}
		if (ie instanceof InstanceInvokeExpr) {
			Value instance = ((InstanceInvokeExpr) ie).getBase();
			Local thisLocal = targetMethod.getActiveBody().getThisLocal();
			if(entryValue.contains(instance)){
				exitValue.add(thisLocal);
			}
		}
		return exitValue;
	}

	@Override
	public Set<Value> callExitFlowFunction(
			Context<SootMethod, Unit, Set<Value>> context,
			SootMethod targetMethod, Unit unit, Set<Value> outValue) {
		Set<Value>  afterCallValue = topValue();
		if (unit instanceof AssignStmt) {
			Value lhsOp = ((AssignStmt) unit).getLeftOp();
			if(outValue.contains(RETURN_LOCAL)){
				//SootField l = ((FieldRef) lhsOp).getField();
				//RefType r = l.getDeclaringClass().getType();
				afterCallValue.add(lhsOp);
			}
		}
		return afterCallValue;
	}

	@Override
	public Set<Value> callLocalFlowFunction(
			Context<SootMethod, Unit, Set<Value>> context, Unit node,
			Set<Value> outValue) {
		// Initialise result to the input
		        Set<Value>  afterCallValue = new HashSet<Value>();
				afterCallValue.addAll(outValue);
				// Remove information for return value (as it's value will flow from the call)
				if (node instanceof AssignStmt) {
					Value lhsOp = ((AssignStmt) node).getLeftOp();
					afterCallValue.remove(lhsOp);
				}
				return afterCallValue;
	}

	@Override
	public Set<Value> boundaryValue(SootMethod entryPoint) {
		return new HashSet<Value>();
	}

	@Override
	public Set<Value> copy(Set<Value> src) {
		Set<Value> result = new HashSet<Value>();
		result.addAll(src);
		return result;
	}

	@Override
	public Set<Value> meet(Set<Value> op1, Set<Value> op2) {
		Set<Value> result = new HashSet<Value>();
		result.addAll(op1);
		result.addAll(op2);
		return result;
	}

	@Override
	public ProgramRepresentation<SootMethod, Unit> programRepresentation() {
		return DefaultJimpleRepresentation.v();
	}

	@Override
	public Set<Value> topValue() {
		return new HashSet<Value>();
	}

}

