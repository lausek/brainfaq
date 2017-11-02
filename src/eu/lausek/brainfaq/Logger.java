package eu.lausek.brainfaq;

import java.io.PrintStream;

public class Logger {

	private static boolean enabled = true;
	private static final long startTime;

	static {
		startTime = System.currentTimeMillis();
	}
	
	public static void setActive(boolean state) {
		Logger.enabled = state;
	}
	
	public static void error(String msg) {
		log(System.err, msg);
		if (enabled) {
			System.exit(1);
		}
	}

	public static void log(String msg) {
		log(System.out, msg);
	}

	public static void log(PrintStream stream, String msg) {
		if (enabled) {
			stream.printf("[%.3f] %s \n", (System.currentTimeMillis() - startTime) / 1000.0f, msg);
		}
	}

}
