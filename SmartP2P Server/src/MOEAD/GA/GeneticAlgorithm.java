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

 


package MOEAD.GA;

import java.util.ArrayList;
import java.util.Random;
import java.text.NumberFormat;


public class GeneticAlgorithm {
    
    //VARIABLES
    //=========
    protected MOEAD.Algorithm alg = null;
    public MOEAD.Solutions.Solution[] currentPopIPforGA;
    public int currentSubproblem = 0;
    public ArrayList keepsWV = null;
    double crossoverRate = 0.7;
    double mutationRate = 0.1;
    Random randomGen = null;
    NumberFormat nf;

    
    
    /** Creates a new instance of GeneticAlgorithm */
    public GeneticAlgorithm(MOEAD.Algorithm alg) {
        
        this.alg = alg;
        this.currentPopIPforGA = alg.currentPopIP;
        this.keepsWV = alg.keepsWV;
        nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(10);
        
        for(int i = 0 ; i < alg.popSize ; i++){
            
            this.currentSubproblem = i;            
            
            //CHOOSE SELECTION STRATEGY
            MOEAD.GA.Selection.RandomNeighborhoodSelectionZHANG randSelect = new MOEAD.GA.Selection.RandomNeighborhoodSelectionZHANG(currentPopIPforGA, currentSubproblem, alg.neighborhoodSizeT);
            ArrayList selectedParents = new ArrayList();
            selectedParents = randSelect.select();
            //----------------------------------
                       
             randomGen = new Random(System.currentTimeMillis());
             double randC = (double) randomGen.nextInt(100)/100;
             MOEAD.Solutions.Solution offspring = new MOEAD.Solutions.QRTPSolution.OneSolution(alg.chromosomeSize);
             if(randC < crossoverRate){            
                 //CHOOSE CROSSOVER STRATEGY   
                 MOEAD.GA.Crossover.TwoPointCrossover crossover = new MOEAD.GA.Crossover.TwoPointCrossover(selectedParents, alg); 
                 offspring = crossover.recombine();
            }else{            
                 
                offspring = (MOEAD.Solutions.Solution)currentPopIPforGA[currentSubproblem];
            }
            //----------------------------------
            
             
             MOEAD.Solutions.Solution mutatedOffspring = new MOEAD.Solutions.QRTPSolution.OneSolution(alg.chromosomeSize);
             MOEAD.GA.Mutation.SwapMutation mutation = new  MOEAD.GA.Mutation.SwapMutation(offspring, alg, mutationRate);
             mutatedOffspring = mutation.mutate();           
            //----------------------------------
           
             
             MOEAD.Solutions.Solution repairedOffspring = new MOEAD.Solutions.QRTPSolution.OneSolution(alg.chromosomeSize);
             MOEAD.LocalSearchMethods.RepairH.RepairTree repairT = new  MOEAD.LocalSearchMethods.RepairH.RepairTree(mutatedOffspring);
             repairedOffspring = repairT.repair();           
            //----------------------------------
                        
             
            updateIP(repairedOffspring);
        }//end of for popSize
    }//end of constructor GeneticAlgorithm()
    
