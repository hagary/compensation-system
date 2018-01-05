
public class StaffEntry {
	private int groupID;
	private String groupName;
	private String groupFullName;
	private String staffName;
	private boolean isLecturer;
	@Override
	public String toString() {
		return "StaffEntry [groupID=" + groupID + ", groupName=" + groupName
				+ ", groupFullName=" + groupFullName + ", staffName="
				+ staffName + ", isLecturer=" + isLecturer + "]";
	}
	public int getGroupID() {
		return groupID;
	}
	public void setGroupID(int groupID) {
		this.groupID = groupID;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getGroupFullName() {
		return groupFullName;
	}
	public void setGroupFullName(String groupFullName) {
		this.groupFullName = groupFullName;
	}
	public String getStaffName() {
		return staffName;
	}
	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}
	public boolean isLecturer() {
		return isLecturer;
	}
	public void setLecturer(boolean isLecturer) {
		this.isLecturer = isLecturer;
	}
}
