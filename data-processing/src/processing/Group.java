package processing;
import java.util.ArrayList;

 class WeekDaySlot implements Comparable{
	 int week;
	 int day;
	 int slot;
	 
	 public WeekDaySlot(int week, int day, int slot) {
		this.week = week;
		this.day = day;
		this.slot = slot;
		
	}
	 
	 @Override
		public String toString() {
		 if(week == 0)
			return "(" + day + "," + slot +")" ;
		 else if(slot == 0)
			 return "(" + week + ","  + day + ")";
		 else 
			 return "(" + week + ","  + day + "," + slot +")";
		}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		WeekDaySlot other = (WeekDaySlot)o;
		   if(other.week == week && other.day == day && other.slot == slot)
			   return 0;
		   else return 1;
		}
 	}


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
