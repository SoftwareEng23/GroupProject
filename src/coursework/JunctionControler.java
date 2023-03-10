package coursework;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.io.*;  
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Arrays;
/**
* @author Ryan Spowart
* Controller Class within MVC architecture
* Manages junction simulation
* Import & exports
*/

public class JunctionControler {

	HashMap <String, Queue <Vehicle> > Phase; //Stores the phase and queue of vehicles
	HashMap <String, Statistics> PhaseStats; //Stores the statistics attributed to each phase
	ArrayList<Integer> PhaseTime; //Stores the time for each phase
	
	//Constructor
	public JunctionControler() {
		//Initialising variables
		Phase = new HashMap<>();
		PhaseStats = new HashMap<String, Statistics>();
		PhaseTime = new ArrayList<Integer>();
		

		importVehicles();//Importing data from csv
		importPhases();
		
		//Initialising PhaseStats
		for (int i = 0; i < PhaseTime.size(); i ++) {
			PhaseStats.put(""+i,new Statistics());
		}
	}
	
	//Methods
	/*
	 * Adds a new vehicle to the system
	 * Status not required as it will always initially be waiting.
	 * Validation not included
	 */
	public void AddVehicle(Object[] uiInput) {
		System.out.println((String)uiInput[7]);
		try {
			Phase.get((String)uiInput[7]).add(new Vehicle(uiInput));
		} catch (ValidationExeption e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Imports vehicles from a csv and adds them to the correct data structures.
	 * Data stored within Datafiles directory
	 */
	private void importVehicles() {

		Scanner sc;
		Queue<Vehicle> q = new LinkedList<>();
		Vehicle tmpVeh;
		try {
			System.out.println(" Beginning Vehicle File Import.");
			sc = new Scanner(new File("./DataFiles/Vehicles.csv"));
			sc.useDelimiter(",");   //sets the delimiter pattern 
			
			while (sc.hasNextLine())  //returns a boolean value  
			{  
				//Splitting csv line into an array
				String[] csvRow = sc.nextLine().split(",");
				try {
					// creating a new vehicle object
					tmpVeh = new Vehicle(csvRow);
					q = new LinkedList<>();
					
					//Avoiding overwriting existing queues
					if(Phase.containsKey(csvRow[6])) {
						q = Phase.get(csvRow[6]);
						q.add(tmpVeh);
						Phase.put(csvRow[6], q);
					
					}else {
						q.add(tmpVeh); //Adding new vehicle to queue
						Phase.put(csvRow[6],q); // adding queue and to correct segment
					}
					
				}catch (ValidationExeption e) {
					e.printStackTrace();
				}  
			}   
			
			System.out.println(Phase);
			System.out.println(" File Import Complete.");
			sc.close();  //closes the scanner  
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
	}
	/*
	 * Imports Phases from a csv and adds them to the correct data structures.
	 * Data stored within Datafiles directory
	 * Also Validates the input data upon entry
	 */
	private void importPhases() {
		Scanner sc;
		try {
			System.out.println(" Beginning Phase File Import.");
			sc = new Scanner(new File("./DataFiles/Phases.csv"));
			sc.useDelimiter(",");   //sets the delimiter pattern 
			
			while (sc.hasNextLine())  //returns a boolean value  
			{  
				try { // Validating file input
					String nextLine = sc.nextLine();
					if(Integer.parseInt(nextLine) < 10 ) { //Minimum bounds
						PhaseTime.add(10);
						throw new ValidationExeption("Phase Below valid range...Adding Default");
					}else if(Integer.parseInt(nextLine) > 500){ //Maximum bounds
						PhaseTime.add(500);
						throw new ValidationExeption("Phase Above valid range...Adding Default");
					}else if(PhaseTime.size() > 8){ //Maximum amount of phases
						throw new ValidationExeption("too many phases in file");
					}else {
						PhaseTime.add(Integer.parseInt(nextLine));
					}
				}catch (ValidationExeption e) { //printing exception here to allow the file to continue being read
					e.printStackTrace();
				}  
				//Splitting csv line into an array
				
			}   
			//Validating for to few phases.
			if(PhaseTime.size() < 8) {
				while(PhaseTime.size() < 8) {
					PhaseTime.add(60);
				}
				throw new ValidationExeption("not enough phases in file...Adding default durations.");
			}
			
			System.out.println(PhaseTime);
			System.out.println(" File Import Complete.");
			sc.close();  //closes the scanner  
			//Printing error stacktraces
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ValidationExeption e) {
			e.printStackTrace();
		} catch(NumberFormatException e) {
			e.printStackTrace();
		}
	}
	/*
	 * Generates a report from the statistics stores and saves to a .txt file
	 * Outputs the text file to the datafiles directory
	 * will save over previous file automatically.
	 * Also outputs the generated report to console.
	 */
	public String generateReport() {
		//Declarations
		int[] totalv = new int[PhaseTime.size()+1];
		int total = 0;
		int p = 1;
		//Initialisation
		for (int i : totalv) {
			totalv[i] = 0;
		}
		
		//Combining wait time
		int totalTme = 0;
		for (int s = 0; s < PhaseStats.size(); s++) {
			totalTme += PhaseStats.get("" + s).WaitTime;
		}

	    try {
	      FileWriter myWriter = new FileWriter("./DataFiles/Report.txt");
	      
	      
	      //For Each Phase, works theoretically with any number of phases
			while ( p <  PhaseTime.size()) {
				//P is for each Phase , segment Right & straight and left turn lanes
				// converts to segment
				Queue <Vehicle> CurrentPhase = Phase.get(""+(p+2)/2);
				
				//for each vehicle in the current segment the number of vehicles passed is calculated
				for (Vehicle v : CurrentPhase) {
					if(v.Direction.equals("Right") || v.Direction.equals("Straight")) { // Even -- Right & Strait
						totalv[p] += 1;
						total ++;
					}else{
						totalv[p+1] += 1;
						total ++;
					}
				}
				//Formatting and outputting data
				if(totalv[p] != 0) {
					System.out.println("p" + p + " : " + totalv[p]);
					myWriter.write("p" + p + " : " + totalv[p]+"\n");
				}else {
					System.out.println("p" + p + " : " + 0);
					myWriter.write("p" + p + " : " + 0+"\n");
				}
				if(totalv[p+1] != 0) {
					System.out.println("p" + (p+1) + " : " + totalv[p+1]);
					myWriter.write("p" + (p+1) + " : " + totalv[p+1]+"\n");

				}
				else {
					System.out.println("p" + (p+1) + " : " + 0);
					myWriter.write("p" + (p+1) + " : " + 0+"\n");

				}
				p+=2;
			};
			//Outputting calculated totals
			System.out.println("Total Emissions : " + CalcTotalEmissions() + "g");
			System.out.println("Average Wait : " + totalTme/ total + "s" );
			myWriter.write("Total Emissions : " + CalcTotalEmissions() + "g"+"\n");
			myWriter.write("Average Wait : " + totalTme/ total + "s" +"\n");

	      
	      
	      
	      myWriter.close();
	      System.out.println("Successfully wrote to the file.");
	    } catch (IOException e) {
	      System.out.println("An error occurred.");
	      e.printStackTrace();
	    }
			  
		
		
		
		return "";
	}
	/*
	 * Phase Calculations for vehicle transition through junction
	 * also calculates statistics
	 */
	public void CalcPhases() {
		int count = 0;
		while(!isCrossed() && count < 1000) {
			int p = 0;
			int phaseTime = 0;
			while ( p <  PhaseTime.size()) {
				int curP = PhaseTime.get(p);
				int vTime = 0;
				phaseTime += curP;
				Queue <Vehicle> CurrentPhase = Phase.get("" + (p+2)/2);
				//P is for each Phase , segment Right & straight and left turn lanes
				// converts to segment
				for (Vehicle v : CurrentPhase) {
					if((curP - v.CrossTime)>=0 &&(v.Direction.equals("Right") || v.Direction.equals("Straight")) &&! v.Status.equals("Crossed")) { // Even -- Right & Strait
						vTime += v.CrossTime;
						UpdateStats( v, vTime, phaseTime,  p);
						
					}else if ((curP - v.CrossTime)>=0 && !v.Status.equals("Crossed")){ //Odd -- Left
						vTime += v.CrossTime;
						UpdateStats( v, vTime, phaseTime,  p);
					}
				}
				p++;
			};
			count ++;
		}
	}
	/*
	 * returns whether all vehicles have crossed or not
	 */
	private Boolean isCrossed() {
		int p = 0;
		while ( p <  PhaseTime.size()) {
			Queue <Vehicle> CurrentPhase = Phase.get("" + (p+2)/2);
			for (Vehicle v : CurrentPhase) {
				if(v.Status != "Crossed") {
					System.out.println("Vehicle not crossed found");
					return false;
				}
			}
			p++;
		};
		return true;
	}
	/*
	 * Updates the statistics, only used within Calc Phases
	 * Reduces repeating code
	 */
	private void UpdateStats(Vehicle v, int vTime, int curP, int p) {
		v.Status = "Crossed";
		PhaseStats.get("" + p).WaitTime += vTime + curP; 
		PhaseStats.get("" + p).WaitLength += v.Length;
		PhaseStats.get("" + p).CrossTime += v.CrossTime;
		PhaseStats.get(""+p).Emissions += v.WaitEmissions(PhaseStats.get(""+p).WaitTime) + v.CrossEmisions();
	}
	
	/*
	 * Calculates the total emissions
	 */
	public int CalcTotalEmissions() {
		int total = 0;
		for (int s = 0; s < PhaseStats.size(); s++) { //summing all emissions stored
			total += PhaseStats.get("" + s).Emissions;
		}
		
		return total;
	}

	/*
	 * Converts the stored data structures to a generic format for us in gui elements
	 * for vehicles
	 */

	public Object[][] vehToObj() {
		ArrayList<Vehicle> vehicles = new ArrayList<Vehicle>();
		for(Queue<Vehicle> q : Phase.values()) {	//Converting vehicles hashmap to a list
			for(Vehicle v : q) {
				vehicles.add(v);
			}
		}
		
		Object[][] output = new Object[vehicles.size()][8];	//converting array list to generic format
		for(int i = 0; i < vehicles.size(); i++) {
			output[i] = vehicles.get(i).toObject();
		}
		return output;
	}
	
	/*
	 * Converts the stored data structures to a generic format for us in gui elements
	 * for Phases
	 */
	public Object[][] phaseToObj(){
		Object[][] output = new Object[PhaseTime.size()][2];
		for(int i = 0; i < PhaseTime.size(); i ++) {
			output[i][0] = "P " + (i + 1);
			output[i][1] = PhaseTime.get(i);
		}
		return output;
	}
	/*
	 * Converts the stored data structures to a generic format for us in gui elements
	 * for Segment Statistics
	 */
	public Object[][] segToObj(){
		
		Object[][] Segments = new Object [4][4];
		for(int x = 0; x < 4; x++) {	//initialising new generic object
			for(int y = 0; y < 4; y++) {
				Segments[x][y] = 0;
			}
		}
		//Assigning data to generic format
		for(int s =0; s < 8; s++) {
			Segments[s/2][0] = "S" + ((s/2)+1);
			Segments[s/2][1] = (Integer)Segments[s/2][1] + PhaseStats.get(""+s).WaitTime;
			Segments[s/2][2] = (Integer)Segments[s/2][2] + PhaseStats.get(""+s).WaitLength;
			Segments[s/2][3] = (Integer)Segments[s/2][3] + PhaseStats.get(""+s).CrossTime;

		}
		
		return Segments;
	}
}
