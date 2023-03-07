package coursework;

public class Vehicle {
	
	String ID;		  // Registration plate equivalent
	String Type;	  // Car - Truck - Bus
	int CrossTime;    // Seconds
	String Direction; // Left - Right - Straight
	int Length;		  // Meters
	int Emission;	  // Grams of CO2 emitted
	String Status;	  // Waiting - driving?
	
	//Constructor
	public Vehicle(String ID, String Type, int CrossTime, int Length, String Direction, int Emission) {
		this.ID = ID;
		this.Type = Type;
		this.CrossTime = CrossTime;
		this.Direction = Direction;
		this.Length = Length;
		this.Emission = Emission;
		this.Status = "Waiting";
	}
	
	//Methods
	//Calculates the emissions a car generates to cross the junction
	public int CrossEmisions() {
		return Emission * CrossTime;
	}
	//Calculates the emissions a vehicle generates while waiting an amount of time
	public int WaitEmissions(int wait) {
		return Emission * wait;
	}
	public String toString() {
		return ID + " | " + Status + " | " +  Type + " | " + CrossTime + " | " + Length + " | " + Direction + " | " + Emission + "g";
		
	}
}