package eu.lausek.brainfaq;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

public class Program {

	private PrintStream stream = System.out;
	private char[] program = null;

	public Program(String path) throws FileNotFoundException, IOException {
		parse(new BufferedReader(new FileReader(path)));
	}

	public Program(String path, PrintStream stream) throws FileNotFoundException, IOException {
		this(path);
		this.stream = stream;
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
		final int BUFFER_SIZE_BLOCK = 32;
		final int BUFFER_SIZE_START = 10000;

		program = new char[BUFFER_SIZE_START];

		char[] buffer = new char[BUFFER_SIZE_BLOCK];
		int total = 0;

		while (reader.read(buffer) > 0) {
			for (int i = 0; i < BUFFER_SIZE_BLOCK; i++) {
				// TODO: make this dependent on opcodes
				switch (buffer[i]) {
				case 43:
				case 44:
				case 45:
				case 46:
				case 60:
				case 62:
				case 91:
				case 93:
					program[total++] = buffer[i];

					// If program array is too short, resize it to the double length
					if (total == program.length) {
						program = Arrays.copyOf(program, program.length * 2);
					}

					break;
				}
			}
		}

		program = Arrays.copyOf(program, total);
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
				stream.print((char) regs.get());
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
