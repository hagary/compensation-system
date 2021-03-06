package processing;
import java.util.ArrayList;

public class Group {
	String studyGroup;
	String tutNum;
	ArrayList<WeekDaySlot> occupiedSlots; 
	ArrayList<Integer> daysOff;
	ArrayList<WeekDaySlot> compDates;
	int size;
	public Group(String studyGroup, String tutID, int Size) {
		super();
		occupiedSlots = new ArrayList<WeekDaySlot>();
		daysOff = new ArrayList<Integer>();
		compDates = new ArrayList<WeekDaySlot>();
		this.studyGroup = studyGroup;
		this.tutNum = tutID;
		this.size = Size;
	}
	
	
	public int getSize() {
		return size;
	}


	public void setSize(int size) {
		this.size = size;
	}


	@Override
	public String toString() {
		return "Group [studyGroup=" + studyGroup + ", tutNum=" + tutNum
				+ ", slotsNum=  " + occupiedSlots.size() + ", occupiedSlots=" + occupiedSlots + ", daysOff=" + daysOff
				+ ", compDates=" + compDates + "]";
	}
	
	public String getStudyGroup() {
		return studyGroup;
	}
	public void setStudyGroup(String studyGroup) {
		this.studyGroup = studyGroup;
	}
	public String getTutNum() {
		return tutNum;
	}
	public void setTutNum(String tutNum) {
		this.tutNum = tutNum;
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
	
	
	
	
	
	
}
