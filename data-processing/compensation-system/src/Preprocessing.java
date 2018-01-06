import java.util.ArrayList;
import java.util.Hashtable;


public class Preprocessing {
	
	public static Hashtable<String,Group> groupHT;
	public static ArrayList<GroupEntry> GE;
	
	public static void main(String[] args) throws Exception{
		//System.out.println(CSVReader.readFile().toString());
		//System.out.println(CSVReader.readGroupFile().toString());
		 GE = CSVReader.readGroupFile();
		 groupHT = new Hashtable<>();
		for (GroupEntry groupEntry : GE) {
			String studyGroup = groupEntry.getStudyGroup();
			
			if(!groupHT.containsKey(studyGroup))
				groupHT.put(studyGroup, new Group(studyGroup,0));
			
			WeekDaySlot time = new WeekDaySlot(0, groupEntry.getDay(), groupEntry.getSlot());
			groupHT.get(studyGroup).getOccupiedSlots().add(time);
		}
	}
	
	public static void processTutorials(){
		for (GroupEntry groupEntry : GE) {
			String studyGroup = groupEntry.getStudyGroup();
			String tutName = groupEntry.getGroupFullName();
			int tutNum = getTutNum(tutName);

			String key = studyGroup + " " + tutNum; 
			if(!groupHT.containsKey(key))
				groupHT.put(key, new Group(studyGroup,tutNum));
			
			WeekDaySlot time = new WeekDaySlot(0, groupEntry.getDay(), groupEntry.getSlot());
			groupHT.get(key).getOccupiedSlots().add(time);
		}
	}
	
	public static int getTutNum(String tutName){
		String[] tutSplit = tutName.split(" ");
		return Integer.parseInt(tutSplit[1].substring(1));
	}
	
	public static void processLectures(){
		for (GroupEntry groupEntry : GE) {
			String studyGroup = groupEntry.getStudyGroup();
			String key = studyGroup + " 0";
			if(!groupHT.containsKey(key))
				groupHT.put(key, new Group(studyGroup,0));
			
			WeekDaySlot time = new WeekDaySlot(0, groupEntry.getDay(), groupEntry.getSlot());
			groupHT.get(key).getOccupiedSlots().add(time);
		}
	}
}
