package com.mdm.sdu.mdm.model.vpr;


import com.mdm.sdu.mdm.utils.SortByProbability;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * markov model train and test,output the accuracy and the average precision
 */
public class MM_Vpr {


    public static void main(String[] args) throws IOException {

        String trainPath = "input/VPR_Train_Data.csv";
        String testPath = "input/VPR_Test_Data.csv";

        MM_Vpr test = new MM_Vpr();
        int topK = 10;
        HashMap<String, HashMap<String, Integer>> neighborKid = test
                .fieProcess(trainPath, 1);
        HashMap<String, ArrayList<String>> predictKid = test
                .getPredictKid(neighborKid);
        test.test(testPath, predictKid, topK);

    }

    public HashMap<String, HashMap<String, Integer>> fieProcess(String path1,
                                                                int K) {

        HashMap<String, HashMap<String, Integer>> neighborKid = new HashMap<String, HashMap<String, Integer>>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(path1));
            while (true) {
                String line = br.readLine();
                if (line == null)
                    break;
                String[] temp = line.split(",");
                for (int i = 1; i <= Math.min(K, temp.length - 1); i++) {
                    for (int j = 0; j < temp.length - i; j++) {
                        String prefix = temp[j].split("@")[0];
                        for (int k = j + 1; k < j + i; k++) {
                            prefix += "#" + temp[k].split("@")[0];
                        }
                        String predictKid = temp[i + j].split("@")[0];
                        saveNeighborTwoKid(prefix, predictKid, neighborKid);
                    }
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return neighborKid;

    }

    private void saveNeighborTwoKid(String prefix, String predict,
                                    HashMap<String, HashMap<String, Integer>> neighborKid) {

        if (neighborKid.containsKey(prefix)) {
            HashMap<String, Integer> predictCountHm = neighborKid.get(prefix);
            if (predictCountHm.containsKey(predict)) {
                predictCountHm.put(predict, predictCountHm.get(predict) + 1);
            } else {
                predictCountHm.put(predict, 1);
            }
        } else {
            HashMap<String, Integer> predictCountHm = new HashMap<String, Integer>();
            predictCountHm.put(predict, 1);
            neighborKid.put(prefix, predictCountHm);
        }
    }

    // prefixKid,predictKid@prob
    public HashMap<String, ArrayList<String>> getPredictKid(
            HashMap<String, HashMap<String, Integer>> neighborKid) {

        HashMap<String, ArrayList<String>> predictKid = new HashMap<String, ArrayList<String>>();
        Set<String> keyset = neighborKid.keySet();
        Iterator<String> itr = keyset.iterator();
        while (itr.hasNext()) {
            String key = itr.next();
            ArrayList<String> predictKidList = new ArrayList<String>();
            HashMap<String, Integer> predictKidcount = neighborKid.get(key);
            // compute sum count for every prefix kid
            Set<String> keyset1 = predictKidcount.keySet();
            Iterator<String> itr1 = keyset1.iterator();
            int sum = 0;
            while (itr1.hasNext()) {
                sum += predictKidcount.get(itr1.next());
            }
            // compute frequency
            Iterator<String> itr2 = keyset1.iterator();
            while (itr2.hasNext()) {
                String key1 = itr2.next();
                double prob = predictKidcount.get(key1) / (sum + 0.0);
                predictKidList.add(key1 + "@" + prob);
            }
            Collections.sort(predictKidList, new SortByProbability());
            predictKid.put(key, predictKidList);
        }
        return predictKid;
    }

    private String getPredict(String traSequence,
                              HashMap<String, ArrayList<String>> predictKid) {
        String predict = "#";
        String[] temp = traSequence.split("#");
        for (int i = 1; i < temp.length; i++) {
            String prefixTra = "";
            for (int j = i; j < temp.length; j++) {
                prefixTra += "#" + temp[j];
            }
            // remove the first @
            if (predictKid.containsKey(prefixTra.substring(1))) {
                predict = predictKid.get(prefixTra.substring(1)).get(0)
                        .split("@")[0];
                break;
            }
        }
        return predict;
    }

    // get final predict list with prob
    public static ArrayList<String> getGMMpredictListWithProb(String traSequence,
                                                              HashMap<String, ArrayList<String>> predictKid) {

        ArrayList<String> predictList = new ArrayList<String>();
        String[] temp = traSequence.split("#");
        for (int i = 1; i < temp.length; i++) {
            String prefixTra = "";
            for (int j = i; j < temp.length; j++) {
                prefixTra += "#" + temp[j];
            }
            // remove the first @
            if (predictKid.containsKey(prefixTra.substring(1))) {
                predictList = predictKid.get(prefixTra.substring(1));
                break;
            }
        }
        return predictList;
    }

    private ArrayList<String> normalizeScore(ArrayList<String> predictLocation) {

        ArrayList<String> normalPredictLocation = new ArrayList<String>();
        double sum = 0.0;
        for (int i = 0; i < predictLocation.size(); i++) {
            sum += Double.parseDouble(predictLocation.get(i).split("@")[1]);
        }
        for (int i = 0; i < predictLocation.size(); i++) {
            double newScore = Double.parseDouble(predictLocation.get(i).split(
                    "@")[1])
                    / sum;
            normalPredictLocation.add(predictLocation.get(i).split("@")[0]
                    + "@" + newScore);
        }
        return normalPredictLocation;
    }

    private void test(String path,
                      HashMap<String, ArrayList<String>> predictKid, int topK)
            throws IOException {
        BufferedReader rd = null;
        double count = 0.0;
        int sum = 0;
        int accuracy[] = new int[topK];
        double[] ap = new double[topK];
        double scoreSum = 0;
        Map<Integer, Integer> distribution = new HashMap<Integer, Integer>();

        try {
            rd = new BufferedReader(new FileReader(path));
            while (true) {
                String line = rd.readLine();
                if (line == null)
                    break;
                String[] temp = line.split(",");

                String traSequence = "";


                int length = temp.length - 1;

                for (int i = 0; i < length; i++)
                    traSequence += "#" + temp[i].split("@")[0];
                // String predict = getPredict(traSequence, predictKid);
                ArrayList<String> predictList = getGMMpredictListWithProb(traSequence,
                        predictKid);
                String actual = temp[length].split("@")[0];


                // zhenguihua score
                ArrayList<String> normalPredictLocation = normalizeScore(predictList);
                // sort all the predicted locations
                Collections.sort(normalPredictLocation, new SortByProbability());
                // compute perplexity
                for (int i = 0; i < normalPredictLocation.size(); i++) {
                    if (normalPredictLocation.get(i).split("@")[0]
                            .equals(actual)) {
                        scoreSum += Math.log(Double
                                .parseDouble(normalPredictLocation.get(i)
                                        .split("@")[1]));
                        break;
                    }
                }


                sum++;
                for (int j = 1; j <= 10; j++) {
                    for (int i = 0; i < Math.min(j, predictList.size()); i++) {
                        if (predictList.get(i).split("@")[0].equals(actual)) {
                            accuracy[j - 1]++;
                            ap[j - 1] += 1.0 / (i + 1);


                            break;
                        }
                    }
                }
            }
            rd.close();

            for (int i = 0; i < 10; i++) {
                System.out.print(accuracy[i] / (sum + 0.0) + "\t");
            }
            System.out.println();
            for (int i = 0; i < 10; i++) {
                System.out.print(ap[i] / (sum + 0.0) + "\t");
            }


            System.out.println();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


}
