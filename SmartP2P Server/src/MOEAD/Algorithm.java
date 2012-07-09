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

 


package MOEAD;

import java.util.*;
import java.util.Random;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.text.*;
import java.text.NumberFormat;

//import MOEAD.Problems.Problem;
import MOEAD.PopInits.*;
//import MOEAD.Solutions.*;

/* The main structure of the MOEAD algorithm
*/
public class Algorithm {
    
    //=============================== VARIABLES===============================/
    //Global General Variables
    //=======================
    
    /**Population Size*/
    public int popSize = 0;
    
     /**Current generation*/
    public int gen = 0;
    
    /**termination criterion as well as the latest generation to be created*/
    public int termination = 5;
    
    /**The current IP population of size equals popSize*/
    public MOEAD.Solutions.Solution[] currentPopIP;
    
    /**The current IP population of size equals popSize*/
    public ArrayList currentEP = new ArrayList();

    /** The size of one solution*/
    public int chromosomeSize=0;
    
    /**The arrayList which keeps all the weighVectors*/     
     public ArrayList keepsWV = null;
     
     /**The neighborhood size of each subproblem*/ 
      public int neighborhoodSizeT = 10;
      
      /**The maximum solutions to modify in the neighborhood*/ 
      public  int maximumToModify = 0;
     
     NumberFormat nf;
     String newFolderPath = "./";
    //"C:/Users/natalie/Desktop/Mobile Agent Routing/MOEADWITHLS/MOEAD/";
    String newFolderName = "";
    public String link = "";
    public double time = 0.0;
    double timeStart = 0.0;
    
    public String queryString = " ";
    
       
       
     
    //Problem-specific Variables
    //=======================
                
    /** The chosen problem instance*/
    public MOEAD.Problems.QueryRoutingTreeProblem problem = null;
     
    
    //=============================END OF VARIABLES============================/
    
    
    /** Creates a new instance */
    public Algorithm(int id, String word) { 
       

       /* queryString = "0:";
        if(id <= 9){queryString += "0";}
        queryString += id + ":0[0-9]";*/
        
        
        nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(10);
        
        String newFolderName1 = "src/MOEAD/Exp"+(id);
        link = newFolderPath + newFolderName1;
        File f1 = new File(link);
        f1.mkdir();
        
        problemInitialization(id, word);

        //CHOOSE decomposition based on number of objectives
        decomposeThreeObjProblem();
        
        currentPopIP = new MOEAD.Solutions.QRTPSolution.OneSolution[popSize];
        //CHOOSE PROBLEMS CHROMOSOME SIZE
      
        timeStart = System.currentTimeMillis();
        startAlgorithm();
        
    
    }//END OF CONSTRUCTOR
    

    void problemInitialization(int id, String word){
    
        //CHOOSE PROBLEM
        problem = new MOEAD.Problems.QueryRoutingTreeProblem(this);
        
        //initialize problem based on specific requirements
        problem.readDatabase(id, word);
    
    }//END OF problemInitialization() method
    
    void startAlgorithm(){
                   
        //initialize population
        initializePopulation();
        updateEP();
        
        do{
            
            System.out.println();
            System.out.println("GENERATION "+gen);
            System.out.println("=============");
            
                               
            //start reproduction 
              ga();
                
            //start Local Search
              //ls();
                        
           //update the extrnal population
             updateEP();
            
            gen=gen+1;
            
        }while(gen < termination);
        
        double timeStop = System.currentTimeMillis();        
        time = timeStop - timeStart;        
        //in minutes
        System.out.println(time);
        //write data for statistics
        writeData();
                
    
    }//END OF startAlgorithm() method
    
    void initializePopulation(){
        
        PopInit popInit = new MOEAD.PopInits.Random.RandomPopInit(this);
        popInit.initialize();
        this.currentPopIP = popInit.getPopIP();
            
               
     }//END OF initializePopulation() method
     
    
    void decomposeThreeObjProblem(){
         
          int n = 3;//number of objectives
          double delta = 0.05255529; // step size for popSize=200
          int p = (int) (1/delta); //has to be integer
      
          
          keepsWV = new ArrayList();
                   
              double theta1 = 0.0;// represents W1
              double maxWeight1 = 1;
                                   
              while(theta1 <= maxWeight1){
              
                  double endingPointOfW2 = (p - (theta1/delta))*delta;
                  double theta2 = 0.0;
                  double maxWeight2 = endingPointOfW2;
                  
                  while(theta2 <= maxWeight2){
                  
                  double[] weightVector = new double[n];
                            weightVector[0]= theta1;//value of W1
                            weightVector[1]= theta2; 
                            weightVector[2]= 1-(theta1+theta2);
                            
                            keepsWV.add(weightVector);                            
                            theta2 = theta2 + delta;
                  }
                  
                  theta1 = theta1 + delta;
              }
                       
              popSize = keepsWV.size();
              System.out.println("popSize "+popSize);  
         
     }//END OF initializePopulation() method
     
     
     void ga(){
         
         MOEAD.GA.GeneticAlgorithm ga = new MOEAD.GA.GeneticAlgorithm(this);
         currentPopIP = ga.getCurrentIP();
         
         //PRINT INTERNAL POPULATION
         /*for(int i = 0 ; i < currentPopIP.length; i++){             
             System.out.println();
             System.out.println("Subproblem "+i);
             MOEAD.Solutions.Solution testSol = (MOEAD.Solutions.Solution)currentPopIP[i];
             //int[] testChromosome = testSol.getChromosome();
             //for(int j = 0; j < testChromosome.length; j++){         
             //System.out.print(testChromosome[j]+" ");             
             //}System.out.println();
             System.out.println("Makespan "+testSol.getMakespan() +" Max Tardiness "+testSol.getMaxTardiness());         
         }*/
     }//END OF ga() method
     
