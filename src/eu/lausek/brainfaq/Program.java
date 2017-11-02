package eu.lausek.brainfaq;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

public class Program {

	private PrintStream stream = System.out;
	private char[] program = null;

	public Program(String path) throws FileNotFoundException, IOException {
		// parsers are so stateful, we want to drop them immediately
		try (Parser p = new Parser(path)) {
			program = p.parse();
		}
	}

	public Program(String path, PrintStream stream) throws FileNotFoundException, IOException {
		this(path);
		this.stream = stream;
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
			if (program[i] == '[' || program[i] == ']') {
				if (bracket == program[i]) {
					if (depth == 0) {
						return i;
					}
					depth++;
				} else {
					depth--;
				}
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
		int times = 1;

		Logger.log("Executing...");

		for (int ptr = 0; ptr < program.length; ptr++) {

			// is cell a number? 0 (48 ascii) to 9 (57 ascii)
			if (47 < program[ptr] && program[ptr] < 58) {
				// translate char into int
				times = program[ptr] - 48;
				continue;
			}

			switch (program[ptr]) {
			case '>':
				regs.next(times);
				break;

			case '<':
				regs.prev(times);
				break;

			case '+':
				regs.increment(times);
				break;

			case '-':
				regs.decrement(times);
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
				continue;
			}

			times = 1;
		}

		Logger.log("Done!");

	}

}