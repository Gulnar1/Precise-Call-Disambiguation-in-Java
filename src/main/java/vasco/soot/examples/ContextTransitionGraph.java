package vasco.soot.examples;

import java.util.HashMap;
import java.util.Map;

import soot.RefType;
import soot.Unit;
import soot.Value;

public class ContextTransitionGraph {
	
	  public Map<Map<Value, RefType>,Integer> nodes;
	  public Map<Integer,Map<Unit,Integer>> links;
	  
	  public ContextTransitionGraph(){
		  nodes = new HashMap<Map<Value, RefType>, Integer>();
		  Map<Value, RefType> mp = new HashMap<Value, RefType>();
		  nodes.put(mp, new Integer(0));
		  links = new HashMap<Integer,Map<Unit,Integer>>();
	  }
	  public void addEdgeLink(Integer s, Unit node, Integer d){
				Map<Unit, Integer> mp = links.containsKey(s) ? links.get(s) : new HashMap<Unit, Integer>();
				mp.put(node, d);
				links.put(s, mp);
	  }
	  public void printCTG(){
		  int node =0, edges = 0;
		  System.out.println("--------- NODES IN CTG ------------");
		  for(Map<Value, RefType> mp : nodes.keySet()){
			  int i = nodes.get(mp);
			  System.out.println(i + "->" + mp);
			  node ++;
		  }
		  System.out.println("*************** TOTAL NODES IN CTG = " + node + " ************");
		  System.out.println("--------- EDGES IN CTG ------------");
		  
		  for(int state : links.keySet()){
			  Map<Unit,Integer> l = links.get(state);
			  System.out.println(state + "->" + l);
			  edges = edges + l.size();
		  }
		  System.out.println("******* TOTAL EDGES IN CTG = " + edges + "**************");
	  }
}
