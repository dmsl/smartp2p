/*
 * This is framework for searching objects (e.g. images, etc.) captured 
 * by the users in a mobile social community. Our framework is founded on an 
 * in-situ data storage model, where captured objects remain local on their owner�s 
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

 

package MOEAD.GA.Selection;

import java.util.*;
import java.util.Random;


public class RandomNeighborhoodSelectionZHANG{
    
    MOEAD.Solutions.Solution[] currentPopIPforGA;
    Random randomGen = null;
    int currentSubproblem = 0;
    ArrayList selectedParents = new ArrayList();
    int neighborhoodSizeT=0;
    
    /** Creates a new instance of RandomSelection */
    public RandomNeighborhoodSelectionZHANG(MOEAD.Solutions.Solution[] currentPopIPforGA, int currentSubproblem, int neighborhoodSizeT) {
        
        this.currentPopIPforGA = currentPopIPforGA;
        this.currentSubproblem = currentSubproblem;
        this.neighborhoodSizeT = neighborhoodSizeT;
    }
    
    
    public ArrayList select(){
    

        ArrayList neighbourSolutions = new ArrayList();
        int goingUp = currentSubproblem+1; 
        int goingDown = currentSubproblem-1;
        
        while (neighbourSolutions.size() < neighborhoodSizeT){
        
            if(goingUp < currentPopIPforGA.length){
            
                neighbourSolutions.add((MOEAD.Solutions.Solution)currentPopIPforGA[goingUp]);
                goingUp = goingUp + 1;
            
            }
            
            if(neighbourSolutions.size() >= neighborhoodSizeT){break;}
            
            if (goingDown > 0){
            
                neighbourSolutions.add((MOEAD.Solutions.Solution)currentPopIPforGA[goingDown]);
                goingDown = goingDown - 1;
            }
        }
            
        ArrayList parents = new ArrayList(); 
        int parent1Number = 0;
        int parent2Number = 0;
        
        while(parent1Number == parent2Number){
            
            randomGen = new Random(System.currentTimeMillis());            
            parent1Number = randomGen.nextInt(neighbourSolutions.size());
            parent2Number = randomGen.nextInt(neighbourSolutions.size());
            
        }
        
        parents.add((MOEAD.Solutions.Solution)neighbourSolutions.get(parent1Number));
        parents.add((MOEAD.Solutions.Solution)neighbourSolutions.get(parent2Number));        
        return parents;    
    }
    
}