    public void updateIP(MOEAD.Solutions.Solution currentRepairedOffspring){
        
            ArrayList minMax = calculateMinMax();
            double minEnergy = Double.parseDouble((String)minMax.get(0));
            double maxEnergy = Double.parseDouble((String)minMax.get(1));
            double minTime = Double.parseDouble((String)minMax.get(2));
            double maxTime = Double.parseDouble((String)minMax.get(3));
            double minRecall = Double.parseDouble((String)minMax.get(4));
            double maxRecall = Double.parseDouble((String)minMax.get(5));
    
            double[] weightVector = (double[])keepsWV.get(currentSubproblem);
        
            MOEAD.Solutions.Solution repairedOffspring = alg.problem.evaluateSolution(currentRepairedOffspring);
            MOEAD.Solutions.Solution existingSolution = (MOEAD.Solutions.Solution)currentPopIPforGA[currentSubproblem];
            
            
            /* PRINT CURRENT AND OFFSPRING SOLUTIONS
             System.out.println();
             System.out.println("Before Update Subproblem "+currentSubproblem);
             System.out.println("Existing Solution ");
             int[] existingSolutionChromosome = existingSolution.getChromosome();
             for(int j = 0; j < existingSolutionChromosome.length; j++){         
             System.out.print(existingSolutionChromosome[j]+" "); 
             }   System.out.println();
             System.out.println("Makespan "+existingSolution.getMakespan() +" Flow Time "+existingSolution.getFlowTime());
             
             System.out.println("Offspring Solution ");
             int[]  mutatedOffspringChromosome = mutatedOffspring.getChromosome();
             for(int j = 0; j < mutatedOffspringChromosome.length; j++){         
             System.out.print(mutatedOffspringChromosome[j]+" "); 
             }   System.out.println();
             System.out.println("Makespan "+mutatedOffspring.getMakespan() +" Flow Time "+mutatedOffspring.getFlowTime());
             */
            
            //CHOOSE DECOMPOSITION TECHNIQUE
            //weighted Sum
            //double mutatedOffspringFitness = weightVector[0]*mutatedOffspring.getMakespan() + weightVector[1]*mutatedOffspring.getMaxTardiness();
            //double existingSolutionFitness = weightVector[0]*existingSolution.getMakespan() + weightVector[1]*existingSolution.getMaxTardiness();
            //weighted Sum normalized
            double repairedOffspringFitness = (((weightVector[0]*repairedOffspring.getEnergy() - minEnergy)/(maxEnergy - minEnergy)) + 
                                             ((weightVector[1]*repairedOffspring.getTime() - minTime)/(maxTime - minTime)) -
                                             ((weightVector[2]*repairedOffspring.getRecall() - minRecall)/(maxRecall - minRecall)));
            
            double existingSolutionFitness = (((weightVector[0]*existingSolution.getEnergy() +  - minEnergy)/(maxEnergy - minEnergy)) + 
                                             ((weightVector[1]*existingSolution.getTime()- minTime)/(maxTime - minTime)) -
                                             ((weightVector[2]*existingSolution.getRecall() - minRecall)/(maxRecall - minRecall)));
            
                        
            //OPTIMIZING MAKESPAN AND TOTAL FLOW TIME
            if(repairedOffspringFitness < existingSolutionFitness){
            
                currentPopIPforGA[currentSubproblem] = repairedOffspring;
                //System.out.println("Offspring better ");
            }//System.out.println();
            
            
    ////////////////**************UPDATE NEIGHBORS*************////////////////////
    //===========================================================================//
     
    int movement = 1;
    int count = 0;
     
    if(alg.maximumToModify > 0){
     while(true){
     
     MOEAD.Solutions.Solution newSolution = currentPopIPforGA[currentSubproblem];
     
     
     if(currentSubproblem+movement < currentPopIPforGA.length){
         
              weightVector = (double[])keepsWV.get(currentSubproblem+movement);             
      
     MOEAD.Solutions.Solution neighborSolution = (MOEAD.Solutions.Solution)currentPopIPforGA[currentSubproblem+movement];
     
           double newSolutionFitness = (((weightVector[0]*newSolution.getEnergy() - minEnergy)/(maxEnergy - minEnergy)) + 
                                             ((weightVector[1]*newSolution.getTime() - minTime)/(maxTime - minTime)) -
                                             ((weightVector[2]*newSolution.getRecall() - minRecall)/(maxRecall - minRecall)));
            
            double neighborSolutionFitness = (((weightVector[0]*neighborSolution.getEnergy() +  - minEnergy)/(maxEnergy - minEnergy)) + 
                                             ((weightVector[1]*neighborSolution.getTime()- minTime)/(maxTime - minTime)) -
                                             ((weightVector[2]*neighborSolution.getRecall() - minRecall)/(maxRecall - minRecall)));
                        
     
    
     
     if(newSolutionFitness < neighborSolutionFitness){
         
                
        currentPopIPforGA[currentSubproblem+movement] = newSolution;
         count = count + 1;
      
    }}
     
     if(count >= alg.maximumToModify){
     
         break;
     
     }
     
     if(currentSubproblem-movement >= 0){
         
             weightVector = (double[])keepsWV.get(currentSubproblem-movement);             
      
     MOEAD.Solutions.Solution neighborSolution = (MOEAD.Solutions.Solution)currentPopIPforGA[currentSubproblem-movement];
     
           double newSolutionFitness = (((weightVector[0]*newSolution.getEnergy() - minEnergy)/(maxEnergy - minEnergy)) + 
                                             ((weightVector[1]*newSolution.getTime() - minTime)/(maxTime - minTime)) -
                                             ((weightVector[2]*newSolution.getRecall() - minRecall)/(maxRecall - minRecall)));
            
            double neighborSolutionFitness = (((weightVector[0]*neighborSolution.getEnergy() +  - minEnergy)/(maxEnergy - minEnergy)) + 
                                             ((weightVector[1]*neighborSolution.getTime()- minTime)/(maxTime - minTime)) -
                                             ((weightVector[2]*neighborSolution.getRecall() - minRecall)/(maxRecall - minRecall)));
                        
     
     
     if(newSolutionFitness < neighborSolutionFitness){
         
                
        currentPopIPforGA[currentSubproblem-movement] = newSolution;
         count = count + 1;   
    }}
     
     movement = movement + 1;
     
     if(movement >= alg.neighborhoodSizeT || count >= alg.maximumToModify){
     
         break;
     
     }}}
     

   //////////////////////////*******************************************///////////////////////////  
            
            
            
    }// END OF updateIP method
    
    
    public ArrayList calculateMinMax(){
    
        ArrayList minMax = new ArrayList();
        double minEnergy = Double.MAX_VALUE;
        double maxEnergy = Double.MIN_VALUE;
        double minTime = Double.MAX_VALUE;
        double maxTime = Double.MIN_VALUE;
        double minRecall = Double.MAX_VALUE; 
        double maxRecall = Double.MIN_VALUE;
        
        int solutionWithMinEnergy = 0;
        int solutionWithMinTime = 0;
        int solutionWithMaxRecall = 0;
        
        for(int i = 0 ; i < currentPopIPforGA.length ; i++){
        
            MOEAD.Solutions.Solution testSol = (MOEAD.Solutions.Solution)currentPopIPforGA[i];
            if(testSol.getEnergy() < minEnergy){minEnergy = testSol.getEnergy();solutionWithMinEnergy=i;}
            if(testSol.getEnergy() > maxEnergy) maxEnergy = testSol.getEnergy();
            if(testSol.getTime() < minTime){ minTime = testSol.getTime();solutionWithMinTime=i;}
            if(testSol.getTime() > maxTime) maxTime = testSol.getTime();
            if(testSol.getRecall() < minRecall) minRecall = testSol.getRecall();
            if(testSol.getRecall() > maxRecall) {maxRecall = testSol.getRecall(); solutionWithMaxRecall=i;}
        }

        minMax.add(String.valueOf(minEnergy));
        minMax.add(String.valueOf(maxEnergy));
        minMax.add(String.valueOf(minTime));
        minMax.add(String.valueOf(maxTime)); 
        minMax.add(String.valueOf(minRecall));
        minMax.add(String.valueOf(maxRecall));
        
        if(alg.gen == alg.termination-1 && currentSubproblem == currentPopIPforGA.length-1){
        
            MOEAD.Solutions.Solution test =  (MOEAD.Solutions.Solution)currentPopIPforGA[solutionWithMinEnergy];
            System.out.println("Min Energy Sol: minEnergy"+nf.format(test.getEnergy())+" time "+nf.format(test.getTime())+" recall "+nf.format(test.getRecall()));
            test =  (MOEAD.Solutions.Solution)currentPopIPforGA[solutionWithMinTime];
            System.out.println("Min Time Sol: Energy"+nf.format(test.getEnergy())+" minTime "+nf.format(test.getTime())+" recall "+nf.format(test.getRecall()));
            test =  (MOEAD.Solutions.Solution)currentPopIPforGA[solutionWithMaxRecall];
            System.out.println("Max Recall Sol: Energy"+nf.format(test.getEnergy())+" time "+nf.format(test.getTime())+" MaxRecall "+nf.format(test.getRecall()));
            
        }
        
        return minMax;    
    
    }//END OF calculateMinMax() method 
    
    
    public MOEAD.Solutions.Solution[] getCurrentIP(){
    
        return currentPopIPforGA;
    
    }
    
}
