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

 

package MOEAD.GA.Crossover;

import java.util.*;
import java.util.Random;


public class TwoPointCrossover {
    
    ArrayList selectedParents;
    protected MOEAD.Algorithm alg;
    protected Random randomGen = new Random(System.currentTimeMillis());

    
    
    /** Creates a new instance of TwoPointCrossover 
       THIS TWO-POINT CROSSOVER ID BASED ON ISHIBUCHI
     */
    public TwoPointCrossover(ArrayList selectedParents, MOEAD.Algorithm alg) {
        
        this.selectedParents = selectedParents;
        this.alg = alg;
        
    }
    
   
    
    
    public MOEAD.Solutions.Solution recombine(){
    
        MOEAD.Solutions.Solution offspring1 = new MOEAD.Solutions.QRTPSolution.OneSolution(alg.chromosomeSize);
        int[] cromosomeOffspring1 = new int[alg.chromosomeSize];
        
        MOEAD.Solutions.Solution offspring2 = new MOEAD.Solutions.QRTPSolution.OneSolution(alg.chromosomeSize);
        int[] cromosomeOffspring2 = new int[alg.chromosomeSize];
        
        MOEAD.Solutions.Solution parent1 = (MOEAD.Solutions.Solution)selectedParents.get(0);
        int[] cromosomeParent1 = parent1.getChromosome();
        
        MOEAD.Solutions.Solution parent2 = (MOEAD.Solutions.Solution)selectedParents.get(1);
        int[] cromosomeParent2 = parent2.getChromosome();
        

        
        
        int crspoint1 = 0; 
        int crspoint2 = 0;
        int crsp1 = 0; 
        int crsp2 = 0;
	
        while (crspoint1 == crspoint2){
             
            crspoint1 = randomGen.nextInt(alg.chromosomeSize); 
            crspoint2 = randomGen.nextInt(alg.chromosomeSize);
        }
        
        if (crspoint1 < crspoint2){
        
            crsp1 = crspoint1;
            crsp2 = crspoint2;
        }else{
        
            crsp1 = crspoint2;
            crsp2 = crspoint1;        
        }
        
        
        for (int i = 0 ; i < crsp1 ; i++){
                
             cromosomeOffspring1[i]=cromosomeParent1[i];
             cromosomeOffspring2[i]=cromosomeParent2[i];
          
            }
            
              
            
            for (int i = crsp1 ; i < crsp2 ; i++){
              
             cromosomeOffspring1[i]=cromosomeParent2[i];
             cromosomeOffspring2[i]=cromosomeParent1[i];
           
            }
        
        
         for (int i = crsp2 ; i < alg.chromosomeSize ; i++){
                
             cromosomeOffspring1[i]=cromosomeParent1[i];
             cromosomeOffspring2[i]=cromosomeParent2[i];                           
            }        
        
        
         
         offspring1.setChromosome(cromosomeOffspring1); 
         offspring2.setChromosome(cromosomeOffspring2);
         
         int selectOff = randomGen.nextInt(2);
         if(selectOff == 0){
              return offspring1;
         }else{
              return offspring2;         
         }
    }//end of recombine() method
}//end of class
