import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map.Entry;


public class Preprocessing {
	
	public static Hashtable<String,Group> groupHT;
	public static Hashtable<String,Staff> staffHT;
	public static Hashtable<String,Staff> tutHT;
	public static Hashtable<String, ArrayList<String>> TutorialGroup;
	public static Hashtable<String,ArrayList<WeekDaySlot>> groupTutorialSchedule;
	public static ArrayList<GroupEntry> GE;
	public static ArrayList<StaffEntry> SE;

	
	public static void main(String[] args) throws Exception{
		 GE = CSVReader.readGroupFile();
		 SE = CSVReader.readStaffFile();
		 groupHT = new Hashtable<>();
		 staffHT = new Hashtable<>();
		 tutHT = new Hashtable<String, Staff>(); 
		 TutorialGroup = new Hashtable<String, ArrayList<String>>();
		 groupTutorialSchedule = new Hashtable<String, ArrayList<WeekDaySlot>>();
		 processTutorialGroup();
		 processLectures();
		 processTutorials();
		 processStaff();
		 staffDaysOff();
		 groupDaysOff();
		 System.out.println(groupHT);
	}
	
	
	
	public static void staffDaysOff(){
		for (Entry<String, Staff> staffEntry :staffHT.entrySet()) {
			ArrayList<Integer> days = new ArrayList<Integer>();
			ArrayList<WeekDaySlot> occup = staffEntry.getValue().getOccupiedSlots();
			for (WeekDaySlot weekDaySlot : occup) {
				if(!days.contains(weekDaySlot.day))
					days.add(weekDaySlot.day);
			}
			for (int i = 1; i < 8; i++) {
				if(!days.contains(i))
					staffHT.get(staffEntry.getKey()).getDaysOff().add(i);
			}
		}
	}
	
	public static void groupDaysOff(){
		for (Entry<String,Group> groupEntry : groupHT.entrySet()) {
			ArrayList<Integer> days = new ArrayList<Integer>();
			ArrayList<WeekDaySlot> occup =groupEntry.getValue().getOccupiedSlots();
			for (WeekDaySlot weekDaySlot : occup) {
				if(!days.contains(weekDaySlot.day))
					days.add(weekDaySlot.day);
			}
			for (int i = 1; i < 8; i++) {
				if(!days.contains(i))
					groupHT.get(groupEntry.getKey()).getDaysOff().add(i);
			}
		}
	}
	
	//process the occupied slots of the staff 
	public static void processStaff(){
		for (StaffEntry staffEntry : SE) {
			String staffID = staffEntry.getStaffName();
			String groupFullName = staffEntry.getGroupFullName();
			String groupName = staffEntry.getGroupName();
			String key = mapStaffGroupFullName(groupFullName, groupName);
			if(groupTutorialSchedule.containsKey(key)){
				ArrayList<WeekDaySlot> time = groupTutorialSchedule.get(key);
				if(!staffHT.containsKey(staffID))
					staffHT.put(staffID, new Staff(staffID,staffEntry.isLecturer()));
				for (WeekDaySlot weekDaySlot : time) {
					staffHT.get(staffID).getOccupiedSlots().add(weekDaySlot);
				}
			}
		}
	}
	
	
	//map the group full name in the staff data to be the same in the group data
	public static String mapStaffGroupFullName(String groupFullName, String groupName){
		String[] split = groupFullName.split(" ");
		return split[0]+" " + split[1] + " " + groupName;
		
	}
	
	//Create the hash table which have group name as keys
	// and its tutorials as values
	// and create hashtable which have tutorials as keys
	// and group name as values to use it in processing staff schedule
	public static void processTutorialGroup(){
		for (GroupEntry groupEntry : GE) {
			String studyGroup = groupEntry.getStudyGroup();
			String tutName = groupEntry.getGroupName();
			String tutID = getTutID(tutName);
			if(!TutorialGroup.containsKey(studyGroup))
				TutorialGroup.put(studyGroup, new ArrayList<String>());
			if(!isLecture(tutName)){
				if(!TutorialGroup.get(studyGroup).contains(tutID))
					TutorialGroup.get(studyGroup).add(tutID);
			}
		}
	}
	
	
	//Fill the occupied slots of every tutorial
	// and fill the occupied slots of every tutorial course to use it in getting staff schedule
	public static void processTutorials(){
		for (GroupEntry groupEntry : GE) {
			String studyGroup = groupEntry.getStudyGroup();
			String tutName = groupEntry.getGroupName();
			String tutID = getTutID(tutName);
			String studyGroupFullName = groupEntry.getGroupFullName();
			WeekDaySlot time = new WeekDaySlot(0, groupEntry.getDay(), groupEntry.getSlot());
			// the schedule of every tutorial course to use it in stuff schedule
			if(!groupTutorialSchedule.containsKey(studyGroupFullName))
				groupTutorialSchedule.put(studyGroupFullName,new ArrayList<WeekDaySlot>());
			groupTutorialSchedule.get(studyGroupFullName).add(time);
			if(!isLecture(tutName)){
			String key = studyGroup + " " + tutID; 
			if(!groupHT.containsKey(key))
				groupHT.put(key, new Group(studyGroup,tutID));
			
			groupHT.get(key).getOccupiedSlots().add(time);
		 }else{
			 ArrayList<String> tuts = TutorialGroup.get(studyGroup);
			 for (String tut : tuts) {
				String key = studyGroup + " " + tut; 
				if(!groupHT.containsKey(key))
					groupHT.put(key, new Group(studyGroup,tut));
				    groupHT.get(key).getOccupiedSlots().add(time);
			}
		  }	
		}
	}

	
	
	
	
	//check if the group name is lecture or tutorial
	//e.g DMET L01 --> true, 5DMET P20 --> false
	public static boolean isLecture(String tutName){
		String[] tutSplit = tutName.split(" ");
		return tutSplit[1].charAt(0) == 'L';
	}
	
	public static String getTutID(String tutName){
		String[] tutSplit = tutName.split(" ");
		int tutNum = Integer.parseInt(tutSplit[1].substring(1));
		return (tutSplit[0] + tutNum);
	}
	
	public static void processLectures(){
		for (GroupEntry groupEntry : GE) {
			String studyGroup = groupEntry.getStudyGroup();
			String key = studyGroup + " 0";
			if(!groupHT.containsKey(key))
				groupHT.put(key, new Group(studyGroup,"0"));	
			WeekDaySlot time = new WeekDaySlot(0, groupEntry.getDay(), groupEntry.getSlot());
			groupHT.get(key).getOccupiedSlots().add(time);
		}
	}
}
