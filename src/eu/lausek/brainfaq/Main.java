package eu.lausek.brainfaq;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author lausek
 *
 */

public class Main {

	private static long startTime;
	
	private static void error(String msg) {
		log(System.err, msg);
		System.exit(1);
	}
	
	private static void log(String msg) {
		log(System.out, msg);
	}

	private static void log(PrintStream stream, String msg) {
		stream.printf("[%.3f] %s \n", (System.currentTimeMillis() - startTime) / 1000.0f, msg);
	}
	
	public static void main(String[] args) {

		BufferedReader handle = null;
		String path = null;
		char[] program = null;
		
		startTime = System.currentTimeMillis();
		
		if (args.length == 0) {
			error("No file specified");
		}
		
		path = args[0];

		try {
			handle = new BufferedReader(new FileReader(path));
			log("Loading '"+path+"'");
			program = parse(handle);
		} catch (FileNotFoundException e) {
			error("Couldn't locate file '" + path + "'");
		} catch (IOException e) {
			error("Cannot work with file '" + path + "'");
		}

		try {
			execute(program);
		} catch (IOException e) {
			error("Error while reading input from user");
		}

	}

	public static char[] parse(BufferedReader reader) throws IOException {
		final int BUFFER_SIZE = 32;
		List<char[]> lineArray = new ArrayList<>();
		
		char[] buffer = new char[BUFFER_SIZE];
		while (reader.read(buffer) > 0) {
			lineArray.add(buffer.clone());
		}

		char[] program = new char[lineArray.size() * BUFFER_SIZE];
		int block = 0;
		for (char[] line : lineArray) {
			for (int i = 0; i < BUFFER_SIZE; i++) {
				// TODO: skip useless chars
				program[block * (BUFFER_SIZE) + i] = line[i];
			}
			block++;
		}

		return program;
	}

	/**
	 * Find the next correct occurrence of bracket. If bracket is '[' search will go
	 * forward, otherwise backwards.
	 * 
	 * @param program
	 *            The program that should be searched
	 * @param from
	 *            Which index should we use for start
	 * @param bracket
	 *            The bracket for which a closing one is needed ('[' or ']')
	 * @return
	 */
	private static int findNext(char[] program, int from, char bracket) {
		int step = bracket == '[' ? -1 : 1;
		int depth = 0; // how deep is the nesting level?

		for (int i = from + step; 0 <= i && i < program.length; i += step) {
			switch (program[i]) {
			case '[':
			case ']':
				if (bracket == program[i]) {
					if (depth == 0) {
						return i;
					}
					depth++;
				} else {
					depth--;
				}
				break;
			}
		}
		// TODO: add place
		error("Incorrect nesting of brackets starting at "+from);
		return 0;
	}

	public static void execute(char[] program) throws IOException {
		Registers regs = new Registers();
		
		log("Executing...");
		
		for (int ptr = 0; ptr < program.length; ptr++) {

			switch (program[ptr]) {
			case '>':
				regs.next();
				break;

			case '<':
				regs.prev();
				break;

			case '+':
				regs.increment();
				break;

			case '-':
				regs.decrement();
				break;

			case '[':
				if (regs.get() == 0) {
					ptr = findNext(program, ptr, ']');
				}
				break;

			case ']':
				if (regs.get() != 0) {
					ptr = findNext(program, ptr, '[') - 1;
				}
				break;

			case '.':
				System.out.print((char) regs.get());
				break;

			case ',':
				regs.set(System.in.read());
				break;

			default:
				break;
			}
		}
		
		log("Done!");

	}

}
