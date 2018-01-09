package processing;

public class Compensation{
	String staffID;
	String groupID;
	WeekDaySlot time;
	String roomID;
	public Compensation(String staffID, String groupID, WeekDaySlot time,
			String roomID) {
		super();
		this.staffID = staffID;
		this.groupID = groupID;
		this.time = time;
		this.roomID = roomID;
	}
	@Override
	public String toString() {
		return "Compensation [staffID=" + staffID + ", groupID=" + groupID
				+ ", time=" + time + ", roomID=" + roomID + "]";
	}
}