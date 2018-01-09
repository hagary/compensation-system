package processing;
import java.util.ArrayList;


public class Staff {
	
	String ID;
	ArrayList<WeekDaySlot> occupiedSlots; 
	ArrayList<Integer> daysOff;
	ArrayList<WeekDaySlot> compDates;
	ArrayList<String> tutorials;
	boolean isLecturer;
	
	
	
	
	public Staff(String iD, boolean isLecturer ) {
		super();
		ID = iD;
		this.isLecturer = isLecturer;
		occupiedSlots = new ArrayList<WeekDaySlot>();
		compDates = new ArrayList<WeekDaySlot>();
		daysOff = new ArrayList<Integer>();
		tutorials = new ArrayList<String>();
	}

	@Override
	public String toString() {
		return "Staff [ID=" + ID + ", slotsNum=  " + occupiedSlots.size()  + ", occupiedSlots=" + occupiedSlots
				+ ", daysOff=" + daysOff + ", compDates=" + compDates
				+ ", tutorials=" + tutorials + ", isLecturer=" + isLecturer
				+ "]";
	}

	public ArrayList<String> getTutorials() {
		return tutorials;
	}

	public void setTutorials(ArrayList<String> tutorials) {
		this.tutorials = tutorials;
	}

	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
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
	public boolean isLecturer() {
		return isLecturer;
	}
	public void setLecturer(boolean isLecturer) {
		this.isLecturer = isLecturer;
	}
}
