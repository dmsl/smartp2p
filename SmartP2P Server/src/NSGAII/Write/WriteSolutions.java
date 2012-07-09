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

 

package NSGAII.Write;

import java.util.*;
import java.io.*;

public class WriteSolutions {
    
    protected NSGAII.Algorithm alg = null;
    ArrayList frontToPrintEP = null;
     String link = "";
    
    /** Creates a new instance of WriteSolutions */
    public WriteSolutions(NSGAII.Algorithm alg, ArrayList frontToPrintEP) {
        
        this.alg= alg;
        this.frontToPrintEP = frontToPrintEP;
        this.link = alg.link;
        
        writeNondDomSolutions();
        writeTree();   
        writeTime();
        
    }
    
    public void writeNondDomSolutions(){
        
        String newFolderName1 = "/BestPF/";
        String spoonFeeding = link + newFolderName1;
        File f = new File(spoonFeeding); 
        f.mkdir();
          
        
     try {
           
            int count = 0;
            BufferedWriter out = new BufferedWriter(new FileWriter(spoonFeeding+"outBests.log",true));
            BufferedWriter out1 = new BufferedWriter(new FileWriter(spoonFeeding+"outBests1.log",true));
            
                                          
           int i = 0 ; 
                
                
                ArrayList temp = frontToPrintEP;
                
     
                
                for (int j = 0 ; j < temp.size()  ; j++ ){
                    
                    
                    if(count < alg.popSize){
                    //Deletes the solutions with 0 coverage and lifetime in Initial Pop.
              if ((((NSGAII.Solutions.Solution) temp.get(j)).getEnergy()) != 0.0 &&
                 ((((NSGAII.Solutions.Solution) temp.get(j)).getTime())) != 0.0 &&
                 ((((NSGAII.Solutions.Solution) temp.get(j)).getRecall())) != 0.0){
                    
                    out.write(String.valueOf(j+1));
                    out.write(" "+String.valueOf(((NSGAII.Solutions.Solution) temp.get(j)).getEnergy()));
                    out.write(" "+String.valueOf(((NSGAII.Solutions.Solution) temp.get(j)).getTime()));
                    out.write(" "+String.valueOf(((NSGAII.Solutions.Solution) temp.get(j)).getRecall()));
                    
                    //The same but without the solution number for Smetric
                    out1.write(" "+String.valueOf(((NSGAII.Solutions.Solution) temp.get(j)).getEnergy()));
                    out1.write(" "+String.valueOf(((NSGAII.Solutions.Solution) temp.get(j)).getTime()));
                    out1.write(" "+String.valueOf(-((NSGAII.Solutions.Solution) temp.get(j)).getRecall()));
                    
                    out.write("\n");
                    out1.write("\n");
                    count = count + 1;
                    }
                }
                }
                           
            
            
            out.close();
            out1.close();
            
        } catch (IOException e) {
        }
    }//end writeNondDomSolutions
    
    
    public void writeTree(){
    
            
       String newFolderName1 = "/PFtrees/";
        String spoonFeeding = link + newFolderName1;
        File f = new File(spoonFeeding); 
        f.mkdir();

             ArrayList temp = frontToPrintEP;
                
     
                
                for (int i = 0 ; i < temp.size()  ; i++ ){
            
                     try {
                    BufferedWriter out = new BufferedWriter(new FileWriter(spoonFeeding+"PFtree"+i+".log",true));            
                    int[] testTree = (int[])((NSGAII.Solutions.Solution)temp.get(i)).getChromosome();
                    
                    int maxSize = 0;
                    for (int j = 0 ; j < testTree.length; j++){
                    
                        
                        out.write(testTree[j]+" ");
                        out.write("\n");
                    }
                    out.close();
                  
                     }catch (IOException e) {        
                     }
                }
    }//end of writeTree
    
    public void writeTime(){
    
        String newFolderName1 = "/Time/";
        String spoonFeeding = link + newFolderName1;
        File f = new File(spoonFeeding); 
        f.mkdir(); 
        
        
        try{
                    
            BufferedWriter out = new BufferedWriter(new FileWriter(spoonFeeding+"time.log",true));  
            out.write(String.valueOf(alg.time));
            out.close();
                  
                     
        }catch (IOException e) {}
                    
    
    
    }//end of writeTime
    
}//end of class
