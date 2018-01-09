package web_service;

import java.io.BufferedReader;
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
        
        
        he.sendResponseHeaders(200, 0);
        OutputStream os = he.getResponseBody();
        os.write(c.toString().getBytes());
        os.close();
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
}
