package vasco.soot.examples;

import soot.IntType;
import soot.Local;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
//import soot.jimple.AddExpr;
import soot.jimple.AssignStmt;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
//import soot.jimple.BinopExpr;
//import soot.jimple.CastExpr;
import soot.jimple.InstanceInvokeExpr;
//import soot.jimple.IntConstant;
//import soot.jimple.Constant;
import soot.jimple.InvokeExpr;
//import soot.jimple.MulExpr;
//import soot.jimple.NumericConstant;
import soot.jimple.ReturnStmt;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
//import soot.jimple.UnopExpr;
//import soot.jimple.internal.AbstractNegExpr;
import soot.jimple.internal.JimpleLocal;
import soot.toolkits.scalar.*;
import vasco.Context;
import vasco.BackwardInterProceduralAnalysis;
import vasco.ProgramRepresentation;
import vasco.soot.DefaultJimpleRepresentation;
public class LiveVariableAnalysis extends BackwardInterProceduralAnalysis<SootMethod, Unit, FlowSet<Local>>{
	// An artificial local representing returned value of a procedure (used because a method can have multiple return statements).
	private static final Local RETURN_LOCAL = new JimpleLocal("@return", IntType.v());
	
	public LiveVariableAnalysis() {
		super();
		verbose = true;
	}
	@Override
	public FlowSet<Local> normalFlowFunction(
			Context<SootMethod, Unit, FlowSet<Local>> context, Unit node,
			FlowSet<Local> outValue) {
		FlowSet<Local> inValue = new ArraySparseSet<Local>();
		outValue.copy(inValue);
		if (node instanceof AssignStmt) {
		 for (ValueBox def: node.getDefBoxes()) {
			Value value = def.getValue();
			if (value instanceof Local)
				inValue.remove((Local) value);
		 }
		 for (ValueBox use: node.getUseBoxes()) {
			Value value = use.getValue();
			if (value instanceof Local)
				inValue.add((Local) value);
			/*if (value instanceof FieldRef)
			{
				FieldRef ffr = (FieldRef) value;
                SootField l =  ffr.getField();
                System.out.println(" found fieldref " + l);
			}
			if (value instanceof StaticFieldRef)
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
            inValue.add((Local)rhsOp);
            inValue.remove(RETURN_LOCAL);
		}
		return inValue;
	}
	@Override
	public FlowSet<Local> callEntryFlowFunction(
			Context<SootMethod, Unit, FlowSet<Local>> context,
			SootMethod targetMethod, Unit unit, FlowSet<Local> entryValue) {
		// Initialise result to empty map
		FlowSet<Local> exitValue = topValue();
		// Map arguments to parameters
		InvokeExpr ie = ((Stmt) unit).getInvokeExpr();
		for (int i = 0; i < ie.getArgCount(); i++) {
			Value arg = ie.getArg(i);
			Local param = targetMethod.getActiveBody().getParameterLocal(i);
			if(entryValue.contains((Local)arg)){
				exitValue.add((Local)param);
			}
		}
		// And instance of the this local
		if (ie instanceof InstanceInvokeExpr) {
			Value instance = ((InstanceInvokeExpr) ie).getBase();
			Local thisLocal = targetMethod.getActiveBody().getThisLocal();
			if(entryValue.contains((Local)instance)){
				exitValue.add(thisLocal);
			}
		}
		// Return the exit value at the called method
		return exitValue;
	}
	@Override
	public FlowSet<Local> callExitFlowFunction(
			Context<SootMethod, Unit, FlowSet<Local>> context,
			SootMethod targetMethod, Unit unit, FlowSet<Local> outValue) {
		// Initialise result to an empty value
		FlowSet<Local> afterCallValue = topValue();
		// Only propagate return values
		if (unit instanceof AssignStmt) {
			Value lhsOp = ((AssignStmt) unit).getLeftOp();
			if(outValue.contains(RETURN_LOCAL)){
				afterCallValue.add((Local)lhsOp);
			}
		}
		// Return the result with the returned value
		return afterCallValue;
	}
	@Override
	public FlowSet<Local> callLocalFlowFunction(
			Context<SootMethod, Unit, FlowSet<Local>> context, Unit unit,
			FlowSet<Local> outValue) {
		// Initialise result to the input
		FlowSet<Local> afterCallValue = new ArraySparseSet<Local>();
		outValue.copy(afterCallValue);
		// Remove information for return value (as it's value will flow from the call)
		if (unit instanceof AssignStmt) {
			Value lhsOp = ((AssignStmt) unit).getLeftOp();
			afterCallValue.remove((Local) lhsOp);
		}
		return afterCallValue;
	}
	@Override
	public FlowSet<Local> boundaryValue(SootMethod entryPoint) {
		return new ArraySparseSet<Local>();
	}
	@Override
	public FlowSet<Local> copy(FlowSet<Local> src) {
		FlowSet<Local> result = new ArraySparseSet<Local>();
		src.copy(result);
		return result;
	}
	@Override
	public FlowSet<Local> meet(FlowSet<Local> op1, FlowSet<Local> op2) {
		FlowSet<Local> result = new ArraySparseSet<Local>();
		op1.union(op2, result);
		return result;
	}
	@Override
	public ProgramRepresentation<SootMethod, Unit> programRepresentation() {
		return DefaultJimpleRepresentation.v();
	}
	@Override
	public FlowSet<Local> topValue() {
		return new ArraySparseSet<Local>();
	}
	
}
