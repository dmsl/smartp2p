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

 
package NSGAII;

import java.util.*;
import java.util.Comparator;
import java.io.*;
import java.text.NumberFormat;

public class Update implements Comparator{
    
  
    protected ArrayList F,Fronts,sortByCoverage,sortByE,sortByT,sortByR;
    protected Algorithm alg;
    public NSGAII.Solutions.Solution[] newPop;
    public ArrayList frontToPrint;
    ArrayList popFromGA = null;
    public static final double inf = 10000.0;
     NumberFormat nf;
   
    
    /** Creates a new instance of FastNonDominatedSort */
    public Update(Algorithm alg)  {
        
        /*System.out.println("CREATING FRONTS");
        System.out.println("===============");*/
        
        nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(10);        
        this.alg = alg;
        
        if(alg.gen == 0 ){
            
            popFromGA = new ArrayList();
        
            for(int i = 0 ; i < alg.currentPopIP.length ; i++){
            
                popFromGA.add((NSGAII.Solutions.QRTPSolution.OneSolution)alg.currentPopIP[i]);            
            
            }}else{
                         
                popFromGA = alg.popFromGA;
            
            }
        

        
        
        newPop = new NSGAII.Solutions.Solution[alg.popSize];
        frontToPrint = alg.frontToPrint;
        initialize();
        startComparisonOfSolutions();
        F = new ArrayList();
        Fronts = new ArrayList();
        createRanks();

       /* System.out.println("Calculating Crowd Dist.");
        System.out.println("=======================");*/
        sortFrontsbyE();
        sortFrontbyT();
        sortFrontbyR();
        calculateCrowdingDistanceE();
        calculateCrowdingDistanceT();
        calculateCrowdingDistanceR();
        calculateTotalCrowdingDistance();
        sortByCrowdingDistance();
        
                     
    }
    
    public NSGAII.Solutions.Solution[] getNewPop(){
    
            return newPop;
        
    }   
    
     public ArrayList getFrontToPrint(){
    
            return frontToPrint;
        
    }   
    
