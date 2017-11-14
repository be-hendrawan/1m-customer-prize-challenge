/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package redmarttote;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author HP DV6
 */
public class RedMartTote {

    List<RedMartItem> inputData = new ArrayList<>();
    KnapsackItem[][] knapsackTable;
    int toteVolume =  45 * 30 * 35;
    int lowestVolume = Integer.MAX_VALUE;

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        RedMartTote pgm = new RedMartTote();
        pgm.LoadData();
        pgm.SolveKnapsack();
    }

    void LoadData() throws Exception {
        // Load data from server
        InputStream is = new URL("https://s3-ap-southeast-1.amazonaws.com/geeks.redmart.com/coding-problems/products.csv").openConnection().getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String dataRow = reader.readLine();
                
        int[] toteDimension = {30, 35, 45};
        while (dataRow != null) {
            String[] dataArray = dataRow.split(",");
            
            // Include only item that can fit individually
            int[] itemDimension = { Integer.valueOf(dataArray[2]), Integer.valueOf(dataArray[3]), Integer.valueOf(dataArray[4]) };
            Arrays.sort(itemDimension);
            if (itemDimension[0] <= toteDimension[0] && itemDimension[1] <= toteDimension[1] && itemDimension[2] <= toteDimension[2]) {
                int itemVolume = Integer.valueOf(dataArray[2]) * Integer.valueOf(dataArray[3]) * Integer.valueOf(dataArray[4]);
                inputData.add(new RedMartItem(Integer.valueOf(dataArray[0]), Integer.valueOf(dataArray[1]), itemVolume, Integer.valueOf(dataArray[5])));
                
                // Obtain the lowest volume
                if (itemVolume < lowestVolume) {
                    lowestVolume = itemVolume;
                }
            }
            dataRow = reader.readLine();
        }
    }
    
    void SolveKnapsack() {
           
        // Proof of Concept. Original algorithm from http://rerun.me/2014/05/27/the-knapsack-problem/
        /*
        inputData = new ArrayList<>();
        inputData.add(new RedMartItem(1, 10, 5, 0));
        inputData.add(new RedMartItem(2, 40, 4, 0));
        inputData.add(new RedMartItem(3, 30, 6, 0));
        inputData.add(new RedMartItem(4, 50, 3, 0));        
        toteVolume = 10;
        lowestVolume = 3;
        */
        
        // Actual knapsack algorithm is [inputData.size()+1][toteVolume+1], but encounter java heap size error due to large dataset.
        // So, create a truncated table by lowestVolume, and map the volume to variable actualVolume
        knapsackTable = new KnapsackItem[inputData.size()+1][toteVolume+1-lowestVolume+1];
        
        for (int col = 0; col <= toteVolume+1-lowestVolume; col++) {
            knapsackTable[0][col] = new KnapsackItem(0,0,0,"");
        }

        for (int row = 0; row <= inputData.size(); row++) {
            knapsackTable[row][0] = new KnapsackItem(0,0,0,"");
        }
        
        int newTotalPrice;
        int newWeight;
        
        for (int item=1; item<=inputData.size(); item++) {
            
            // Clean up unused index to free up memory due to large dataset            
            if (item > 1) {
                for (int col = 0; col <= toteVolume-lowestVolume+1; col++) {
                    knapsackTable[item-2][col] = null;
                }
            }
                        
            System.out.println("Processing item " + item);
                        
            RedMartItem curItem = inputData.get(item-1);  
            for (int volumeIndex=1; volumeIndex<=toteVolume-lowestVolume+1; volumeIndex++) {
                int actualVolume = volumeIndex+lowestVolume-1;
                            
                if (curItem.Volume <= actualVolume) {
                    KnapsackItem prevHighest = knapsackTable[item-1][volumeIndex];
                                        
                    // For those that below lowestVolume, create a 0 value item.
                    KnapsackItem curHighest;
                    if (actualVolume-curItem.Volume >= lowestVolume) {
                        curHighest = knapsackTable[item-1][volumeIndex-curItem.Volume];
                    }
                    else {
                        curHighest = new KnapsackItem(0,0,0,"");
                    }
                    
                    newTotalPrice = curItem.Price + curHighest.TotalPrice;
                    newWeight = curItem.Weight + curHighest.TotalWeight;
                    
                    if ( (newTotalPrice > prevHighest.TotalPrice) ||
                         (newTotalPrice == prevHighest.TotalPrice && newWeight < prevHighest.TotalWeight) ) {
                        knapsackTable[item][volumeIndex] = new KnapsackItem(curHighest.TotalPrice + curItem.Price, curHighest.TotalProductID + curItem.ProductID, curHighest.TotalWeight + curItem.Weight, curHighest.ItemList.concat("-" + curItem.ProductID));
                    }
                    else {
                        knapsackTable[item][volumeIndex] = knapsackTable[item-1][volumeIndex];
                    }
                }
                else {
                    knapsackTable[item][volumeIndex] = knapsackTable[item-1][volumeIndex];
                }
            }            
        }
        
        KnapsackItem knapsackSolution = knapsackTable[inputData.size()][toteVolume-lowestVolume];
        System.out.println("Total Price: " + knapsackSolution.TotalPrice);
        System.out.println("Total ProductID: " + knapsackSolution.TotalProductID);
        System.out.println("Item List: " + knapsackSolution.ItemList);
    }

    private static class RedMartItem {
        int ProductID;
        int Price;
        Integer Volume;
        int Weight;

        private RedMartItem(int ProductID, int Price, int Volume, int Weight) {
            this.ProductID = ProductID;
            this.Price = Price;
            this.Volume = Volume;
            this.Weight = Weight;
        }
    }
    
    private static class KnapsackItem {
        int TotalPrice;
        int TotalProductID;
        int TotalWeight;
        String ItemList;

        public KnapsackItem(int TotalPrice, int TotalProductID, int TotalWeight, String ItemList) {
            this.TotalPrice = TotalPrice;
            this.TotalProductID = TotalProductID;
            this.TotalWeight = TotalWeight;
            this.ItemList = ItemList;
        }
    }
}
