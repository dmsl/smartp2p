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

 
package BFS;


import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.text.*;

public class BFSAlg
{

        public BFS.Problems.QueryRoutingTreeProblem problem = null;
	int networkSize = 0;
	ArrayList tree = new ArrayList();
	ArrayList availableChildren = new ArrayList();
        ArrayList parents = new ArrayList();
	int children;

        double[][] distance_list;
        Random randomGen = new Random(System.currentTimeMillis());
        String newFolderPath = "./";
        String newFolderName = "";
        public String link = "";
        public double time = 0.0;
        double timeStart = 0.0;


	public BFSAlg(int id, String word){

        newFolderName = "src/BFS/Exp"+(id);
        link = newFolderPath + newFolderName;
        File f1 = new File(link);
        f1.mkdir();

             //CHOOSE PROBLEM
                problem = new BFS.Problems.QueryRoutingTreeProblem(this);

               //initialize problem based on specific requirements
               problem.readDatabase(id, word);
               distance_list = problem.distance_list;
               networkSize = problem.chromosomeSize;

            children = (int)Math.log(networkSize);
            System.out.println(children);
            createArrayList();
            createTree();
            writeTree();

	}

	public void createArrayList(){

		for(int i = 0 ; i < networkSize; i++){

		      availableChildren.add(String.valueOf(i));
                      tree.add(String.valueOf("-1"));

		}
	}


	public void createTree(){

            tree.set(0, "0");
            parents.add("0");
            availableChildren.remove(0);

            int currentParent = 0;

            while(availableChildren.size() > children){

                System.out.println("TREE SIZE: "+tree.size());

                for(int i = 0 ; i < children; i++){

                    System.out.println(availableChildren.size());
                    int rand = randomGen.nextInt(availableChildren.size());
                    System.out.println(String.valueOf(rand));
                    tree.set(Integer.parseInt((String)availableChildren.get(rand)), String.valueOf(currentParent));
                    parents.add(availableChildren.get(rand));
                    availableChildren.remove(rand);
                    if(availableChildren.size() <= children) break;
                }
                System.out.println();

                parents.remove(0);
                currentParent = Integer.parseInt((String)parents.get(0));
            }


             System.out.println("Last Parent "+currentParent);
            for(int i = 0 ; i < availableChildren.size(); i++){

                tree.set(Integer.parseInt((String)availableChildren.get(i)), String.valueOf(currentParent));
            }

           int[] chromosome = new int[networkSize];



            for(int i = 0 ; i < tree.size(); i++){

                System.out.println(i+" "+tree.get(i));
                chromosome[i] = Integer.parseInt((String)tree.get(i));

            }


	    for(int i = 0 ; i < problem.chromosomeSize; i++){
                System.out.print(chromosome[i]+"\t");
            }
            System.out.println("");
            
        
        
        
           BFS.Solutions.Solution oneSolution = new BFS.Solutions.QRTPSolution.OneSolution(problem.chromosomeSize);
           oneSolution.setChromosome(chromosome);
           
           
           BFS.Solutions.Solution repairedOffspring = new BFS.Solutions.QRTPSolution.OneSolution(problem.chromosomeSize);
           BFS.LocalSearchMethods.RepairH.RepairTree repairT = new  BFS.LocalSearchMethods.RepairH.RepairTree(oneSolution);
           repairedOffspring = repairT.repair();  
           repairedOffspring = problem.evaluateSolution(repairedOffspring);
           
                
           System.out.print("Flooding Sol: energy "+repairedOffspring.getEnergy()+" time "+repairedOffspring.getTime()+" recall "+repairedOffspring.getRecall());
           double timeStop = System.currentTimeMillis();        
           time = timeStop - timeStart;        
           //in minutes
           System.out.println(time);
           //write data for statistics
           writeData(repairedOffspring);
    }
    
     void writeData(BFS.Solutions.Solution repairedOffspring){
         
          BFS.Write.WriteSolutions ws = new BFS.Write.WriteSolutions(this, repairedOffspring);
         
     }//END OF writeData() method


         public void writeTree(){



             String file = newFolderPath + newFolderName + "\\treeForSize"+networkSize+".log";

             try {
             BufferedWriter out = new BufferedWriter(new FileWriter(file,true));

                for (int i = 0 ; i < tree.size()  ; i++ ){

                    out.write(tree.get(i)+" ");
                    out.write("\n");

                }
                    out.close();


             }catch (IOException e) {}
             
             file = newFolderPath + newFolderName + "\\tree"+".log";

             try {
             BufferedWriter out = new BufferedWriter(new FileWriter(file,true));

                for (int i = 0 ; i < tree.size()  ; i++ ){

                    out.write(tree.get(i)+" ");
                    out.write("\n");

                }
                    out.close();


             }catch (IOException e) {}
             

         }//end of writeTree
         




	public static void main(String args[]){

	BFSAlg bfs = new BFSAlg(1, "oprimization");

	}
}