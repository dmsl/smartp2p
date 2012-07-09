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

 

package NSGAII.GA.Mutation;

import java.util.*;
import java.util.Random;

public class SwapMutation {
    
    NSGAII.Solutions.Solution offspring;
    protected  NSGAII.Algorithm alg;
    Random randomGen = new Random(System.currentTimeMillis());
    double mutationRate = 0.0;
    
    /** Creates a new instance of SwapMutation */
    public SwapMutation(NSGAII.Solutions.Solution offspring, NSGAII.Algorithm alg, double mutationRate) {
        
        this.offspring = offspring;
        this.alg = alg;
        this.mutationRate = mutationRate;
        
    }
    
    public NSGAII.Solutions.Solution mutate(){
    
        NSGAII.Solutions.Solution mutatedOffspring = new NSGAII.Solutions.QRTPSolution.OneSolution(alg.chromosomeSize);
        int[] mutatedChromosome = new int[alg.chromosomeSize];
        
        int[] offspringChromosome=offspring.getChromosome(); 
         
         for(int i = 0; i < offspringChromosome.length; i++){         
            mutatedChromosome[i] = offspringChromosome[i];             
         }
        
        for(int i = 0; i < mutatedChromosome.length ; i++){
        
            double randM = (double) randomGen.nextInt(100+1)/100;
        
			if(randM < mutationRate){
			
			int swapPositionA = i;
			int swapPositionB = i;
			
			
			while(swapPositionA == swapPositionB){
			 swapPositionB = randomGen.nextInt(alg.chromosomeSize);
			}        
			
			
			int geneA = (int)mutatedChromosome[swapPositionA];
			int geneB = (int)mutatedChromosome[swapPositionB];

			
			mutatedChromosome[swapPositionA] = geneB;
			mutatedChromosome[swapPositionB] = geneA; 
			
			}else{
			   mutatedChromosome[i] = (int)mutatedChromosome[i];         
			}    
        
		}
        
        mutatedOffspring.setChromosome(mutatedChromosome);    
        return mutatedOffspring;
    }
    
}
