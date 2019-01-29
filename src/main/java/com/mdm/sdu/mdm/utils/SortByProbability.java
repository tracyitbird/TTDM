package com.mdm.sdu.mdm.utils;

import java.util.Comparator;

/**
 * sort collection by probability
 */

public class SortByProbability implements Comparator<String> {


	public int compare(String arg0, String arg1) {
		int start1 = arg0.indexOf("@")+1;
		int start2 = arg1.indexOf("@")+1;
		double prob1 = Double.parseDouble(arg0.substring(start1,arg0.length()));
		double prob2 = Double.parseDouble(arg1.substring(start2,arg1.length()));
		return prob1 == prob2 ? 0 : 
            (prob1 > prob2 ? -1 : 1);
	}

}
