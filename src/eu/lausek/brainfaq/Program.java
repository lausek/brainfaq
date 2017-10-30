package eu.lausek.brainfaq;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Program {

	private char[] program = null;

	public Program(String path) throws FileNotFoundException, IOException {
		parse(new BufferedReader(new FileReader(path)));
	}

	/**
	 * Loads a BufferedReader into a char array for execution.
	 * 
	 * TODO: this needs some optimization
	 * 
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	public void parse(BufferedReader reader) throws IOException {
		final int BUFFER_SIZE = 32;
		java.util.List<char[]> lineArray = new java.util.ArrayList<>();

		char[] buffer = new char[BUFFER_SIZE];
		while (reader.read(buffer) > 0) {
			lineArray.add(buffer.clone());
		}

		// TODO: check if nesting is correct
		// TODO: only move valid commands
		program = new char[lineArray.size() * BUFFER_SIZE];
		int block = 0;
		for (char[] line : lineArray) {
			for (int i = 0; i < BUFFER_SIZE; i++) {
				program[block * (BUFFER_SIZE) + i] = line[i];
			}
			block++;
		}
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

		Logger.error("Incorrect nesting of brackets starting at " + from);
		return 0;
	}

	/**
	 * Execute a loaded bf program.
	 * 
	 * @param program
	 * @throws IOException
	 */
	public void execute() throws IOException {
		Registers regs = new Registers();

		Logger.log("Executing...");

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

		Logger.log("Done!");

	}

}