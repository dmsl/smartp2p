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

 

package NSGAII.GA;

import java.util.ArrayList;
import java.util.Random;

public class GeneticAlgorithm {
    
    //VARIABLES
    //=========
    protected NSGAII.Algorithm alg = null;
    public NSGAII.Solutions.Solution[] currentPopIPforGA;
    public int currentSubproblem = 0;
    public ArrayList keepsWV = null;
    double crossoverRate = 0.7;
    double mutationRate = 0.1;
    Random randomGen = null;
    ArrayList newPopulation = null;
    
    /** Creates a new instance of GeneticAlgorithm */
    public GeneticAlgorithm(NSGAII.Algorithm alg) {
        
        this.alg = alg;
        this.currentPopIPforGA = alg.currentPopIP;
        this.keepsWV = alg.keepsWV;   
        
       
        
       newPopulation =  new ArrayList();
              
              for (int i = 0 ; i < currentPopIPforGA.length; i++){
              
                  newPopulation.add(( NSGAII.Solutions.Solution)currentPopIPforGA[i]);
              }
        
        //System.out.println("STARTING GA...");
        
        for(int i = 0 ; i < alg.popSize ; i++){
            
            this.currentSubproblem = i;            
            
            //CHOOSE SELECTION STRATEGY
            NSGAII.GA.Selection.RandomSelection randSelect = new NSGAII.GA.Selection.RandomSelection(currentPopIPforGA, currentSubproblem);
            ArrayList selectedParents = new ArrayList();
            selectedParents = randSelect.select();
            //----------------------------------
                       
             randomGen = new Random(System.currentTimeMillis());
             double randC = (double) randomGen.nextInt(100)/100;
             NSGAII.Solutions.Solution offspring = new NSGAII.Solutions.QRTPSolution.OneSolution(alg.chromosomeSize);
             if(randC < crossoverRate){            
                 //CHOOSE CROSSOVER STRATEGY   
             NSGAII.GA.Crossover.TwoPointCrossover crossover = new NSGAII.GA.Crossover.TwoPointCrossover(selectedParents, alg); 
                 offspring = crossover.recombine();
            }else{            
                 
                offspring = (NSGAII.Solutions.Solution)currentPopIPforGA[currentSubproblem];
            }
            //----------------------------------
            
             
             NSGAII.Solutions.Solution mutatedOffspring = new NSGAII.Solutions.QRTPSolution.OneSolution(alg.chromosomeSize);
             NSGAII.GA.Mutation.SwapMutation mutation = new  NSGAII.GA.Mutation.SwapMutation(offspring, alg, mutationRate);
             mutatedOffspring = mutation.mutate();           
            //----------------------------------
           
             
             NSGAII.Solutions.Solution repairedOffspring = new NSGAII.Solutions.QRTPSolution.OneSolution(alg.chromosomeSize);
             NSGAII.LocalSearchMethods.RepairH.RepairTree repairT = new  NSGAII.LocalSearchMethods.RepairH.RepairTree(mutatedOffspring);
             repairedOffspring = repairT.repair();           
            //----------------------------------
             
            
             NSGAII.Solutions.Solution repairedOffspringEv = alg.problem.evaluateSolution(repairedOffspring);
             newPopulation.add(( NSGAII.Solutions.Solution)repairedOffspringEv);
             
        }//end of for popSize
    }//end of constructor GeneticAlgorithm()
    
    
    public ArrayList getNewPop(){
    
        return newPopulation;
    
    }
    
}
