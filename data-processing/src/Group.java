import java.util.ArrayList;

 class WeekDaySlot{
	 int week;
	 int day;
	 int slot;
	 
	 public WeekDaySlot(int week, int day, int slot) {
		this.week = week;
		this.day = day;
		this.slot = slot;
	}
}


public class Group {
	String studyGroup;
	int tutNum;
	ArrayList<WeekDaySlot> occupiedSlots; 
	ArrayList<Integer> daysOff;
	public Group(String studyGroup, int tutNum) {
		super();
		occupiedSlots = new ArrayList<WeekDaySlot>();
		daysOff = new ArrayList<Integer>();
		this.studyGroup = studyGroup;
		this.tutNum = tutNum;
	}
	ArrayList<WeekDaySlot> compDates;
	
	
	public String getStudyGroup() {
		return studyGroup;
	}
	public void setStudyGroup(String studyGroup) {
		this.studyGroup = studyGroup;
	}
	public int getTutNum() {
		return tutNum;
	}
	public void setTutNum(int tutNum) {
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