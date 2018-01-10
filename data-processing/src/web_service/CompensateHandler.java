package web_service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

import processing.Preprocessing;
import processing.Compensation;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
public class CompensateHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange he) throws IOException {
		InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
		BufferedReader br = new BufferedReader(isr);
		String query = br.readLine();
		System.out.println(query);
		// parse request
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parseQuery(query, parameters);

		Compensation c = null;
		try {
			c = reserveCompensation(parameters);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File file = null;
		if(c==null)
			//No result found!
			file = new File("front-end/sorry.html");
		else
		 file = createHTMLResponse("front-end/output-template.html", "front-end/output.html", c);

		he.sendResponseHeaders(200, 0);
		OutputStream os = he.getResponseBody();
		FileInputStream fs = new FileInputStream(file);
		final byte[] buffer = new byte[0x10000];
		int count = 0;
		while ((count = fs.read(buffer)) >= 0) {
			os.write(buffer,0,count);
		}
		fs.close();
		os.close();
	}
	public static Compensation reserveCompensation(HashMap<String, Object>  parameters) throws IOException, InterruptedException{
		String staffID = (String) parameters.get("instructor");
		String groupName = (String) parameters.get("group");
		String tutNum = (String) parameters.get("tutorial");
		String groupID = groupName + " " + tutNum;
		String startWeek = (String) parameters.get("startWeek");
		String startDay = (String) parameters.get("startDay");
		String prefDay1 = (String) parameters.get("prefDay1");
		String prefSlot1 = (String) parameters.get("prefSlot1");
		String prefDay2 = (String) parameters.get("prefDay2");
		String prefSlot2 = (String) parameters.get("prefSlot2");
		String prefDay3 = (String) parameters.get("prefDay3");
		String prefSlot3 = (String) parameters.get("prefSlot3");
		String BPrior = (String) parameters.get("BPrior");
		String CPrior = (String) parameters.get("CPrior");
		String DPrior = (String) parameters.get("DPrior");
		String roomType = (String) parameters.get("roomType");

		Compensation c = null;
		try {
			c = Preprocessing.query(staffID, groupID, prefDay1, prefSlot1, prefDay2, prefSlot2, prefDay3,
					prefSlot3, BPrior, CPrior, DPrior, roomType, startWeek, startDay);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c;
	}
	public static void parseQuery(String query, Map<String, 
			Object> parameters) throws UnsupportedEncodingException {

		if (query != null) {
			String pairs[] = query.split("[&]");
			for (String pair : pairs) {
				String param[] = pair.split("[=]");
				String key = null;
				String value = null;
				if (param.length > 0) {
					key = URLDecoder.decode(param[0], 
							System.getProperty("file.encoding"));
				}

				if (param.length > 1) {
					value = URLDecoder.decode(param[1], 
							System.getProperty("file.encoding"));
				}

				if (parameters.containsKey(key)) {
					Object obj = parameters.get(key);
					if (obj instanceof List<?>) {
						@SuppressWarnings("unchecked")
						List<String> values = (List<String>) obj;
						values.add(value);

					} else if (obj instanceof String) {
						List<String> values = new ArrayList<String>();
						values.add((String) obj);
						values.add(value);
						parameters.put(key, values);
					}
				} else {
					parameters.put(key, value);
				}
			}
		}
	}

	public static File createHTMLResponse(String templateFile, String outputFile, Compensation c) throws IOException{
		File htmlTemplateFile = new File(templateFile);
		String htmlString = FileUtils.readFileToString(htmlTemplateFile, "US-ASCII");
		htmlString = htmlString.replace("$group", c.getGroupID());
		htmlString = htmlString.replace("$week", c.getTime().getWeek()+"");
		htmlString = htmlString.replace("$day", c.getTime().getDay()+"");
		htmlString = htmlString.replace("$slot", c.getTime().getSlot()+"");
		htmlString = htmlString.replace("$room", c.getRoomName());

		File newHtmlFile = new File(outputFile);
		FileUtils.writeStringToFile(newHtmlFile, htmlString, "US-ASCII");
		
		return newHtmlFile;
	}
}
