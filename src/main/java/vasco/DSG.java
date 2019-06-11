package vasco;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import soot.Value;
import soot.RefType;
public class DSG {
	
	public Set<HashMap<Value,RefType>> dsg;
	public DSG(){
		dsg = new HashSet<HashMap<Value,RefType>>();
	}
	public DSG(DSG d){
		this.dsg = d.dsg;
	}
	public DSG copy(){
		DSG  res = new DSG();
		res.dsg = this.dsg;
		return res;
	}
	public DSG meet(DSG x){
		DSG res = new DSG();
		res.dsg.addAll(this.dsg);
		res.dsg.addAll(x.dsg);
		return res;
	}
	public void set(Value v, RefType r){
		
		for(HashMap<Value,RefType> hm : this.dsg){
			hm.put(v,r);
		}	
	}
	public void set(Value v1, Value v2){
		for(HashMap<Value,RefType> hm : this.dsg){
			if(hm.get(v2) != null){
				hm.put(v1, hm.get(v2));
			}
		}	
	}

}
