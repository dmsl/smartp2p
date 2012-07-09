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

 

package MOEAD.PopInits.Random;

import java.util.Random;
import java.util.ArrayList;


public class RandomPopInit implements MOEAD.PopInits.PopInit {
    
    protected MOEAD.Algorithm alg = null;
     /**A solution chromosome is an array of ingtegers of size jobs*/
    public int[] chromosome;
    public int chromosomeSize;
    public MOEAD.Solutions.Solution[] currentPopIP;
    public int seed = 19580427;
    
    
    /** Creates a new instance of RandomPopInit */
    public RandomPopInit(MOEAD.Algorithm alg) {
        
        this.alg = alg; 
        this.chromosomeSize = alg.chromosomeSize;
        this.currentPopIP = alg.currentPopIP;
        
        
    }
    
    public void initialize(){
   
        seed = (int) System.currentTimeMillis();
        Random randomChildren =  new Random(seed);
        
        
        
       for (int i =0 ; i < alg.popSize; i++){ 
           
           chromosome = new int[chromosomeSize];
           
           for(int j = 0 ; j < chromosomeSize; j++){
                      
                 if(j==0){
                 
                     chromosome[j]=j;
                 }else{
                 
                     chromosome[j]=-1;
                 }                
             }

       //Adding for each parent its children until a child alreasy has a parent

           for(int j = 0 ; j < chromosomeSize; j++){
             
                while(true){
                 int randP = randomChildren.nextInt(chromosomeSize);
                                 
                 if(chromosome[randP] == -1 && randP != j && chromosome[j] != -1){
                 
                     chromosome[randP]=j;
                 
                 }else{
                 
                     break;
                 }                
             }
           }
                     
           
           MOEAD.Solutions.Solution oneSolution = new MOEAD.Solutions.QRTPSolution.OneSolution(chromosomeSize);
           oneSolution.setChromosome(chromosome);
           oneSolution = alg.problem.evaluateSolution(oneSolution);
           currentPopIP[i] = oneSolution;
           
       }//END of i for filling popIP
    }//END OF initialize() method
    
     public MOEAD.Solutions.Solution[] getPopIP(){
     
        
         return currentPopIP;
         
     }//END OF getPopIP() method
    
}//END OF CLASS
