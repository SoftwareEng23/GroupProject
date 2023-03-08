package coursework;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.io.*;  
import java.util.Scanner; 
import java.util.ArrayList;
import java.util.Arrays;


public class JunctionControler {

	HashMap <String, Queue <Vehicle> > Phase;
	HashMap <String, Statistics> PhaseStats;
	ArrayList<Integer> PhaseTime;
	//Constructor
	public JunctionControler() {
		Phase = new HashMap<>();
		PhaseStats = new HashMap<String, Statistics>();
		PhaseTime = new ArrayList<Integer>();
		

		importVehicles();
		importPhases();
		//Initialising PhaseStats
		for (int i = 0; i < PhaseTime.size(); i ++) {
			PhaseStats.put(""+i,new Statistics());
		}
		//CalcPhases();
		//generateReport();
	}
	public void AddVehicle(Object[] uiInput) {
		Queue<Vehicle> q = new LinkedList<>();
		Vehicle tmpVeh;
		
		tmpVeh = new Vehicle((String)uiInput[0], (String)uiInput[1], (Integer)uiInput[2], (Integer)uiInput[3], (String)uiInput[4], (Integer)uiInput[5],(String)uiInput[6]);
		q = new LinkedList<>();
		
		//Avoiding overwriting existing queues
		if(Phase.containsKey(uiInput[6])) {
			q = Phase.get(uiInput[6]);
			q.add(tmpVeh);
			Phase.put((String)uiInput[6], q);
		
		}else {
			q.add(tmpVeh); //Adding new vehicle to queue
			Phase.put((String)uiInput[6],q); // adding queue and to correct segment
		}
	}
	//Methods
	public void importVehicles() {

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
				// creating a new vehicle object
				tmpVeh = new Vehicle(csvRow[0], csvRow[1], Integer.parseInt(csvRow[2]), Integer.parseInt(csvRow[3]), csvRow[4], Integer.parseInt(csvRow[5]),csvRow[6]);
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
				
			}   
			
			System.out.println(Phase);
			System.out.println(" File Import Complete.");
			sc.close();  //closes the scanner  
			
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}  
	}
	
	public void importPhases() {
		Scanner sc;
		try {
			System.out.println(" Beginning Phase File Import.");
			sc = new Scanner(new File("./DataFiles/Phases.csv"));
			sc.useDelimiter(",");   //sets the delimiter pattern 
			
			while (sc.hasNextLine())  //returns a boolean value  
			{  
				//Splitting csv line into an array
				PhaseTime.add(Integer.parseInt(sc.nextLine()));
			}   
			
			System.out.println(PhaseTime);
			System.out.println(" File Import Complete.");
			sc.close();  //closes the scanner  
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}  
	}
	
	public String generateReport() {
		
		int[] totalv = new int[PhaseTime.size()+1];
		int total = 0;
		int p = 1;
		
		for (int i : totalv) {
			totalv[i] = 0;
		}
		
		int totalTme = 0;
		for (int s = 0; s < PhaseStats.size(); s++) {
			totalTme += PhaseStats.get("" + s).WaitTime;
		}
		while ( p <  PhaseTime.size()) {
			//P is for each Phase , segment Right & straight and left turn lanes
			// converts to segment
			Queue <Vehicle> CurrentPhase = Phase.get(""+(p+2)/2);
			
			for (Vehicle v : CurrentPhase) {
				if(v.Direction.equals("Right") || v.Direction.equals("Straight")) { // Even -- Right & Strait
					totalv[p] += 1;
					total ++;
				}else{
					totalv[p+1] += 1;
					total ++;
				}
			}
			if(totalv[p] != 0) {
				System.out.println("p" + p + " : " + totalv[p]);
			}else {
				System.out.println("p" + p + " : " + 0);
			}
			if(totalv[p+1] != 0) {
				System.out.println("p" + (p+1) + " : " + totalv[p+1]);
			}
			else {
				System.out.println("p" + (p+1) + " : " + 0);
			}
			p+=2;
		};
		
		System.out.println("Total Emissions : " + CalcTotalEmissions() + "g");
		System.out.println("Average Wait : " + totalTme/ total + "s" );
		return "";
	}
	public void CalcPhases() {
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
				
				if(p % 2 == 0 && (curP - v.CrossTime)>=0 &&(v.Direction.equals("Right") || v.Direction.equals("Straight")) &&! v.Status.equals("Crossed")) { // Even -- Right & Strait
					vTime += v.CrossTime;
					UpdateStats( v, vTime, phaseTime,  p);
					
				}else if ((curP - v.CrossTime)>=0 && !v.Status.equals("Crossed")){ //Odd -- Left
					vTime += v.CrossTime;
					UpdateStats( v, vTime, phaseTime,  p);
				}else {
					break; 
				}
			}
			p++;
		};
	}
	public int CalcTotalEmissions() {
		int total = 0;
		for (int s = 0; s < PhaseStats.size(); s++) {
			total += PhaseStats.get("" + s).Emissions;
		}
		
		return total;
	}
	
	public void UpdateStats(Vehicle v, int vTime, int curP, int p) {
		v.Status = "Crossed";
		PhaseStats.get("" + p).WaitTime += vTime + curP; 
		PhaseStats.get("" + p).WaitLength += v.Length;
		PhaseStats.get("" + p).CrossTime += v.CrossTime;
		PhaseStats.get(""+p).Emissions += (v.Emission * (PhaseStats.get(""+p).WaitTime / 60 ));
	}
	
	public Object[][] vehToObj() {
		ArrayList<Vehicle> vehicles = new ArrayList<Vehicle>();
		for(Queue<Vehicle> q : Phase.values()) {
			for(Vehicle v : q) {
				vehicles.add(v);
			}
		}
		
		Object[][] output = new Object[vehicles.size()][8];
		for(int i = 0; i < vehicles.size(); i++) {
			output[i] = vehicles.get(i).toObject();
		}
		return output;
	}
	
	public Object[][] phaseToObj(){
		Object[][] output = new Object[PhaseTime.size()][2];
		for(int i = 0; i < PhaseTime.size(); i ++) {
			output[i][0] = "P " + (i + 1);
			output[i][1] = PhaseTime.get(i);
		}
		return output;
	}
	
	public Object[][] segToObj(){
		
		Object[][] Segments = new Object [4][4];
		for(int x = 0; x < 4; x++) {
			for(int y = 0; y < 4; y++) {
				Segments[x][y] = 0;
			}
		}
		for(int s =0; s < 8; s++) {
			Segments[s/2][0] = "S" + ((s/2)+1);
			Segments[s/2][1] = (Integer)Segments[s/2][1] + PhaseStats.get(""+s).WaitTime;
			Segments[s/2][2] = (Integer)Segments[s/2][2] + PhaseStats.get(""+s).WaitLength;
			Segments[s/2][3] = (Integer)Segments[s/2][3] + PhaseStats.get(""+s).CrossTime;

		}
		
		return Segments;
	}
	
}
