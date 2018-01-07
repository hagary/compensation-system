import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map.Entry;


public class Preprocessing {
	
	public static Hashtable<String,Group> groupHT;
	public static Hashtable<String,Staff> staffHT;
	public static Hashtable<String,Staff> tutHT;
	public static Hashtable<String,Room> roomHT;
	public static Hashtable<String, ArrayList<String>> TutorialGroup;
	public static Hashtable<String,ArrayList<WeekDaySlot>> groupTutorialSchedule;
	public static ArrayList<GroupEntry> GE;
	public static ArrayList<StaffEntry> SE;
	public static ArrayList<WeekDaySlot> officialHolidays;

	
	public static void main(String[] args) throws Exception{
		 GE = CSVReader.readGroupFile();
		 SE = CSVReader.readStaffFile();
		 groupHT = new Hashtable<>();
		 staffHT = new Hashtable<>();
		 tutHT = new Hashtable<String, Staff>(); 
		 roomHT = new Hashtable<String, Room>(); 
		 TutorialGroup = new Hashtable<String, ArrayList<String>>();
		 groupTutorialSchedule = new Hashtable<String, ArrayList<WeekDaySlot>>();
		 processTutorialGroup();
		 processLectures();
		 processTutorials();
		 processStaff();
		 staffDaysOff();
		 groupDaysOff();
		 System.out.println(groupHT);
		 officialHolidays = new ArrayList<WeekDaySlot>();
		 officialHolidays.add(new WeekDaySlot(3, 3, 0));
		 ArrayList<WeekDaySlot> preftTimes = new ArrayList<WeekDaySlot>();
		 preftTimes.add(new WeekDaySlot(0, 1, 1));
		 ArrayList<Integer> prefLocs = new ArrayList<Integer>();
		 prefLocs.add(2);
		 prefLocs.add(3);
		 prefLocs.add(1);
		writeLogicalFactsAndQueryToFiles("0xF647336BCE5A1BD337998EFF78653F42443A1BD0","MET Computer Science 5th Semester II 5CSEN17",1,1,
				preftTimes,1,prefLocs);
	}
	
	
	public static void getOfficialHoildays(ArrayList<WeekDaySlot> offHolidays ){
		officialHolidays = offHolidays;
	}
	
	
	//calculate the off days of the stuff
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
	
	//calculate the off days of each group
	public static void groupDaysOff(){
		for (Entry<String,Group> groupEntry : groupHT.entrySet()) {
			ArrayList<Integer> days = new ArrayList<Integer>();
			ArrayList<WeekDaySlot> occup = groupEntry.getValue().getOccupiedSlots();
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
					if(!staffHT.get(staffID).getOccupiedSlots().contains(weekDaySlot))
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
			int groupSize = groupEntry.getGroupSize();
			if(!isLecture(tutName)){
			String key = studyGroup + " " + tutID; 
			if(!groupHT.containsKey(key))
				groupHT.put(key, new Group(studyGroup,tutID,groupSize));
			groupHT.get(key).setSize(groupSize);
			groupHT.get(key).getOccupiedSlots().add(time);
		 }else{
			 ArrayList<String> tuts = TutorialGroup.get(studyGroup);
			 for (String tut : tuts) {
				String key = studyGroup + " " + tut; 
				if(!groupHT.containsKey(key))
					groupHT.put(key, new Group(studyGroup,tut,0));
				    groupHT.get(key).getOccupiedSlots().add(time);
			}
		  }	
			
			// fill the occupied list of the rooms and get its location, type and capacity
			String roomID = groupEntry.getRoomName();
			int capacity = groupEntry.getRoomCapacity();
			if(!roomHT.containsKey(roomID)){
				String sessionType = groupEntry.getSessionType();
				int roomType = getRoomType(sessionType);
				int roomLoc = getRoomLoc(roomID);
				roomHT.put(roomID, new Room(roomID,capacity,roomLoc,roomType));
			}else{
				int maxCapacity = Math.max(roomHT.get(roomID).getCapacity(),capacity);
				roomHT.get(roomID).setCapacity(maxCapacity);
			}
			roomHT.get(roomID).getOccupiedSlots().add(time);		
		}
	}

	
	public static int getRoomLoc(String RoomID){
		char firstChar = RoomID.charAt(0);
		switch(firstChar){
		case'B': return 1;
		case'C': return 2;
		case'D': return 3;
		case'H': return(getLecLoc(RoomID));		
		default: return 0;		
		}
	}
	
	public static int getLecLoc(String lectureLoc){
		int lecNum = Integer.parseInt(lectureLoc.substring(1));
		if(lecNum < 8)
		return 1;
		if(lecNum < 15)
		return 2;
		return 3;
	}
	
	public static int getRoomType(String SessionType){
		switch(SessionType){
			case"Practical": return 1;
			case"Tutorial": return 2;
			case"Lecture": return 3;
			default: return 0;
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
			int groupSize = groupEntry.getGroupSize();
			if(!groupHT.containsKey(key))
				groupHT.put(key, new Group(studyGroup,"0",groupSize));	
			WeekDaySlot time = new WeekDaySlot(0, groupEntry.getDay(), groupEntry.getSlot());
			groupHT.get(key).getOccupiedSlots().add(time);
		}
	}
	
