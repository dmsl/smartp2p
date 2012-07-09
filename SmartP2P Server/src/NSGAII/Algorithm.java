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

 package NSGAII;


import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.text.*;

//import NSGAII.Problems.Problem;
import NSGAII.PopInits.*;
//import NSGAII.Solutions.*;

/* The main structure of the NSGAII algorithm
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
    protected int termination = 500;
    
    /**The current IP population of size equals popSize*/
    public NSGAII.Solutions.Solution[] currentPopIP;
    
    /**The current IP population of size equals popSize*/
    public ArrayList currentEP = new ArrayList();

    /** The size of one solution*/
    public int chromosomeSize=0;
    
    /**The arrayList which keeps all the weighVectors*/     
     public ArrayList keepsWV = null;
     
     /**The neighborhood size of each subproblem*/ 
      public int neighborhoodSizeT = 14;
      
      /**The maximum solutions to modify in the neighborhood*/ 
      public  int maximumToModify = 0;

     /**The EP which keeps non-dominated solutions
     */
     public ArrayList frontToPrint;
     ArrayList popFromGA = null;
     
     NumberFormat nf;
     String newFolderPath = "./";
    String newFolderName = "";
    public String link = "";
    public double time = 0.0;
    double timeStart = 0.0;
       
     
    //Problem-specific Variables
    //=======================
                
    /** The chosen problem instance*/
    public NSGAII.Problems.QueryRoutingTreeProblem problem = null;
     
    
    //=============================END OF VARIABLES============================/
    
    
    /** Creates a new instance */
    public Algorithm(int id, String word) {

        frontToPrint = new ArrayList();
        nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(10);
        
         String newFolderName1 = "src/NSGAII/Exp"+(id);
        link = newFolderPath + newFolderName1;
        File f1 = new File(link);
        f1.mkdir();
        
        //CHOOSE decomposition based on number of objectives
        decomposeThreeObjProblem();
        
        problemInitialization(id, word);
        
        
        currentPopIP = new NSGAII.Solutions.QRTPSolution.OneSolution[popSize];
        //CHOOSE PROBLEMS CHROMOSOME SIZE
        //chromosomeSize = problem.activeUsers;
        
        timeStart = System.currentTimeMillis();
        startAlgorithm();
        
    
    }//END OF CONSTRUCTOR
    
    
    void problemInitialization(int id, String word){
    
        //CHOOSE PROBLEM
        problem = new NSGAII.Problems.QueryRoutingTreeProblem(this);
        
        //initialize problem based on specific requirements
        problem.readDatabase(id, word);
    
    }//END OF problemInitialization() method
    
    void startAlgorithm(){
                   
        //initialize population
        initializePopulation();
        
        
        do{
            
            
            
            /*System.out.println();
            System.out.println("GENERATION "+gen);
            System.out.println("=============");*/
            
            update();
                               
            //start reproduction 
              ga();
            
            //write data for statistics
            //writeData();   
            
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
        
        PopInit popInit = new NSGAII.PopInits.Random.RandomPopInit(this);
        popInit.initialize();
        this.currentPopIP = popInit.getPopIP();
            
               
     }//END OF initializePopulation() method
     
    
    void decomposeThreeObjProblem(){
         
          int n = 3;//number of objectives
          double delta = 0.05255529; // step size for popSize = 200
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
         
         NSGAII.GA.GeneticAlgorithm ga = new NSGAII.GA.GeneticAlgorithm(this);         
         currentPopIP = null;
         currentPopIP = new NSGAII.Solutions.Solution[popSize];
         popFromGA = new ArrayList();
         popFromGA = ga.getNewPop();         
         
     }//END OF ga() method
     
      
      
    public void update(){

        Update update = new Update(this);
        frontToPrint = null;
        frontToPrint = new ArrayList();
        frontToPrint = update.getFrontToPrint();  
        currentPopIP = update.getNewPop();
        
         if(gen == termination-1){
             calculateMinMax();
         }

         //PRINT INTERNAL POPULATION
         /*for(int i = 0 ; i < currentPopIP.length; i++){             
             System.out.println();
             System.out.println("Subproblem "+i);
             NSGAII.Solutions.Solution testSol = (NSGAII.Solutions.Solution)currentPopIP[i];
             //int[] testChromosome = testSol.getChromosome();
             //for(int j = 0; j < testChromosome.length; j++){         
             //System.out.print(testChromosome[j]+" ");             
             //}System.out.println();
             System.out.println("Makespan "+testSol.getMakespan() +" Max Tardiness "+testSol.getMaxTardiness());         
         }*/

        }
    
     public void calculateMinMax(){
    
        double minEnergy = Double.MAX_VALUE;
        double maxEnergy = Double.MIN_VALUE;
        double minTime = Double.MAX_VALUE;
        double maxTime = Double.MIN_VALUE;
        double minRecall = Double.MAX_VALUE; 
        double maxRecall = Double.MIN_VALUE;
        
        int solutionWithMinEnergy = 0;
        int solutionWithMinTime = 0;
        int solutionWithMaxRecall = 0;
        
        for(int i = 0 ; i < frontToPrint.size() ; i++){
        
            NSGAII.Solutions.Solution testSol = (NSGAII.Solutions.Solution)frontToPrint.get(i);
            if(testSol.getEnergy() < minEnergy){minEnergy = testSol.getEnergy();solutionWithMinEnergy=i;}
            if(testSol.getEnergy() > maxEnergy) maxEnergy = testSol.getEnergy();
            if(testSol.getTime() < minTime){ minTime = testSol.getTime();solutionWithMinTime=i;}
            if(testSol.getTime() > maxTime) maxTime = testSol.getTime();
            if(testSol.getRecall() < minRecall) minRecall = testSol.getRecall();
            if(testSol.getRecall() > maxRecall) {maxRecall = testSol.getRecall(); solutionWithMaxRecall=i;}
        }
        
            NSGAII.Solutions.Solution test =  (NSGAII.Solutions.Solution)frontToPrint.get(solutionWithMinEnergy);
            System.out.println("Min Energy Sol: minEnergy"+nf.format(test.getEnergy())+" time "+nf.format(test.getTime())+" recall "+nf.format(test.getRecall()));
            test =  (NSGAII.Solutions.Solution)frontToPrint.get(solutionWithMinTime);
            System.out.println("Min Time Sol: Energy"+nf.format(test.getEnergy())+" minTime "+nf.format(test.getTime())+" recall "+nf.format(test.getRecall()));
            test =  (NSGAII.Solutions.Solution)frontToPrint.get(solutionWithMaxRecall);
            System.out.println("Max Recall Sol: Energy"+nf.format(test.getEnergy())+" time "+nf.format(test.getTime())+" MaxRecall "+nf.format(test.getRecall()));
            
        
        
        
    
    }//END OF calculateMinMax() method 
      
     
     void writeData(){
         
          NSGAII.Write.WriteSolutions ws = new NSGAII.Write.WriteSolutions(this, frontToPrint);
         
     }//END OF writeData() method
    
}//END OF CLASS Algorithm