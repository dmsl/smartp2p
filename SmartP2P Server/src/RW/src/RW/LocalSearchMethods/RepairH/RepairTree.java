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

 package RW.src.RW.LocalSearchMethods.RepairH;


import java.util.*;
import java.util.Random;
import java.io.*;

public class RepairTree {
    
    RW.src.RW.Solutions.Solution mutatedOffspring;
    Random randomGen = new Random(System.currentTimeMillis());

    
    /** Creates a new instance of RepairTree */
    public RepairTree(RW.src.RW.Solutions.Solution mutatedOffspring) {
    
        this.mutatedOffspring = mutatedOffspring;   
    }
    
    
    public RW.src.RW.Solutions.Solution repair(){
    
        int[] mutatedChromosome = mutatedOffspring.getChromosome();
        
        RW.src.RW.Solutions.Solution repairedOffspring = new RW.src.RW.Solutions.QRTPSolution.OneSolution(mutatedChromosome.length);
        int[] repairedChromosome = new int[mutatedChromosome.length];
        
        //INITIALIZE REPAIRED CHROMOSOME
        for(int i = 1; i < repairedChromosome.length; i++){ 
            
            repairedChromosome[i] = mutatedChromosome[i];
        }       
        
        
        //REPAIR IF ROOT DOES NOT HAVE ITSELF AS PARENT.
        repairedChromosome[0] = 0;
        
        for(int i = 1; i < repairedChromosome.length; i++){        

             //REPAIR IF ANY OTHER NODE HAS ITSELF AS PARENT.
            if(repairedChromosome[i] == i){
                             
                while(true){
                
                    int randM = randomGen.nextInt(repairedChromosome.length);
                    if(repairedChromosome[randM] != -1 && randM != repairedChromosome[i] && randM != i){
                         repairedChromosome[i] = randM;  
                         break;
                    }}
            }                 
            //CHECK IF A NODE'S PARENT IS DICONNECTED.
            if(repairedChromosome[i] != -1){
            if(repairedChromosome[repairedChromosome[i]] == -1){
                             
                while(true){
                
                    int randM = randomGen.nextInt(repairedChromosome.length);
                    if(repairedChromosome[randM] != -1 && randM != repairedChromosome[i] && randM != i){
                         repairedChromosome[i] = randM;  
                         break;
                    }}
            }} 
        }
        
        ArrayList keepConnected = new ArrayList();
        keepConnected.add("0");
        
        for(int i = 1; i < repairedChromosome.length; i++){
            
            if(findConnected(i, repairedChromosome, new int[repairedChromosome.length]) == true){
            
                keepConnected.add(String.valueOf(i));
            }            
        }
        
         //CHECK FOR LOOPS!
         for(int i = 1; i < repairedChromosome.length; i++){ 
    
             if (checkForLoops(i, repairedChromosome, new int[repairedChromosome.length]) == false){

                 int randM = randomGen.nextInt(keepConnected.size());
                 repairedChromosome[i] = Integer.parseInt((String)keepConnected.get(randM));
                 keepConnected.add(String.valueOf(i));
               
             }
             }         

        repairedOffspring.setChromosome(repairedChromosome);
        return repairedOffspring;

    }//end of repair() method
   
    public boolean findConnected(int node, int[] repairedChromosome, int[] check){
        if(node != -1){
        if(node == 0)return true;
        if(check[node]==1) return false;
        check[node]=1;
        return findConnected(repairedChromosome[node], repairedChromosome, check);       
        }else return false;
    }//end of checkForLoops() method 
    
    public boolean checkForLoops(int node, int[] repairedChromosome, int[] check){
        if(node == 0 || node == -1)return true;
        if(check[node]==1) return false;
        check[node]=1;
        return checkForLoops(repairedChromosome[node], repairedChromosome, check);       
            
    }//end of checkForLoops() method 
    
}//end of class