	public static void writeLogicalFactsAndQueryToFiles(String TA_ID, String groupName, int startWeek, int startDay,
			ArrayList<WeekDaySlot> prefTimes, int prefRoomType,  ArrayList<Integer> prefRoomLoc) throws IOException{
		Path path = Paths.get("test.pl");
		
		if(Files.exists(path,LinkOption.NOFOLLOW_LINKS))
			Files.delete(path);
		
		ArrayList<String> kowledgeBaseLines = generateKnowledgeBase(TA_ID,groupName,startWeek,startDay,prefTimes,prefRoomType,prefRoomLoc);
		Files.write(path, kowledgeBaseLines,Charset.defaultCharset());
		
//		path = Paths.get("Logic-Agent/query.pl");
//		if(Files.exists(path, LinkOption.NOFOLLOW_LINKS))
//			Files.delete(path);
		

//		ArrayList<String> queryLines = generateQuery();
//		Files.write(path, queryLines,Charset.defaultCharset());
	}
	
	public static ArrayList<String> generateKnowledgeBase(String TA_ID, String groupName, int startWeek, int startDay,
			ArrayList<WeekDaySlot> prefTimes, int prefRoomType,  ArrayList<Integer> prefRoomLoc){
		ArrayList<String> lines = new ArrayList<String>();
		//Staff data
		lines.add("TA = (TAOccup, TAComp, TAOff),");
		Staff staff = staffHT.get(TA_ID);
		ArrayList<Integer> daysOff = staff.getDaysOff();
		ArrayList<WeekDaySlot> TAOccupied = staff.getOccupiedSlots();
		lines.add("TAOccup = " + TAOccupied.toString() + ",");
		lines.add("TAOff = " + daysOff.toString() + ",");
		
		//Group data
		lines.add("Group = (GroupOccup, GroupComp, GroupOff, GroupSize),");
		Group group = groupHT.get(groupName);
		ArrayList<WeekDaySlot> groupOccupied = group.getOccupiedSlots();
		ArrayList<WeekDaySlot> groupComp = group.getCompDates();

		lines.add("GroupSize = " + group.getSize() + "," );
		lines.add("GroupOccup = " + groupOccupied.toString() + ",");
		lines.add("GroupComp = " + groupComp.toString()  + "," );
		//Start dates
		lines.add("CompStart = " + "(" + startWeek + "," + startDay + "),");
		
		//Rooms data
		lines.add("Rooms = [RoomsIDs, RoomsLocs, RoomsCaps, RoomsTypes, RoomsOccupLists, RoomsCompLists],");
		ArrayList<Integer> roomsIDs = new ArrayList<Integer>();
		ArrayList<Integer> roomsLocs = new ArrayList<Integer>();
		ArrayList<Integer> roomsCaps = new ArrayList<Integer>();
		ArrayList<Integer> roomsTypes = new ArrayList<Integer>();
		ArrayList<Integer> roomsOccupLists = new ArrayList<Integer>();
		ArrayList<Integer> roomscompLists = new ArrayList<Integer>();

		int i = 1;
	    for (Entry<String,Room> roomEntry : roomHT.entrySet()) {
	    	roomsIDs.add(i);
	    	roomsLocs.add(roomEntry.getValue().location);
	    	roomsCaps.add(roomEntry.getValue().capacity);
	    	roomsTypes.add(roomEntry.getValue().type);
	    	ArrayList<WeekDaySlot> occuplists = roomEntry.getValue().occupiedSlots;
	    	roomsOccupLists.add(mapOccupiedSlots(occuplists));
	    	ArrayList<WeekDaySlot> complists =  roomEntry.getValue().compDates;
	    	i++;
	    }
	    
	    lines.add("RoomsIDs = "+ roomsIDs.toString()+",");
	    lines.add("RoomsLocs = "+ roomsLocs.toString()+",");
	    lines.add("RoomsCaps = "+ roomsCaps.toString()+",");
	    lines.add("RoomsTypes = "+ roomsTypes.toString()+",");
	    lines.add("RoomsOccupLists = "+ roomsOccupLists.toString() + ",");
	    lines.add("RoomsCompLists = " + roomscompLists.toString() + "," );
	    //Preferences data
	    lines.add("Preferences = (PrefTimes, PrefRoomType, PrefRoomLocs),");
	    lines.add("PrefTimes = " + prefTimes.toString() + ",");
	    lines.add("PrefRoomType = " + prefRoomType + ",");
	    lines.add("PrefRoomLocs = " + prefRoomLoc.toString() + ",");
	    
	    
	    //holiday dates
	    lines.add("Holidays = " + officialHolidays.toString() + ",");
	    //Input and query format
	    lines.add("IN = (TA, Group, CompStart, Holidays, Rooms, Preferences),");
	    lines.add("compensate(IN, OUT).");
	    
	    return lines;

	}
	
	public static int mapOccupiedSlots(ArrayList<WeekDaySlot> occuplists){
		int[] slots = new int[31];
		int mapInt = 0;
		for(WeekDaySlot time : occuplists){
    		int slotNum = time.day*time.slot;
    		slots[slotNum] = 1;
    	}
		for (int j = 0; j < slots.length-1; j++) {
			mapInt += Math.pow(2, j)*slots[30-j];
		}
		return mapInt;
	}
	
	
	
}
