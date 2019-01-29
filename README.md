# TTDM
Travel Time Difference Model (TTDM),  a prediction model which
exploits the difference between the shortest travel time 
and the actual travel time to predict next location.
# DataSet

```

├── Taxi_Test_Data.csv
├── Taxi_Train_Data.csv
├── VPR_Test_Data.csv
├── VPR_Train_Data.csv

```
We use two datasets, VPR datasets and Taxi dataset to
evaluate our model. The two datasets
are in /input folder of our project.You can divide 
the training set  and test set using 
the ratio you want to use. You can download the data on google drive and put them in input folder,the link is 
```
https://drive.google.com/open?id=1BpV_ADF2uxbupAiULEu-aHO5s-FvlWt_
```

# QuickStart


```
├── graph
│   ├── SingleSlotGraph.java 
│   └── WeightedEdge.java
├── ksp
│   ├── MulThreadODPath.java
│   └── ODData.java
├── model
│   ├── taxi
│   │   ├── GenLocationMap.java
│   │   ├── GenTaxiDataGraph.java
│   │   ├── MM_Taxi.java
│   │   ├── ShortestTimeCalTaxi.java
│   │   └── TTDM_Taxi.java
│   └── vpr
│       ├── GenVPRDataGraph.java
│       ├── MM_Vpr.java
│       ├── ShortestTimeCalVpr.java
│       └── TTDM_Vpr.java
└── utils
    └── SortByProbability.java
```
- Download the datasets on google drive,the linke is given above,  put the files on the corresponding directory of the project

- Run MM_Taxi.java in model/taxi directory can get 

  markov model performance based on the Taxi datasets in the 

  input folder ,while run  TTDM_Taxi.java can get the performance
  
  joint model with parameter lambda decrease from 1 to 0
  
  the step 0.1.
- Run MM_Vpr.java in model/Vpr directory can get 
  
    markov model performance based on the VPR datasets in the 
  
    input folder ,while run  TTDM_Vpr.java can get the performance
    
    of joint model with parameter lambda decrease from 1 to 0
    
    the step 0.1.
# Step By Step

## Generate graph based on training data.

 For VPR data, just run the **GenVPRDataGraph.java**
 and pass the filename you want to generate as a 
 parameter.
 
 For Taxi data, you need to run **GenLocationMap.java**
 first to generate the location_long_map as the 
 node of the graph must be in long form but in taxi data
 is String and then run **GenTaxiDataGraph.java** to get the graph.
 
## Calculate the shortest path of any two locations in a graph offline

 Run **MulThreadODPath.java**,the input is the graph generated above
 and the output is file contains the shortest time information of the input graph. 
 The process is time-consuming so we use multi-thread to speed. Note tha
 we reuse of open source implement  code of the shortest path algorithm here.

## Store the graph and shortest time file in data structure



```
├── graph
│   ├── Taxi_graph.csv
│   └── VPR_graph.csv
├── location_long_map
├── time
│   ├── Taxi_shortest_time.csv
│   └── VPR_shortest_time.csv
```
the /output folder contains the graph and shortest time file we have generated.We store these information in hashmaps.
You can download the files on goole drive,the link is :
```
https://drive.google.com/open?id=1BpV_ADF2uxbupAiULEu-aHO5s-FvlWt_
```

 please see **ShortestTimeCalVpr.java** and
 **ShortestTimeCalTaxi.java**
 
## Train and Test

 The core code of the TTDM(can be seen in TTDM_taxi.java and TTDM_Vpr.java) is as follows:
 
 ``` java
 while (choosen.hasNext()) {
    hoop++;
    if (hoop > 20) break;
    double actualTime = 0.0;
    double shortestTime = 0.0;
    String predictStr = choosen.next();
    String[] predictNode = predictStr.split("@");
 
    Pair<Long, Long> tmpPair = new Pair<Long, Long>(locationMap.get(lastLoction), locationMap.get(predictNode[0]));
 	    double lastweight = 0.0;
 	    if (graphMap.containsKey(tmpPair)) {
 		 lastweight = graphMap.get(tmpPair);
 		}
 	    double validLength = length - 1;
 	    for (int index = 1; index < length; index++) {
 	        String passbyLoaction = temp[index].split("@")[0];
 		String passbytime = temp[index].split("@")[1];
 	        double pathshorttime = ShortestTimeCalTaxi.getShortestTime(locationMap.get(passbyLoaction), locationMap.get(predictNode[0]))
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
 
 ```


   
   