      void ls(){
          
          //MOEAD.LocalSearchMethods.LocalSearch ls = new MOEAD.LocalSearchMethods.LocalSearch(this);
          //currentPopIP = ls.getCurrentIP();
          
          //PRINT INTERNAL POPULATION
         /*for(int i = 0 ; i < currentPopIP.length; i++){             
             System.out.println();
             System.out.println("Subproblem "+i);
             MOEAD.Solutions.Solution testSol = (MOEAD.Solutions.Solution)currentPopIP[i];
             //int[] testChromosome = testSol.getChromosome();
             //for(int j = 0; j < testChromosome.length; j++){         
             //System.out.print(testChromosome[j]+" ");             
             //}System.out.println();
             System.out.println("Makespan "+testSol.getMakespan() +" Max Tardiness "+testSol.getMaxTardiness());
         }*/
         
     }//END OF ls() method
     
      
      /**Based on Ischibushi and MOEA/D papers for flowshop, they use 2-obj
       *makespan and max tardiness, for third objecitve the use average flowtime.
       */
     void updateEP(){
         
         for(int i=0; i<currentPopIP.length; i++){
         
             if(currentEP.size() == 0){
                 
                 currentEP.add((MOEAD.Solutions.Solution)currentPopIP[i]); 
            
             }else{
             
                MOEAD.Solutions.Solution newSolution = (MOEAD.Solutions.Solution)currentPopIP[i];
                 
                boolean dominated = false;
                
for(int j = 0 ; j < currentEP.size(); j ++){
                 
   MOEAD.Solutions.Solution oldSolution = (MOEAD.Solutions.Solution)currentEP.get(j); 
                     
if(oldSolution.getEnergy() > newSolution.getEnergy() && oldSolution.getTime() > newSolution.getTime() && oldSolution.getRecall() < newSolution.getRecall() ||
oldSolution.getEnergy() > newSolution.getEnergy() && oldSolution.getTime() > newSolution.getTime() && oldSolution.getRecall() == newSolution.getRecall() ||
oldSolution.getEnergy() > newSolution.getEnergy() && oldSolution.getTime() == newSolution.getTime() && oldSolution.getRecall() < newSolution.getRecall() ||
oldSolution.getEnergy() == newSolution.getEnergy() && oldSolution.getTime() > newSolution.getTime() && oldSolution.getRecall() < newSolution.getRecall() ||
oldSolution.getEnergy() > newSolution.getEnergy() && oldSolution.getTime() == newSolution.getTime() && oldSolution.getRecall() == newSolution.getRecall() ||
oldSolution.getEnergy() == newSolution.getEnergy() && oldSolution.getTime() > newSolution.getTime() && oldSolution.getRecall() == newSolution.getRecall() ||
oldSolution.getEnergy() == newSolution.getEnergy() && oldSolution.getTime() == newSolution.getTime() && oldSolution.getRecall() < newSolution.getRecall())
{                    
                       currentEP.remove(j);
                       //j=0;
                       
}
   
if(oldSolution.getEnergy() < newSolution.getEnergy() && oldSolution.getTime() < newSolution.getTime() && oldSolution.getRecall() > newSolution.getRecall() ||
oldSolution.getEnergy() < newSolution.getEnergy() && oldSolution.getTime() < newSolution.getTime() && oldSolution.getRecall() == newSolution.getRecall() ||
oldSolution.getEnergy() < newSolution.getEnergy() && oldSolution.getTime() == newSolution.getTime() && oldSolution.getRecall() > newSolution.getRecall() ||
oldSolution.getEnergy() == newSolution.getEnergy() && oldSolution.getTime() < newSolution.getTime() && oldSolution.getRecall() > newSolution.getRecall() ||
oldSolution.getEnergy() < newSolution.getEnergy() && oldSolution.getTime() == newSolution.getTime() && oldSolution.getRecall() == newSolution.getRecall() ||
oldSolution.getEnergy() == newSolution.getEnergy() && oldSolution.getTime() < newSolution.getTime() && oldSolution.getRecall() == newSolution.getRecall() ||
oldSolution.getEnergy() == newSolution.getEnergy() && oldSolution.getTime() == newSolution.getTime() && oldSolution.getRecall() > newSolution.getRecall() ||
oldSolution.getEnergy() == newSolution.getEnergy() && oldSolution.getTime() == newSolution.getTime() && oldSolution.getRecall() == newSolution.getRecall())
{   
    //new solution is dominated
    dominated = true;     
}
}// end of for j 
                
                     if(dominated == false){
                     
                         currentEP.add((MOEAD.Solutions.Solution)newSolution);
                     }                                             
             }//end of if statement  
         }// end of for i

         
         if(this.gen == this.termination-1){
         System.out.println("PRINTING EP of size "+currentEP.size());
          for(int i=0; i<currentEP.size(); i++){
              
               MOEAD.Solutions.Solution solution = (MOEAD.Solutions.Solution)currentEP.get(i);
               System.out.println("Solution "+i+" Min-Energy: "+nf.format(solution.getEnergy())+", Min-Time: "+nf.format(solution.getTime())
               +" and Max-Recall "+nf.format(solution.getRecall()));

          }}
         
     }//END OF updateEP() method
     
     void writeData(){
         
          MOEAD.Write.WriteSolutions ws = new MOEAD.Write.WriteSolutions(this, currentEP);
         
     }//END OF writeData() method
    
}//END OF CLASS Algorithm