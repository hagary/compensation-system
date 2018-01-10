package processing;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map.Entry;

import javafx.util.Pair;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;


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
	
	static int[][] roomCompEachWeek;
	static  ArrayList<String> roomsNames;
	static  ArrayList<Integer> roomsIDs;
	static	ArrayList<Integer> roomsLocs;
	static	ArrayList<Integer> roomsCaps;
	static	ArrayList<Integer> roomsTypes;
	static	ArrayList<Integer> roomsOccupLists;
	
	//User Input
	static  ArrayList<WeekDaySlot> preftTimes;
	static  int[] prefLocs;
	static  String stuffID;
	static  String groupName;
	static int prefRoomType;
	static int startWeek;
	static int startDay;

	public static Compensation query(String StuffID, String GroupID,String Day1,String Slot1,String Day2,
			String Slot2, String Day3, String Slot3, String B, String C, String D,String prefType,String week,String Day) throws IOException, ParserConfigurationException, SAXException{
		formatFronEnd(StuffID, GroupID, Day1, Slot1, Day2, Slot2, Day3, Slot3, B, C, D, prefType, week, Day);
		 writeLogicalFactsAndQueryToFiles();
		 PrologExecuter.executeProlog("../prolog/compensation-system.pl");
		 return storeCompensation(StuffID, GroupID);
	}
	
	
	public static void init() throws Exception{
		 GE = CSVReader.readGroupFile();
		 SE = CSVReader.readStaffFile();
		 groupHT = new Hashtable<>();
		 staffHT = new Hashtable<>();
		 tutHT = new Hashtable<String, Staff>(); 
		 roomHT = new Hashtable<String, Room>(); 
		 TutorialGroup = new Hashtable<String, ArrayList<String>>();
		 groupTutorialSchedule = new Hashtable<String, ArrayList<WeekDaySlot>>();
		 preProcessData();
		 officialHolidays = new ArrayList<WeekDaySlot>();
		 officialHolidays.add(new WeekDaySlot(3, 3, 0));
		 
		 OutputHandler.initHandler();
	}
	
	public static void formatFronEnd(String StuffID, String GroupID,String Day1,String Slot1,String Day2,
		String Slot2, String Day3, String Slot3, String B, String C, String D,String prefType,String week,String Day){
		stuffID = StuffID;
		groupName = GroupID;
		preftTimes = new ArrayList<WeekDaySlot>();
		preftTimes.add(new WeekDaySlot(0,Integer.parseInt(Day1), Integer.parseInt(Slot1)));
		preftTimes.add(new WeekDaySlot(0,Integer.parseInt(Day2), Integer.parseInt(Slot2)));
		preftTimes.add(new WeekDaySlot(0,Integer.parseInt(Day3), Integer.parseInt(Slot3)));
		startWeek = Integer.parseInt(week);
		startDay = Integer.parseInt(Day);
		switch(prefType){
		case"Lab": prefRoomType = 1;break; 
		case"Room": prefRoomType = 2;break; 
		case"Hall":	prefRoomType = 3;break;
		}
		Pair<Integer, Integer> pairB = new Pair<Integer, Integer>(1, Integer.parseInt(B));
		Pair<Integer, Integer> pairC = new Pair<Integer, Integer>(2, Integer.parseInt(C));
		Pair<Integer, Integer> pairD = new Pair<Integer, Integer>(3, Integer.parseInt(D));
		ArrayList<Pair<Integer, Integer> > pairs = new ArrayList<Pair<Integer, Integer> >();
		pairs.add(pairB); pairs.add(pairC); pairs.add(pairD);
		final Comparator<Pair<Integer, Integer>> compare = Comparator.comparing(Pair::getValue);
		Collections.sort(pairs, compare);
		prefLocs = new int[3];
		prefLocs[0] = pairs.get(0).getKey();
		prefLocs[1] = pairs.get(1).getKey();
		prefLocs[2] = pairs.get(2).getKey();
	}
	
	public static void preProcessData(){
		 processTutorialGroup();
		 processLectures();
		 processTutorials();
		 processStaff();
		 staffDaysOff();
		 groupDaysOff();
		 roomsIDs = new ArrayList<Integer>();
		 roomsLocs = new ArrayList<Integer>();
		 roomsCaps = new ArrayList<Integer>();
		 roomsTypes = new ArrayList<Integer>();
		 roomsOccupLists = new ArrayList<Integer>();
		 roomsNames = new ArrayList<String>();
		 roomCompEachWeek = new int[16][roomHT.size()];
		 for (int j = 0; j < 16; j++) {
			 Arrays.fill(roomCompEachWeek[j],0);
		 }
			int i = 1;
		    for (Entry<String,Room> roomEntry : roomHT.entrySet()) {
		    	roomsNames.add(roomEntry.getValue().ID);
		    	roomsIDs.add(i);
		    	roomsLocs.add(roomEntry.getValue().location);
		    	roomsCaps.add(roomEntry.getValue().capacity);
		    	roomsTypes.add(roomEntry.getValue().type);
		    	ArrayList<WeekDaySlot> occuplists = roomEntry.getValue().occupiedSlots;
		    	roomsOccupLists.add(mapOccupiedSlots(occuplists));
		    	i++;
		    }

	}
	
	public static void getOfficialHoildays(ArrayList<WeekDaySlot> offHolidays){
		officialHolidays = offHolidays;
	}
	
	public static Compensation storeCompensation(String staffID, String groupID) throws ParserConfigurationException, SAXException, IOException{
		Compensation comp = OutputHandler.readCompensationXML(staffID, groupID);
		comp.roomName = roomsNames.get(Integer.parseInt(comp.roomID) - 1);
		OutputHandler.storeCompensationCSV(comp);
		// Store TA compensation date.
		staffHT.get(comp.staffID).getCompDates().add(comp.time);
		// Store group compensation date.
		groupHT.get(comp.groupID).getCompDates().add(comp.time);
		// Store room compensation date.
		int slotNum = (comp.time.day - 1) * 5 + comp.time.slot;
		roomCompEachWeek[comp.time.week - 1][Integer.parseInt(comp.roomID)-1] |= 1<<(slotNum-1);
		return comp;
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
		return (tutNum+"");
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
	
	public static void writeLogicalFactsAndQueryToFiles() throws IOException{
		Path path = Paths.get("../prolog/query.pl");
		
		if(Files.exists(path,LinkOption.NOFOLLOW_LINKS))
			Files.delete(path);
		
		ArrayList<String> kowledgeBaseLines = generateKnowledgeBase(stuffID,groupName,startWeek,startDay,preftTimes,prefRoomType,prefLocs);
		Files.write(path, kowledgeBaseLines,Charset.defaultCharset());
		
//		path = Paths.get("Logic-Agent/query.pl");
//		if(Files.exists(path, LinkOption.NOFOLLOW_LINKS))
//			Files.delete(path);
		

//		ArrayList<String> queryLines = generateQuery();
//		Files.write(path, queryLines,Charset.defaultCharset());
	}
	
	public static ArrayList<String> generateKnowledgeBase(String TA_ID, String groupName, int startWeek, int startDay,
		ArrayList<WeekDaySlot> prefTimes, int prefRoomType,  int[] prefRoomLoc){
		ArrayList<String> lines = new ArrayList<String>();
		lines.add("query:-");
		
		//Staff data
		lines.add("TA = (TAOccup, TAComp, TAOff),");
		Staff staff = staffHT.get(TA_ID);
		ArrayList<Integer> daysOff = staff.getDaysOff();
		ArrayList<WeekDaySlot> TAOccupied = staff.getOccupiedSlots();
		ArrayList<WeekDaySlot> TAComp = staff.getCompDates();

		lines.add("TAOccup = " + TAOccupied.toString() + ",");
		lines.add("TAOff = " + daysOff.toString() + ",");
		lines.add("TAComp = " + TAComp.toString() + ",");
		
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
	    lines.add("RoomsIDs = "+ roomsIDs.toString()+",");
	    lines.add("RoomsLocs = "+ roomsLocs.toString()+",");
	    lines.add("RoomsCaps = "+ roomsCaps.toString()+",");
	    lines.add("RoomsTypes = "+ roomsTypes.toString()+",");
	    lines.add("RoomsOccupLists = "+ roomsOccupLists.toString() + ",");
	    lines.add("RoomsCompLists = " + Arrays.deepToString(roomCompEachWeek) + "," );
	    //Preferences data
	    lines.add("Preferences = (PrefTimes, PrefRoomType, PrefRoomLocs),");
	    lines.add("PrefTimes = " + prefTimes.toString() + ",");
	    lines.add("PrefRoomType = " + prefRoomType + ",");
	    lines.add("PrefRoomLocs = " + Arrays.toString(prefRoomLoc) + ",");
	    
	    
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
    		int slotNum = (time.day - 1) * 5 +  time.slot;
    		slots[slotNum] = 1;
    	}
		for (int j = 0; j < slots.length-1; j++) {
			mapInt += Math.pow(2, j)*slots[30-j];
		}
		return mapInt;
	}
	
	
	
}
