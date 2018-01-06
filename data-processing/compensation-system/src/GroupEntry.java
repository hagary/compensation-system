
public class GroupEntry {
	private int groupID;
	private String groupName;
	private String groupFullName;
	private String courseCode;
	private String courseName;
	private int day;
	private int slot;
	private String studyGroup;
	private String sessionType;
	private String roomName;
	private int roomCapacity;
	private int groupSize;

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
	public String getCourseCode() {
		return courseCode;
	}
	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
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
	public String getStudyGroup() {
		return studyGroup;
	}
	public void setStudyGroup(String studyGroup) {
		this.studyGroup = studyGroup;
	}
	public String getSessionType() {
		return sessionType;
	}
	public void setSessionType(String sessionType) {
		this.sessionType = sessionType;
	}
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	public int getRoomCapacity() {
		return roomCapacity;
	}
	public void setRoomCapacity(int roomCapacity) {
		this.roomCapacity = roomCapacity;
	}
	public int getGroupSize() {
		return groupSize;
	}
	public void setGroupSize(int groupSize) {
		this.groupSize = groupSize;
	}
	
	@Override
	public String toString() {
		return "GroupEntry [groupID=" + groupID + ", groupName=" + groupName
				+ ", groupFullName=" + groupFullName + ", courseCode="
				+ courseCode + ", courseName=" + courseName + ", day=" + day
				+ ", slot=" + slot + ", studyGroup=" + studyGroup
				+ ", sessionType=" + sessionType + ", roomName=" + roomName
				+ ", roomCapacity=" + roomCapacity + ", groupSize=" + groupSize
				+ "]";
	}
}
