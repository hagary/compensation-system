package processing;
import java.io.IOException;


public class PrologExecuter {
	public static void executeProlog(String prologFile) throws IOException, InterruptedException{
		Process p = Runtime.getRuntime().exec("/usr/local/bin/swipl -q -f " + prologFile + " -t query");
		System.out.println("Waiting for prolog ...");
		p.waitFor();
		System.out.println("Prolog done.");
	}
}
