package processing;
import java.util.ArrayList;


public class Room {
	String ID;
	int capacity;
	int location;
	int type;
	ArrayList<WeekDaySlot> occupiedSlots; 
	ArrayList<Integer> daysOff;
	ArrayList<WeekDaySlot> compDates;
	
	
	public Room(String iD, int capacity, int location, int type) {
		super();
		ID = iD;
		this.capacity = capacity;
		this.location = location;
		this.type = type;
		occupiedSlots = new ArrayList<WeekDaySlot>();
		compDates = new ArrayList<WeekDaySlot>();
		daysOff = new ArrayList<Integer>();
	}
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public int getCapacity() {
		return capacity;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	public int getLocation() {
		return location;
	}
	public void setLocation(int location) {
		this.location = location;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public ArrayList<WeekDaySlot> getOccupiedSlots() {
		return occupiedSlots;
	}
	public void setOccupiedSlots(ArrayList<WeekDaySlot> occupiedSlots) {
		this.occupiedSlots = occupiedSlots;
	}
	public ArrayList<Integer> getDaysOff() {
		return daysOff;
	}
	public void setDaysOff(ArrayList<Integer> daysOff) {
		this.daysOff = daysOff;
	}
	public ArrayList<WeekDaySlot> getCompDates() {
		return compDates;
	}
	public void setCompDates(ArrayList<WeekDaySlot> compDates) {
		this.compDates = compDates;
	}
	@Override
	public String toString() {
		return "Room [ID=" + ID + ", capacity=" + capacity + ", location="
				+ location + ", type=" + type + ", slotsNum=  " + occupiedSlots.size()  + ", occupiedSlots="
				+ occupiedSlots + ", daysOff=" + daysOff + ", compDates="
				+ compDates + "]";
	}
	
	
}
