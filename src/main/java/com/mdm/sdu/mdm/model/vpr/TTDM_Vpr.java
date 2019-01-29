package com.mdm.sdu.mdm.model.vpr;

import com.mdm.sdu.ksp.edu.asu.emit.algorithm.utils.Pair;
import com.mdm.sdu.mdm.utils.SortByProbability;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * TTDM train and test ,get the accuracy and the average precision with lambda change by the step 0.1
 */
public class TTDM_Vpr {


	public static void main(String[] args) throws IOException {

		String trainPath = "input/VPR_Train_Data.csv";
		String testPath = "input/VPR_Test_Data.csv";
		String graph_path = "output/graph/VPR_graph.csv";
		String time_path = "output/time/VPR_shortest_time.csv";






		TTDM_Vpr test = new TTDM_Vpr();
		int topK = 10;

			HashMap<String, HashMap<String, Integer>> neighborKid = test
					.fieProcess(trainPath, 1);
			HashMap<String, ArrayList<String>> predictKid = test
					.getPredictKid(neighborKid);

			for(double a=1.0;a>=0.0;a=a-0.1) {

				test.test(testPath, predictKid, topK, a);
			}

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
			HashMap<String, ArrayList<String>> predictKid, int topK,double a)
			throws IOException {


		Map<Pair<Long, Long>, Double> graphMap = ShortestTimeCalVpr.graphMap;






			BufferedReader rd = null;

			Random r = new Random();

			int sum = 0;
			int accuracy[] = new int[topK];
			double[] ap = new double[topK];


			int short_count = 0;

			int time_count = 0;


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


					ArrayList<String> normalPredictLocation = normalizeScore(predictList);
					// sort all the predicted locations
					Collections.sort(normalPredictLocation, new SortByProbability());


					sum++;





					String[] lastNode = temp[length - 1].split("@");
					String lastLoction = lastNode[0];
					String lastTime = lastNode[1];


					Iterator<String> choosen = normalPredictLocation.iterator();
					ArrayList<String> timefactorList = new ArrayList<String>();

					ArrayList<String> finalResult = new ArrayList<String>();


					boolean flag = true;


					int hoop = 0;

					//calculate the time difference


					while (choosen.hasNext()) {

						hoop++;
						if (hoop > 20) {
							break;
						}


						double actualTime = 0.0;
						double shortestTime = 0.0;

						String predictStr = choosen.next();

						String[] predictNode = predictStr.split("@");

						Pair<Long, Long> tmpPair = new Pair<Long, Long>(Long.valueOf(lastLoction), Long.valueOf(predictNode[0]));
						double lastweight = 0.0;
						if (graphMap.containsKey(tmpPair)) {
							lastweight = graphMap.get(tmpPair);
						}

						int validLength = length - 1;

						for (int index = 1; index < length; index++) {
							String passbyLoaction = temp[index].split("@")[0];

							String passbytime = temp[index].split("@")[1];

							double pathshorttime = ShortestTimeCalVpr.getShortestTime(Long.valueOf(passbyLoaction), Long.valueOf(predictNode[0]));


							if (pathshorttime == Double.MAX_VALUE) {

								validLength--;

								continue;

							} else {

								double pathActualTime = (Long.valueOf(lastTime) - Long.parseLong(passbytime)) / 1000 + lastweight;


								if (pathshorttime > pathActualTime) {
									validLength--;
									continue;

								}

								shortestTime += pathshorttime;


								actualTime += pathActualTime;


							}


						}


						if (validLength == 0) {
							flag = false;
							break;


						} else {


							double differ = (actualTime - shortestTime) / (validLength + 0.0);


							double timefactor = 1.0 / (((differ + 0.00001) / 60.0));


							timefactorList.add(predictNode[0] + "@" + timefactor);

						}


					}

					//一条数据计算结束


					if (flag == true) {

						time_count++;


						ArrayList<String> normalTimeList = normalizeScore(timefactorList);


						for (String gmm : normalPredictLocation) {


							String key = gmm.split("@")[0];
							String prob = gmm.split("@")[1];
							String timeprob = "0.0";

							for (String c : normalTimeList) {
								String timelocation = c.split("@")[0];
								if (key.equals(timelocation)) {
									timeprob = c.split("@")[1];
									break;

								}
							}

							double finalPro = a*Double.parseDouble(prob) + (1-a)*Double.parseDouble(timeprob);


							finalResult.add(key + "@" + finalPro);


						}


					} else {


						short_count++;
						finalResult = normalPredictLocation;

					}


					Collections.sort(finalResult, new SortByProbability());


					for (int j = 1; j <= 10; j++) {
						for (int i = 0; i < Math.min(j, finalResult.size()); i++) {
							if (finalResult.get(i).split("@")[0].equals(actual)) {
								accuracy[j - 1]++;
								ap[j - 1] += 1.0 / (i + 1);


								break;
							}
						}
					}



				}
				rd.close();

				System.out.println("a:"+a);


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
