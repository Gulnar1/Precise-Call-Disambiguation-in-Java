package vasco.soot.examples;

import java.util.HashSet;
import java.util.Set;

import soot.Local;
import soot.SootField;
import soot.RefType;
import soot.SootMethod;
import soot.Unit;
import soot.IntType;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.ReturnStmt;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.internal.JimpleLocal;
import soot.toolkits.scalar.*;
import vasco.BackwardInterProceduralAnalysis;
import vasco.Context;
import vasco.ProgramRepresentation;
import vasco.soot.DefaultJimpleRepresentation;
public class LiveAnalysis extends BackwardInterProceduralAnalysis<SootMethod, Unit, Set<SootField>>{
	private static final Local RETURN_LOCAL = new JimpleLocal("@return", IntType.v());
	
	public LiveAnalysis() {
		super();
		verbose = true;
	}
	@Override
	public Set<SootField> normalFlowFunction(
			Context<SootMethod, Unit, Set<SootField>> context, Unit node,
			Set<SootField> outValue) {
		Set<SootField>  inValue = new HashSet<SootField>();
		inValue.addAll(outValue);
		if (node instanceof AssignStmt) {
			 for (ValueBox def: node.getDefBoxes()) {
				Value value = def.getValue();
				if (value instanceof FieldRef)
					inValue.remove(((FieldRef) value).getField());
			 }
			 for (ValueBox use: node.getUseBoxes()) {
				Value value = use.getValue();
				//if (value instanceof Local)
					//inValue.add((Local) value);
				if (value instanceof FieldRef)
				{
					FieldRef ffr = (FieldRef) value;
	                SootField l =  ffr.getField();
	                RefType r = l.getDeclaringClass().getType();
	                inValue.add(l);
				}
				/*if (value instanceof StaticFieldRef)
				{
					StaticFieldRef ffr = (StaticFieldRef) value;
	                SootField l =  ffr.getField();
	                System.out.println(" found Staticfieldref " + l);
				}
				if (value instanceof InstanceFieldRef)
				{
					InstanceFieldRef ffr = (InstanceFieldRef) value;
					SootField s =  ffr.getField();
	                Value l =  (Value) ffr.getBase();
	                System.out.println(" found Instancefieldref " + l);
	                //inValue.add((Local) l);
				}*/
			 }
			} else if (node instanceof ReturnStmt) {
				Value rhsOp = ((ReturnStmt) node).getOp();
				if(rhsOp instanceof FieldRef){
					SootField l = ((FieldRef) rhsOp).getField();
					RefType r = l.getDeclaringClass().getType();
					inValue.add(l);
				}
	            inValue.remove(RETURN_LOCAL);
			}
		return inValue;
	}

	@Override
	public Set<SootField> callEntryFlowFunction(
			Context<SootMethod, Unit, Set<SootField>> context,
			SootMethod targetMethod, Unit unit, Set<SootField> entryValue) {
		Set<SootField> exitValue = topValue();
		InvokeExpr ie = ((Stmt) unit).getInvokeExpr();
		for (int i = 0; i < ie.getArgCount(); i++) {
			Value arg = ie.getArg(i);
			Local param = targetMethod.getActiveBody().getParameterLocal(i);
			if(entryValue.contains((SootField)arg)){
				SootField l = (SootField)param;
				RefType r = l.getDeclaringClass().getType();
				exitValue.add(l);
			}
		}
		/*if (ie instanceof InstanceInvokeExpr) {
			Value instance = ((InstanceInvokeExpr) ie).getBase();
			Local thisLocal = targetMethod.getActiveBody().getThisLocal();
			if(entryValue.contains((SootField)instance)){
				exitValue.add((SootField) thisLocal);
			}
		}*/
		return exitValue;
	}

	@Override
	public Set<SootField> callExitFlowFunction(
			Context<SootMethod, Unit, Set<SootField>> context,
			SootMethod targetMethod, Unit unit, Set<SootField> outValue) {
		Set<SootField>  afterCallValue = topValue();
		if (unit instanceof AssignStmt) {
			Value lhsOp = ((AssignStmt) unit).getLeftOp();
			if(outValue.contains(RETURN_LOCAL) && lhsOp instanceof FieldRef){
				SootField l = ((FieldRef) lhsOp).getField();
				RefType r = l.getDeclaringClass().getType();
				afterCallValue.add(l);
			}
		}
		return afterCallValue;
	}

	@Override
	public Set<SootField> callLocalFlowFunction(
			Context<SootMethod, Unit, Set<SootField>> context, Unit node,
			Set<SootField> outValue) {
		// Initialise result to the input
		        Set<SootField>  afterCallValue = new HashSet<SootField>();
				afterCallValue.addAll(outValue);
				// Remove information for return value (as it's value will flow from the call)
				if (node instanceof AssignStmt) {
					Value lhsOp = ((AssignStmt) node).getLeftOp();
					//afterCallValue.remove((Local) lhsOp);
					if(lhsOp instanceof FieldRef){
						SootField l = ((FieldRef) lhsOp).getField();
						RefType r = l.getDeclaringClass().getType();
						afterCallValue.remove(l);
					}
				}
				return afterCallValue;
	}

	@Override
	public Set<SootField> boundaryValue(SootMethod entryPoint) {
		return new HashSet<SootField>();
	}

	@Override
	public Set<SootField> copy(Set<SootField> src) {
		Set<SootField> result = new HashSet<SootField>();
		result.addAll(src);
		return result;
	}

	@Override
	public Set<SootField> meet(Set<SootField> op1, Set<SootField> op2) {
		Set<SootField> result = new HashSet<SootField>();
		result.addAll(op1);
		result.addAll(op2);
		return result;
	}

	@Override
	public ProgramRepresentation<SootMethod, Unit> programRepresentation() {
		return DefaultJimpleRepresentation.v();
	}

	@Override
	public Set<SootField> topValue() {
		return new HashSet<SootField>();
	}

}
