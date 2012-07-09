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

 

package NSGAII.Solutions.QRTPSolution;

import java.util.ArrayList;

public class OneSolution implements NSGAII.Solutions.Solution {
    
    int [] chromosome = null;
    double energy =0.0;
    double time = 0.0;
    double recall = 0.0;
    ArrayList sp;
    double crowdistE = 0.0;
    double crowdistT = 0.0;
    double crowdistR = 0.0;
    double crowdist = 0.0; 
    int np,rank, indPos, gen, divRank = 0;
    
    /** Creates a new instance of OneSolution */
    public OneSolution(int chromosomeSize) {
        
        chromosome = new int[chromosomeSize];
        sp = new ArrayList();
        
    }
    
     public void setChromosome(int[] chromosome){
     
         this.chromosome = chromosome;
     }
     
     public int[] getChromosome(){
     
         return chromosome;
     }
     
     public void setEnergy(double energy){
     
         this.energy = energy;
     }
     
     public double getEnergy(){
     
         return energy;     
     }
     
     public void setTime(double time){
     
          this.time = time;
     }
     
     public double getTime(){
     
          return time;           
     }    
     
      public void setRecall(double recall){
     
          this.recall = recall;
     }
      
      
     
     public double getRecall(){
     
          return recall;           
     }
     
     
     /******************NSGA - II RELATED**************/
    
    public void setNpInitialize(int np1){
    
    np =  np1;
    }
    
    public void setNp(int np1){
    
    np = np + np1;
    }
    
    public void setNpMinus(int np1){
    
    np = np1;
    }
    
    public void setSpInitialize(){
    
    sp = new ArrayList();
    }
    
    public void setSp(NSGAII.Solutions.Solution sol){
    
    sp.add(sol);
    }
    
    public void setRank(int rank){
   
    this.rank = rank;
    }
      
    public int getRank(){
    
    return rank;
    }
    
     public void setDivRank(int divRank){
   
    this.divRank = divRank;
    }
      
    public int getDivRank(){
    
    return divRank;
    }
    
    public int getNp(){
    
        return np;
    }
    
    public ArrayList getSp(){
    
    return sp;
    }
    
    public void setCrowdingDistanceE(double crowdistE){
    
    this.crowdistE = crowdistE;
    }
    
    public double getCrowdingDistanceE(){
    
    return crowdistE;
    }
    
     public void setCrowdingDistanceT(double crowdistT){
    
    this.crowdistT = crowdistT;
    }
    
    public double getCrowdingDistanceT(){
    
    return crowdistT;
    }
    
    public void setCrowdingDistanceR(double crowdistR){
    
    this.crowdistR = crowdistR;
    }
    
    public double getCrowdingDistanceR(){
    
    return crowdistR;
    }
    
    public void setCrowdingDistanceTotal(double crowdist){
    
    this.crowdist = crowdist;
    }
    
    public double getCrowdingDistanceTotal(){
    
    return crowdist;
    }
    
    public int getIndPos(){
    
    return indPos;
    }
    
     public void setIndividualPos(int indPos){
    
    this.indPos = indPos;
    }
    
     
    public void setGen(int gen){
    
    this.gen = gen;
    }
      
    public int getGen(){
    
    return gen;
    }
    /*************************************************/
    
        
   
}
