package processing;

public class Compensation{
	String staffID;
	String groupID;
	WeekDaySlot time;
	String roomID;
	String roomName;
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
	public String getStaffID() {
		return staffID;
	}
	public void setStaffID(String staffID) {
		this.staffID = staffID;
	}
	public String getGroupID() {
		return groupID;
	}
	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}
	public WeekDaySlot getTime() {
		return time;
	}
	public void setTime(WeekDaySlot time) {
		this.time = time;
	}
	public String getRoomID() {
		return roomID;
	}
	public void setRoomID(String roomID) {
		this.roomID = roomID;
	}
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
}