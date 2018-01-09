import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class CSVReader {

	public static ArrayList<GroupEntry> readGroupFile() throws Exception{

		BufferedReader br = new BufferedReader(new FileReader(new File("Groups.csv")));
		String line = "";
		ArrayList<GroupEntry> entries = new ArrayList<GroupEntry>();
		br.readLine(); //skip first line
		while((line = br.readLine()) != null){
			String [] columns = line.split(",");
			GroupEntry entry =  new GroupEntry();
			
			entry.setGroupID(Integer.parseInt(columns[0]));
			entry.setGroupName(columns[1]);
			entry.setGroupFullName(columns[2]);
			entry.setCourseCode(columns[3]);
			entry.setCourseName(columns[4]);
			entry.setDay(Integer.parseInt(columns[5]));
			entry.setSlot(Integer.parseInt(columns[6]));
			entry.setStudyGroup(columns[7]);
			entry.setSessionType(columns[8]);
			entry.setRoomName(columns[9]);
			entry.setRoomCapacity(Integer.parseInt(columns[10]));
			entry.setGroupSize(Integer.parseInt(columns[11]));
			
			entries.add(entry);
		}
		br.close();
		return entries;
	}
	
	
	public static ArrayList<StaffEntry> readStaffFile() throws Exception{

		BufferedReader br = new BufferedReader(new FileReader(new File("Staff.csv")));
		String line = "";
		ArrayList<StaffEntry> entries = new ArrayList<StaffEntry>();
		br.readLine(); //skip first line
		while((line = br.readLine()) != null){
			String [] columns = line.split(",");
			StaffEntry entry =  new StaffEntry();
			
			entry.setGroupID(Integer.parseInt(columns[0]));
			entry.setGroupName(columns[1]);
			entry.setGroupFullName(columns[2]);
			entry.setStaffName(columns[3]);
			boolean isLecturer = columns[4].equals("0") ? false : true;
			entry.setLecturer(isLecturer);
			
			entries.add(entry);
		}
		br.close();
		return entries;
	}
}
