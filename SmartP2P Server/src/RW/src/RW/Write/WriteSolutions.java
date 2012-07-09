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

package RW.src.RW.Write;



import java.util.*;
import java.io.*;


public class WriteSolutions {
    
    protected RW.src.RW.RWAlg alg = null;
    RW.src.RW.Solutions.Solution solution;
    String link = "";

    
    /** Creates a new instance of WriteSolutions */
    public WriteSolutions(RW.src.RW.RWAlg alg, RW.src.RW.Solutions.Solution solution) {
        
        this.alg= alg;
        this.solution = solution;
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
           
            
            BufferedWriter out = new BufferedWriter(new FileWriter(spoonFeeding+"outBests.log",true));
            BufferedWriter out1 = new BufferedWriter(new FileWriter(spoonFeeding+"outBests1.log",true));
                    
                    out.write(String.valueOf(1));
                    out.write(" "+String.valueOf(((RW.src.RW.Solutions.Solution) solution).getEnergy()));
                    out.write(" "+String.valueOf(((RW.src.RW.Solutions.Solution) solution).getTime()));
                    out.write(" "+String.valueOf(((RW.src.RW.Solutions.Solution) solution).getRecall()));
                    
                    //The same but without the solution number for Smetric
                    out1.write(" "+String.valueOf(((RW.src.RW.Solutions.Solution)solution).getEnergy()));
                    out1.write(" "+String.valueOf(((RW.src.RW.Solutions.Solution) solution).getTime()));
                    out1.write(" "+String.valueOf(-((RW.src.RW.Solutions.Solution)solution).getRecall()));
                    
                    out.write("\n");
                    out1.write("\n");
            
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
                    
        try{
        BufferedWriter out = new BufferedWriter(new FileWriter(spoonFeeding+"PFtree"+0+".log",true));            
                    int[] testTree = (int[])((RW.src.RW.Solutions.Solution)solution).getChromosome();
                    for (int j = 0 ; j < testTree.length; j++){
                        
                        out.write(testTree[j]+" ");
                        out.write("\n");
                    }
                    out.close();
        }catch (IOException e) {}
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
