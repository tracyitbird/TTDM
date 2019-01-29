package com.mdm.sdu.mdm.model.vpr;

import com.mdm.sdu.ksp.edu.asu.emit.algorithm.graph.Graph;
import com.mdm.sdu.ksp.edu.asu.emit.algorithm.graph.VariableGraph;
import com.mdm.sdu.ksp.edu.asu.emit.algorithm.utils.Pair;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created  by liuqingjie on 2019/1/5 .
 */
public class ShortestTimeCalVpr {


    public static Map<Pair<Long, Long>, Double> shortestTimeMap = new HashMap<Pair<Long, Long>, Double>();

    public static Map<Pair<Long, Long>, Double> graphMap = new HashMap<Pair<Long, Long>, Double>();

    public static Graph graph;

    static {


        graph = new VariableGraph("output/graph/VPR_graph.csv");

        graphMap = graph.getVertexPairWeightIndex();


        try {
            BufferedReader br = new BufferedReader(new FileReader("output/time/VPR_shortest_time.csv"));

            String line;
            while ((line = br.readLine()) != null) {
                String[] linearray = line.split(":");
                if (linearray.length != 2) {
                    continue;
                }

                String path = linearray[0];
                double time = Double.valueOf(linearray[1]);
                int begin = path.indexOf('[');
                int end = path.indexOf(']');
                String midpath = path.substring(begin + 1, end);

                String[] midpathArray = midpath.split(",");

                long startnode = Long.valueOf(midpathArray[0].trim());
                long endnode = Long.valueOf(midpathArray[midpathArray.length - 1].trim());

                Pair<Long, Long> tmppath = new Pair<Long, Long>(startnode, endnode);

                shortestTimeMap.put(tmppath, time);
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }


    }


    public static double getShortestTime(long start, long end) {


        double shortestime = 0.0;
        if (start == end) {
            return shortestime;
        }
        Pair<Long, Long> pair = new Pair<Long, Long>(start, end);

        if (shortestTimeMap.containsKey(pair)) {
            shortestime = shortestTimeMap.get(pair);
        } else {
            shortestime = Double.MAX_VALUE;
        }

        return shortestime;

    }


    public static void main(String[] args) {


    }


}
