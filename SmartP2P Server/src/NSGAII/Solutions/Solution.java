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

 

package NSGAII.Solutions;

import java.util.ArrayList;

public interface Solution {
    
    public void setChromosome(int[] chromosome);
    public int[] getChromosome();
    public void setEnergy(double energy);
    public double getEnergy();
    public void setTime(double time);
    public double getTime();    
    public void setRecall(double recall);
    public double getRecall();    
    public void setNpInitialize(int np1);  
    public void setSpInitialize(); 
    public void setNpMinus(int np1);   
    public void setNp(int np1);    
    public int getNp();
    public void setSp(NSGAII.Solutions.Solution sol);  
    public ArrayList getSp();
    public void setRank(int rank);      
    public int getRank();
    public void setDivRank(int divRank);
    public int getDivRank();
    public void setCrowdingDistanceE(double crowdistE);
    public double getCrowdingDistanceE();
    public void setCrowdingDistanceT(double crowdistT);
    public double getCrowdingDistanceT();
    public void setCrowdingDistanceR(double crowdistR);
    public double getCrowdingDistanceR();
    public void setCrowdingDistanceTotal(double crowdist);
    public double getCrowdingDistanceTotal();
    public int getIndPos();
    public void setIndividualPos(int indPos);
    public void setGen(int gen);
    public int getGen();
}