    public void initialize(){
    
        for (int i = 0 ; i < popFromGA.size() ; i++){
        
        ((NSGAII.Solutions.QRTPSolution.OneSolution)popFromGA.get(i)).setCrowdingDistanceE(0.0);
        ((NSGAII.Solutions.QRTPSolution.OneSolution)popFromGA.get(i)).setCrowdingDistanceT(0.0);
        ((NSGAII.Solutions.QRTPSolution.OneSolution)popFromGA.get(i)).setCrowdingDistanceR(0.0);
        ((NSGAII.Solutions.QRTPSolution.OneSolution)popFromGA.get(i)).setCrowdingDistanceTotal(0.0);
        ((NSGAII.Solutions.QRTPSolution.OneSolution)popFromGA.get(i)).setNpInitialize(0);
        ((NSGAII.Solutions.QRTPSolution.OneSolution)popFromGA.get(i)).setSpInitialize();
        ((NSGAII.Solutions.QRTPSolution.OneSolution)popFromGA.get(i)).setRank(0);
        }
        
    
    }
       
    
    public void startComparisonOfSolutions(){
    
        for (int i = 0 ; i < popFromGA.size() ; i++){
           
            //sets the initial position of a solution in the population 
            //before sorting and the generation, these are both unique numbers
            ((NSGAII.Solutions.QRTPSolution.OneSolution)popFromGA.get(i)).setIndividualPos(i);
            ((NSGAII.Solutions.QRTPSolution.OneSolution)popFromGA.get(i)).setGen(alg.gen);
            
            //System.out.println("Solution "+i+" has Np: "+((NSGAII.Solutions.QRTPSolution.OneSolution)merged[i]).getNp()+" and rank: "+((NSGAII.Solutions.QRTPSolution.OneSolution)merged[i]).getRank());
        }
        
         //System.out.println("Start comparison");
    for (int i = 0 ; i < popFromGA.size() ; i++){
           for (int j = 0 ; j < popFromGA.size() ; j++){
            
            //compares all the solutions once so solution i 
            //is compared with solutions below it only to avoid
            //make comparisons twice
            if (j > i){
               
            //compares a solution from the population with all the others and
            //returns an integer. If the result is 1 then i dominates j
            //if it is 0 they cannot be compared. If it is -1 then j dominates i
           
           
                
           int result = compare((NSGAII.Solutions.QRTPSolution.OneSolution)popFromGA.get(i) , (NSGAII.Solutions.QRTPSolution.OneSolution)popFromGA.get(j));
           
           if (result == 1 ){
               
               //System.out.println(i+" dominates "+j+" result "+ result);
                        /* System.out.println(i+" HAD np: "+((NSGAII.Solutions.QRTPSolution.OneSolution)merged[i]).getNp());
                         System.out.println(j+" HAD np: "+((NSGAII.Solutions.QRTPSolution.OneSolution)merged[j]).getNp());*/
                          ((NSGAII.Solutions.QRTPSolution.OneSolution)popFromGA.get(i)).setSp((NSGAII.Solutions.QRTPSolution.OneSolution)popFromGA.get(j));
                          ((NSGAII.Solutions.QRTPSolution.OneSolution)popFromGA.get(j)).setNp(1);
                         /*System.out.println(i+" has np: "+((NSGAII.Solutions.QRTPSolution.OneSolution)merged[i]).getNp());
                         System.out.println(j+" has np: "+((NSGAII.Solutions.QRTPSolution.OneSolution)merged[j]).getNp());*/

           }
           else if (result == -1){

                           ((NSGAII.Solutions.QRTPSolution.OneSolution)popFromGA.get(j)).setSp((NSGAII.Solutions.QRTPSolution.OneSolution)popFromGA.get(i));
                           ((NSGAII.Solutions.QRTPSolution.OneSolution)popFromGA.get(i)).setNp(1);

           }else if (result == 0){
            
           }else{}//the solutions are the same
        
        }
        }
            // System.out.println("Finish comparison");
    }
        
       
        
    }// end of startComparisonOfSolutions()
    
    
    //This is nonDominated comparison for Maximization only
    //that means the max for both objectives are the best
    public int compare(Object o1, Object o2) {
        
                
        NSGAII.Solutions.QRTPSolution.OneSolution temp1 = (NSGAII.Solutions.QRTPSolution.OneSolution) o1;
        NSGAII.Solutions.QRTPSolution.OneSolution temp2 = (NSGAII.Solutions.QRTPSolution.OneSolution) o2;
       
        //temp2 dominates temp1
        
  if((temp2.getEnergy() > temp1.getEnergy() && temp2.getTime()  > temp1.getTime() && temp2.getRecall() < temp1.getRecall())||
    (temp2.getEnergy()  > temp1.getEnergy() && temp2.getTime()  == temp1.getTime() && temp2.getRecall() == temp1.getRecall())||
    (temp2.getEnergy() == temp1.getEnergy()&& temp2.getTime() > temp1.getTime() && temp2.getRecall() == temp1.getRecall())||
    (temp2.getEnergy() == temp1.getEnergy()&& temp2.getTime() == temp1.getTime() && temp2.getRecall() < temp1.getRecall())||
    (temp2.getEnergy() > temp1.getEnergy()&& temp2.getTime() > temp1.getTime() && temp2.getRecall() == temp1.getRecall())||
    (temp2.getEnergy() > temp1.getEnergy()&&temp2.getTime() == temp1.getTime() && temp2.getRecall() < temp1.getRecall())||
    (temp2.getEnergy() == temp1.getEnergy()&& temp2.getTime() > temp1.getTime() &&  temp2.getRecall() < temp1.getRecall())){  

        
            return 1;
            
    }else if((temp1.getEnergy()> temp2.getEnergy()&&temp1.getTime()> temp2.getTime()&&temp1.getRecall()< temp2.getRecall() )||
    (temp1.getEnergy()  > temp2.getEnergy()&&temp1.getTime()  == temp2.getTime()&& temp1.getRecall() ==  temp2.getRecall() )||
    (temp1.getEnergy() == temp2.getEnergy()&& temp1.getTime() > temp2.getTime() && temp1.getRecall() == temp2.getRecall())||
    (temp1.getEnergy() == temp2.getEnergy() &&temp1.getTime() == temp2.getTime()&& temp1.getRecall() < temp2.getRecall())||
    (temp1.getEnergy() > temp2.getEnergy() && temp1.getTime() > temp2.getTime() && temp1.getRecall() == temp2.getRecall())||
    (temp1.getEnergy() > temp2.getEnergy() && temp1.getTime() == temp2.getTime() && temp1.getRecall() < temp2.getRecall())||
    (temp1.getEnergy() == temp2.getEnergy() && temp1.getTime() > temp2.getTime() && temp1.getRecall() < temp2.getRecall())) {
        
             return -1;
       
    }else{
    
         return 0;
    }  
        
    }
    
    
    public void createRanks(){
    
     
		for (int i = 0 ; i < popFromGA.size() ; i++){
			//for (int i = 0 ; i < 5 ; i++){
		
			if ((((NSGAII.Solutions.QRTPSolution.OneSolution)popFromGA.get(i)).getNp()) == 0){
			
			   ((NSGAII.Solutions.QRTPSolution.OneSolution)popFromGA.get(i)).setRank(0);
			   F.add((NSGAII.Solutions.QRTPSolution.OneSolution)popFromGA.get(i));
			   
			}
		
		} 
	   // System.out.println("After checking np=0");
		
		Fronts.add(F);
		int rankValue = 1;
	 
		
		while(F.isEmpty() == false){
		 
		 ArrayList Q = new ArrayList();
		  
		for (int i = 0; i < F.size() ; i++){
		
			  //From the F set (front 1) get all the solutions in Sp that a solution in front 1 dominates
			 ArrayList temp = ((NSGAII.Solutions.QRTPSolution.OneSolution) F.get(i)).getSp();
			 
			 for (int j = 0; j < temp.size() ; j++){
			 
				 //makes the value np of all the solutions in Sp of a solution in front 1 equals np-1
				// System.out.println("This solution had np"+((NSGAII.Solutions.QRTPSolution.OneSolution) temp.get(j)).getNp());
				((NSGAII.Solutions.QRTPSolution.OneSolution) temp.get(j)).setNpMinus(((NSGAII.Solutions.QRTPSolution.OneSolution) temp.get(j)).getNp()-1);
				//if any of this solutions np becomes 0 then this solution added in front 2
				 // System.out.println("This solution has np"+((NSGAII.Solutions.QRTPSolution.OneSolution) temp.get(j)).getNp());
				 if (((NSGAII.Solutions.QRTPSolution.OneSolution) temp.get(j)).getNp() == 0){
					 //System.out.println("Gets in");
					 ((NSGAII.Solutions.QRTPSolution.OneSolution) temp.get(j)).setRank(rankValue);
					 Q.add((NSGAII.Solutions.QRTPSolution.OneSolution) temp.get(j));

				}
			 
				 
			 }
		}
			rankValue++;
			F = Q;
			Fronts.add(F);
			
		}
    
	}
    
    
      public void sortFrontsbyE(){
    
        sortByE = new ArrayList();
    
        for (int i = 0 ; i < Fronts.size() ; i++){
            
       
            ArrayList tempfront = (ArrayList) Fronts.get(i);
        
			for (int j = 0 ; j < tempfront.size() ; j++){
				
				for (int a = 0 ; a <  tempfront.size() ; a++){
			
					if (a > j){
						
					NSGAII.Solutions.QRTPSolution.OneSolution first = (NSGAII.Solutions.QRTPSolution.OneSolution) tempfront.get(j);
					double x  = first.getEnergy();
					NSGAII.Solutions.QRTPSolution.OneSolution second = (NSGAII.Solutions.QRTPSolution.OneSolution) tempfront.get(a);
					double y  = second.getEnergy();
					
						if (y < x){
						
							tempfront.set(j, second);
							tempfront.set(a, first);
						
						}else{}
					}        
				}      
			}   
			
			sortByE.add(tempfront);
		}
    
    }
      
      
    public void sortFrontbyT(){
    
        sortByT = new ArrayList();
    
        for (int i = 0 ; i < Fronts.size() ; i++){
            
       
            ArrayList tempfront = (ArrayList) Fronts.get(i);
        
			for (int j = 0 ; j < tempfront.size() ; j++){
				
				for (int a = 0 ; a <  tempfront.size() ; a++){
			
					if (a > j){
						
						NSGAII.Solutions.QRTPSolution.OneSolution first = (NSGAII.Solutions.QRTPSolution.OneSolution) tempfront.get(j);
						double x  = first.getTime();
						NSGAII.Solutions.QRTPSolution.OneSolution second = (NSGAII.Solutions.QRTPSolution.OneSolution) tempfront.get(a);
						double y  = second.getTime();
						
						if (y < x){
						
							tempfront.set(j, second);
							tempfront.set(a, first);
						
						}else{}
					}        
				}      
			}   
        
              sortByT.add(tempfront);
		}
    
    }
    
