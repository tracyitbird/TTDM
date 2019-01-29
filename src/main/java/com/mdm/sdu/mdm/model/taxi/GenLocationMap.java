package com.mdm.sdu.mdm.model.taxi;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Since the location information's form in the taxi dataset is a String ,
 * we need to convert it to the form of long.
 * run this class and you can get the output file: output/location_long_map
 */
public class GenLocationMap {



    public static void initlocationmap(String path) throws IOException {

         Map<String,Long> locationToLongMap = new HashMap<String, Long>();



        Set<String> locationSet =  new HashSet<String>();

        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));

        String line ;

        while(true) {
            line = bufferedReader.readLine();
            if (line == null) {
                break;
            }
            String[] lineArray = line.split(",");
            for (int i = 1; i < lineArray.length; i++) {
                String location = lineArray[i].split("@")[0];
                locationSet.add(location);


            }


        }
        System.out.println("location num:"+locationSet.size());

        bufferedReader.close();

        long code = 1l;


        for(String s:locationSet){
            locationToLongMap.put(s,code);
            code++;

        }

        System.out.println("location map size:"+locationToLongMap.size());

        BufferedWriter br = new BufferedWriter(new FileWriter("location_long_map"));

        for(String s:locationToLongMap.keySet()){
            br.write(s);
            br.write("    ");
            br.write(""+locationToLongMap.get(s));
            br.newLine();
        }
        br.flush();
        br.close();




    }

    public static void main(String[] args) {

        String path =args[0];

        try {
            GenLocationMap.initlocationmap(path);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }







}
