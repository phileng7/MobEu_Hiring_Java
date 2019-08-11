package com.mobiquityinc.packer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mobiquityinc.exception.APIException;

public class Packer {
	
	private static int MAX_WEIGHT_VALUE = 100;		//Max weight that a package can take is ≤ 100
	private static int MAX_ITEM_WEIGHT_VALUE = 100;	//Max weight of an item is ≤ 100
	private static int MAX_ITEM_COST_VALUE = 100;	//Max cost of an item is ≤ 100	
	
	float maxSumWeight=0;	//maximum sub-array weight
	float maxSumCost=0;		//maximum sub-array cost
	String maxIndex;		//maximum sub-array indexes
	
	Packer(){		
	}
	
	class Item{  
	    float weight;  
	    float cost;  
	    Item(float weight,float cost){   
	        this.weight=weight;  
	        this.cost=cost;  
	    }  
	} 
	
	/*
	 * Process a subset
	 * lstItem - items validated
	 * subset - current subset being processed
	 * maxValue - maximum weight for the package
	 */
	private void processSubset(Map<Integer,Item> lstItem, String subset, int maxValue) {
		
		float sumWeight=0;	//weight sum of the sub array 
		float sumCost=0;	//cost sum of the sub array
		
		String[] strSubset = subset.split(",");
		for (int i=0; i<strSubset.length; i++) {
			int index = Integer.valueOf(strSubset[i]);
			sumWeight+=lstItem.get(index).weight;
			//Discard weight sum greater than the package maximum weight
			if (sumWeight > maxValue) return;
			sumCost+=lstItem.get(index).cost;
		}

		if (sumWeight>0) {
			//the priority is total cost as large as possible OR
			//prefer to send a package which weights less in case there is more than one package with the same price
			if ((sumCost > maxSumCost) || (sumCost == maxSumCost && sumWeight < maxSumWeight)) {		
				maxSumWeight = sumWeight;
				maxSumCost = sumCost;
				maxIndex = subset;
			} 								
		}
	}
	
	/*
	 * Get all subsets and process each one
	 * lstItem - items validated
	 * maxValue - maximum weight for the package
	 */
	private void execSubsets(Map<Integer,Item> lstItem, int maxValue) {
		List<String> subsets = new ArrayList<>();
		//Combination of all subsets from lstItem Index(key)
		lstItem.forEach((index,value) -> {
			int subsetsSize = subsets.size();
			for (int j = 0; j < subsetsSize; j++) {
				subsets.add(String.valueOf(subsets.get(j) + "," +  index));
			}
			subsets.add(String.valueOf(index));
		});
		//Process each subset
		subsets.forEach(subset -> processSubset(lstItem, subset, maxValue));
	}
	
	//Validate each field of the line and call for processing
	private String execPack(String line) throws APIException {
		Map<Integer,Item> lstItem = new HashMap<>();
		
		//Reset variables
		maxSumWeight=0;		//maximum sub-array weight
		maxSumCost=0;		//maximum sub-array cost
		maxIndex = null;	//maximum sub-array indexes
		
		//First split the line and validate
		if (!line.contains(":"))
			throw new APIException("Line without \':\' delimiter => [" + line + "]");
		String[] strArr = line.split(" : ");
		if (strArr.length < 2)
			throw new APIException("Line missing information => [" + line + "]");
		if (strArr[0]==null || strArr[0].trim().isEmpty())
			throw new APIException("Line missing maximum weight package => [" + line + "]");
		
		//get the max weight[0] value and the rest[1]
		if (!strArr[0].matches("-?\\d+(\\.\\d+)?"))
			throw new APIException("Maximum weight package must be numeric => [" + line + "]");
		int maxValue = Integer.valueOf(strArr[0]);	//maximum weight for the package
		if (maxValue > MAX_WEIGHT_VALUE)	//Constraint: Max weight that a package can take is ≤ 100	
			return "-";
		
		//get each group(index,weight,cost) of the line, validate and put in a list to be processed
		for (String item : strArr[1].split(" ")) {
			String[] strValues = item.replaceAll("^\\(|\\)$", "").split(",");
			if (strValues.length < 3)
				throw new APIException("Field missing \',\' delimiter or information => [" + strArr[1] + "]");
			if (!strValues[0].matches("-?\\d+(\\.\\d+)?"))
				throw new APIException("Index must be numeric => [" + line + "]");
			int itemIndex = Integer.valueOf(strValues[0]);		//index
			if (!strValues[1].matches("-?\\d+(\\.\\d+)?"))
				throw new APIException("Weight must be numeric => [" + line + "]");
			float itemWeight = Float.valueOf(strValues[1]);		//weight
			//Constraint: Max weight of an item is ≤ 100
			//Max weight of an item must be less than Max weight of the package
			if (itemWeight <= MAX_ITEM_WEIGHT_VALUE && itemWeight <= maxValue) {
				//Extract number from the String discarding non-digits
				String extractCost = strValues[2].replaceAll("\\D+","");
				if (!extractCost.matches("-?\\d+(\\.\\d+)?"))
					throw new APIException("Cost must be numeric => [" + line + "]");
				float itemCost = Float.valueOf(extractCost);	//cost
				//Constraint: Max cost of an item is ≤ 100
				if (itemCost <= MAX_ITEM_COST_VALUE) {
					//add valid register to the list
					//System.out.println(itemIndex + " " + itemWeight + " " + itemCost);
					Item newItem = new Item(itemWeight,itemCost);
					lstItem.put(itemIndex, newItem);		//map key=index
				}
			}
		}
		//If no one is valid then return
		if (lstItem.isEmpty()) {
			return "-";
		}
		
		//Display
//		lstItem.forEach((k,v) -> System.out.println("Key(Index) = " + k + ", Weight = " + v.weight + ", Cost = " + v.cost)); 
//		System.out.println("\nLine result:");
		
		//From the validated list, generate subsets
		execSubsets(lstItem, maxValue);
		
		if (maxIndex==null || maxIndex.trim().isEmpty()) {
			return "-";
		}
		//return index numbers
		return maxIndex;
	}

	/*
	 * filePath - the path filename to be read
	 */
	public static String pack(String filePath) throws APIException {
		FileInputStream fileInputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		StringBuffer res = new StringBuffer();

		try {
			fileInputStream = new FileInputStream(filePath);
			// Euro special character charset
			inputStreamReader = new InputStreamReader(fileInputStream, "Cp1250");
			bufferedReader = new BufferedReader(inputStreamReader);
			String line;
			Packer packer = new Packer();
			while ((line = bufferedReader.readLine()) != null) {				
				//Process the line and return the items to mount the package
				res.append(packer.execPack(line) + "\n");
			}
			bufferedReader.close();
			inputStreamReader.close();
			fileInputStream.close();
		} catch (FileNotFoundException e) {
			throw new APIException("File not found");
			//e.printStackTrace();
		} catch (IOException e) {
			throw new APIException("Read file exception");
			//e.printStackTrace();
		}
		return res.toString();
	}
}
