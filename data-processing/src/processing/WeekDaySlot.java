package processing;

public class WeekDaySlot implements Comparable{
	int week;
	int day;
	int slot;

	public int getWeek() {
		return week;
	}

	public void setWeek(int week) {
		this.week = week;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

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