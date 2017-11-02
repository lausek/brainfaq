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

	public void resizeIfOverflow(int total) {
		if (program.length <= total) {
			program = Arrays.copyOf(program, program.length * 2);
		}
	}

	/**
	 * Commands, for which inline compression is allowed/supported
	 * 
	 * @param op
	 * @return
	 */
	public boolean codeCompressSupported(char op) {
		return op == 43 || op == 45 || op == 60 || op == 62;
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
		final int MORE_TIMES_THAN = 2;

		program = new char[BUFFER_SIZE_START];

		char[] buffer = new char[BUFFER_SIZE_BLOCK];
		int total = 0, times = 1, i = 0;
		char lastChar = ' ';

		// put = how many chars have been transferred
		for (int put = reader.read(buffer); put > 0; put = reader.read(buffer)) {
			for (i = 0; i < put; i++) {
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
					if (lastChar != ' ') {
						if (lastChar != buffer[i]) {

							// If program array is too short, resize it to the double length
							resizeIfOverflow(total + 2);

							if (times > MORE_TIMES_THAN && codeCompressSupported(lastChar)) {
								program[total++] = (char) (48 + times);
								program[total++] = lastChar;
							} else {
								for (int x = 1; x <= times; x++) {
									program[total++] = lastChar;
								}
							}

							times = 1;
							lastChar = ' ';

						} else {

							// TODO: if times gets bigger than 36, add current char and reset counter
							if (9 < times + 1) {
								// If program array is too short, resize it to the double length
								resizeIfOverflow(total + 2);

								if (times > MORE_TIMES_THAN && codeCompressSupported(lastChar)) {
									program[total++] = (char) (48 + times);
									program[total++] = lastChar;
								} else {
									for (int x = 1; x <= times; x++) {
										program[total++] = lastChar;
									}
								}

								lastChar = ' ';
								times = 1;
							} else {
								times++;
							}

						}
					}

					lastChar = buffer[i];

					break;
				}

			}

			if (lastChar != ' ') {

				// If program array is too short, resize it to the double length
				resizeIfOverflow(total + 2);

				if (times > MORE_TIMES_THAN && codeCompressSupported(lastChar)) {
					program[total++] = (char) (48 + times);
					program[total++] = lastChar;
				} else {
					for (int x = 1; x <= times; x++) {
						program[total++] = lastChar;
					}
				}

				times = 1;
				lastChar = ' ';

			}

			// TODO: add code from change clause here too. lastChar could be skipped
			// eventually

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
		int times = 1;

		Logger.log("Executing...");

		for (int ptr = 0; ptr < program.length; ptr++) {

			// is cell a number? 0 (47 ascii) to 9 (57 ascii)
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
