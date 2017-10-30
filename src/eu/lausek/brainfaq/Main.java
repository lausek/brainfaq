package eu.lausek.brainfaq;

import java.io.*;

/**
 * 
 * @author lausek
 *
 */

public class Main {

	public static void main(String[] args) {

		String path = null;

		if (args.length == 0) {
			Logger.error("No file specified");
		}

		path = args[0];
		
		Program program = null;
		try {
			Logger.log("Loading '" + path + "'");
			program = new Program(path);
		} catch (FileNotFoundException e) {
			Logger.error("Couldn't locate file '" + path + "'");
		} catch (IOException e) {
			Logger.error("Cannot work with file '" + path + "'");
		}

		try {
			program.execute();
		} catch (IOException e) {
			Logger.error("Error while reading input from user");
		}

	}

}
