/*
 * This is framework for searching objects (e.g. images, etc.) captured 
 * by the users in a mobile social community. Our framework is founded on an 
 * in-situ data storage model, where captured objects remain local on their owner’s 
 * smartphones and searches take place over a lookup structure we compute dynamically. 
 * Initially, a query user invokes a search to find an object of interest. 
 * Our structure concurrently optimizes several conflicting objectives using a 
 * MultiObjective Optimization approach and calculates a set of high quality nondominated 
 * Query Routing Trees (QRTs) in a single run. The optimal set is then forwarded to the query 
 * user to select a QRT to be searched based on instant requirements and preferences. 
 * To demonstrate the SmartP2P we utilize our cloud of smartphones (SmartLab) composed 
 * of 40 Android devices. The conference attendees will be able to appreciate how social 
 * content can be efficiently shared without revealing their personal content to a centralized 
 * authority. 
 * 
 *Copyright (C) 2011 - 2012 Christos Aplitsiotis
 *This program is free software: you can redistribute it and/or modify
 *it under the terms of the GNU General Public License as published by
 *the Free Software Foundation, either version 3 of the License, or
 *at your option) any later version.
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *GNU General Public License for more details.
 *?ou should have received a copy of the GNU General Public License
 *along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

 

package MOEAD.Problems;

import java.util.*;
import java.util.Random;

public class QueryRoutingTreeProblem {
    
   
    double[][] distance_list;
    int[][] file_list;
    //Bluetooth Class 2.0 consumes 14.965 mW (mJ/s) per byte so 1.87mW per bit
    double blueTooth_EnergyCons = 0.0018;
    //3G consumes 26.785 mW (mJ/s) per byte so 3.34mW per bit
    double threeG_EnergyCons = 0.00334;    
    //data rate up to 64kbps
    double blueTooth_Uplink = 64000.0;
    //uplink 384kbps
    double threeG_Uplink = 384000.0;
    double relevant = 0.0;
     double totalPapers = 0.0;
    
    int[] parent_list;
    Random randomGen = new Random(System.currentTimeMillis());    
    protected MOEAD.Algorithm alg = null;

    

    
    /** Creates a new instance of QueryRoutingTreeProblem */
    public QueryRoutingTreeProblem(MOEAD.Algorithm alg) {
    
        this.alg=alg;
           
    }
    
	//APLITSIOTIS
    public void readDatabase(int id, String word){
    
        MOEAD.SQLServer.DBConnector obj = new  MOEAD.SQLServer.DBConnector("127.0.0.1", 1433, "SMARTP2P");

		alg.chromosomeSize = obj.getCountUsers();		
        distance_list = obj.getDistanceOfUsers(id);
		file_list = obj.getNumberOfPapers(word, id);
		
                for (int i = 0; i < alg.chromosomeSize; i++){            

                    relevant = relevant + file_list[i][1];
                    totalPapers = totalPapers + file_list[i][0];
                }        
                
                System.out.println("Active Users: "+alg.chromosomeSize);    
    }
    
    public MOEAD.Solutions.Solution evaluateSolution(MOEAD.Solutions.Solution currentSolution){
    
        parent_list = currentSolution.getChromosome();
        
        try{
        //EVALUATING TOTAL ENERGY CONSUMPTION
        double totalEnergy = 0.0;
        for (int i = 1; i < parent_list.length; i++){

               totalEnergy = totalEnergy + evaluateEnergy(i);
            }
        if(totalEnergy==0)totalEnergy=100000;
        currentSolution.setEnergy(totalEnergy);
        //System.out.println("Total Energy Consumption "+totalEnergy);
         }catch(java.lang.StackOverflowError e){
       
           System.out.println("Exception in Energy");
           System.exit(0);
        }
        
        try{
         //EVALUATING MAX TRANSMITTED TIME
        double maxTime = evaluateTime(0);
        if(maxTime==0)maxTime=100000;

        currentSolution.setTime(maxTime);
        //System.out.println("Max Time "+maxTime);
        }catch(java.lang.StackOverflowError e){
       
           System.out.println("Exception in Time");
           System.exit(0);
        }
        
         //EVALUATING RECALL
         double recall = evaluateRecall();                    
         currentSolution.setRecall(recall);
         System.out.println("Recall: "+recall);

                return currentSolution;
            }
    
    public double evaluateEnergy(int node){
    
      
           
       if (node == 0 || parent_list[node] == -1){
           return 0.0;
       }else{

                if (distance_list[node][parent_list[node]] <= 30){

                    // here 2.0 is the total size of packets to be transmitted over uplink to see how much time we transmit
                    return((blueTooth_EnergyCons * ((file_list[node][2]*8)/blueTooth_Uplink)) + evaluateEnergy(parent_list[node]));

                }else{

                    // here 2 is the total size of packets to be transmitted over uplink to see how much time we transmit
                    return ((threeG_EnergyCons * ((file_list[node][2]*8)/threeG_Uplink)) + evaluateEnergy(parent_list[node]));
                }
            }
      
    }//END OF evaluateEnergy() METHOD
    
    public double evaluateTime(int node){
    
            double max_time = 0.0;
            double child_time = 0.0;      

                 
            for (int i = 1; i < parent_list.length; i++){

                //if you are a child of mine
                if (parent_list[i] == node){

                    child_time = evaluateTime(i);
                    if (child_time > max_time) max_time = child_time;
                }
            }

            if(node == 0) return max_time;
                
            if (distance_list[node][parent_list[node]] <= 30){

                //getTime(ENUMERATION.W_PROFILE, getFileSize(node))
                // here 2 is the total size of packets to be transmitted
                return (max_time + (file_list[node][2]*8)/blueTooth_Uplink);

            }else{

                // here 2 is the total size of packets to be transmitted
                return (max_time + (file_list[node][2]*8)/threeG_Uplink);
            }
       
    }//END OF evaluateTime() METHOD
    
    
    public double evaluateRecall(){
    
        double totalRecall = 0.0;
        for(int i = 1 ; i < parent_list.length; i++){
            
            if(parent_list[i] != -1){
            
                //the 2 is the total number of files to be transmitted, not the size
                // totalRecall = totalRecall + parent_list[i].getNumberOfFile;   
                totalRecall = totalRecall + (file_list[i][1]/relevant);
            }
        }        
           return totalRecall; 
       
    
    }//END OF evaluateRecall() METHOD
}//end of class