     public void sortFrontbyR(){
    
        sortByR = new ArrayList();
    
        for (int i = 0 ; i < Fronts.size() ; i++){
            
       
            ArrayList tempfront = (ArrayList) Fronts.get(i);
			
			for (int j = 0 ; j < tempfront.size() ; j++){
				
				for (int a = 0 ; a <  tempfront.size() ; a++){
			
					if (a > j){
						
						NSGAII.Solutions.QRTPSolution.OneSolution first = (NSGAII.Solutions.QRTPSolution.OneSolution) tempfront.get(j);
						double x  = first.getRecall();
						NSGAII.Solutions.QRTPSolution.OneSolution second = (NSGAII.Solutions.QRTPSolution.OneSolution) tempfront.get(a);
						double y  = second.getRecall();
						
						if (y > x){
						
							tempfront.set(j, second);
							tempfront.set(a, first);
						
						}else{}
					}        
				}      
			}   
			
			sortByR.add(tempfront);
		}
    

    }
    
   
    public void calculateCrowdingDistanceE(){
    
		for (int i = 0 ; i < sortByE.size() ; i++){
		 
			ArrayList tempfront = (ArrayList)  sortByE.get(i);

		   
			if (tempfront.size() > 2){

				((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(0)).setCrowdingDistanceE(inf);
				 double coverMax =   ((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(0)).getEnergy();
				((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(tempfront.size()-1)).setCrowdingDistanceE(inf);
				double coverMin =   ((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(tempfront.size()-1)).getEnergy();
				
				for (int j = 1 ; j < tempfront.size()-1 ; j++){
				
					double crowdist =0;
					
					if (coverMax - coverMin == 0){
					
						crowdist = 0;
					}else{
						crowdist = ((((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(j-1)).getEnergy())-(((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(j+1)).getEnergy()))/
										(coverMax - coverMin);
					}
						 ((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(j)).setCrowdingDistanceE(crowdist);
			 
				}
				
			}else{
			
				 for(int j = 0 ; j < tempfront.size() ; j++){
					 
					 ((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(j)).setCrowdingDistanceE(inf);
				 }

				   
			} 
			
			sortByE.set(i, tempfront);
		
		}    
    }
    
    
    public void calculateCrowdingDistanceT(){
    
		for (int i = 0 ; i < sortByT.size() ; i++){
		  
			ArrayList tempfront = (ArrayList)sortByT.get(i);
			//System.out.println("This is Front "+i+" with size "+tempfront.size());

		 
			if (tempfront.size() > 2){

				((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(0)).setCrowdingDistanceT(inf);
					 double coverMax =   ((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(0)).getTime();
					((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(tempfront.size()-1)).setCrowdingDistanceT(inf);
					double coverMin =   ((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(tempfront.size()-1)).getTime();
			
				for (int j = 1 ; j < ((ArrayList)sortByT.get(i)).size()-1 ; j++){
				
					double crowdist =0;
					
					if (coverMax - coverMin == 0){
					
						crowdist = 0;
					}else{
						crowdist = ((((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(j-1)).getTime())-(((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(j+1)).getTime()))/
										(coverMax - coverMin);
					}
						 ((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(j)).setCrowdingDistanceT(crowdist);
						  //System.out.println("Solution "+j+" has crowd. dist lif "+((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(j)).getCrowdingDistanceLifetime());

				}
			}else{

				for (int j = 0 ; j < ((ArrayList)sortByT.get(i)).size() ; j++){
					(NSGAII.Solutions.QRTPSolution.OneSolution) ((ArrayList)sortByT.get(i)).get(j)).setCrowdingDistanceT(inf);
			
				}       
			} 
				
				sortByT.set(i, tempfront);
			
		} 
	}
    
        public void calculateCrowdingDistanceR(){
    
		for (int i = 0 ; i < sortByR.size() ; i++){
		  
			ArrayList tempfront = (ArrayList)sortByR.get(i);
		 
			if (tempfront.size() > 2){

				((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(0)).setCrowdingDistanceR(inf);
				 double coverMax =   ((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(0)).getRecall();
				((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(tempfront.size()-1)).setCrowdingDistanceR(inf);
				double coverMin =   ((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(tempfront.size()-1)).getRecall();
		  
			for (int j = 1 ; j < ((ArrayList)sortByR.get(i)).size()-1 ; j++){
			
			double crowdist =0;
			
			if (coverMax - coverMin == 0){
			
				crowdist = 0;
			}else{
				crowdist = ((((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(j-1)).getRecall())-(((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(j+1)).getRecall()))/
								(coverMax - coverMin);
			}
				 ((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(j)).setCrowdingDistanceR(crowdist);
			}
			}else{
				for (int j = 0 ; j < ((ArrayList)sortByR.get(i)).size() ; j++){
					((NSGAII.Solutions.QRTPSolution.OneSolution) ((ArrayList)sortByR.get(i)).get(j)).setCrowdingDistanceR(inf);

				}       
			} 
				
				sortByR.set(i, tempfront);
			
		} 
}
    
   
    
    
    public void calculateTotalCrowdingDistance(){
    
    for (int i = 0 ; i < Fronts.size() ; i++){
    
        //the list that has all the fronts
        ArrayList tempfront = (ArrayList) Fronts.get(i);
        double E = 0.0; 
        double PL = 0.0; 
        double DA = 0.0; 
        
        //the list that has all the fronts sorted by energy cons.
            for (int j = 0 ; j < sortByE.size() ; j ++){
            
                ArrayList tempfront1 = (ArrayList) sortByE.get(j);
                
                for (int a=0 ; a < tempfront.size() ; a++){ 
                for (int b=0 ; b < tempfront1.size() ; b++){
                
                //when the same solution is found in two lists then the one sorted by coverage
                    //gives to the general one the value of the crowding distance based on coverage
                if(((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(a)).getIndPos() == ((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront1.get(b)).getIndPos() &&
                ((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(a)).getGen() == ((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront1.get(b)).getGen()){
                
                //calculates the value of croud. dist. based on coverage
                E = ((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront1.get(b)).getCrowdingDistanceE();
                ((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(a)).setCrowdingDistanceE(E);
                }
                
                } 
            }
            }
        
        //this list has the fronts sorted by path loss
        for (int j = 0 ; j < sortByT.size() ; j ++){
            
                ArrayList tempfront1 = (ArrayList) sortByT.get(j);
                
                for (int a=0 ; a < tempfront.size() ; a++){ 
                    for (int b=0 ; b < tempfront1.size() ; b++){
                
                     //when the same solution is found in two lists then the one sorted by lifetime
                    //gives to the general one the value of the crowding distance based on lifetime
                if(((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(a)).getIndPos() == ((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront1.get(b)).getIndPos() &&
                ((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(a)).getGen() == ((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront1.get(b)).getGen()){
                    
                                  
                     PL = ((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront1.get(b)).getCrowdingDistanceT();
                     ((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(a)).setCrowdingDistanceT(PL);
                
                }
                
                }
        }
        }
        
        
         //this list has the fronts sorted by path loss
        for (int j = 0 ; j < sortByR.size() ; j ++){
            
                ArrayList tempfront1 = (ArrayList) sortByR.get(j);
                
                for (int a=0 ; a < tempfront.size() ; a++){ 
                    for (int b=0 ; b < tempfront1.size() ; b++){
                
                     //when the same solution is found in two lists then the one sorted by lifetime
                    //gives to the general one the value of the crowding distance based on lifetime
                if(((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(a)).getIndPos() == ((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront1.get(b)).getIndPos() &&
                ((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(a)).getGen() == ((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront1.get(b)).getGen()){
                    
                                  
                     PL = ((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront1.get(b)).getCrowdingDistanceR();
                     ((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(a)).setCrowdingDistanceR(PL);

                }
                
                }
        }
        }
        
        for (int j = 0 ; j < tempfront.size() ; j++){
        
 ((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(j)).setCrowdingDistanceTotal(((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(j)).getCrowdingDistanceE() +
 ((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(j)).getCrowdingDistanceT() + ((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(j)).getCrowdingDistanceR()  );
       // System.out.println("The total crowding distance for solution "+j+" in front "+i+" is "+((NSGAII.Solutions.QRTPSolution.OneSolution)tempfront.get(j)).getCrowdingDistanceTotal());
        }
        
        
        //the front is returned back to the general list with the values of crowded distances
        //for each solution
        Fronts.set(i,tempfront);
        
    } 

    
    }
   
        
        
    //the general list with the fronts is sorted based on the crowded distances 
    //in each front separately
    public void sortByCrowdingDistance(){
    
    
   
    
        for (int i = 0 ; i < Fronts.size() ; i++){
            
       
            ArrayList tempfront = (ArrayList) Fronts.get(i);
        
        for (int j = 0 ; j < tempfront.size() ; j++){
            
            for (int a = 0 ; a <  tempfront.size() ; a++){
        
                if (a > j){
                    
            NSGAII.Solutions.QRTPSolution.OneSolution first = (NSGAII.Solutions.QRTPSolution.OneSolution) tempfront.get(j);
            double x  = first.getCrowdingDistanceTotal();
            NSGAII.Solutions.QRTPSolution.OneSolution second = (NSGAII.Solutions.QRTPSolution.OneSolution) tempfront.get(a);
            double y  = second.getCrowdingDistanceTotal();
            
            if (y >= x){
           
            tempfront.set(j, second);
            tempfront.set(a, first);
            
            }else{ 
           
            tempfront.set(j, first);
            tempfront.set(a, second);
            }
    }        
    }      
    }   
        //System.out.println("After sorting there are "+((ArrayList)Fronts.get(i)).size()+" in "+(i));
        Fronts.set(i,tempfront);
    }  
   
   int count = 0; 
   
  // System.out.println("Fronts "+Fronts.size());
   
   for (int i = 0 ; i < Fronts.size() ; i++){
    
        ArrayList temp = (ArrayList) Fronts.get(i);
    
      //  System.out.println("temp "+temp.size());
        
    int divRank = 0;
        
    for (int j = 0 ; j < temp.size() ; j++){
    
        if (count < alg.popSize){
           
           
        ((NSGAII.Solutions.QRTPSolution.OneSolution) temp.get(j)).setDivRank(divRank);    
        newPop[count] = (NSGAII.Solutions.QRTPSolution.OneSolution) temp.get(j);    
        count = count+1;
        divRank = divRank+1;
        
          }
    }
    }
   
   
   ArrayList tempfrontToPrint = new ArrayList();
   ArrayList oldfrontToPrint = frontToPrint;
   ArrayList newfrontToPrint = new ArrayList();
   
    //System.out.println();
    //System.out.println("In Generation "+alg.gen+" the PF is: ");
    for(int j = 0 ; j < newPop.length ; j++){
    
       if(((NSGAII.Solutions.QRTPSolution.OneSolution)newPop[j]).getRank() == 0){
        
       //System.out.println("Solution "+j+" with COV: "+((NSGAII.Solutions.QRTPSolution.OneSolution)popForGA[j]).getCoverage()+" and Life:"+((NSGAII.Solutions.QRTPSolution.OneSolution)popForGA[j]).getLifetime());    
      
       tempfrontToPrint.add(((NSGAII.Solutions.QRTPSolution.OneSolution)newPop[j]));
    }
    }

    for(int i = 0 ; i < tempfrontToPrint.size() ; i++){
    
    NSGAII.Solutions.QRTPSolution.OneSolution temp = (NSGAII.Solutions.QRTPSolution.OneSolution)tempfrontToPrint.get(i);   
       
    oldfrontToPrint.add(temp);
       
   }
    
     //System.out.println("The size is "+oldfrontToPrint.size());
    
    //WE COMPARE ALL THE SOLUTIONS TO KEEP ONLY THE BEST
    
    for(int i = 0 ; i < oldfrontToPrint.size() ; i++){
        
         boolean dominated = false;
         boolean exist = false;
         NSGAII.Solutions.QRTPSolution.OneSolution temp1 = temp1 = (NSGAII.Solutions.QRTPSolution.OneSolution)oldfrontToPrint.get(i);
        
        for(int j = 0 ; j < oldfrontToPrint.size() ; j++){
    
              NSGAII.Solutions.QRTPSolution.OneSolution temp2 = (NSGAII.Solutions.QRTPSolution.OneSolution)oldfrontToPrint.get(j);
              
              if (i != j && temp1 != null && temp2 != null){
           
      if(temp1.getEnergy() <= 0 || temp1.getTime() <= 0 ||  temp1.getRecall() <= 0){   
         
          temp1 = null;
          oldfrontToPrint.set(i,null);
       
       }}
              
          
       if (i != j && temp1 != null && temp2 != null){
           
       if((temp1.getEnergy() > temp2.getEnergy() && temp1.getTime() > temp2.getTime() && temp1.getRecall() < temp2.getRecall())||
     (temp1.getEnergy() == temp2.getEnergy() && temp1.getTime() == temp2.getTime() && temp1.getRecall() == temp2.getRecall())||
     (temp1.getEnergy() > temp2.getEnergy() && temp1.getTime() == temp2.getTime() && temp1.getRecall() ==  temp2.getRecall())||
     (temp1.getEnergy() == temp2.getEnergy() && temp1.getTime() > temp2.getTime() && temp1.getRecall() == temp2.getRecall())||
     (temp1.getEnergy() == temp2.getEnergy() && temp1.getTime() == temp2.getTime() && temp1.getRecall() < temp2.getRecall())||
     (temp1.getEnergy() > temp2.getEnergy() && temp1.getTime() > temp2.getTime() && temp1.getRecall() == temp2.getRecall())||
     (temp1.getEnergy() > temp2.getEnergy() && temp1.getTime() == temp2.getTime() && temp1.getRecall() < temp2.getRecall())||
     (temp1.getEnergy() == temp2.getEnergy() && temp1.getTime() > temp2.getTime() && temp1.getRecall() < temp2.getRecall()))
     {       
       
          temp1 = null;
          oldfrontToPrint.set(i,null);
       
       }     
       }}
    }
     
     
       for(int i = 0 ; i < oldfrontToPrint.size() ; i++){
           
      if ((NSGAII.Solutions.QRTPSolution.OneSolution)oldfrontToPrint.get(i) != null){
       
          
          NSGAII.Solutions.Solution newSol = new NSGAII.Solutions.QRTPSolution.OneSolution(alg.chromosomeSize);
          newSol.setChromosome(((NSGAII.Solutions.QRTPSolution.OneSolution)oldfrontToPrint.get(i)).getChromosome());
          newSol.setRecall(((NSGAII.Solutions.QRTPSolution.OneSolution)oldfrontToPrint.get(i)).getRecall());
          newSol.setEnergy(((NSGAII.Solutions.QRTPSolution.OneSolution)oldfrontToPrint.get(i)).getEnergy());
          newSol.setTime(((NSGAII.Solutions.QRTPSolution.OneSolution)oldfrontToPrint.get(i)).getTime());
          newfrontToPrint.add(newSol);
       
      }}
   
    
   if(alg.gen == alg.termination-1){
    System.out.println();
    System.out.println("In Generation "+alg.gen+" the PF is: ");
    for(int j = 0 ; j < newfrontToPrint.size() ; j++){
    
     NSGAII.Solutions.QRTPSolution.OneSolution temp = (NSGAII.Solutions.QRTPSolution.OneSolution)newfrontToPrint.get(j);
        
 System.out.println("Solution "+j+" with E: "+nf.format(temp.getEnergy())+" T:"+nf.format(temp.getTime())+" R:"+nf.format(temp.getRecall()));
      
   }
   //System.out.println();
   }
      
   frontToPrint = newfrontToPrint;

 }
 
}//end of class
