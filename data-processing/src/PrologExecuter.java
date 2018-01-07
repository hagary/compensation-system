import java.io.IOException;


public class PrologExecuter {
	// public static void main(String[] args) throws IOException {
	// 	executeProlog("../prolog/compensation-system.pl");
	// }
	public static void executeProlog(String prologFile) throws IOException{
		Runtime.getRuntime().exec("/usr/local/bin/swipl -q -f " + prologFile + " -t query");
	}
}
