import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringJoiner;

class Compensation{
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
}
public class OutputHandler {
	static PrintWriter pw;
	static BufferedWriter bw;
	public static void main(String[] args) throws IOException {
		initHandler();
		storeCompensationCSV(new Compensation("hamada", "balabizo", 
				new WeekDaySlot(1, 1, 1), "masna3 el karasy"));


	}
	public static void initHandler() throws IOException{
		String fileName = "Compensations.csv";
		bw = new BufferedWriter(new FileWriter(fileName));
		pw = new PrintWriter(bw);
		pw.println("staff_id,group_id,week,day,slot,room_id");
		pw.flush();
	}
	public static void storeCompensationCSV(Compensation c) throws FileNotFoundException{
		pw = new PrintWriter(bw);
		StringJoiner joiner = new StringJoiner(",");
		joiner.add(c.staffID).add(c.groupID).
		add("" + c.time.week).add("" + c.time.day).add("" + c.time.slot)
		.add(c.roomID);
		pw.println(joiner.toString());
		pw.flush();
	}
}
