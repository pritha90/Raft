package utility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;




public class HelperFunctions{
	
	
	public static String[] readConfigFile(String filename) throws IOException {
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    String everything = sb.toString();
		    everything = everything.trim();
		    System.out.println("Config");
		    System.out.println(everything);
		    return everything.split(",");
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}

    }
}