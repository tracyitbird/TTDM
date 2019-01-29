package com.mdm.sdu.mdm.model.vpr;

import com.mdm.sdu.mdm.graph.SingleSlotGraph;
import com.mdm.sdu.mdm.graph.WeightedEdge;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * gen vpr dataset graph
 */
public class GenVPRDataGraph {

    public static SingleSlotGraph constructTrajGraph(String path) throws FileNotFoundException {

        SingleSlotGraph singleSlotGraph = new SingleSlotGraph();


        BufferedReader br = new BufferedReader(
                new FileReader(path));

        while (true) {
            try {
                String line = br.readLine();

                if (line == null) {
                    break;
                }


                String[] record = line.split(",");


                for (int i = 1; i < record.length; i++) {
                    String[] movingi = record[i].split("@");

                    if (movingi.length == 2) {
                        String location = movingi[0];
                        String timestamp = movingi[1];
//
                        int j = i + 1;
                        if (j < record.length) {
                            String[] movingj = record[j].split("@");

                            //if (movingj.length == 2) {
                            String locationj = movingj[0];
                            String timestampj = movingj[1];
                            WeightedEdge tmpedge = new WeightedEdge(location, locationj);
                            double traveltime = Double.parseDouble(timestampj) - Double.parseDouble(timestamp);


                            if (singleSlotGraph.map.containsKey(location)) {


                                if (singleSlotGraph.map.get(location).contains(tmpedge)) {
                                    int index = singleSlotGraph.map.get(location).indexOf(tmpedge);

                                    WeightedEdge edge = singleSlotGraph.map.get(location).get(index);


                                    int tmpcount = edge.getCount();
                                    tmpcount++;

                                    edge.setCount(tmpcount);

                                    double tmpweightsum = edge.getWeightSum();


                                    tmpweightsum += traveltime;

                                    edge.setWeightSum(tmpweightsum);


                                    double weightavg = Math.floor((tmpweightsum / tmpcount) / 1000);

                                    edge.setWeight(weightavg);


                                } else {

                                    tmpedge.setWeightSum(traveltime);
                                    tmpedge.setCount(1);
                                    tmpedge.setWeight(traveltime / 1000);
                                    singleSlotGraph.map.get(location).add(tmpedge);


                                }

                            } else {

                                tmpedge.setWeightSum(traveltime);
                                tmpedge.setCount(1);
                                tmpedge.setWeight(Math.floor(traveltime / 1000));
                                ArrayList<WeightedEdge> edgeArrayList = new ArrayList<WeightedEdge>();
                                edgeArrayList.add(tmpedge);

                                singleSlotGraph.map.put(location, edgeArrayList);

                            }
                            // }


                        } else {

                            if (!singleSlotGraph.map.containsKey(location)) {

                                ArrayList<WeightedEdge> edgeArrayList = new ArrayList<WeightedEdge>();
                                singleSlotGraph.map.put(location, edgeArrayList);


                            }


                        }
                    }


                }


            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        return singleSlotGraph;


    }


    public static void genGraphFile(SingleSlotGraph slotGraph, String filename) {


        Set<String> nodeset = new HashSet<String>();
        for (String s : slotGraph.map.keySet()) {
            nodeset.add(s);
            for (WeightedEdge edge : slotGraph.map.get(s)) {
                nodeset.add(edge.to());
            }

        }
        int countnode = nodeset.size();

        System.out.println(countnode);


        File outdir = new File("output/time/");
        if (!outdir.exists()) outdir.mkdir();
        String fileName = outdir + "/" + filename;
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(fileName));

            writer.write(countnode + " ");
            writer.newLine();
            writer.newLine();

            for (String key : slotGraph.map.keySet()) {
                ArrayList<WeightedEdge> edges = slotGraph.map.get(key);
                for (WeightedEdge edge : edges) {
                    writer.write(edge.from());
                    writer.write("    ");
                    writer.write(edge.to());
                    writer.write("    ");
                    writer.write(String.valueOf(edge.getWeight()));
                    writer.newLine();
                }


            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                writer.flush();
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


    public static void main(String[] args) throws FileNotFoundException {

        String datapath = "input/VPR_Train_Data.csv";

        SingleSlotGraph slotGraph = GenVPRDataGraph.constructTrajGraph(datapath);

        GenVPRDataGraph.genGraphFile(slotGraph, args[0]);




    }


}
