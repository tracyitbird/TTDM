package com.mdm.sdu.mdm.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * the edge of the traffic graph
 */
public class WeightedEdge {


    private String start; //starting point of the edge
    private String end; //ending point of the edge

    private double  weight;//average weight of the edge

    private int count ;//to cal average weight

    private double weightSum; //to cal average weight


    public WeightedEdge(String start, String end) {
        this.start = start;
        this.end = end;
    }

    public WeightedEdge(String start, String end, double weight) {
        this.start = start;
        this.end = end;
        this.weight = weight;
    }

    public String from(){
        return start;
    }

    public String to(){
        return end;
    }

    public double getWeight(){
        return weight;
    }


    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getWeightSum() {
        return weightSum;
    }

    public void setWeightSum(double weightSum) {
        this.weightSum = weightSum;
    }

    @Override
    public String toString() {
        return String.format("%s->%s% .2f",start,end,weight);
    }



    @Override
    public boolean equals(Object o) {
        if(o == this)
            return true;

        if(!(o instanceof WeightedEdge))
            return false;

        WeightedEdge other = (WeightedEdge)o;
        return other.start.equals(start) && other.end.equals(end);



    }

    @Override
    public int hashCode() {
        int result = start.hashCode();
        result = 31 * result + end.hashCode();
        return result;
    }

    public static void main(String[] args) {


    }


}
