import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringJoiner;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

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
	@Override
	public String toString() {
		return "Compensation [staffID=" + staffID + ", groupID=" + groupID
				+ ", time=" + time + ", roomID=" + roomID + "]";
	}
}
public class OutputHandler {
	static PrintWriter pw;
	static BufferedWriter bw;
	
//	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
//		initHandler();
//		Compensation c = readCompensationXML("hamada", "balabizo");
//		storeCompensationCSV(c);
//	}
	
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
	
	public static Compensation readCompensationXML(String staffID, String groupID) throws ParserConfigurationException, SAXException, IOException{
		String fileName = "output.xml";
		File xmlFile = new File(fileName);
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(xmlFile);
		doc.getDocumentElement().normalize();
		
		int week = Integer.parseInt(
				doc.getElementsByTagName("week").item(0).getTextContent());
		int day = Integer.parseInt(
				doc.getElementsByTagName("day").item(0).getTextContent());
		int slot = Integer.parseInt(
				doc.getElementsByTagName("slot").item(0).getTextContent());
		String roomID = 
				doc.getElementsByTagName("day").item(0).getTextContent();
		
		return new Compensation(staffID, groupID, new WeekDaySlot(week, day, slot), 
				roomID);
	}
}
