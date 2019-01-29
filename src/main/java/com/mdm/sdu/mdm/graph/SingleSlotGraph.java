package com.mdm.sdu.mdm.graph;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 **trajectory graph, including nodes and edges
 */
public class SingleSlotGraph {

    public int v;
    public int e;

    //public ArrayList<WeightedEdge>[] adj;

    public Map<String, ArrayList<WeightedEdge>> map = new HashMap<String, ArrayList<WeightedEdge>>();

    public SingleSlotGraph() {

    }

    /**
     *
     *
     * @param e
     */
    public void addEdge(WeightedEdge e) {
        if (map.get(e.from()) != null) {
            map.get(e.from()).add(e);
        } else {
            ArrayList<WeightedEdge> edges = new ArrayList<WeightedEdge>();
            edges.add(e);
            map.put(e.from(), edges);
        }

        this.e++;
    }



    public void printEdge(){
        for(String key:map.keySet()){
            for(WeightedEdge e:map.get(key)){
                System.out.println(e+" ");
            }
        }

    }




    public static void main(String[] args) {

        SingleSlotGraph  singleSlotGraph = new SingleSlotGraph();

        WeightedEdge edge1 = new WeightedEdge("1","2",1.0);
        WeightedEdge edge2 = new WeightedEdge("2","3",2.0);

        WeightedEdge edge3 = new WeightedEdge("3","2",2.0);

        WeightedEdge edge4 = new WeightedEdge("4","5",1.0);

        singleSlotGraph.addEdge(edge1);
        singleSlotGraph.addEdge(edge2);

        singleSlotGraph.addEdge(edge3);

        singleSlotGraph.addEdge(edge4);
        singleSlotGraph.printEdge();




    }




}
